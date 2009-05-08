package imageflow.models.unit;
import helper.PaintUtil;
import ij.gui.GenericDialog;
import imageflow.models.Connection;
import imageflow.models.Input;
import imageflow.models.MacroElement;
import imageflow.models.Output;
import imageflow.models.parameter.AbstractParameter;
import imageflow.models.parameter.BooleanParameter;
import imageflow.models.parameter.ChoiceParameter;
import imageflow.models.parameter.DoubleParameter;
import imageflow.models.parameter.IntegerParameter;
import imageflow.models.parameter.Parameter;
import imageflow.models.parameter.ParameterFactory;
import imageflow.models.parameter.StringParameter;
import imageflow.models.unit.UnitModelComponent.Size;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;



/**
 * Backend-unit logic of a single node-element.
 * Model for an instance of a workflow-element.
 * This model is a basic model for an element in the graph and 
 * contains most of the graph logic.
 * 
 * Next to meta data, it basically it contains 3 major parts.
 * 3 Lists with {@link Input}s, {@link Output}s and {@link Parameter}.
 * 
 * In the heart of the model is an Macro-Object, which
 * contains the syntax, in which the parameters injected on runtime
 * and which is executed by ImageJ.
 * @author danielsenff
 *
 */
public class UnitElement extends AbstractUnit {

	final private static int PIN_TOLERANCE = 18;

	/**
	 * name of this unit, this is not displayed, Label is displayed
	 */
	protected String unitName;    
	protected File iconfile;
	/**
	 * unit color
	 */
	private Color color = new Color(0xA0A0A0);
	/**
	 * help text, describing the unit's functionality
	 */
	protected String infoText; 

	/**
	 * unitIcon on the graphpanel
	 */
	protected BufferedImage unitIcon;
	protected NodeIcon unitComponentIcon;
	/**
	 * function icon, illustrates the purpose of the element
	 */
	protected BufferedImage icon; 

	/**
	 * unused, status of this unit
	 */
	public enum Status {OK, ERROR, WAITING };
	/**
	 * Type of this UnitElement
	 *
	 */
	public enum Type {SOURCE, FILTER, SINK};
	/**
	 * {@link Status} of this Unit.
	 */
	protected Status status;

	/**
	 * boolean indicating if this unit is a display unit
	 * The result of DisplayUnits will be shown after executing the workflow.  
	 */
	protected boolean isDisplayUnit = false;  

	// all arrays start at 1, this will make it easy to detect unconnected inputs and outputs
	/**
	 * input array
	 */
	protected ArrayList<Input> inputs;
	/**
	 * output array
	 */
	protected ArrayList<Output> outputs;
	/**
	 * parameters that control the functionality of the unit
	 */
	protected ArrayList<Parameter> parameters;

	/**
	 * Size the component will be displayed on the workspace.
	 */
	protected Size compontentSize;


	/**
	 * @param unitName
	 * @param unitsImageJSyntax
	 * @param numInputs
	 * @param numOutputs
	 * @param numParameters
	 */
	public UnitElement  (
			final String unitName,
			final String unitsImageJSyntax) {
		super(new Point(30,30), new MacroElement(unitsImageJSyntax));
		setDimension(new Dimension(100,100));

		init(unitName);
	}

	/**
	 * @param origin
	 * @param unitName
	 * @param unitsImageJSyntax
	 * @param numInputs
	 * @param numOutputs
	 * @param numParameters
	 */
	public UnitElement  (
			final Point origin,
			final String unitName,
			final String unitsImageJSyntax) {
		super(origin, new MacroElement(unitsImageJSyntax));
		setDimension(new Dimension(100,100));

		init(unitName);
	}

	/**
	 * @param origin
	 * @param unitName
	 * @param macroElement
	 * @param numInputs
	 * @param numOutputs
	 * @param numParameters
	 */
	public UnitElement  (
			final Point origin,
			final String unitName,
			final MacroElement macroElement) {
		super(origin, macroElement);
		setDimension(new Dimension(100,100));

		init(unitName);
	}

