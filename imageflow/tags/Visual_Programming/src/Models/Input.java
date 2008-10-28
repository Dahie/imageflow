package Models;

import java.awt.Point;

import graph.Node;
import graph.Pinnable;
import Models.unit.UnitElement;

public class Input implements Pinnable {
	protected int unitNumber;		// the number of this unit
	protected Node unit;
	protected int inputNumber;	// the number of this input 

	protected String displayName; 	// the name to be displayed in the context help
	protected String shortDisplayName = "I"; // the short name to be displayed on the unit's icon
	
	protected String imageID; 		// the id of the image connected to this input
	protected String imageTitle;  	// the title of the image connected this output
	
	protected int inputImageBitDepth; // the int value indicates the acceptable image types 
	
	protected boolean needToCopyInput; 		// flag indicating if the image at this input needs to be duplicated
	protected boolean requiredInput = true;
	
	

	// connected to this input:
	protected int fromUnitNumber;
	protected int fromOutputNumber;
	
	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 */
	public void setConnection(final int fromUnitNumber, final int fromOutputNumber) {
		this.fromUnitNumber = fromUnitNumber;
		this.fromOutputNumber = fromOutputNumber;
		this.imageTitle = "Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
		this.imageID = "ID_Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
	}
	
	/**
	 * Returns whether or not this Input is connected.
	 */
	public boolean isConnected() {
		boolean isConnected = (fromUnitNumber > 0) || (fromOutputNumber > 0);
		return isConnected;
	}
	

	public void setupInput(String displayName, String shortDisplayName, int inputImageBitDepth, boolean needToCopyInput) {
		this.displayName = displayName;
		this.shortDisplayName = shortDisplayName;
		this.inputImageBitDepth = inputImageBitDepth;
		this.setNeedToCopyInput(needToCopyInput);
	}

	public Input(int unitNumber, int inputNumber) {
		this.unitNumber = unitNumber;
		this.inputNumber = inputNumber;
	}

	public String getImageTitle() {
		return imageTitle;
	}

	public void setNeedToCopyInput(boolean needToCopyInput) {
		this.needToCopyInput = needToCopyInput;
	}

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
	public void setRequiredInput(boolean requiredInput) {
		this.requiredInput = requiredInput;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString()+" fromUnit: " +this.fromUnitNumber + " fromOutput: "+this.fromOutputNumber;
	}

	/* (non-Javadoc)
	 * @see graph.Pinnable#getIndex()
	 */
	public int getIndex() {
		return this.inputNumber;
	}

	/* (non-Javadoc)
	 * @see graph.Pinnable#getLocation()
	 */
	public Point getLocation() {
		return null;
	}

	/* (non-Javadoc)
	 * @see graph.Pinnable#getMark()
	 */
	public int getMark() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see graph.Pinnable#getName()
	 */
	public String getName() {
		return this.displayName;
	}

	/* (non-Javadoc)
	 * @see graph.Pinnable#getParent()
	 */
	public Node getParent() {
		return this.unit;
	}

	/* (non-Javadoc)
	 * @see graph.Pinnable#setMark(int)
	 */
	public void setMark(int mark) {
		// TODO Auto-generated method stub
		
	}
	
}
