package de.danielsenff.imageflow.models;

import visualap.Node;
import visualap.Pin;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * Connection between two {@link Pin}s, {@link Input} and {@link Output}.
 * @author danielsenff
 *
 */
public class Connection {
	public int id;		// the id of this connection
	
	protected Pin from, to;
	
	/**
	 * Connection-Status
	 *
	 */
	public enum Status {OK, MISSING_TO_UNIT, MISSING_FROM_UNIT, MISSING_BOTH }
	
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
	 * @param fromOutput
	 * @param toInput
	 */
	public Connection(final Pin fromOutput, final Pin toInput) {
		this.from = fromOutput;
		this.to = toInput;
		UnitElement fromUnit = (UnitElement) fromOutput.getParent();
		UnitElement toUnit = (UnitElement) toInput.getParent();
		
		id = getID(fromUnit.getUnitID(), fromOutput.getIndex(), 
				toUnit.getUnitID(), toInput.getIndex());
	}


	/**
	 * connect the inputs and outputs of the units
	 * @param unitElements
	 */
	public void connect() {
		((Output)this.from).connectTo(to);
		((Input)this.to).connectTo(from);
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
	 * Good for finding corrupt connections to non-existant units.
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
	public boolean isConnectedToUnit(final UnitElement unit) {
		return (getFromUnit().equals(unit)) || (getToUnit().equals(unit));
	}
	
	/**
	 * Is true, when the ImageBitDepth given by the {@link Output} is 
	 * supported by this Input.
	 * @return
	 */
	public boolean areImageBitDepthCompatible() {
		Input input = ((Input)this.to);
		Output output = ((Output)this.from);
		
//		boolean areCompatible = (input.getImageBitDepth()&output.getImageBitDepth()) != 0;
//		boolean areCompatible = input.isImageBitDepthCompatible(output.getImageBitDepth());
		boolean areCompatible = output.isImageBitDepthCompatible(input.getImageBitDepth());
		return areCompatible;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isConnected() {
		Output from = (Output)this.from;
		Input to = (Input)this.to;
		
		System.out.println(from +" and "+ to);
		
		return (from.isConnectedWith(to) && to.isConnectedWith(from)); 
//		if(((Input)this.to).isConnected() 
//				&& ((Output)this.from).isConnected()) return true;
		// technically, if the connection isn true, this connection object is kinda zombie
	}
	
	public boolean causesLoop() {
		if(((Input)this.to).isConnectedInInputBranch(getFromUnit())) return true;
		if(((Output)this.from).existsInInputSubgraph(getToUnit())) return true;
		return false;
	}
}