	/**
	 * @param unitName
	 * @param numInputs
	 * @param numOutputs
	 * @param numParameters
	 */
	private void init(final String unitName) {

		this.label = unitName;
		this.unitName = unitName;

		this.inputs = new ArrayList<Input>();
		this.outputs = new ArrayList<Output>();
		this.parameters = new ArrayList<Parameter>();

		unitComponentIcon= new NodeIcon(this);
		unitIcon = unitComponentIcon.getImage();
		this.compontentSize = Size.BIG;
	}


	/**
	 * Adds an Output to the unit
	 * @param name 
	 * @param shortname 
	 * @param outputBitDepth 
	 * @param doDisplay 
	 * @return 
	 */
	public boolean addOutput(String name, 
			final String shortname, 
			int outputBitDepth,
			boolean doDisplay) 
	{
		Output newOutput = new Output(this,this.outputs.size()+1);
		newOutput.setupOutput(name, shortname, outputBitDepth);
		newOutput.setDoDisplay(doDisplay);
		notifyModelListeners();
		return this.outputs.add(newOutput);
	}

	/**
	 * Adds an Output-Object to the unit.
	 * @param newOutput
	 * @return
	 */
	public boolean addOutput(final Output newOutput) {
		notifyModelListeners();
		return this.outputs.add(newOutput);
	}

	/**
	 * Get one {@link Output} at the index. Indecies start with 0;
	 * @param index
	 * @return
	 */
	public Output getOutput(final int index) {
		return getOutputs().get(index);
	}

	/**
	 * Returns a list of all {@link Output}s
	 * @return
	 */
	public ArrayList<Output> getOutputs() {
		return this.outputs;
	}

	/**
	 * Number of {@link Output}s.
	 * @return
	 */
	public int getOutputsCount() {
		return this.outputs.size();
	}

	/**
	 * Returns the {@link Input} at the given index. Indecies start with 0.
	 * @param index
	 * @return
	 */
	public Input getInput(final int index) {
		return this.inputs.get(index);
	}

	/**
	 * List of all attached {@link Input}s
	 * @return
	 */
	public ArrayList<Input> getInputs() {
		return this.inputs;
	}

	/**
	 * Returns if this unit has inputs attached to this unit.
	 * @return
	 */
	public boolean hasInputs() {
		return (this.inputs.size() > 0);
	}

	/**
	 * Add new Input by setup.
	 * @param displayName 
	 * @param shortDisplayName 
	 * @param inputImageBitDepth 
	 * @param needToCopyInput 
	 * @param required This input needs to be connected for this unit to work. 
	 * @return 
	 */
	public boolean addInput(String displayName, 
			String shortDisplayName, 
			int inputImageBitDepth, 
			boolean needToCopyInput, 
			final boolean required) {
		Input newInput = new Input(this, this.inputs.size()+1);
		newInput.setupInput(displayName, shortDisplayName, inputImageBitDepth, needToCopyInput);
		boolean add = this.inputs.add(newInput);
		notifyModelListeners();
		return add;
	}

	/**
	 * @param displayName
	 * @param shortDisplayName
	 * @param inputImageBitDepth
	 * @param needToCopyInput
	 * @return
	 */
	public boolean addInput(final String displayName, 
			final String shortDisplayName, 
			final int inputImageBitDepth, 
			final boolean needToCopyInput) {
		return this.addInput(displayName, shortDisplayName, inputImageBitDepth, needToCopyInput, true);
	}

	/**
	 * Add new Input by Object.
	 * @param input
	 * @return
	 */
	public boolean addInput(final Input input) {
		return this.inputs.add(input);
	}

	/**
	 * How many {@link Input}s are actually registered.
	 * @return
	 */
	public int getInputsCount() {
		return this.inputs.size();
	}


	/**
	 * Returns the typename of this unit.
	 * @return the unitName
	 */
	public String getUnitName() {
		return this.unitName;
	}

