package Models.unit;
import ij.IJ;
import ij.ImagePlus;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import Models.Input;
import Models.Output;
import Models.Parameter;
import Models.StringParameter;


/**
 * Backend-unit logic of a single node-element.
 * @author danielsenff
 *
 */
public class UnitElement {
	
	static int ids;
	
	protected int unitID;						// the id of this unit	
	protected String unitName;       			// name of this unit (will be shown) 
	protected BufferedImage icon;           	// icon image
	protected File iconfile;
	protected Color color;				  	// unit color
	protected String infoText;			  	// help text, describing the unit's functionality

	/**
	 * status of this unit
	 */
	public enum Status {OK, ERROR, WAITING };
	public enum Type {SOURCE, FILTER, SINK};
	protected Status status;
	
	
	private boolean isDisplayUnit;			// flag indicating if this unit is a display unit 

	// all arrays start at 1, this will make it easy to detect unconnected inputs and outputs
	protected ArrayList<Input> inputs;				// input array
	protected ArrayList<Output> outputs;				// output array
//	Parameter[] parameters;       	// parameters that control the functionality of the unit
	protected ArrayList<Parameter> parameters;



	protected String unitsImageJSyntax;		// the syntax that is to be used for this unit 

	protected int numMaxParameters;
	protected int numMaxInputs;
	protected int numMaxOutputs;

	private int FIRST_ELEMENT = 0;



	/**
	 * @param unitName
	 * @param unitsImageJSyntax
	 * @param numInputs
	 * @param numOutputs
	 * @param numParameters
	 */
	public UnitElement(
			final String unitName,
			final String unitsImageJSyntax,
			final int numInputs, 
			final int numOutputs, 
			final int numParameters) {
		
		ids++;
		this.unitID = ids;
		this.unitName = unitName;
		this.unitsImageJSyntax = unitsImageJSyntax;
		this.numMaxParameters = numParameters;
		
		this.inputs = new ArrayList<Input>();
		/*for (int i = 1; i < getInputs().length; i++) {
			getInputs()[i] = new Input(unitID,i);
		}*/
		
		/*this.outputs = new Output[numOutputs+1]; 
		for (int i = 1; i < outputs.length; i++) {
			outputs[i] = new Output(unitID,i);
		}*/
		this.outputs = new ArrayList<Output>();
		if(outputs.size() > 0)
			outputs.get(FIRST_ELEMENT).setDoDisplay(false);
		
		
		this.parameters = new ArrayList<Parameter>();
//		this.parameters = new Parameter[numParameters+1];
		/*for (int i = 1; i < parameters.length; i++) {
			parameters[i] = new Parameter(i);
		}*/
	}
	

	
	/**
	 * for source units only
	 * @return
	 */
	public int getBitDepth() {
//		String path = parameters[1].stringValue;
		final String path = ((StringParameter)parameters.get(0)).getStringValue();
		final ImagePlus imp = IJ.openImage(path);
		imp.close();
		final int bitDepth = imp.getBitDepth();
		return bitDepth;
	}


	

	/**
	 * Adds an Output to the unit
	 * @param string
	 * @param string2
	 * @param i
	 */
	public boolean addOutput(String name, 
			String shortname, 
			int outputBitDepth) {
		Output newOutput = new Output(unitID,this.outputs.size()+1);
		newOutput.setupOutput(name, shortname, outputBitDepth);
		return this.outputs.add(newOutput);
	}

	
	public Output getOutput(int index) {
		return this.outputs.get(index);
	}
	
	public ArrayList<Output> getOutputs() {
		return this.outputs;
	}
	
	/**
	 * 
	 */
	public int getOutputsCount() {
		return this.outputs.size();
	}


	/**
	 * @param index
	 * @return
	 */
	public Input getInput(final int index) {
		return this.inputs.get(index);
	}
	
	public ArrayList<Input> getInputs() {
		return this.inputs;
	}
	

