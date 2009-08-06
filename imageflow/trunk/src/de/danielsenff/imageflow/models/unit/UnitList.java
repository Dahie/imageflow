/**
 * 
 */
package de.danielsenff.imageflow.models.unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import visualap.GList;
import visualap.Node;
import visualap.Pin;
import de.danielsenff.imageflow.models.Displayable;
import de.danielsenff.imageflow.models.Model;
import de.danielsenff.imageflow.models.ModelListener;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.unit.UnitElement.Type;


/**
 * List holding all {@link UnitElement}s. For the moment this is just an empty wrapper.
 * @author danielsenff
 *
 */
public class UnitList extends GList<Node> implements Model, Cloneable {

	private ConnectionList connections;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8204689428123811757L;
	private ArrayList<ModelListener> listeners;

	/**
	 * 
	 */
	public UnitList() {
		this.listeners = new ArrayList<ModelListener>();
		this.connections = new ConnectionList();
	}


	@Override
	public UnitList clone() {
		UnitList clone = new UnitList();

		Collection<Connection> tmpConn = new Vector<Connection>();
		HashMap<Pin, Pin> correspondingPins = new HashMap<Pin, Pin>();
		for (Node node : this) {
			try {
				Node nodeClone = node.clone();
				clone.add(nodeClone);
				if(node instanceof UnitElement) {
					UnitElement unit = (UnitElement) node;
					UnitElement unitClone = (UnitElement) nodeClone;

					/*
					 * clone inputs
					 */

					for (int i = 0; i < unit.getInputsCount(); i++) {
						Input input = unit.getInput(i);
						correspondingPins.put(input, unitClone.getInput(i));

						Connection connection;
						// we visit each connection
						if(input.isConnected()) {
							connection = input.getConnection();
							cloneConnection(clone, tmpConn, correspondingPins, connection);
						}
					}

					/* 
					 * clone outputs
					 */

					for (int o = 0; o < unit.getOutputsCount(); o++) {
						Output output = unit.getOutput(o);
						correspondingPins.put(output, unitClone.getOutput(o));

						// we visit each connection
						if(output.isConnected()) {
							for (Connection connection : output.getConnections()) {
								cloneConnection(clone, tmpConn, correspondingPins, connection);
							}
						}

					}

				}

			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		return clone;
	}

	/**
	 * @param clone
	 * @param tmpConn
	 * @param correspondingPins
	 * @param conn
	 */
	private void cloneConnection(UnitList clone,
			Collection<Connection> tmpConn,
			HashMap<Pin, Pin> correspondingPins, Connection conn) {
		if(tmpConn.contains(conn)) {
			// if this connection is already in the list
			// the corresponding unit on the other end has already been cloned
			// so we can create a new connection to this clone
			Output output = (Output) correspondingPins.get(conn.getOutput());
			Input input = (Input) correspondingPins.get(conn.getInput());

			Connection newConn = new Connection(output, input);
			clone.getConnections().add(newConn);
		} else {
			// we are on the first end of this connection
			tmpConn.add(conn);
		}
	}

	@Override
	public boolean add(Node node, String label) {
		notifyModelListeners();
		return super.add(node, label);
	}

	@Override
	public boolean add(Node o) {
		notifyModelListeners();
		return super.add(o);
	}

	@Override
	public boolean addAll(java.util.Collection<? extends Node> c) {
		notifyModelListeners();
		return super.addAll(c);
	};

	@Override
	public Node remove(int index) {
		notifyModelListeners();
		return super.remove(index);
	}

	/**
	 * Checks all Inputs if they are connected or not. 
	 * @param networkOK
	 * @return
	 */
	public boolean areAllInputsConnected() {
		// check inputs of all units
		for (final Node node : this) {
			if(node instanceof UnitElement) {
				final UnitElement unit = (UnitElement) node;

				// does the unit actually have inputs?
				if(unit.hasInputs()) {

					//check all inputs of this unit
					for (Input input : unit.getInputs()) {
						// is this input connected?
						if (!input.isConnected() 
								// is this input actually required?
								&& input.isRequired()) {
							System.err.println(input + " is not connected");
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Iterates over all units in the list to find the given ID.
	 * @param id
	 * @return
	 */
	public Node getUnit(final int id) {
		for (Node node : this) {
			AbstractUnit unit = (AbstractUnit) node;
			if(unit.getUnitID() == id) 
				return unit;
		}
		return null;
	}


	/**
	 * Remove this Unit from the workflow.
	 * This also removes and replaces the connections between possibly connected unit.
	 * @param unit 
	 * @return 
	 */
	@Override
	public boolean remove(final Node node) {
		if(node instanceof UnitElement) {
			UnitElement unit = (UnitElement) node;
			// new connection between the nodes that this deleted node was inbetween
			replaceConnection(unit);

			// delete old connections
			unbindUnit(unit);
		}

		// remove unit itself
		return removeUnchecked(node);
	}

	/**
	 * Remove this Unit from the workflow.
	 * This doesn't touch connections at all.
	 * @param unit 
	 * @return 
	 */
	public boolean removeUnchecked(final Node node) {
		// remove unit itself
		boolean remove = super.remove(node);
		notifyModelListeners();
		return remove;
	}
	
	

	/**
	 * Removes all connections to this {@link UnitElement}.
	 * @param unit
	 */
	public void unbindUnit(final UnitElement unit) {
		// find connections which are attached to this unit
		Collection<Connection> tmpConn = new Vector<Connection>();
		for (Connection connection : getConnections()) {
			if(connection.isConnectedToUnit(unit)
					&& !connection.isLocked()) {
				// put in list to delete
				tmpConn.add(connection);
			}
		}
		for (Connection connection : tmpConn) {
			// delete connections
			connections.remove(connection);
		}

	}


	private void replaceConnection(final UnitElement unit) {
		// replacing makes only sense, when it has inputs and outputs
		if(!unit.hasInputsConnected() || !unit.hasOutputsConnected()) {
			return;
		}

		// get the outputs of the currently connected inputs
		int numberConnectedInputs = unit.getInputsCount();
		ArrayList<Output> connectedOutputs = new ArrayList<Output>(numberConnectedInputs);
		// we use a vector, add stuff to the end and iterate over this without indicies
		// check if pins are connected and ignore, if not
		for (int i = 0; i < numberConnectedInputs; i++) {
			Input input = unit.getInput(i);
			if(input.isConnected()) {
				connectedOutputs.add(input.getFromOutput());
			}
		}


		// same for inputs
		int numberConnectedOutputs = unit.getOutputsCount();
		ArrayList<Input> connectedInputs = new ArrayList<Input>(numberConnectedOutputs);
		for (int i = 0; i < numberConnectedOutputs; i++) {
			Output output = unit.getOutput(i);
			if(output.isConnected()) 
				for (Connection connection : output.getConnections()) {
					Input toInput = connection.getInput();
					connectedInputs.add(toInput);
				}


		}

		// now we create new connections based on the lists of 
		// formerly connected outputs and inputs.
		// if it doesn't match, discard

		// get the longer list, inputs or outputs
		int numPins = Math.min(connectedInputs.size(), connectedOutputs.size());
		for (int i = 0; i < numPins; i++) {
			getConnections().add(connectedInputs.get(i), connectedOutputs.get(i));
		}
	}


	/**
	 * Returns true, if any {@link UnitElement} in this UnitList is set as a displayUnit
	 * @return
	 */
	public boolean hasUnitAsDisplay() {
		for (Node node : this) {
			if(node instanceof Displayable) {
				if(((Displayable)node).isDisplay()) 
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns true, if any source in this UnitList is set as a displayUnit
	 * @return
	 */
	public boolean hasSourcesAsDisplay() {
		for (int i = 0; i < size(); i++) {
			final UnitElement unit = (UnitElement) get(i);
			if(unit.getUnitType() == Type.SOURCE && unit.isDisplay()) 
				return true;
		}
		return false;
	}


	public void addModelListener(ModelListener listener) {
		if (! this.listeners.contains(listener)) {
			this.listeners.add(listener);
			notifyModelListener(listener);
		}
	}

	public void notifyModelListener(ModelListener listener) {
		listener.modelChanged(this);
	}

	public void notifyModelListeners() {
		for (final ModelListener listener : this.listeners) {
			notifyModelListener(listener);
		}
	}

	public void removeModelListener(ModelListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Get the {@link ConnectionList}
	 * @return
	 */
	public ConnectionList getConnections() {
		return connections;
	}

	/**
	 * Add a {@link Connection}
	 * @param con
	 * @return
	 */
	public boolean addConnection(Connection con) {
		return this.connections.add(con);
	}

	/**
	 * Adds the {@link Connection}s of a {@link ConnectionList} to this ConnectionList.
	 * They are added, the current ConnectionList is not replaced.
	 * @param newConnList
	 */
	public void addConnectionList(ConnectionList newConnList) {
		for (Connection conn : newConnList) {
			this.connections.add(conn);
		}
	}

	/**
	 * Resets all marks to zero.
	 * @param units
	 */
	public void unmarkUnits() {
		for (Node node : this) {
			if(node instanceof UnitElement) {
				UnitElement unit = (UnitElement) node;
				unit.setMark(0);
			}

		}
	}

	@Override
	public void clear() {
		super.clear();
		connections.clear();
		notifyModelListeners();
	}

	/**
	 * Number of units in this List.
	 * @return
	 */
	public int getSize() {
		return super.size();
	}


	public void setConnectionList(ConnectionList connList) {
		this.connections = connList;
	}

}