	/**
	 * If activated, the unit will display the current image.
	 * @param isDisplayUnit
	 */
	public void setDisplayUnit(final boolean isDisplayUnit) {
		this.isDisplayUnit = isDisplayUnit;
		for (Output output : getOutputs()) {
			output.setDoDisplay(isDisplayUnit);
		}

		notifyModelListeners();
	}

	/**
	 * Returns whether or not this unit should display the current state of the image.
	 * @return 
	 */
	public boolean isDisplayUnit() {
		return this.isDisplayUnit;
	}

	@Override
	public void drag(int dx, int dy) {
		super.drag(dx, dy);
		notifyModelListeners();
	}

	/**
	 * Add a Parameter to the unit
	 * @param parameter
	 * @return 
	 */
	public boolean addParameter(final Parameter parameter){
		final int parameterNumber = parameters.size()+1;
		parameter.setParameterNumber(parameterNumber);
		boolean add = this.parameters.add(parameter);
		notifyModelListeners();
		return add;
	}


	/**
	 * Returns how many assigned {@link AbstractParameter}s this unit actually has.
	 * @return
	 */
	public int getParametersCount() {
		return this.parameters.size();
	}

	/**
	 * List of all {@link AbstractParameter}s available for this unit
	 * @return
	 */
	public ArrayList<Parameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Parameter on index i.
	 * @param i
	 * @return
	 */
	public Parameter getParameter(final int i) {
		return this.parameters.get(i);
	}


	/**
	 * Returns the what type this is. 
	 * SOURCE - only {@link Output}
	 * SINK - only {@link Input}
	 * FILTER - {@link Input} and {@link Output}
	 * @return
	 */
	public Type getUnitType() {
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
		return super.toString() + " Name:"+this.label + " Type:" + this.getUnitType();
	}

	/**
	 * Returns the Icon of the filter.
	 * @return the icon
	 */
	public Image getIcon() {
		return this.icon;
	}

	/**
	 * @param bufferedImage the icon to set
	 */
	public void setIcon(BufferedImage bufferedImage) {
		this.icon = bufferedImage;
		this.unitComponentIcon.setIcon(bufferedImage);
		notifyModelListeners();
	}

	/**
	 * Updates the units representational {@link NodeIcon} on the workspace.
	 */
	public void updateUnitIcon() {
		this.unitIcon = new NodeIcon(this).getImage();
	}


	/**
	 * Checks if there is an {@link Input} or an {@link Output} at this mouse coordinates. 
	 */
	@Override
	public Object contains(final int x, final int y) {
		if ((x >= origin.x - PIN_TOLERANCE)
				&&(x < origin.x + PIN_TOLERANCE))	{
			int inputsMaxCount = getInputsCount();
			for (int i = 0; i < inputsMaxCount; i++) {
				int lower_y = PaintUtil.alignY(inputsMaxCount, i, getDimension().height, NodeIcon.pinSize)+origin.y;
				if ((y >= lower_y)&&(y <= lower_y + PIN_TOLERANCE*2)) {
					return getInput(i);
				}
			}
		}
		if ((x >= origin.x + getDimension().width - PIN_TOLERANCE)
				&&(x < origin.x + getDimension().width + PIN_TOLERANCE)) {
			int outputsCount = getOutputsCount();
			for (int i = 0; i < outputsCount; i++) {
				int lower_y = PaintUtil.alignY(outputsCount, i, getDimension().height, NodeIcon.pinSize)+origin.y;
				if ((y >= lower_y)&&(y <= lower_y + PIN_TOLERANCE*2)) {
					return getOutput(i);
				}
			}
		}
		return super.contains(x,y);
	}


	@Override
	public Dimension getDimension() {
		return NodeIcon.getDimensionFromSize(this.compontentSize);
	}

