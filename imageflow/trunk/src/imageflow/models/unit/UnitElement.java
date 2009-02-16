package imageflow.models.unit;
import graph.NodeAbstract;
import helper.PaintUtil;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import imageflow.backend.Model;
import imageflow.backend.ModelListener;
import imageflow.models.Input;
import imageflow.models.MacroElement;
import imageflow.models.Output;
import imageflow.models.parameter.AbstractParameter;
import imageflow.models.parameter.BooleanParameter;
import imageflow.models.parameter.ChoiceParameter;
import imageflow.models.parameter.DoubleParameter;
import imageflow.models.parameter.FileParameter;
import imageflow.models.parameter.IntegerParameter;
import imageflow.models.parameter.Parameter;
import imageflow.models.parameter.ParameterFactory;
import imageflow.models.parameter.StringParameter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;



/**
 * Backend-unit logic of a single node-element.
 * @author danielsenff
 *
 */
public class UnitElement extends AbstractUnit {

	/**
	 * name of this unit, this is not display, Label is displayed
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
	 * function icon
	 */
	protected BufferedImage icon; 

	/**
	 * status of this unit
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


	private boolean isDisplayUnit = false;			// flag indicating if this unit is a display unit 

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
	 * Maximum number of Parameters, that can be added to this UnitElement.
	 */
	//	protected int numMaxParameters;
	/**
	 * Maximum number of {@link Input}s, that can be added to this UnitElement.
	 */
	//	protected int numMaxInputs;
	/**
	 * Maximum number of {@link Output}s, that can be added to this UnitElement.
	 */
	//	protected int numMaxOutputs;

