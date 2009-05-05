package imageflow.models;

import graph.Node;
import graph.Pin;

import imageflow.models.unit.UnitElement;

import java.awt.Point;


/**
 * @author danielsenff
 *
 */
public class Input extends Pin implements Connectable {
	/**
	 * the number of this unit
	 */
//	protected int unitNumber;
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
//	protected int fromUnitNumber;
	/**
	 * Connected from this {@link Output}
	 */
//	protected int fromOutputNumber;
	/**
	 * Connected from this {@link UnitElement}
	 */
	protected UnitElement fromUnit;
	/**
	 * Output from which this Input is connected
	 */
	protected Output from;
	
	
	/**
	 * @param fromUnit
	 * @param inputNumber
	 * @param nodeParent
	 */
	public Input(final UnitElement nodeParent, int inputNumber) {
		super("input", inputNumber, nodeParent.getInputsCount(), nodeParent);
	}
	
	
	/**
	 * @param fromUnit
	 * @param inputNumber
	 * @param nodeParent
	 */
	/*public Input(final UnitElement fromUnit, final int inputNumber, final UnitElement nodeParent) {
		super("input", inputNumber, nodeParent.getInputsCount(), nodeParent);
	}*/
	
	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 */
	private void generateID(final int fromUnitNumber, final int fromOutputNumber) {
		this.imageTitle = "Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
		this.imageID = "ID_Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
	}
	
	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnit
	 * @param fromOutputNumber
	 */
	public void connectTo(final UnitElement fromUnit, final int fromOutputNumber) {
		connectTo(fromUnit.getOutput(fromOutputNumber-1));
	}
	
	/**
	 * Sets the connection between this input and an output.
	 */
	public void connectTo(final Pin fromOutput) {
		this.from = (Output)fromOutput;
		this.fromUnit = (UnitElement) fromOutput.getParent();
		generateID(fromUnit.getUnitID(), fromOutput.getIndex());
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
	 * Gets the Images bitdepth.
	 * @return
	 */
	public int getImageBitDepth() {
		return inputImageBitDepth;
	}

	/**
	 * Get the abbreviated DisplayName
	 * @return
	 */
	public String getShortDisplayName() {
		return shortDisplayName;
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
		return super.toString()+" fromUnit: " + this.fromUnit + " fromOutput: "+ this.from;
	}

	
	
	/* (non-Javadoc)
	 * @see graph.Pin#getLocation()
	 */
	@Override
	public Point getLocation() {
		int height = parent.getDimension().height;
		int nump = ((UnitElement) parent).getInputsCount();
		this.nump = nump;
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
	 * @return
	 */
	public int getFromUnitNumber() {
		return fromUnit.getUnitID();
	}

	/**
	 * Returns whether or not this Input is connected.
	 * @return 
	 */
	public boolean isConnected() {
		return (fromUnit != null) 
//			&& (fromUnitNumber > 0)
			&& (this.from != null);
	}
	
	
	/**
	 * Checks if this Input is connected with the given {@link Output}
	 * @param output 
	 * @return
	 */
	public boolean isConnectedWith(Pin output) {
		if(output instanceof Output && isConnected()) {
			return this.from.equals(output);
		}
		return false;
	}


	/**
	 * Resets the this Input, so that it is unconnected.
	 */
	public void disconnectAll() {
		generateID(0, 0); // reset connection
		this.fromUnit = null;
		this.from = null;
	}


	/**
	 * Returns true, if the imageBitDepth in question is supported
	 * by this Input.
	 * @param imageBitDepth
	 * @return
	 */
	public boolean isImageBitDepthCompatible(final int imageBitDepth) {
		if(getImageBitDepth() != -1 && imageBitDepth != -1) {
			return (getImageBitDepth()&imageBitDepth) != 0;	
		}
		return false;
	}


	/**
	 * The {@link Output} which is connected with this Input.
	 * @return
	 */
	public Output getFromOutput() {
		return from;
	}

	/**
	 * Returns true if the graph branch connected to this input contains the unit.
	 * @param goal
	 * @return
	 */
	public boolean isConnectedInInputBranch(Node goal) {
		// self reference
		
		
		if(goal.equals(parent)) 
//			 can only check inputs, which are connected
//			return traverseInput(this, goal);
			return false;
		else
			return traverseOutput((UnitElement) this.parent, goal);
		 
		
	}

	private static boolean traverseInput(Input start, Node goal) {
		if(start.getParent().equals(goal)) {
			return false;
		} else if(start.isConnected()) {
			for (Input input : start.getFromUnit().getInputs()) {
				if(traverseInput(input, goal));
					return true;
			}
		}
		return false;
	}
	

	private static boolean traverseOutput(UnitElement parent, Node goal) {
		if(parent.equals(goal)) {
			return true;
		} else if (parent.hasOutputsConnected()) {
			for (Output output : parent.getOutputs()) {
				if(output.isConnected()) {
					for (Connection connection : output.getConnections()) {
						if(traverseOutput(connection.getToUnit(), goal))
							return true;
					}
					
					
				}
			}
		} 
		
		
		/*if(parent.hasInputsConnected()) {
			for (Input input : parent.getInputs()) {
				// check if this parent is already what we are looking for
				if(input.getParent().equals(goal)) { 
					return true;
				//check if this input is connected to more
				} else if(input.isConnected()) {
					// if it is connected, maybe we have more luck here
					return traverseOutput(input.getFromUnit(), goal);
				}
			}
		}*/
		return false;
	}
	

}