	/* (non-Javadoc)
	 * @see graph.Node#paint(java.awt.Graphics, java.awt.image.ImageObserver)
	 */
	@Override
	public Rectangle paint(Graphics g, ImageObserver io) {
		//TODO move from model to view
		if (unitIcon == null) {
			// obj != null, icon == null
			g.setColor(selected ? Color.red : new Color(250, 220, 100));
			g.fillRect(origin.x, origin.y, getDimension().width, getDimension().height);
			g.setColor(Color.black);
			g.drawRect(origin.x, origin.y, getDimension().width-1, getDimension().height-1);
		} else {

			// obj != null
			if (selected) {
				/*g.setColor(Color.red);
				g.drawRect(origin.x-2, origin.y-2, getDimension().width+4, getDimension().height+4);*/
				g.setColor(new Color(0,0,255,40));
				g.fillRoundRect(origin.x-2, origin.y-2, getDimension().width+4, getDimension().height+4, 
						unitComponentIcon.arc, unitComponentIcon.arc);
				g.setColor(new Color(0,0,0,44));
				g.drawRoundRect(origin.x-2, origin.y-2, getDimension().width+4, getDimension().height+4, 
						unitComponentIcon.arc, unitComponentIcon.arc);
			}
			Image unitIcon = unitComponentIcon.getImage(this.compontentSize);
			g.drawImage(unitIcon, this.origin.x, this.origin.y, null);
			//			unitComponentIcon.paintBigIcon((Graphics2D) g);
			//			unitComponentIcon.paintMediumIcon((Graphics2D) g);
			//			unitComponentIcon.paintSmallIcon((Graphics2D) g);
		}


		//draw inputs
		int numberInputs = getInputsCount();
		for (int i = 0; i < numberInputs; i++) {
			g.setColor(Color.BLACK);
			int y =  PaintUtil.alignY(numberInputs, i, unitComponentIcon.getHeight(), NodeIcon.pinSize);
			g.fillRect(origin.x, origin.y+y, NodeIcon.pinSize, NodeIcon.pinSize);
		}

		//draw outputs
		int numberOutputs = getOutputsCount();
		for (int i = 0; i < numberOutputs; i++) {
			g.setColor(Color.BLACK);

			int x = (unitComponentIcon.getWidth() - 8) + origin.x;
			int y = PaintUtil.alignY(numberOutputs, i, unitComponentIcon.getHeight(), NodeIcon.pinSize)+origin.y;

			Polygon po=new Polygon(); 
			po.addPoint(x, y); //top
			po.addPoint(x + NodeIcon.pinSize, y + (NodeIcon.pinSize/2)); //pointy
			po.addPoint(x, y+NodeIcon.pinSize); //bottom
			g.fillPolygon(po);
			g.drawPolygon(po);
		}



		// during draggin
		if (dragging != null) {
			/*g.setColor(Color.black);
			g.drawRect(dragging.x, dragging.y, dragging.width-1, dragging.height-1);*/

			g.setColor(new Color(0,0,255, 40));
			g.fillRoundRect(dragging.x+5, dragging.y+5, getDimension().width-10, getDimension().height-10, 
					unitComponentIcon.arc, unitComponentIcon.arc);
			g.setColor(new Color(0,0,0));
			g.drawRoundRect(dragging.x+5, dragging.y+5, getDimension().width-10, getDimension().height-10, 
					unitComponentIcon.arc, unitComponentIcon.arc);
		}

		return new Rectangle(origin, getDimension());
	}



	/* (non-Javadoc)
	 * @see graph.NodeAbstract#setObject(java.lang.Object)
	 */
	@Override
	public void setObject(Object obj) {
		this.obj = obj;
	}


