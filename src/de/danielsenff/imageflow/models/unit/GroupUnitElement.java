/**
 * Copyright (C) 2008-2011 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import de.danielsenff.imageflow.models.NodeList;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.Pin;
import de.danielsenff.imageflow.models.connection.ProxyInput;
import de.danielsenff.imageflow.models.connection.ProxyOutput;

/**
 * Group unit is similar to the graph controller and contains own lists of Units and Connections.
 * @author Daniel Senff
 *
 */
public class GroupUnitElement extends UnitElement {

	/**
	 * List of units included in this Group
	 */
	protected UnitList units;
	private Vector<Connection> originalConnections;
	private Collection<Connection> internalConnections;
	private Collection<Connection> externalConnections;


	/**
	 * @param point
	 * @param name
	 */
	public GroupUnitElement(final Point point, final String name) {
		super(point, "name", "");
		setLabel(name);
		init();
	}

	/**
	 * @param origin
	 * @param unitName
	 * @param selections 
	 * @param allUnits 
	 * @param unitsImageJSyntax
	 */
	public GroupUnitElement(Point origin, String unitName,
			final Collection<Node> selections, final UnitList allUnits) {
		super(origin, unitName, "");
		init();
		try {
			putUnits(selections, allUnits);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		this.units = new UnitList();
		this.internalConnections = new Vector<Connection>();
		this.externalConnections = new Vector<Connection>();
		this.originalConnections = new Vector<Connection>();
	}

	/**
	 * Add a number of Units to this group.
	 * @param unitsToAdd
	 * @param allUnits
	 * @throws Exception
	 */
	public void putUnits(final Collection<Node> unitsToAdd, final UnitList allUnits) throws Exception {

		findRelevantConnections(unitsToAdd, allUnits.getConnections());
		
		if(selectedUnitsConsistentGroup(unitsToAdd, allUnits)) {
			for (Node node : unitsToAdd) {
				this.units.add(node);
			}

			/*
			 * determine position for group unit on workflow
			 */
			int x = (int) getUnit(0).getOrigin().getX();
			int y = (int) getUnit(0).getOrigin().getY();
			setOrigin(new Point(x, y));

			dealWithConnections(allUnits.getConnections());
		} else throw new Exception("group not albe");

		/*
		 * remove original units from workflow
		 */
		for (Node node : this.units) {
			/* this removes not only the node from the unitList
			 but also disconnects all connections to this unit.
			 Therefore all connections are stored before doing this. */
			allUnits.remove(node);
		}

		// Reconnecting the connections within the group.
		for (Connection connection : internalConnections) {
			connection.connect();
		}

		
		/* uh, reconnect the external connections
		if the target-unit is contained in the group
		just not sure why not connecting from-units */
		for (Connection connection : externalConnections) {
			if(this.units.contains(connection.getToUnit())
					//|| this.units.contains(connection.getFromUnit())
			) {
				connection.connect();
			}
		}
		
		// proxy connections
		
		for (Input input : getInputs()) {
			((ProxyInput)input).getEmbeddedInput().setLocked(true);
		}
		for (Output output : getOutputs()) {
			((ProxyOutput)output).getEmbeddedOutput().setLocked(true);
		}
		
		
		int lowestX = 3000, lowestY = 3000;
		for (Node node : getNodes()) {
			if(node.getOrigin().x < lowestX) {
				lowestX = node.getOrigin().x;
			}
			if(node.getOrigin().y < lowestY) {
				lowestY = node.getOrigin().y;
			} 
		}
		
		// offset from original
		int deltaX = lowestX - 25;
		int deltaY = lowestY - 25;
		int x = 0, y = 0;
		for (Node node : getNodes()) {
			x = node.getOrigin().x; 
			y = node.getOrigin().y;
			node.getOrigin().setLocation(x-deltaX, y-deltaY);
		}
	}

	/**
	 * True if the selected Units result in a consistent group.
	 * @param allUnits 
	 * @param unitsToAdd 
	 * @return
	 */
	private boolean selectedUnitsConsistentGroup(Collection<Node> unitsToAdd, UnitList allUnits) {

		/*
		 * for each input we test, if every unit in input graph is 
		 */

		for (Connection connection : this.externalConnections) {
			for (Node node : unitsToAdd) {
				if(unitsToAdd.contains(connection.getToUnit())
						&& connection.getInput().isConnectedInInputBranch(node))
					return false;
			}
		}

		return true;
	}

	/**
	 * @param unitsToAdd
	 * @param connection
	 * @return
	 */
	private boolean isToUnitIn(final Collection<Node> unitsToAdd, final Connection connection) {
		UnitElement unit = (UnitElement)connection.getFromUnit();
		if(unitsToAdd.contains(unit)
				&& unitsToAdd.contains(connection.getToUnit())) {
			// unit is in selection
			// check if the preceding unit is also in selection
			return true;

		} else {
			for (Input input : unit.getInputs()) {
				if(input.isConnected() ) {
					return isToUnitIn(unitsToAdd, input.getConnection());
				}	
			}
		}
		return false;
	}

	/**
	 * @param allConnections
	 */
	public void dealWithConnections(final ConnectionList allConnections) {

		for (Connection connection : externalConnections) {
			this.originalConnections.add(connection);
		}

		/*
		 * create inputs and outputs based on external connections
		 */
		HashMap<Output, ProxyOutput> externalOutputs = new HashMap<Output, ProxyOutput>();
		ProxyInput pInput;
		Connection newConnection;
		ProxyOutput pOutput;
		for (Connection externalConnection : externalConnections) {
			if(contains(externalConnection.getToUnit())) 
			{
				pInput = new ProxyInput(externalConnection.getInput(), this, getInputsCount()+1);
				addInput(pInput);
				newConnection = new Connection(externalConnection.getOutput(), pInput);
				allConnections.add(newConnection);
			}
			if(contains(externalConnection.getFromUnit())) {
				if(externalOutputs.containsKey(externalConnection.getOutput())) {
					// output already used, take existing proxy
					pOutput = externalOutputs.get(externalConnection.getOutput());
				} else {
					// output not yet used, create new
					pOutput = new ProxyOutput(externalConnection.getOutput(), this, getOutputsCount()+1);
					addOutput(pOutput);
					externalOutputs.put(externalConnection.getOutput(), pOutput);
				}
				newConnection = new Connection(pOutput, externalConnection.getInput());
				allConnections.add(newConnection);
			}
		}
	}

	private final void findRelevantConnections(Collection<Node> unitsToAdd, final ConnectionList allConnections) {
		for (Connection connection : allConnections) {
			for (Node node : unitsToAdd) {
				if (connection.isConnectedToUnit(node) 
						&& !externalConnections.contains(connection))
					externalConnections.add(connection);
			}
		}

		for (Connection connection : externalConnections) {
			for (Node node : unitsToAdd) {
				for (Node node2 : unitsToAdd) {
					if(connection.getFromUnit().equals(node)
							&& connection.getToUnit().equals(node2)) {
						internalConnections.add(connection);
					}	
				}
			}
		}
		
		/*
		 * determine which connections "leave" the group
		 */

		for (Connection connection : internalConnections) {
			externalConnections.remove(connection);
		}
	}


	/**
	 * @return the externalConnections
	 */
	public Collection<Connection> getExternalConnections() {
		return externalConnections;
	}

	
	/**
	 * Returns true if the node in question is contained in this GroupUnit.
	 * @param node
	 * @return
	 */
	public boolean contains(Node node) {
		return this.units.contains(node);
	}

	/**
	 * Number of elements grouped in this GroupUnit.
	 * @return
	 */
	public int getGroupSize() {
		return this.units.getSize();
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public Node getUnit(int i) {
		return this.units.get(i);
	}

	/**
	 * Returns a Collection of Units embedded in this Group.
	 * @return
	 */
	public NodeList<Node> getNodes() {
		return this.units;
	}

	/**
	 * @return the originalConnections
	 */
	public Vector<Connection> getOriginalConnections() {
		return this.originalConnections;
	}


	/**
	 * @return the internalConnections
	 */
	public final Collection<Connection> getInternalConnections() {
		return this.internalConnections;
	}

	@Override
	public GroupUnitElement clone() {

		GroupUnitElement groupClone = new GroupUnitElement(getOrigin(), getLabel());
		groupClone = initClone(groupClone);
		groupClone.setOriginalUnit(this);
		
		return groupClone;
	}

	/**
	 * Initiate a clone the contents of this group into the given groupClone.
	 * @param groupClone
	 * @return
	 */
	protected GroupUnitElement initClone(final GroupUnitElement groupClone) {
		/*
		 * clone included units
		 */
		HashMap<Pin, Pin> correspondingPins = new HashMap<Pin, Pin>();
		UnitElement unit, unitClone;
		Node c;
		for (Node node : getNodes()) {
			try {
				c = node.clone();
				groupClone.getNodes().add(c);

				if(node instanceof UnitElement) {
					unit = (UnitElement) node;
					unitClone = (UnitElement) c;
					for (int i = 0; i < unit.getInputsCount(); i++) {
						correspondingPins.put(unit.getInput(i), unitClone.getInput(i));
					}
					for (int i = 0; i < unit.getOutputsCount(); i++) {
						correspondingPins.put(unit.getOutput(i), unitClone.getOutput(i));
					}
				}

			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * reconnect internal connections
		 * the internal connections have the original pins
		 * we have to create new connections with the respective pins 
		 */
		Input cloneInput;
		Output cloneOutput;
		Connection newConnection; 
		for (Connection originalConnection : getInternalConnections()) {
			cloneInput = (Input) correspondingPins.get(originalConnection.getInput());
			cloneOutput = (Output) correspondingPins.get(originalConnection.getOutput());
			newConnection = new Connection(cloneInput, cloneOutput);
			groupClone.getInternalConnections().add(newConnection);
		}


		/*
		 * clone pins
		 */
		Input input, embeddedInputClone;
		ProxyInput pInput;
		for (int i = 0; i < getInputsCount(); i++) {
			if(getInput(i) instanceof ProxyInput) {
				input = ((ProxyInput)getInput(i)).getEmbeddedInput();
				embeddedInputClone = (Input) correspondingPins.get(input); 
				pInput = new ProxyInput(embeddedInputClone, groupClone, i+1);
				groupClone.addInput(pInput);
			} else {
				super.cloneInput(groupClone, i);
			}
		}
		Output output, embeddedOutputClone;
		ProxyOutput pOutput;
		for (int i = 0; i < getOutputsCount(); i++) {
			if(getOutput(i) instanceof ProxyOutput) {
				output = ((ProxyOutput)getOutput(i)).getEmbeddedOutput();
				embeddedOutputClone = (Output) correspondingPins.get(output); 
				pOutput = new ProxyOutput(embeddedOutputClone, groupClone, i+1);
				groupClone.addOutput(pOutput);
			} else
				super.cloneOutput(groupClone, i);
		}

		return groupClone;
	}
	
	@Override
	public String getHelpString() {
		String string = "Grouped units: \n";
		for (Node node : getNodes()) {
			string += node.getLabel() + " \n ";
		}
		return string;
	}
}
