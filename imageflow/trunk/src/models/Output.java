package models;

import graph.Pin;

import java.awt.Point;

import models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class Output extends Pin {
	protected int unitNumber;			// the number of this unit
	protected int outputNumber;		// the number of this output (mostly there is only one output)

	protected String name; 			// the name to be displayed in the context help
	protected String shortName = "O"; // the short name to be displayed on the unit's icon

	protected int outputBitDepth; 	// the int value indicates the type of the generated image 

	protected String imageTitle;  	// the title of the image generated from this output
	protected String imageID; 		// the id of the image generated from this output
	protected boolean doDisplay; 	// indicates that this output should be shown
		
	
	/**
	 * @param unitNumber
	 * @param outputNumber
	 * @param nodeParent
	 */
	public Output(final int unitNumber, final int outputNumber,  final UnitElement nodeParent) {
		super("output", outputNumber, nodeParent.getOutputsMaxCount(), nodeParent);
		this.unitNumber = unitNumber;
		this.outputNumber = outputNumber;
		this.imageTitle = "Unit_" + unitNumber + "_Output_" + outputNumber;
		this.imageID = "ID_Unit_" + unitNumber + "_Output_" + outputNumber;
	}



	public void setupOutput(final String name, final String shortname, final int outputBitDepth) {
		this.name = name;
		this.shortName = shortname;
		this.outputBitDepth = outputBitDepth;
	}


	public void setDoDisplay(final boolean doDisplay) {
		this.doDisplay = doDisplay;
	}


	/**
	 * Returns whether or not this output displays it's image. 
	 * @return
	 */
	public boolean isDoDisplay() {
		return doDisplay;
	}

	public String getImageTitle() {
		return this.imageTitle;
	}
	
	/**
	 * 
	 * @return the imageID
	 */
	public String getImageID() {
		return this.imageID;
	}

	/* (non-Javadoc)
	 * @see graph.Pin#getLocation()
	 */
	@Override
	public Point getLocation() {
		System.out.println("Point of origin parent: " + parent.getOrigin());
		Point point = new Point(parent.getOrigin().x+parent.getDimension().width, 
//				parent.getOrigin().y + (parent.getDimension().height*i+parent.getDimension().height/2)/nump
//				PaintUtil.alignY(super.nump, outputNumber, 100, NodeIcon.pinSize)
				parent.getOrigin().y+50
				);
		return point;
	}

}