	private int FIRST_ELEMENT = 0;




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
	}



	/**
	 * for source units only
	 * @return
	 */
	public int getBitDepth() {
		final String path = ((StringParameter)parameters.get(0)).getValue();
		System.out.println("path of initial image " +path);
		final ImagePlus imp = IJ.openImage(path);
		imp.close();
		final int bitDepth = imp.getBitDepth();
		return bitDepth;
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
		return this.outputs.get(index);
	}

	/**
	 * Returns a list of all {@link Output}s
	 * @return
	 */
	public ArrayList<Output> getOutputs() {
		return this.outputs;
	}


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
	 * @return 
	 */
	public boolean addInput(String displayName, 
			String shortDisplayName, 
			int inputImageBitDepth, 
			boolean needToCopyInput) {
		Input newInput = new Input(this, this.inputs.size()+1);
		newInput.setupInput(displayName, shortDisplayName, inputImageBitDepth, needToCopyInput);
		notifyModelListeners();
		return this.inputs.add(newInput);
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
		if(outputs.size() > 1)
			outputs.get(FIRST_ELEMENT ).setDoDisplay(isDisplayUnit);
		notifyModelListeners();
	}

	/**
	 * Returns whether or not this unit should display the current state of the image.
	 * @return 
	 */
	public boolean isDisplayUnit() {
		return isDisplayUnit;
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
	 * Returns the what type this is. 
	 * SOURCE - only {@link Output}
	 * SINK - only {@link Input}
	 * FILTER - {@link Input} and {@link Output}
	 * @return
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
		return super.toString() + " Name:"+this.label + " Type:" + this.getType();
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

	public void updateUnitIcon() {
		this.unitIcon = new NodeIcon(this).getImage();
	}


	/**
	 * Checks if there is an input or an output at this mouse coordinates. 
	 */
	@Override
	public Object contains(final int x, final int y) {
		int tolerance = 8;
		if ((x >= origin.x - tolerance)
				&&(x < origin.x + tolerance))	{
			int inputsMaxCount = getInputsCount();
			for (int i = 0; i < inputsMaxCount; i++) {
				int lower_y = PaintUtil.alignY(inputsMaxCount, i, getDimension().height, NodeIcon.pinSize)+origin.y;
				if ((y >= lower_y)&&(y <= lower_y + tolerance*2)) {
					return getInput(i);
				}
			}
		}
		if ((x >= origin.x + getDimension().width - tolerance)
				&&(x < origin.x + getDimension().width + tolerance)) {
			int outputsCount = getOutputsCount();
			for (int i = 0; i < outputsCount; i++) {
				int lower_y = PaintUtil.alignY(outputsCount, i, getDimension().height, NodeIcon.pinSize)+origin.y;
				if ((y >= lower_y)&&(y <= lower_y + tolerance*2)) {
					return getOutput(i);
				}
			}
		}
		return super.contains(x,y);
	}


	/* (non-Javadoc)
	 * @see graph.Node#paint(java.awt.Graphics, java.awt.image.ImageObserver)
	 */
	@Override
	public Rectangle paint(Graphics g, ImageObserver io) {
		Color saveColor = g.getColor();
		Font saveFont = g.getFont();
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
			unitComponentIcon.paintBigIcon((Graphics2D) g);
		}


		//draw inputs
		int numberInputs = getInputsCount();
		for (int i = 0; i < numberInputs; i++) {
			g.setColor(Color.BLACK);
			int y =  PaintUtil.alignY(numberInputs, i, unitIcon.getHeight(null), NodeIcon.pinSize);
			g.fillRect(origin.x, origin.y+y, NodeIcon.pinSize, NodeIcon.pinSize);
		}

		//draw outputs
		int numberOutputs = getOutputsCount();
		for (int i = 0; i < numberOutputs; i++) {
			g.setColor(Color.BLACK);

			int x = (unitIcon.getWidth(null) - 8) + origin.x;
			int y = PaintUtil.alignY(numberOutputs, i, unitIcon.getHeight(null), NodeIcon.pinSize)+origin.y;

			Polygon po=new Polygon(); 
			//			System.out.println(" x"+x+" y"+y);
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
	public void setObject(Object obj) {
		this.obj = obj;
	}



	/* (non-Javadoc)
	 * @see graph.Node#clone()
	 */
	@Override
	public UnitElement clone() {
		System.out.println(this.obj);
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
			clone.addParameter(
					ParameterFactory.createParameter(parameter.getDisplayName(), 
							parameter.getValue(), parameter.getHelpString()));
		}
		clone.setDisplayUnit(this.isDisplayUnit);
		clone.setColor(this.color);
		clone.setIcon(this.icon);
		return clone;
	}

	private Object cloneNonClonableObject(Object obj) throws CloneNotSupportedException {
		Object clobj;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.close();
			clobj = (new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))).readObject();
		} catch (Exception ex) {
			throw new CloneNotSupportedException(ex.getMessage());
		}
		return clobj;
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
	 * @param unit
	 * @return
	 */
	public boolean hasAllInputsMarked() {
		boolean hasAllMarked = true;

		if(getInputsCount() > 0) {
			// check each input, if it's parent has been registered
			for (Input input : getInputs()) {
				if(input.isConnected()) {
					int mark = input.getFromUnit().getOutput(0).getMark();
					// if mark is not set
					if(mark == 0) {
						// this connected output hasn't been registered and is missing a mark, 
						// so the whole unit isn't ready set. 
						return false;
					} 
					// else mark is already set, so this output is fine
				} else {
					// since something must be missing, it is set to be false
					return false;
				}

			}

		} else {
			// if there are no inputs, it's true
			return true;
		}

		return hasAllMarked;
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
	 * Is true as soon as it finds one connected {@link Input}.
	 * @return
	 */
	public boolean hasInputsConnected() {
		for (final Input input : inputs) {
			if(input.isConnected())
				return true;
		}
		return false;
	}


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
		gd.addMessage(label);
		gd.addMessage(" ");


		// label field 
		gd.addStringField("Unit label", this.getLabel(),40);

		final ArrayList<Parameter> parameterList = getParameters();

		if (parameterList.isEmpty())
			gd.addMessage("No parameters that can be set");
		else if(parameterList.get(0) instanceof FileParameter) {
			// TODO open filechooser

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
					gd.addStringField(parameter.getDisplayName(), (String)parameter.getValue(),40);
				}		
			}

		}

		// show properties window
		gd.showDialog();

		if( gd.wasCanceled())
			return;

		String newLabel = (String) (gd.getNextString()).trim();
		setLabel(newLabel);

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
	 * returns the color of the unit.
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
	 * @return the infoText
	 */
	public String getHelpString() {
		return infoText;
	}

	public void setHelpString(String helpString) {
		this.infoText = helpString;		
	}


}

