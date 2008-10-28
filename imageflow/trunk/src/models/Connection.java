package models;

import graph.Edge;
import graph.Pin;
import models.unit.UnitElement;
import models.unit.UnitList;




/**
 * Connection between two {@link Pin}s, {@link Input} and {@link Output}.
 * @author danielsenff
 *
 */
public class Connection extends Edge {
	public int id;		// the id of this connection
	
							// a connection between
	int fromOutputNumber;	// the output with this number
	int fromUnitNumber;		// of the unit with this number
	protected UnitElement fromUnit;
	
	// is connected to
	int toInputNumber;		// the input of this number 
	int toUnitNumber;		// of the unit with this number
	protected UnitElement toUnit;
	
	/**
	 * Connection-Status
	 * @author danielsenff
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
		
		id = getID(fromUnitNumber, fromOutputNumber, toUnitNumber, toInputNumber);
		
	}
	
	

	/**
	 * connect the inputs and outputs of the units
	 * @param unitElements
	 */
	public void connect(final UnitList unitElements) {
		
//		UnitElement toUnit = (UnitElement) unitElements.get(toUnitNumber);
//		UnitElement fromUnit = (UnitElement) unitElements.get(fromUnitNumber);
		
		
		super.to = toUnit.getInput(toInputNumber-1);
		((Input) super.to).setConnection(fromUnitNumber, fromOutputNumber);	
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
	
}
