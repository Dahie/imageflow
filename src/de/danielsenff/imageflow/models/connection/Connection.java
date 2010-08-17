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

import visualap.Node;
import visualap.Pin;
import de.danielsenff.imageflow.models.Lockable;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * Connection between two {@link Pin}s, {@link Input} and {@link Output}.
 * @author Daniel Senff
 *
 */
public class Connection implements Lockable {
	/**
	 * the id of this connection
	 */
	public int id; 
	
	protected Pin from, to;
	
	/**
	 * Connection-Status
	 *
	 */
	public enum Status {OK, MISSING_TO_UNIT, MISSING_FROM_UNIT, MISSING_BOTH }
	
	protected boolean locked = false;
	
	/**
	 * @param fromUnit
	 * @param fromOutputNumber index starting with 1
	 * @param toUnit
	 * @param toInputNumber index starting with 1
	 */
	public Connection(final UnitElement fromUnit, 
			final int fromOutputNumber, 
			final UnitElement toUnit, 
			final int toInputNumber) {
		this(fromUnit.getOutput(fromOutputNumber-1), toUnit.getInput(toInputNumber-1));
	}
	
	/**
	 * Creates a Connection between the two defined pins.
	 * The order for Input and Output is irrelevant.
	 * @param fromOutput
	 * @param toInput
	 */
	public Connection(final Pin pin1, final Pin pin2) {
		
		if(pin1 instanceof Input && pin2 instanceof Output) {
			this.from = pin2;
			this.to = pin1;
		} else {
			this.from = pin1;
			this.to = pin2;
		}
			
		
		UnitElement fromUnit = (UnitElement) getOutput().getParent();
		UnitElement toUnit = (UnitElement) getInput().getParent();
		
		id = getID(fromUnit.getUnitID(), getOutput().getIndex(), 
				toUnit.getUnitID(), getInput().getIndex());
	}

	/**
	 * connect the inputs and outputs of the units
	 * @param unitElements
	 */
	public void connect() {
		((Output)this.from).addConnection(this);
		((Input)this.to).setConnection(this);
	}
	
	/**
	 * creates a unique id for each connection
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 * @param toUnitNumber
	 * @param toInputNumber
	 * @return
	 */
	public static int getID(final int fromUnitNumber, 
			final int fromOutputNumber, 
			final int toUnitNumber, 
			final int toInputNumber) {
		
		final int id = (fromUnitNumber<<20) 
			| (fromOutputNumber<<16) | (toUnitNumber<<4) | toInputNumber;   
		return id;
	}
	
	/**
	 * Returns the status of the connection, whether all ends are connected.
	 * Good for finding corrupt connections to non-existent units.
	 * @return 
	 */
	public Status checkConnection() {
		if(getFromUnit() != null && getToUnit() != null) {
			return Status.OK;
		} else if (getFromUnit() == null || getToUnit() != null) {
			return Status.MISSING_FROM_UNIT;
		} else if (getFromUnit() != null || getToUnit() == null) {
			return Status.MISSING_TO_UNIT;
		}
		return Status.MISSING_BOTH;
	}
	
	/**
	 * Returns the {@link UnitElement} from which this connection comes.
	 * @return 
	 */
	public Node getFromUnit() {
		return this.from.getParent();
	}

	/**
	 * Returns the {@link UnitElement} to which this connections leads.
	 * @return
	 */
	public Node getToUnit() {
		return this.to.getParent();
	}
	
	/**
	 * Get the {@link Input}-Pin.
	 * @return
	 */
	public Input getInput() {
		return (Input)this.to;
	}
	
	/**
	 * Get the {@link Output}-pin
	 * @return
	 */
	public Output getOutput() {
		return (Output)this.from;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " fromUnit: "+ getFromUnit() +" toUnit:" + getToUnit();
	}
	
	

	/**
	 * Checks a unit, if it's inputs have already been registered in the algorithm.
	 * @param unit
	 * @return
	 */
	public boolean hasInputMarked() {
		boolean hasMarked = true;
		
		UnitElement toUnit = (UnitElement) getToUnit();
		UnitElement fromUnit = (UnitElement) getFromUnit();
		
		if(toUnit.getInputsCount() > 0) {
			// check each input, if it's parent has been registered
			int mark = fromUnit.getOutput(0).getMark();
			// if mark is not set
			if(mark == 0) {
				// this connected ouput hasn't been registered and 
				// is missing a mark, 
				// so the whole unit isn't ready set. 
				hasMarked = false;
			} 
			// else mark is already set, so this output is fine
		}
		
		return hasMarked;
	}


	/**
	 * Checks if this connection is attached to the {@link Pin}
	 * @param pin
	 * @return
	 */
	public boolean isConnected(Pin pin) {
		return (this.from.equals(pin)) || this.to.equals(pin);
	}


	/**
	 * Gets whether this Connection is connected with this {@link UnitElement}.
	 * @param unit
	 * @return
	 */
	public boolean isConnectedToUnit(final Node unit) {
		return (getFromUnit().equals(unit)) || (getToUnit().equals(unit));
	}
	
	/**
	 * Is true, when the ImageBitDepth given by the {@link Output} is 
	 * supported by this Input.
	 * @return
	 */
	public boolean isCompatible() {
		Input input = ((Input)this.to);
		Output output = ((Output)this.from);
		
		return input.isCompatible(output);
	}

	/**
	 * Checks if this Connection is connected to a from- and a to-pin.
	 * Technically, if the connection isn't true, this connection object is kinda zombie
	 * @return
	 */
	public boolean isConnected() {
		Output from = (Output)this.from;
		Input to = (Input)this.to;
		
		return (from.isConnectedWith(to) && to.isConnectedWith(from)); 
	}
	
	/**
	 * Returns true if the connection is locked and cann not be removed.
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * If set true, the connection can not be removed and is write protected.
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
		this.to.setLocked(locked);
		this.from.setLocked(locked);
	}

	public boolean causesLoop() {
		if(((Input)this.to).isConnectedInOutputBranch(getFromUnit())) return true;
		if(((Output)this.from).existsInInputSubgraph(getToUnit())) return true;
		return false;
	}
}
