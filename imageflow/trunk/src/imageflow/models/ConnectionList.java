/**
 * 
 */
package imageflow.models;

import graph.Edges;
import graph.Pin;

import imageflow.ImageFlow;
import imageflow.models.unit.UnitElement;

import java.util.ArrayList;

import javax.swing.JOptionPane;


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
	
	public boolean add(final Pin from, final Pin to) {
		
		// check if both pins are on the same node
		if(from.getParent().equals(to.getParent())) return false;
		
		System.out.println("add connection");

		// check if connection is between input and output, not 2 outputs or two inputs
		if((from instanceof Output && to instanceof Input) ) {
			final Connection connection = new Connection(
					((UnitElement)from.getParent()), from.getIndex(), 
					((UnitElement)to.getParent()), to.getIndex());
			return this.add(connection);
		} else if (  (from instanceof Input && to instanceof Output) ) {
			final Connection connection = new Connection(
					((UnitElement)to.getParent()), to.getIndex(), 
					((UnitElement)from.getParent()), from.getIndex());
			return this.add(connection);
		}
		
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
		
		// check if connection produces loop
		if(input.isConnectedInInputBranch(output.getParent())) {
			System.out.println("Connection disallowed: Loop detected");
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"The connection you tried to establish is not allowed " + '\n' +
					"and will be dismissed because it would cause loops.",
					"Connection refused", 
					JOptionPane.WARNING_MESSAGE);
			return false;
			}
		
		// check the bit depth
		if(!connection.areImageBitDepthCompatible()) {
			System.out.println("Connection disallowed: Incombatible bit depth");
//			return false;
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
		
		connection.connect();
		
		boolean add = super.add(connection);
		notifyModelListeners();
		return add;
	}
	
	
	public boolean addUnchecked(final Connection connection) {
		connection.connect();
		boolean add = super.add(connection);
		notifyModelListeners();
		return add;
	}
	
	

	@Override
	public Connection remove(final int index) {
		final Connection connection = (Connection) super.remove(index);
		((Input)connection.to).disconnect();
		((Output)connection.from).disconnect();
		notifyModelListeners();
		return connection;
	}
	
	@Override
	public boolean remove(final Object o) {
		final Connection connection = (Connection) o;
		((Input)connection.to).disconnect();
		((Output)connection.from).disconnect();
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