	/* (non-Javadoc)
	 * @see graph.Node#clone()
	 */
	@Override
	public UnitElement clone() {
		// clone the object
		String imageJSyntax;
		try {
			imageJSyntax = (String) cloneNonClonableObject(this.obj);
		} catch (CloneNotSupportedException e) {
			imageJSyntax = ((MacroElement)this.obj).getImageJSyntax();
		}


		UnitElement clone = new UnitElement(new Point(origin.x+15, origin.y+15), 
				this.label, 
				imageJSyntax);
		for (Input input : inputs) {
			clone.addInput(input.getName(), 
					input.getShortDisplayName(), 
					input.getImageBitDepth(), 
					input.isNeedToCopyInput());
		}
		for (Output output : outputs) {
			clone.addOutput(output.getName(), 
					output.getShortDisplayName(), 
					output.getImageBitDepth(),
					output.isDoDisplay());
		}
		for (Parameter parameter : parameters) {
			Parameter newParameter;
			if(parameter instanceof ChoiceParameter) {
				newParameter =	ParameterFactory.createParameter(parameter.getDisplayName(), 
						parameter.getValue(), parameter.getHelpString(), null, ((ChoiceParameter)parameter).getChoiceIndex());
			} else if (parameter instanceof BooleanParameter){
				newParameter =	ParameterFactory.createParameter(parameter.getDisplayName(), 
						parameter.getValue(), parameter.getHelpString(), ((BooleanParameter)parameter).getTrueString(), 0);
			} else {
				newParameter =	ParameterFactory.createParameter(parameter.getDisplayName(), 
						parameter.getValue(), parameter.getHelpString());	
			}
			clone.addParameter(parameter);

		}
		clone.setDisplayUnit(this.isDisplayUnit);
		clone.setColor(this.color);
		clone.setIcon(this.icon);
		return clone;
	}


	/**
	 * Mark this unit by marking its {@link Input}s and {@link Output}s.
	 * @param mark
	 */
	public void setMark(int mark) {
		for (Input input : inputs) {
			input.setMark(mark);
		}
		for (Output output : outputs) {
			output.setMark(mark);
		}
		notifyModelListeners();
	}


	/**
	 * Returns the mark of the attached {@link Output}.
	 * Doesn't help it if the mark is different on any pin
	 * @return
	 */
	public int getMark() {
		return outputs.get(0).getMark();
		// doesn't help it if the mark is different on any pin
	}


	/**
	 * Checks a unit, if it's inputs have already been registered in the algorithm.
	 * It's marked, when it's not 0. The int is conjecture to the order.
	 * TODO: this method name may be confusing: it doesn£t check if all inputs of this unit are marked
	 * @param unit
	 * @return
	 */
	public boolean hasAllInputsMarked() {

		if(hasInputs()) {
			// check each input, if it's parent has been registered
			for (Input input : getInputs()) {
				if( (input.isConnected() && !input.getFromOutput().isMarked())
						|| !input.isConnected() ) {
					// this connected output hasn't been registered and is missing a mark, 
					// so the whole unit isn't ready set. 
					return false;
					// otherwise mark is already set, so this output is fine
				} 
			}
		} 
		// if there are no inputs, it's true
		return true;
	}