	/**
	 * Returns if this unit has inputs.
	 * @return
	 */
	public boolean hasInputs() {
		return (this.inputs.size() == 0);
	}



	/**
	 * @param string
	 * @param string2
	 * @param doesAll
	 * @param b
	 */
	public boolean addInput(String displayName, 
			String shortDisplayName, 
			int inputImageBitDepth, 
			boolean needToCopyInput) {
		Input newInput = new Input(this.unitID, this.inputs.size()+1);
		newInput.setupInput(displayName, shortDisplayName, inputImageBitDepth, needToCopyInput);
		return this.inputs.add(newInput);
	}



	/**
	 * @return
	 */
	public int getInputsActualCount() {
		return this.inputs.size();
	}
	

	/**
	 * @return
	 */
	public Object getInputsPossibleCount() {
		return null;
	}

	

	/**
	 * Returns the name of this unit.
	 * @return the unitName
	 */
	public String getName() {
		return this.unitName;
	}
	
	/**
	 * If activated, the unit will display the current image.
	 * @param isDisplayUnit
	 */
	public void setDisplayUnit(final boolean isDisplayUnit) {
		this.isDisplayUnit = isDisplayUnit;
		if(outputs.size() > 1)
			outputs.get(FIRST_ELEMENT ).setDoDisplay(isDisplayUnit);
	}

	/**
	 * Returns whether or not this unit should display the current state of the image.
	 * @return 
	 */
	public boolean isDisplayUnit() {
		return isDisplayUnit;
	}

	/**
	 * @param class1
	 * @param string
	 * @param string2
	 * @param string3
	 */
	/*public void addParameter(Class type, String string,
			String string2, String string3) {
		
		
		if (type == String.class) {
			StringParameter parameter = new StringParameter(parameterNumber);
		} else if (type == Boolean.class) {
			BooleanParameter parameter = new BooleanParameter(parameterNumber);
		} else if (type == Double.class){
			DoubleParameter parameter = new DoubleParameter(parameterNumber);
		}
		
		addParameter(parameter);
	}*/
		
	/**
	 * Add a Parameter to the unit
	 * @param parameter
	 */
	public boolean addParameter(final Parameter parameter){
		if(this.numMaxParameters > parameters.size()) {
			final int parameterNumber = parameters.size()+1;
			parameter.setParameterNumber(parameterNumber);
			return this.parameters.add(parameter);
		}
		return false;
	}

	/**
	 * Returns how many parameters this can unit have.
	 * @return
	 */
	public int getParametersPossibleCount() {
		return this.numMaxParameters;
	}
	
	/**
	 * Returns how many assigned parameters this unit actually has.
	 * @return
	 */
	public int getParametersActualCount() {
		return this.parameters.size();
	}

	/**
	 * List of all parameters available for this unit
	 * @return
	 */
	public ArrayList<Parameter> getParameters() {
		return this.parameters;
	}
	

	/**
	 * Returns the ID of this Unit.
	 * @return
	 */
	public int getUnitID() {
		return this.unitID;
	}
	
	
	
	/**
	 * Returns the ImageJ-Macro syntax of this unit. 
	 * This syntax is inserted in the macro and constructs the working flow.  
	 * @return
	 */
	public String getImageJSyntax() {
		return this.unitsImageJSyntax;
	}





	/**
	 * 
	 */
	public Type getType() {
		if(this.inputs.size() > 0 && this.outputs.size() == 0) {
			return Type.SINK;
		} else if ( this.inputs.size() == 0  && this.outputs.size() > 0) {
			return Type.SOURCE;
		} else {
			return Type.FILTER;
		}
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String string = super.toString() + " Name:"+this.unitName + " Type:" + this.getType();
		return string;
	}



	/**
	 * @return the icon
	 */
	public BufferedImage getIcon() {
		return this.icon;
	}



	/**
	 * @param icon the icon to set
	 */
	public void setIcon(BufferedImage icon) {
		this.icon = icon;
	}
	
}

