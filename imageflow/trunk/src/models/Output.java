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
	protected String shortDisplayName = "O";

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
	 * Unit to which this Output is connected. 
	 * In contrast to the fromUnit in the {@link Input}, this unit 
	 * is not relevant for the ImageTitle.
	 */
	protected UnitElement toUnit;
	/**
	 * Number of the pin on the toUnit
	 */
	protected int toInputNumber;
	private int toUnitNumber; 
		
	
	
	public Output(final UnitElement nodeParent, final int outputNumber) {
		super("output", outputNumber, nodeParent.getOutputsMaxCount(), nodeParent);
		setConnection(nodeParent.getUnitID(), outputNumber);
	}
	
	/**
	 * @param unitNumber
	 * @param outputNumber
	 * @param nodeParent
	 */
	public Output(final UnitElement toUnit, final int outputNumber,  final UnitElement nodeParent) {
		super("output", outputNumber, nodeParent.getOutputsMaxCount(), nodeParent);
		this.toUnit = toUnit;
		setConnection(nodeParent.getUnitID(), outputNumber);
	}

	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 */
	private void setConnection(final int unitNumber, final int outputNumber) {
//		this.toUnitNumber = unitNumber;
//		this.toOutputNumber = outputNumber;
		this.unitNumber = unitNumber;
		this.outputNumber = outputNumber;
		this.imageTitle = "Unit_" + unitNumber + "_Output_" + outputNumber;
		this.imageID = "ID_Unit_" + unitNumber + "_Output_" + outputNumber;
	}

	
	/**
	 * Sets the connection between this input and an output.
	 * @param toUnit
	 * @param toOutputNumber 
	 */
	public void setConnection(final UnitElement toUnit, final int toOutputNumber) {
		this.toUnit = toUnit;
		this.toInputNumber = toOutputNumber;
//		setConnection(toUnit.getUnitID(), fromOutputNumber);
	}

	/**
	 * @param name
	 * @param shortname
	 * @param outputBitDepth
	 */
	public void setupOutput(final String name, final String shortname, final int outputBitDepth) {
		this.name = name;
		this.shortDisplayName = shortname;
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
	 * Gets the Images bitdepth.
	 * @return
	 */
	public int getImageBitDepth() {
		return this.outputBitDepth;
	}

	/**
	 * Get the abbreviated DisplayName
	 * @return
	 */
	public String getShortDisplayName() {
		return this.shortDisplayName;
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
		Point point = new Point(parent.getOrigin().x+parent.getDimension().width, y);
		return point;
	}



	/**
	 * Returns whether or not this Input is connected.
	 * @return 
	 */
	public boolean isConnected() {
		final boolean isConnected = (this.toUnit != null) 
			|| (this.toUnitNumber > 0)
			|| (this.toInputNumber > 0);
		return isConnected;
	}
	
	/**
	 * Checks if this Input is connected with the given {@link Output}
	 * @param input 
	 * @return
	 */
	public boolean isConnectedWith(Input input) {
		if(this.toUnit != null) {
			return this.toUnit.getInput(this.toInputNumber-1).equals(input);
		}
		return false;
	}

}