	/**
	 * Returns true if an output is marked.
	 * @return
	 */
	public boolean hasMarkedOutput() {
		for (Output output : this.outputs) {

			// output is actually connected
			if(output.isConnected()) {

				int mark = ((UnitElement)output.getParent()).getMark();
				// if mark is set to anything
				if(mark != 0) { 
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Is true as soon as one connected {@link Input} is found.
	 * @return
	 */
	public boolean hasInputsConnected() {
		for (final Input input : inputs) {
			if(input.isConnected())
				return true;
		}
		return false;
	}

	/**
	 * Returns true if this unit has all required inputs connected.
	 * @return
	 */
	public boolean hasRequiredInputsConnected() {
		for (final Input input : inputs) {
			if(input.isRequired() && !input.isConnected()) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Returns true, if this UnitElement has {@link Output}-Pins.
	 * @return
	 */
	public boolean hasOutputs() {
		return !this.outputs.isEmpty();
	}

	/**
	 * Is true as soon as one connected {@link Output} is found.
	 * @return
	 */
	public boolean hasOutputsConnected() {
		for (final Output output : outputs) {
			if(output.isConnected())
				return true;
		}
		return false;
	}


	/**
	 * Displays a Popup-Window with the properties, that can be edited for this UnitElement.
	 */
	public void showProperties() {
		final GenericDialog gd = new GenericDialog(getLabel() + " - Parameters");
		gd.addMessage(getHelpString());
		gd.addMessage(" ");


		// label field 
		gd.addStringField("Unit label", this.getLabel(),40);
		gd.addCheckbox("Display", this.isDisplayUnit);

		final ArrayList<Parameter> parameterList = getParameters();

		if (parameterList.isEmpty()) {
			gd.addMessage("No parameters that can be set");
		} else {
			for (final Parameter parameter : parameterList) {

				if(parameter instanceof DoubleParameter) {
					gd.addNumericField(parameter.getDisplayName(), (Double) parameter.getValue(), 2);
				} else if(parameter instanceof IntegerParameter) {
					gd.addNumericField(parameter.getDisplayName(), (Integer) parameter.getValue(), 0);
				} else if(parameter instanceof BooleanParameter) {
					gd.addCheckbox(parameter.getDisplayName(), (Boolean)parameter.getValue());
				} else if(parameter instanceof ChoiceParameter) {
					gd.addChoice(parameter.getDisplayName(), 
							((ChoiceParameter)parameter).getChoicesArray(), 
							((ChoiceParameter)parameter).getValue());
				} else if(parameter instanceof StringParameter) {
					gd.addStringField(parameter.getDisplayName(), (String)parameter.getValue(), 40);
				}		
			}

		}

		// show properties window
		gd.showDialog();

		if( gd.wasCanceled())
			return;

		String newLabel = (String) (gd.getNextString()).trim();
		setLabel(newLabel);
		boolean isNewDisplay = gd.getNextBoolean();
		setDisplayUnit(isNewDisplay);

		for (final Parameter parameter : parameterList) {
			if(parameter instanceof DoubleParameter) {
				((DoubleParameter) parameter).setValue((double) (gd.getNextNumber()));
			} else if (parameter instanceof IntegerParameter) {
				((IntegerParameter) parameter).setValue((int) (gd.getNextNumber()));
			} else if (parameter instanceof BooleanParameter) {
				((BooleanParameter) parameter).setValue((boolean) (gd.getNextBoolean()));
			} else if (parameter instanceof ChoiceParameter) {
				((ChoiceParameter) parameter).setValue((String) (gd.getNextChoice()));
				// TODO set the ChoiceNumber to be able to save it
			} else if (parameter instanceof StringParameter) {
				String newString = (String) (gd.getNextString()).trim();
				((StringParameter) parameter).setValue(newString);
			}
		}	

		notifyModelListeners();
	}

	/**
	 * Returns whether this unit has parameters or not.
	 * This doesn't concern the unitlabel.
	 * @return
	 */
	public boolean hasParameters() {
		return !parameters.isEmpty();
	}

	/**
	 * Returns the base color of the units representation.
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set the color of the unit.
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
		notifyModelListeners();
	}

	/**
	 * HelpString is a short description for example to use in Tooltips.
	 * @return the infoText
	 */
	public String getHelpString() {
		return this.infoText;
	}

	/**
	 * Sets a new help descriptions message for this unit.
	 * @param helpString
	 */
	public void setHelpString(final String helpString) {
		this.infoText = helpString;		
	}

	/**
	 * Returns true if any {@link Output} connects to a unit that is set as displayable.
	 * @return
	 */
	public boolean hasDisplayBranch() {
		if(isDisplayUnit())
			return true;

		for (Output output : getOutputs()) {
			if(output.isConnected()) {
				for (Connection connection : output.getConnections()) {
					UnitElement next = (UnitElement)connection.getToUnit();
					//					System.out.println(next +" tested by "+ this);
					if(next.hasDisplayBranch()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @return the compontentSize
	 */
	public Size getCompontentSize() {
		return compontentSize;
	}

	/**
	 * @param compontentSize the compontentSize to set
	 */
	public void setCompontentSize(Size compontentSize) {
		this.compontentSize = compontentSize;
		this.unitComponentIcon.setDimension(compontentSize);
		notifyModelListeners();
	}



}

