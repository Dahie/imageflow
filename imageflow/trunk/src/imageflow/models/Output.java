package imageflow.models;

import graph.Node;
import graph.Pin;

import imageflow.models.unit.UnitElement;

import java.awt.Point;


/**
 * @author danielsenff
 *
 */
public class Output extends Pin implements Connectable {
	

	/**
	 * the number of this unit
	 */
//	protected int unitNumber;
	/**
	 * the number of this output (mostly there is only one output)
	 */
//	protected int outputNumber;

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
	 * Input to which this Output is connected
	 */
	protected Input to;
	
	
	/**
	 * @param nodeParent
	 * @param outputNumber
	 */
	public Output(final UnitElement nodeParent, 
			final int outputNumber) {
		super("output", outputNumber, nodeParent.getOutputsCount(), nodeParent);
		generateID(((UnitElement)this.parent).getUnitID(), getIndex());
	}
	
	/**
	 * @param unitNumber
	 * @param outputNumber
	 * @param nodeParent
	 */
	/*public Output(final UnitElement toUnit, 
			final int outputNumber,  
			final UnitElement nodeParent) {
		super("output", outputNumber, nodeParent.getOutputsCount(), nodeParent);
		this.toUnit = toUnit;
	}*/

	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 */
	private void generateID(final int unitNumber, final int outputNumber) {
		this.imageTitle = "Unit_" + unitNumber + "_Output_" + outputNumber;
		this.imageID = "ID_Unit_" + unitNumber + "_Output_" + outputNumber;
	}

	
	/**
	 * Sets the connection between this input and an output.
	 * @param toUnit
	 * @param toOutputNumber 
	 */
	public void connectTo(final UnitElement toUnit, final int toInputNumber) {
		connectTo(toUnit, toUnit.getInput(toInputNumber-1));
	}
	
	public void connectTo(final UnitElement toUnit, final Pin toInput) {
		this.toUnit = toUnit;
		this.to = (Input)toInput;
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
		int nump = ((UnitElement) parent).getOutputsCount();
		this.nump = nump;
		int y =  (i*height / super.nump ) - (height/(2*super.nump)) + parent.getOrigin().y;
		Point point = new Point(parent.getOrigin().x+parent.getDimension().width, y);
		return point;
	}



	/**
	 * Returns whether or not this Input is connected.
	 * @return 
	 */
	public boolean isConnected() {
		return (this.toUnit != null) 
//			&& (this.toUnitNumber > 0)
			&& (this.to != null);
	}
	
	/**
	 * Checks if this Input is connected with the given {@link Output}
	 * @param input 
	 * @return
	 */
	public boolean isConnectedWith(Pin input) {
		if(this.toUnit != null && input instanceof Input) {
			return this.to.equals(input);
		}
		return false;
	}

	/**
	 * Returns the {@link Input} to which this Output is connected.
	 * @return
	 */
	public Input getToInput() {
		return this.to;
	}

	/**
	 * Returns true, if the imageBitDepth in question is supported
	 * by this Input.
	 * @param imageBitDepth
	 * @return
	 */
	public boolean isImageBitDepthCompatible(final int imageBitDepth) {
		return (getImageBitDepth()&imageBitDepth) != 0;
	}

	/**
	 * @param node
	 * @return
	 */
	public boolean knows(final Node node) {
		// self reference is true
		if(node.equals(parent))
			return true;
		
		// can only check inputs, which are connected
		if(this.isConnected()) {
			for (Output output : toUnit.getOutputs()) {
				// check if this parent is already what we are looking for
				if(node.equals(output.getParent())) { 
						return true;
				//check if this input is connected to more
				} else if(output.isConnected()) {
					// if it is connected, maybe we have more luck here
					return output.knows(node);
				}
			}
		}

		// if nothing helps, it's false
		return false;
	}

	/**
	 * Resets the this Output, so that it is unconnected.
	 */
	public void disconnect() {
		generateID(0, 0); // reset connection
		this.toUnit = null;
	}

	/**
	 * @return
	 */
	public UnitElement getToUnit() {
		return this.toUnit;
	}


}
