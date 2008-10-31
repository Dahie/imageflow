package models;

import graph.Pin;

import java.awt.Point;

import models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class Output extends Pin {
	/**
	 * the number of this unit
	 */
	protected int unitNumber;
	/**
	 * the number of this output (mostly there is only one output)
	 */
	protected int outputNumber;

	/**
	 * the name to be displayed in the context help
	 */
	protected String name;
	/**
	 * the short name to be displayed on the unit's icon
	 */
	protected String shortName = "O";

	/**
	 * the int value indicates the type of the generated image
	 */
	protected int outputBitDepth; 

	/**
	 * the title of the image generated from this output
	 */
	protected String imageTitle; 
	/**
	 * the id of the image generated from this output
	 */
	protected String imageID; 
	/**
	 * indicates that this output should be shown
	 */
	protected boolean doDisplay; 
		
	
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



	/**
	 * @param name
	 * @param shortname
	 * @param outputBitDepth
	 */
	public void setupOutput(final String name, final String shortname, final int outputBitDepth) {
		this.name = name;
		this.shortName = shortname;
		this.outputBitDepth = outputBitDepth;
	}


	/**
	 * Activates to display the image at this output.
	 * @param doDisplay
	 */
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

	/**
	 * Title of the image in the macro.
	 * @return
	 */
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
		int height = parent.getDimension().height;
		int y =  (i*height / super.nump ) - (height/(2*super.nump)) + parent.getOrigin().y;
		Point point = new Point(parent.getOrigin().x+parent.getDimension().width, 
				y
				);
		return point;
	}

}
