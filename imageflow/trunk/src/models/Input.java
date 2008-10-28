package models;

import graph.Node;
import graph.Pin;
import helper.PaintUtil;

import java.awt.Point;

import models.unit.NodeIcon;
import models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class Input extends Pin {
	protected int unitNumber;		// the number of this unit
	protected Node unit;
//	protected int i;	// the number of this input 

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
	 * @param unitNumber
	 * @param inputNumber
	 * @param nodeParent
	 */
	public Input(final int unitNumber, final int inputNumber, final UnitElement nodeParent) {
		super("input", inputNumber, nodeParent.getInputsMaxCount(), nodeParent);
		this.unitNumber = unitNumber;
	}
	
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
		final boolean isConnected = (fromUnitNumber > 0) || (fromOutputNumber > 0);
		return isConnected;
	}
	

	public void setupInput(final String displayName, final String shortDisplayName, final int inputImageBitDepth, final boolean needToCopyInput) {
		this.displayName = displayName;
		this.shortDisplayName = shortDisplayName;
		this.inputImageBitDepth = inputImageBitDepth;
		this.setNeedToCopyInput(needToCopyInput);
	}

	

	public String getImageTitle() {
		return imageTitle;
	}

	public void setNeedToCopyInput(final boolean needToCopyInput) {
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
//		System.out.println("Point of origin parent: " + parent.getOrigin());
		int height = parent.getDimension().height;
		System.out.println(i);
		int y =  (i*height / super.nump ) - (height/(2*super.nump)) + parent.getOrigin().y;
		Point point = new Point(parent.getOrigin().x, 
//				parent.getOrigin().y + (parent.getDimension().height*i+parent.getDimension().height/2)/nump
//				PaintUtil.alignY(super.nump, i, 100, NodeIcon.pinSize) -50 + parent.getOrigin().y
				y
				);
//		System.out.println(y);
		return point;
	}
}
