package models;

import graph.Pin;

import java.awt.Point;

import models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class Input extends Pin {
	/**
	 * the number of this unit
	 */
	protected int unitNumber;
//	protected Node unit;
//	protected int i;	// the number of this input 

	/**
	 * the name to be displayed in the context help
	 */
	protected String displayName;
	/**
	 * the short name to be displayed on the unit's icon
	 */
	protected String shortDisplayName = "I";
	
	/**
	 * the id of the image connected to this input
	 */
	protected String imageID;
	/**
	 * the title of the image connected this output
	 */
	protected String imageTitle;  
	
	/**
	 * the int value indicates the acceptable image types
	 */
	protected int inputImageBitDepth; 
	
	/**
	 * flag indicating if the image at this input needs to be duplicated
	 */
	protected boolean needToCopyInput;
	/**
	 * Placeholder for the flag to set inputs not required. Default is required.
	 */
	protected boolean requiredInput = true;
	
	

	// connected to this input:
	/**
	 * Connected from this {@link UnitElement}-number.
	 */
	protected int fromUnitNumber;
	/**
	 * Connected from this {@link Output}
	 */
	protected int fromOutputNumber;
	/**
	 * Connected from this {@link UnitElement}
	 */
	protected UnitElement fromUnit;
	
	
	/**
	 * @param fromUnit
	 * @param inputNumber
	 * @param nodeParent
	 */
	public Input(final UnitElement nodeParent, int inputNumber) {
		super("input", inputNumber, nodeParent.getInputsMaxCount(), nodeParent);
	}
	
	
	/**
	 * @param fromUnit
	 * @param inputNumber
	 * @param nodeParent
	 */
	public Input(final UnitElement fromUnit, final int inputNumber, final UnitElement nodeParent) {
		super("input", inputNumber, nodeParent.getInputsMaxCount(), nodeParent);
		setConnection(fromUnit.getUnitID(), inputNumber);
	}
	
	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 */
	private void setConnection(final int fromUnitNumber, final int fromOutputNumber) {
		this.fromUnitNumber = fromUnitNumber;
		this.fromOutputNumber = fromOutputNumber;
		this.imageTitle = "Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
		this.imageID = "ID_Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
	}
	
	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnit
	 * @param fromOutputNumber
	 */
	public void setConnection(final UnitElement fromUnit, final int fromOutputNumber) {
		this.fromUnit = fromUnit;
		setConnection(fromUnit.getUnitID(), fromOutputNumber);
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
		this.inputImageBitDepth = inputImageBitDepth;
		this.setNeedToCopyInput(needToCopyInput);
	}

	
	/**
	 * Returns whether or not this Input is connected.
	 * @return 
	 */
	public boolean isConnected() {
		final boolean isConnected = fromUnit != null || 
			(fromUnitNumber > 0)
			|| (fromOutputNumber > 0);
		return isConnected;
	}
	

	/**
	 * Get the ImageTitle. 
	 * This is the title of the image later used in the Macro
	 * @return
	 */
	public String getImageTitle() {
		return imageTitle;
	}

	/**
	 * @param needToCopyInput
	 */
	public void setNeedToCopyInput(final boolean needToCopyInput) {
		this.needToCopyInput = needToCopyInput;
	}

	/**
	 * @return
	 */
	public boolean isNeedToCopyInput() {
		return needToCopyInput;
	}

	/**
	 * @return the imageID
	 */
	public String getImageID() {
		return this.imageID;
	}
	
	/**
	 * Returns whether or not this Input is required for running the attached {@link UnitElement}
	 * or if it is optional.
	 * @return the requiredInput
	 */
	public boolean isRequiredInput() {
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
		return super.toString()+" fromUnit: " + this.fromUnitNumber + " fromOutput: "+this.fromOutputNumber;
	}

	
	/* (non-Javadoc)
	 * @see graph.Pin#getLocation()
	 */
	@Override
	public Point getLocation() {
		int height = parent.getDimension().height;
		int y =  (i*height / super.nump ) - (height/(2*super.nump)) + parent.getOrigin().y;
		Point point = new Point(parent.getOrigin().x, y);
		return point;
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
		return this.fromUnit;
	}

	/**
	 * Checks if this Input is connected with the given {@link Output}
	 * @param output 
	 * @param source1Output
	 * @return
	 */
	public boolean isConnectedWith(Output output) {
		if(this.fromUnit != null) {
			return this.fromUnit.getOutput(this.fromOutputNumber-1).equals(output);
		}
		return false;
	}
}
