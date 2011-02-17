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

import java.awt.Point;

import visualap.Node;
import de.danielsenff.imageflow.models.Lockable;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.datatype.ImageDataType;
import de.danielsenff.imageflow.models.unit.AbstractUnit;
import de.danielsenff.imageflow.models.unit.UnitElement;


/**
 * Input Pins
 * @author danielsenff
 *
 */
public class Input extends Pin implements Lockable {
	
	
	/**
	 * the id of the image connected to this input
	 */
	protected String inputID;
	/**
	 * the title of the image connected this input
	 */
	protected String inputTitle;  
	
	/**
	 * flag indicating if the image at this input needs to be duplicated
	 */
	protected boolean needToCopyInput;
	/**
	 * flag to set inputs not required. Default is required.
	 */
	protected boolean requiredInput = true;
	
	/**
	 * write protection
	 */
	protected boolean locked = false;

	/**
	 * Connection
	 */
	protected Connection connection;
	
	/**
	 * @param displayName 
	 * @param shortDisplayName 
	 * @param dataType 
	 * @param parentNode 
	 * @param fromUnit
	 * @param inputNumber
	 * @param required 
	 * @param needToCopyInput 
	 */
	public Input(
			final String displayName,
			final String shortDisplayName,
			final DataType dataType, 
			final UnitElement parentNode, 
			int inputNumber,
			boolean required,
			boolean needToCopyInput) {
		this(dataType, parentNode, inputNumber, required);
		this.setupInput(displayName, shortDisplayName, needToCopyInput);
	}
	
	
	/**
	 * @param dataType 
	 * @param parentNode 
	 * @param inputNumber 
	 */
	public Input(final DataType dataType, 
			final UnitElement parentNode, 
			int inputNumber) {
		this(dataType, parentNode, inputNumber, true);
	}
	
	/**
	 * @param dataType 
	 * @param nodeParent 
	 * @param inputNumber
	 * @param requiredInput
	 */
	public Input(DataType dataType, 
			final UnitElement nodeParent, 
			int inputNumber, 
			boolean requiredInput) {
		super(dataType, inputNumber, nodeParent);
		setRequiredInput(requiredInput);
		if(getDataType() instanceof ImageDataType) {
			((ImageDataType)getDataType()).setParentUnitElement((UnitElement) getParent());
			((ImageDataType)getDataType()).setParentPin(this);
		}
	}

	
	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 */
	private void generateID(final int fromUnitNumber, final int fromOutputNumber) {
		this.inputTitle = "Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
		this.inputID = "ID_Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
	}

	/**
	 * Set a new {@link Connection} for this Input. 
	 * The input may not be locked to be successful.
	 * @param connection
	 */
	public void setConnection(final Connection connection) {
		if(!isLocked()) {
			this.connection = connection;
			generateID(connection.getOutput().getParent().getNodeID(), 
					connection.getOutput().getIndex());	
		}
		
	}

	/**
	 * Setup the basic data.
	 * @param displayName Name of the Pin
	 * @param shortDisplayName Abbreviation of the Pin
	 * @param inputImageBitDepth Flag which defines, what image-formats can be taken.
	 * @param needToCopyInput
	 */
	public void setupInput(final String displayName, 
			final String shortDisplayName, 
			final int inputImageBitDepth, 
			final boolean needToCopyInput) {
		this.displayName = displayName;
		this.shortDisplayName = shortDisplayName;
		this.dataType = DataTypeFactory.createImage(inputImageBitDepth);
		this.setNeedToCopyInput(needToCopyInput);
	}
	
	/**
	 * @param displayName
	 * @param shortDisplayName
	 * @param needToCopyInput
	 */
	public void setupInput(final String displayName, 
			final String shortDisplayName, 
			final boolean needToCopyInput) {
		this.displayName = displayName;
		this.shortDisplayName = shortDisplayName;
		this.setNeedToCopyInput(needToCopyInput);
	}

	/**
	 * Get the ImageTitle. 
	 * This is the title of the image later used in the Macro
	 * @return
	 */
	public String getImageTitle() {
		return inputTitle;
	}

	/**
	 * @param needToCopyInput
	 */
	public void setNeedToCopyInput(final boolean needToCopyInput) {
		this.needToCopyInput = needToCopyInput;
	}

