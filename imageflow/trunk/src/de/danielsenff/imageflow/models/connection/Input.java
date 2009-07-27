package de.danielsenff.imageflow.models.connection;

import java.awt.Point;

import visualap.Node;
import visualap.Pin;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory.Image;
import de.danielsenff.imageflow.models.unit.AbstractUnit;
import de.danielsenff.imageflow.models.unit.UnitElement;


/**
 * Input Pins
 * @author danielsenff
 *
 */
public class Input extends Pin {
	
	
	/**
	 * the id of the image connected to this input
	 */
	protected String inputID;
	/**
	 * the title of the image connected this input
	 */
	protected String inputTitle;  
	
	/**
	 * Type of data expected from the connected {@link Output}.
	 */
	protected DataType dataType;

	/**
	 * flag indicating if the image at this input needs to be duplicated
	 */
	protected boolean needToCopyInput;
	/**
	 * flag to set inputs not required. Default is required.
	 */
	protected boolean requiredInput = true;
	
	

	/**
	 * Connection
	 */
	protected Connection connection;
	
	/**
	 * @param fromUnit
	 * @param inputNumber
	 */
	public Input(
			final String displayName,
			final String shortDisplayName,
			final DataType dataType, 
			final UnitElement parentNode, 
			int inputNumber,
			boolean required,
			boolean needToCopyInput) {
		this(dataType, parentNode, inputNumber, required);
		this.setupInput(displayName, shortDisplayName, needToCopyInput);
	}
	
	
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
			((Image)getDataType()).setParentPin(this);
		}
	}

	
	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 */
	private void generateID(final int fromUnitNumber, final int fromOutputNumber) {
		this.inputTitle = "Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
		this.inputID = "ID_Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
	}
	
	/**
	 * Sets the connection between this input and an output.
	 */
	/*public void connectTo(final Pin fromOutput) {
		this.from = (Output)fromOutput;
		this.fromUnit = (UnitElement) fromOutput.getParent();
		generateID(fromUnit.getUnitID(), fromOutput.getIndex());
	}*/
	
	public void setConnection(Connection connection) {
		this.connection = connection;
		generateID(connection.getOutput().getParent().getUnitID(), 
				connection.getOutput().getIndex());
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
		return inputTitle;
	}

	/**
	 * @param needToCopyInput
	 */
	public void setNeedToCopyInput(final boolean needToCopyInput) {
		this.needToCopyInput = needToCopyInput;
	}

	/**
	 * Returns true, if this Input requires it's input to be copied.
	 * @return
	 */
	public boolean isNeedToCopyInput() {
		return needToCopyInput;
	}
	


	/**
	 * @return the imageID
	 */
	public String getImageID() {
		return this.inputID;
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
		return super.toString()+" DataType: " + getDataType();
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
		return (UnitElement) this.connection.getFromUnit();
	}

	/**
	 * @return
	 */
	public int getFromUnitNumber() {
		return ((AbstractUnit) this.connection.getFromUnit()).getUnitID();
	}

	/**
	 * Returns the current {@link Connection} or null, if this pin is not connected.
	 * @return
	 */
	public Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * Returns whether or not this Input is connected.
	 * @return 
	 */
	public boolean isConnected() {
		return connection != null;
	}
	
	
	/**
	 * Checks if this Input is connected with the given {@link Output}
	 * @param output 
	 * @return
	 */
	public boolean isConnectedWith(Pin output) {
		if(output instanceof Output && isConnected()) {
			return this.connection.getOutput().equals(output);
		}
		return false;
	}


	/**
	 * Resets the this Input, so that it is unconnected.
	 */
	public void disconnectAll() {
		generateID(0, 0); // reset connection
		this.connection = null;
	}


	/**
	 * The {@link Output} which is connected with this Input.
	 * @return
	 */
	public Output getFromOutput() {
//		return from;
		return connection.getOutput();
	}

	/**
	 * Returns true if the graph branch connected to this inputs parent's output contains the unit.
	 * @param goal
	 * @return
	 */
	public boolean isConnectedInOutputBranch(Node goal) {
		// self reference
		if(goal.equals(parent)) 
//			 can only check inputs, which are connected
//			return traverseInput(this, goal);
			return false;
		else
			return traverseOutput((UnitElement) this.parent, goal);
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
			return traverseInput(this, goal);
//			return traverseOutput2(getConnection().getFromUnit(), goal);
	}

	
	/**
	 * 
	 * @param start
	 * @param goal
	 * @return
	 */
	public static boolean traverseInput(final Input start, final Node goal) {
		if(start.getParent().equals(goal)) {
			return true;
		} else if(start.isConnected()) {

			for (Input input : start.getFromUnit().getInputs()) {
				return traverseInput(input, goal);
			}	
			
		}
		return false;
	}
	

	private static boolean traverseOutput(UnitElement parent, Node goal) {
		if(parent.equals(goal)) {
			return true;
		} else if (parent.hasInputsConnected()) {
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
