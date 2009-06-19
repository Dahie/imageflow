package de.danielsenff.imageflow.models;

import java.awt.Point;

import visualap.Node;
import visualap.Pin;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory.Image;
import de.danielsenff.imageflow.models.unit.UnitElement;


/**
 * Input Pins
 * @author danielsenff
 *
 */
public class Input extends Pin {
	
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
	 * Type of data expected from the connected {@link Output}.
	 */
	protected String inputDataType = "Image";
	protected DataType dataType;
	
	/**
	 * the int value indicates the acceptable image types
	 */
	protected int inputImageBitDepth; 
	

	/**
	 * flag indicating if the image at this input needs to be duplicated
	 */
	protected boolean needToCopyInput;
	/**
	 * flag to set inputs not required. Default is required.
	 */
	protected boolean requiredInput = true;
	
	

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
	 */
	public Input(final DataType dataType, 
			final UnitElement parentNode, 
			int inputNumber) {
		this(dataType, parentNode, inputNumber, true);
	}
	
	/**
	 * @param nodeParent
	 * @param inputNumber
	 * @param requiredInput
	 */
	public Input(DataType dataType, 
			final UnitElement nodeParent, 
			int inputNumber, 
			boolean requiredInput) {
		super(dataType, inputNumber, nodeParent);
		setRequiredInput(requiredInput);
		if(getDataType() instanceof DataTypeFactory.Image) {
			((Image)getDataType()).setParentUnitElement((UnitElement) getParent());
		}
	}

	
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
		this.dataType = DataTypeFactory.createImage(inputImageBitDepth);
		this.setNeedToCopyInput(needToCopyInput);
	}
	
	public void setupInput(final String displayName, 
			final String shortDisplayName, 
			final boolean needToCopyInput) {
		this.displayName = displayName;
		this.shortDisplayName = shortDisplayName;
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
	public boolean isRequired() {
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
	public Point getLocation() {
		int height = parent.getDimension().height;
		int nump = ((UnitElement) parent).getInputsCount();
		int y =  (index*height / nump ) - (height/(2*nump)) + parent.getOrigin().y;
		return new Point(parent.getOrigin().x, y);
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
		return (fromUnit != null) && (this.from != null);
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
	 * Sets the connection between this input and an output.
	 * @param fromUnit
	 * @param fromOutputNumber
	 */
	public void connectTo(final UnitElement fromUnit, final int fromOutputNumber) {
		connectTo(fromUnit.getOutput(fromOutputNumber-1));
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

	private static boolean traverseInput(final Input start, final Node goal) {
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
						if(traverseOutput((UnitElement)connection.getToUnit(), goal))
							return true;
					}
				}
			}
		} 

		return false;
	}
	

}