	/**
	 * Returns true, if this Input requires it's input to be copied.
	 * @return
	 */
	public boolean isNeedToCopyInput() {
		return needToCopyInput;
	}
	


	/**
	 * @return the imageID
	 */
	public String getImageID() {
		return this.inputID;
	}
	
	/**
	 * Returns whether or not this Input is required for running the attached {@link UnitElement}
	 * or if it is optional.
	 * @return the requiredInput
	 */
	public boolean isRequired() {
		return this.requiredInput;
	}

	/**
	 * Set if this Input is required for this {@link UnitElement}.
	 * @param requiredInput the requiredInput to set
	 */
	public void setRequiredInput(final boolean requiredInput) {
		this.requiredInput = requiredInput;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString()+" DataType: " + getDataType();
	}

	
	
	/* (non-Javadoc)
	 * @see graph.Pin#getLocation()
	 */
	@Override
	public Point getOrigin() {
		int height = parent.getDimension().height;
		int nump = ((UnitElement) parent).getInputsCount();
		int y =  (index*height / nump ) - (height/(2*nump)) + parent.getOrigin().y;
		return new Point(parent.getOrigin().x, y);
	}
	
	/* (non-Javadoc)
	 * @see graph.Pin#getParent()
	 */
	@Override
	public UnitElement getParent() {
		return (UnitElement)super.getParent();
	}


	/**
	 * Return the unit from which this Input is connected.
	 * @return the fromUnit
	 */
	public UnitElement getFromUnit() {
		return (UnitElement) this.connection.getFromUnit();
	}

	/**
	 * @return
	 */
	public int getFromUnitNumber() {
		return ((AbstractUnit) this.connection.getFromUnit()).getNodeID();
	}

	/**
	 * Returns the current {@link Connection} or null, if this pin is not connected.
	 * @return
	 */
	public Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * Returns whether or not this Input is connected.
	 * @return 
	 */
	public boolean isConnected() {
		return connection != null;
	}
	
	
	/**
	 * Checks if this Input is connected with the given {@link Output}
	 * @param output 
	 * @return
	 */
	public boolean isConnectedWith(Pin output) {
		if(output instanceof Output && isConnected()) {
			return this.connection.getOutput().equals(output);
		}
		return false;
	}


	/**
	 * Resets the this Input, so that it is unconnected.
	 */
	public void disconnectAll() {
		if(!isLocked()) {
			generateID(0, 0); // reset connection
			this.connection = null;	
		}
	}


	/**
	 * The {@link Output} which is connected with this Input.
	 * @return
	 */
	public Output getFromOutput() {
//		return from;
		return connection.getOutput();
	}

	/**
	 * Returns true if the graph branch connected to this inputs parent's output contains the unit.
	 * @param goal
	 * @return
	 */
	public boolean isConnectedInOutputBranch(Node goal) {
		// self reference
		if(goal.equals(parent)) 
//			 can only check inputs, which are connected
//			return traverseInput(this, goal);
			return false;
		else
			return traverseOutput((UnitElement) this.parent, goal);
	}
	
	/**
	 * Returns true if the graph branch connected to this input contains the unit.
	 * @param goal
	 * @return
	 */
	public boolean isConnectedInInputBranch(Node goal) {
		// self reference
		if(goal.equals(parent)) 
//			 can only check inputs, which are connected
//			return traverseInput(this, goal);
			return false;
		else
			return traverseInput(this, goal);
//			return traverseOutput2(getConnection().getFromUnit(), goal);
	}

	
	/**
	 * 
	 * @param start
	 * @param goal
	 * @return
	 */
	public static boolean traverseInput(final Input start, final Node goal) {
		if(start.getParent().equals(goal)) {
			return true;
		} else if(start.isConnected()) {

			for (Input input : start.getFromUnit().getInputs()) {
				return traverseInput(input, goal);
			}	
			
		}
		return false;
	}
	

	private static boolean traverseOutput(UnitElement parent, Node goal) {
		if(parent.equals(goal)) {
			return true;
		} else if (parent.hasInputsConnected()) {
			for (Output output : parent.getOutputs()) {
				if(output.isConnected()) {
					for (Connection connection : output.getConnections()) {
						if(traverseOutput((UnitElement)connection.getToUnit(), goal))
							return true;
					}
				}
			}
		} 

		return false;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean isLocked) {
		this.locked = isLocked;
	}
	
}
