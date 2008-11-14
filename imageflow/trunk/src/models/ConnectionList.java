/**
 * 
 */
package models;

import graph.Edge;
import graph.Edges;
import graph.Pin;

import java.util.ArrayList;

import models.unit.UnitElement;
import backend.Model;
import backend.ModelListener;

/**
 * List that holds all {@link Connection}s.
 * @author danielsenff
 *
 */
public class ConnectionList extends Edges implements Model {
	
	private ArrayList<ModelListener> listeners;
	
	public ConnectionList() {
		this.listeners = new ArrayList<ModelListener>();
	}
	
	/* (non-Javadoc)
	 * @see graph.Edges#add(graph.Pin, graph.Pin)
	 */
	@Override
	public boolean add(final Pin from, final Pin to) {
		
		System.out.println("add connection");
		// check if connection is between input and output, not 2 outputs or two inputs
		if((from instanceof Output && to instanceof Input) ) {
			// connect from Input to Output
			
			final Connection connection = new Connection(((UnitElement)from.getParent()), from.getIndex(), 
					((UnitElement)to.getParent()), to.getIndex());
			System.out.println("new connection: from Unit: " + from.getParent() 
					+ " to Unit: " + to.getParent() 
					+ " at Input" + to.getIndex());
			return add(connection);
		
		} else if (  (from instanceof Input && to instanceof Output) ) {
			// connect from Output to Input
			
			final Connection connection = new Connection(((UnitElement)to.getParent()), to.getIndex(), 
					((UnitElement)from.getParent()), from.getIndex());
			System.out.println("new connection: from Unit: " + from.getParent() 
					+ " to Unit: " + to.getParent() 
					+ " at Input" + to.getIndex());
			return add(connection);
		}
		
		System.out.println("disallowed connection");
		return false;
		
	}
	
	
	
	
	/**
	 * adds the connection, if the pins are already connected, the old connection is deleted
	 * @param connection
	 * @return
	 */
	public boolean add(final Connection connection) {
		final Input input = connection.getToUnit().getInput(connection.toInputNumber-1);
		final Output output = connection.getFromUnit().getOutput(connection.fromOutputNumber-1);
		
		//check if input already got a connection, if yes, delete that one
		for (int i = 0; i < this.size(); i++) {
			final Connection conn = (Connection) this.get(i);
			
			// edges is connected with this input?
			if(conn.isConnected(input)) {
				remove(conn);
				System.out.println("old connection removed");
			}
		}
		
		input.connectTo(connection.fromUnit, connection.fromOutputNumber);
		output.connectTo(connection.toUnit, connection.fromOutputNumber);
		
		return super.add(connection);
	}

	@Override
	public Connection remove(int index) {
		Connection connection = (Connection) this.get(index);
		((Input)connection.from).disconnect();
		notifyModelListeners();
		return (Connection) super.remove(index);
	}
	
	@Override
	public boolean remove(Object o) {
		Connection connection = (Connection) o;
		((Input)connection.to).disconnect();
		notifyModelListeners();
		return super.remove(o);
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

	
	
}
