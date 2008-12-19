/**
 * 
 */
package models;

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
	
	private final ArrayList<ModelListener> listeners;
	
	/**
	 * 
	 */
	public ConnectionList() {
		this.listeners = new ArrayList<ModelListener>();
	}
	
	
	/* (non-Javadoc)
	 * @see graph.Edges#add(graph.Pin, graph.Pin)
	 */
	@Override
	public boolean add(final Pin from, final Pin to) {
		
		// check if both pins are on the same node
		if(from.getParent().equals(to.getParent())) return false;
		
		System.out.println("add connection");
			
			
		// check if connection is between input and output, not 2 outputs or two inputs
		if((from instanceof Output && to instanceof Input) ) {
			// connect from Input to Output
			
			
			final Connection connection = new Connection(((UnitElement)from.getParent()), from.getIndex(), 
					((UnitElement)to.getParent()), to.getIndex());

			
			
			System.out.println("new connection: from Unit: " + from.getParent() 
					+ " to Unit: " + to.getParent() 
					+ " at Input" + to.getIndex());
			return this.add(connection);
		
		} else if (  (from instanceof Input && to instanceof Output) ) {
			// connect from Output to Input
			
			
			// check for loops
//			final boolean isLoop = ((Input)from).knows(to.getParent());
//			if(isLoop) return false;
			
			final Connection connection = new Connection(((UnitElement)to.getParent()), to.getIndex(), 
					((UnitElement)from.getParent()), from.getIndex());
			
			
			
			System.out.println("new connection: from Unit: " + from.getParent() 
					+ " to Unit: " + to.getParent() 
					+ " at Input" + to.getIndex());
			return this.add(connection);
		}
		
		System.out.println("disallowed connection");
		return false;
		
	}
	
	/**
	 * Creates a new Connection and adds it to the ConnectionList
	 * @param from
	 * @param fromOutput
	 * @param to
	 * @param toInput
	 * @return
	 */
	public boolean add(final UnitElement from, 
			final int fromOutput, 
			final UnitElement to, 
			final int toInput) {
		return this.add(new Connection(from, fromOutput, to, toInput));
	}
	
	
	/**
	 * adds the connection, if the pins are already connected, the old connection is deleted
	 * @param connection
	 * @return
	 */
	public boolean add(final Connection connection) {
		final Input input = connection.getToUnit().getInput(connection.to.getIndex()-1);
		final Output output = connection.getFromUnit().getOutput(connection.from.getIndex()-1);
		
		// check the bit depth
		if(!connection.areImageBitDepthCompatible()) {
			System.out.println("Connection disallowed: Incombatible bit depth");
			return false;
			}
		
		//TODO check if connection produces loop
//		if(output.knows(input.getParent())) return false;
//		if(output.knows(input.getParent())) {
		if(input.knows(output.getParent())) {
			System.out.println("Connection disallowed: Loop detected");
			return false;
			}
		
		//check if input already got a connection, if yes, delete that one
		for (int i = 0; i < this.size(); i++) {
			final Connection conn = (Connection) this.get(i);
			
			// edges is connected with this input?
			if(conn.isConnected(input)) {
				remove(conn);
				System.out.println("old connection removed");
			}
		}
		
//		input.connectTo(connection.getFromUnit(), connection.from.getIndex());
//		output.connectTo(connection.getToUnit(), connection.to.getIndex());
		connection.connect();
		
		return super.add(connection);
	}

	@Override
	public Connection remove(final int index) {
		final Connection connection = (Connection) this.get(index);
		((Input)connection.to).disconnect();
		((Output)connection.from).disconnect();
		notifyModelListeners();
		return (Connection) super.remove(index);
	}
	
	@Override
	public boolean remove(final Object o) {
		final Connection connection = (Connection) o;
		((Input)connection.to).disconnect();
		notifyModelListeners();
		return super.remove(o);
	}
	

	public void addModelListener(final ModelListener listener) {
		if (! this.listeners.contains(listener)) {
			this.listeners.add(listener);
			notifyModelListener(listener);
		}
	}

	public void notifyModelListener(final ModelListener listener) {
		listener.modelChanged(this);
	}

	public void notifyModelListeners() {
		for (final ModelListener listener : this.listeners) {
			notifyModelListener(listener);
		}
	}

	public void removeModelListener(final ModelListener listener) {
		this.listeners.remove(listener);
	}

	
	
}
