package models;

import graph.Edge;
import graph.Pin;
import models.unit.UnitElement;

/**
 * Connection between two {@link Pin}s, {@link Input} and {@link Output}.
 * @author danielsenff
 *
 */
public class Connection extends Edge {
	public int id;		// the id of this connection
	
	/** 
	 * From which {@link Output}
	 */
	protected int fromOutputNumber;	
	/**
	 * From which {@link UnitElement}-number
	 */
	protected int fromUnitNumber;
	/**
	 * From which {@link UnitElement} connected.
	 */
	protected UnitElement fromUnit;
	
	/**
	 * Connected to this Input.
	 */
	protected int toInputNumber;
	/**
	 * Connected to this {@link UnitElement}-number.
	 */
	protected int toUnitNumber;	
	/**
	 * Connected to this {@link UnitElement}.
	 */
	protected UnitElement toUnit;
	
	/**
	 * Connection-Status
	 *
	 */
	public enum Status {OK, MISSING_TO_UNIT, MISSING_FROM_UNIT, MISSING_BOTH }
	
	/**
	 * @param fromUnit
	 * @param fromOutputNumber
	 * @param toUnit
	 * @param toInputNumber
	 */
	public Connection(final UnitElement fromUnit, 
			final int fromOutputNumber, 
			final UnitElement toUnit, 
			final int toInputNumber) {
		super(fromUnit.getOutput(fromOutputNumber-1), toUnit.getInput(toInputNumber-1));
		this.fromUnit = fromUnit;
		this.fromUnitNumber = fromUnit.getUnitID();
		this.fromOutputNumber = fromOutputNumber;
		this.toUnit = toUnit;
		this.toUnitNumber = toUnit.getUnitID();
		this.toInputNumber = toInputNumber;
		this.toUnit.getInput(toInputNumber-1).setConnection(fromUnit, fromOutputNumber);
		
		id = getID(fromUnitNumber, fromOutputNumber, toUnitNumber, toInputNumber);
//		connect();
	}


	/**
	 * connect the inputs and outputs of the units
	 * @param unitElements
	 */
	public void connect() {
		
//		UnitElement toUnit = (UnitElement) unitElements.get(toUnitNumber);
//		UnitElement fromUnit = (UnitElement) unitElements.get(fromUnitNumber);
		
		
		super.to = toUnit.getInput(toInputNumber-1);
		((Input) super.to).setConnection(toUnit, fromOutputNumber);	
	}
	
	/**
	 * creates a unique id for each connection
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 * @param toUnitNumber
	 * @param toInputNumber
	 * @return
	 */
	public static int getID(final int fromUnitNumber, final int fromOutputNumber, final int toUnitNumber, final int toInputNumber) {
		
		final int id = (fromUnitNumber<<20) | (fromOutputNumber<<16) | (toUnitNumber<<4) | toInputNumber;   
		return id;
	}
	
	/**
	 * Returns the status of the connection, whether all ends are connected
	 * @return 
	 */
	public Status checkConnection() {
		if(this.fromUnit != null && this.toUnit != null) {
			return Status.OK;
		} else if (this.fromUnit == null || this.toUnit != null) {
			return Status.MISSING_FROM_UNIT;
		} else if (this.fromUnit != null || this.toUnit == null) {
			return Status.MISSING_TO_UNIT;
		}
		return Status.MISSING_BOTH;
	}
	
	/**
	 * Returns the {@link UnitElement} from which this connection comes.
	 * @return 
	 */
	public UnitElement getFromUnit() {
		return this.fromUnit;
	}

	/**
	 * Returns the {@link UnitElement} to which this connections leads.
	 * @return
	 */
	public UnitElement getToUnit() {
		return this.toUnit;
	}
	
	/**
	 * Get the {@link Input}-Pin.
	 * @return
	 */
	public Input getInput() {
		return (Input)super.to;
	}
	
	/**
	 * Get the {@link Output}-pin
	 * @return
	 */
	public Output getOutput() {
		return (Output)super.from;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final String string = super.toString() + " fromUnit: "+ this.fromUnit +" toUnit:" + toUnit;
		return string;
	}
	
	

	/**
	 * Checks a unit, if it's inputs have already been registered in the algorithm.
	 * @param unit
	 * @return
	 */
	public boolean hasInputMarked() {
		boolean hasMarked = true;
		
		if(toUnit.getInputsActualCount() > 0) {
			// check each input, if it's parent has been registered
			int mark = fromUnit.getOutput(0).getMark();
			// if mark is not set
			if(mark == 0) {
				// this connected ouput hasn't been registered and is missing a mark, 
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
}
