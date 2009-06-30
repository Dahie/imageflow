/**
 * 
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import visualap.Node;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.ProxyInput;
import de.danielsenff.imageflow.models.connection.ProxyOutput;

/**
 * @author danielsenff
 *
 */
public class GroupUnitElement extends UnitElement {

	/**
	 * List of units included in this Group
	 */
	protected UnitList units;
	private Vector<Connection> originalConnections;
	private Collection<Connection> internalConnections;

	
	private GroupUnitElement(Point point, String name) {
		super(point, "name", "");
		setLabel(name);
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
		init(selections, allUnits);
	}



	private void init(final Collection<Node> selections, final UnitList allUnits) {
		
		this.units = new UnitList();
		for (Node node : selections) {
			this.units.add(node);
		}
		
		
		
		/*
		 * determine position
		 */
		
		int x, y;
//		for (Node node : this.units) {
			x = (int) getUnit(0).getOrigin().getX();
			y = (int) getUnit(0).getOrigin().getY();
//		}
		setOrigin(new Point(x, y));
			

		/*
		 * determine which connections "leave" the group
		 */
		// get all connections
		ConnectionList allConnections = allUnits.getConnections();
		// see which are connected to the units in the future group
		Collection<Connection> externalConnections = new Vector<Connection>();
		for (Connection connection : allConnections) {
			for (Node node : selections) {
				if (connection.isConnectedToUnit(node) 
						&& !externalConnections.contains(connection))
					externalConnections.add(connection);
			}
		}
		internalConnections = new Vector<Connection>();
		for (Connection connection : externalConnections) {
			for (Node node : selections) {
				for (Node node2 : selections) {
					if(connection.getFromUnit().equals(node)
							&& connection.getToUnit().equals(node2)) {
						internalConnections.add(connection);
					}	
				}
			}
		}

		for (Connection connection : internalConnections) {
			externalConnections.remove(connection);
		}
		
		this.originalConnections = new Vector<Connection>();
		for (Connection connection : externalConnections) {
			this.originalConnections.add(connection);
		}
		
		System.out.println(externalConnections.size());
		System.out.println(internalConnections.size());
		
		
		/*
		 * create inputs and outputs based on external connections
		 */
		HashMap<Output, ProxyOutput> externalOutputs = new HashMap<Output, ProxyOutput>();
		for (Connection connection : externalConnections) {
			if(contains(connection.getToUnit())) 
			{
				ProxyInput pInput = new ProxyInput(connection.getInput(), this, getInputsCount()+1);
				addInput(pInput);
				Connection newconn = new Connection(connection.getOutput(), pInput);
				allUnits.getConnections().add(newconn);
			}
			if(contains(connection.getFromUnit())) 
			{
				ProxyOutput pOutput;
				if(externalOutputs.containsKey(connection.getOutput())) {
					// output already used, take existing proxy
					pOutput = externalOutputs.get(connection.getOutput());
				} else {
					// output not yet used, create new
					pOutput = new ProxyOutput(connection.getOutput(), this, getOutputsCount()+1);
					addOutput(pOutput);
					externalOutputs.put(connection.getOutput(), pOutput);
				}
				Connection newconn = new Connection(pOutput, connection.getInput());
				allUnits.getConnections().add(newconn);
			}
		}
		
		// determine the order of nodes
		
		
		
		
		/*
		 * remove old stuff
		 */
		
		for (Node node : this.units) {
			allUnits.remove(node);
		}
		
		
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
	public Collection<Node> getUnits() {
		return this.units;
	}

	/**
	 * @return the originalConnections
	 */
	public Vector<Connection> getOriginalConnections() {
		return originalConnections;
	}
	
	
	/**
	 * @return the internalConnections
	 */
	public final Collection<Connection> getInternalConnections() {
		return internalConnections;
	}

	@Override
	public GroupUnitElement clone() {
		
		GroupUnitElement groupClone = new GroupUnitElement(getOrigin(), getLabel());
		for (Node node : getUnits()) {
			Node c;
			try {
				c = node.clone();
				groupClone.getUnits().add(c);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		
		return groupClone;
	}
	
}
