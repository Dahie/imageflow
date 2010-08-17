/**
 * Copyright (C) 2008-2010 Daniel Senff
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
package de.danielsenff.imageflow.models.connection;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

import visualap.Pin;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.Model;
import de.danielsenff.imageflow.models.ModelListener;
import de.danielsenff.imageflow.models.unit.UnitElement;


/**
 * List that holds all {@link Connection}s.
 * @author Daniel Senff
 *
 */
public class ConnectionList extends Vector<Connection> implements Model, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ArrayList<ModelListener> listeners;
	
	/**
	 * Constructs a ConnectionList 
	 */
	public ConnectionList() {
		this.listeners = new ArrayList<ModelListener>();
	}

	

	/**
	 * Sees, if a connection between the given Pins exists.
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean containsConnection(final Pin from, final Pin to) {
		for (final Connection connection : this) {
			if (connection.getInput().equals(from) 
					&& connection.getOutput().equals(to))
				return true;
			else if (connection.getInput().equals(to) 
					&& connection.getOutput().equals(from))
				return true;
		}
		return false;
	}
	
	
	/*
	 * add
	 */
	
	
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
	 * Creates a new Connection between two pins and adds it to the list. 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean add(final Pin from, final Pin to) {
		
		// check if both pins are on the same node
		if(from.getParent().equals(to.getParent())) return false;

		// check if connection is between input and output, not 2 outputs or two inputs
		if((from instanceof Input && to instanceof Input)
			 || (from instanceof Output && to instanceof Output)) {
			return false;
		}
		
		return this.add(new Connection(from, to));
		
	}
	
	
	/**
	 * adds the connection, if the pins are already connected, the old connection is deleted
	 * if inputs are locked, the connection will not be added.
	 * @param connection
	 * @return
	 */
	@Override
	public boolean add(final Connection connection) {
		final Input input = connection.getInput();
		final Output output = connection.getOutput();
		
		if(input.isLocked() || output.isLocked()) {
			return false;
		}
		
		// check if connection produces loop
		if(input.isConnectedInOutputBranch(output.getParent())) {
			System.out.println("Connection disallowed: Loop detected");
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"The connection you tried to establish is not allowed " + '\n' +
					"and will be dismissed because it would cause loops.",
					"Connection refused", 
					JOptionPane.WARNING_MESSAGE);
			return false;
			}
		
		// check the bit depth
		if(!connection.isCompatible()) {
			final String dataTypeI = connection.getInput().getDataType().toString();
			final String dataTypeO = connection.getOutput().getDataType().toString();
			System.out.println("Connection disallowed: " +
					"Incompatible dataTypes " + dataTypeO + " to " + dataTypeI);
			//			return false;
		}
		

		
		//check if input already got a connection, if yes, delete that one
		for (int i = 0; i < this.size(); i++) {
			final Connection conn = (Connection) this.get(i);
			// edges is connected with this input?
			if(conn.isConnected(input)) {
				remove(conn);
			}
		}
		
		if(!output.isConnectedWith(input))
			connection.connect();
		
		final boolean add = super.add(connection);
		notifyModelListeners();
		return add;
	}
	
	/**
	 * Adds the connection to the list without any security checking.
	 * @param connection
	 * @return
	 */
	public boolean addUnchecked(final Connection connection) {
		connection.connect();
		final boolean add = super.add(connection);
		notifyModelListeners();
		return add;
	}
	
	

	/*
	 * Remove
	 */
	
	@Override
	public Connection remove(final int index) {
		final Connection connection = (Connection) super.remove(index);
		((Input)connection.to).disconnectAll();
		((Output)connection.from).disconnectAll();
		notifyModelListeners();
		return connection;
	}
	
	@Override
	public boolean remove(final Object o) {
		final Connection connection = (Connection) o;
		((Input)connection.to).disconnectAll();
		((Output)connection.from).disconnectFrom(connection.to);
		notifyModelListeners();
		return super.remove(o);
	}
	


	/*
	 * ModelListener
	 */
	
	
	/*
	 * (non-Javadoc)
	 * @see de.danielsenff.imageflow.models.Model#addModelListener(de.danielsenff.imageflow.models.ModelListener)
	 */
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
	
	@Override
	public ConnectionList clone() {
		final ConnectionList cloneList = new ConnectionList();
		for (final Connection connection : this) {
			final Connection clonConn = new Connection(connection.getOutput(), connection.getInput());
			cloneList.add(clonConn);
		}
		return cloneList;
	}
	
	/**
	 * Finds a connection based on the given id
	 * @param id
	 * @return
	 */
	public Connection getConnectionByID(final int id) {
		for (final Connection connection : this) {
			if(connection.id == id)
				return connection;
		}
		return null;
	}
	
}
