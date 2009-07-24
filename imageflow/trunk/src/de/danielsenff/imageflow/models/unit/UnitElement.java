package de.danielsenff.imageflow.models.unit;
import imagej.GenericDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;

import visualap.Node;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.Displayable;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.parameter.AbstractParameter;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.parameter.DoubleParameter;
import de.danielsenff.imageflow.models.parameter.IntegerParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.ParameterFactory;
import de.danielsenff.imageflow.models.parameter.StringParameter;
import de.danielsenff.imageflow.models.parameter.TextParameter;
import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;
import de.danielsenff.imageflow.utils.PaintUtil;



/**
 * Backend-unit logic of a single node-element.
 * Model for an instance of a workflow-element.
 * This model is a basic model for an element in the graph and 
 * contains most of the graph logic.
 * 
 * Next to meta data, it basically it contains 3 major parts.
 * 3 Lists with {@link Input}s, {@link Output}s and {@link Input}.
 * 
 * In the heart of the model is an Macro-Object, which
 * contains the syntax, in which the parameters injected on runtime
 * and which is executed by ImageJ.
 * @author danielsenff
 *
 */
public class UnitElement extends AbstractUnit implements ProcessingUnit, Displayable {

	/**
	 * Pixel radius of tolerance round the pins  
	 */
	private int pinTolerance = 18;

	/**
	 * name of this unit, this is not displayed, Label is displayed
	 */
	protected String unitName;    
	protected File iconFile;
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
	protected Image unitIcon;
	protected NodeIcon unitComponentIcon;
	/**
	 * function icon, illustrates the purpose of the element
	 */
	protected Image icon; 

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

	private String iconPath;


	/**
	 * @param unitName
	 * @param unitsImageJSyntax
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
	 */
	private void init(final String unitName) {

		this.label = unitName;
		this.unitName = unitName;

		this.inputs = new ArrayList<Input>();
		this.outputs = new ArrayList<Output>();
		this.parameters = new ArrayList<Parameter>();

		this.unitComponentIcon= new NodeIcon(this);
		this.unitIcon = unitComponentIcon.getImage();

		setCompontentSize(Size.BIG);
	}

	/*
	 * Outputs
	 */

	/**
	 * Gets the pinTolerance of this {@link UnitElement}
	 */
	public int getPinTolerance() {
		return pinTolerance;
	}

	/**
	 * Sets the pinTolerance of this {@link UnitElement}
	 * @param pinTolerance Pixel radius of PinTolerance
	 */
	public void setPinTolerance(int pinTolerance) {
		this.pinTolerance = pinTolerance;
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




	/*
	 * Inputs
	 */

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
	 * Add new Input by Object.
	 * @param input
	 * @return
	 */
	public boolean addInput(final Input input) {
		notifyModelListeners();
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
	 * Returns the typename of this unit. This is different to the
	 * name displayed in the workflow.
	 * @return the unitName
	 */
	public String getUnitName() {
		return this.unitName;
	}




	@Override
	public void drag(int dx, int dy) {
		super.drag(dx, dy);
		notifyModelListeners();
	}




	/*
	 * Parameters
	 */


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
	 * Displays a Popup-Window with the properties, that can be edited for this UnitElement.
	 */
	public void showProperties() {
		final GenericDialog gd = new GenericDialog(
				getLabel() + " - Parameters", 
				ImageFlow.getApplication().getMainFrame()) ;
		if(getHelpString() != null)
			gd.addMessage(getHelpString());
		
		// label field 
		gd.addStringField("Unit label", this.getLabel(),40);
		gd.addCheckbox("Display", this.isDisplay());

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
					//					gd.addTextAreas(parameter.getDisplayName(), (String)parameter.getValue(), 4, 40);
				} else if(parameter instanceof TextParameter) {
					gd.addTextField(parameter.getDisplayName(), (String)parameter.getValue(), 40);
					//					gd.addTextAreas(parameter.getDisplayName(), (String)parameter.getValue(), 4, 40);
				} 				
			}
		}
		
		if(hasInputs()) {
			gd.addMessage("Inputs");
			for (Input input : getInputs()) {
				if(input.isConnected())
					gd.addMessage(input.getDisplayName() + " of type " + input.getDataType().getClass().getSimpleName()+ " connected to " + input.getConnection().getFromUnit().getLabel());
				else
					gd.addMessage(input.getDisplayName() + " of type " + input.getDataType().getClass().getSimpleName()+ " is not connected.");
			}
		}
		if(hasOutputs()) {			
			gd.addMessage("Outputs");
			for (Output output : getOutputs()) {
				if(output.isConnected())
					for (Connection conn : output.getConnections()) {
						gd.addMessage(output.getDisplayName() + " of type " + output.getDataType().getClass().getSimpleName()+" connected to " + conn.getToUnit().getLabel());
					}
				else
					gd.addMessage(output.getDisplayName() + " of type " + output.getDataType().getClass().getSimpleName() + " is not connected.");
			}
		}
		

		// show properties window
		gd.showDialog();

		if( gd.wasCanceled())
			return;

		String newLabel = (String) (gd.getNextString()).trim();
		setLabel(newLabel);
		boolean isNewDisplay = gd.getNextBoolean();
		setDisplay(isNewDisplay);

		for (final Parameter parameter : parameterList) {
			if(parameter instanceof DoubleParameter) {
				((DoubleParameter) parameter).setValue((double) (gd.getNextNumber()));
			} else if (parameter instanceof IntegerParameter) {
				((IntegerParameter) parameter).setValue((int) (gd.getNextNumber()));
			} else if (parameter instanceof BooleanParameter) {
				((BooleanParameter) parameter).setValue((boolean) (gd.getNextBoolean()));
			} else if (parameter instanceof ChoiceParameter) {
				((ChoiceParameter) parameter).setValue((String) (gd.getNextChoice()));
				// set the ChoiceNumber to be able to save it
			} else if (parameter instanceof StringParameter) {
				String newString = (String) (gd.getNextString()).trim();
				((StringParameter) parameter).setValue(newString);
			} 
		}	

		notifyModelListeners();
	}

	/**
	 * Returns whether this unit has parameters or not.
	 * This doesn't concern the unit label.
	 * @return
	 */
	public boolean hasParameters() {
		return !parameters.isEmpty();
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
	 * Returns the path to the icon.
	 * @return
	 */
	public String getIconPath() {
		if(this.iconPath !=null)
			return this.iconPath;
		else 
			return "";
	}

	/**
	 * Returns the {@link File} of this unit's icon.
	 * @return
	 */
	public File getIconFile() {
		return this.iconFile;
	}


	public void setIconFile(File iconFile) {
		this.iconFile = iconFile; 
	}

	public void setIconPath(String pathToIcon) {
		this.iconPath = pathToIcon;
	}

	/**
	 * @param image the icon to set
	 */
	public void setIcon(Image image) {
		this.icon = image;
		this.unitComponentIcon.setIcon(image);
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
		if ((x >= origin.x - pinTolerance)
				&&(x < origin.x + pinTolerance))	{
			int inputsMaxCount = getInputsCount();
			for (int i = 0; i < inputsMaxCount; i++) {
				int lower_y = PaintUtil.alignY(inputsMaxCount, i, getDimension().height, NodeIcon.pinSize)+origin.y;
				if ((y >= lower_y)&&(y <= lower_y + pinTolerance*2)) {
					return getInput(i);
				}
			}
		}
		if ((x >= origin.x + getDimension().width - pinTolerance)
				&&(x < origin.x + getDimension().width + pinTolerance)) {
			int outputsCount = getOutputsCount();
			for (int i = 0; i < outputsCount; i++) {
				int lower_y = PaintUtil.alignY(outputsCount, i, getDimension().height, NodeIcon.pinSize)+origin.y;
				if ((y >= lower_y)&&(y <= lower_y + pinTolerance*2)) {
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

			if (selected) {
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

			if(getInput(i).isRequired())
				g.setColor(Color.BLACK);
			else
				g.setColor(new Color(0,0,0,60));

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


	/*
	 * Object
	 */



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
		for (int j = 0; j < getInputsCount(); j++) {
			Input input = getInput(j);				
			Input clonedInput = new Input(input.getDataType().clone(), clone, j+1, input.isRequired());
			clonedInput.setupInput(input.getName(), input.getShortDisplayName(), input.isNeedToCopyInput());
			clone.addInput(clonedInput);
		}
		for (int i = 0; i < getOutputsCount(); i++) {
			Output output = getOutput(i);
			Output clonedOutput = new Output(output.getDataType().clone(), clone, i+1);
			clonedOutput.setupOutput(output.getName(), output.getShortDisplayName());
			clonedOutput.setDoDisplay(output.isDoDisplay());
			clone.addOutput(clonedOutput);
		}
		for (Parameter parameter : parameters) {
			Parameter clonedParameter;
			if(parameter instanceof ChoiceParameter) {
				clonedParameter =	ParameterFactory.createParameter(parameter.getDisplayName(), 
						parameter.getParaType(),
						parameter.getValue(), 
						parameter.getHelpString(), null, 
						((ChoiceParameter)parameter).getChoiceIndex());
			} else if (parameter instanceof BooleanParameter){
				clonedParameter =	ParameterFactory.createParameter(parameter.getDisplayName(), 
						parameter.getParaType(),
						parameter.getValue(), 
						parameter.getHelpString(), 
						((BooleanParameter)parameter).getTrueString(), 0);
			} else {
				clonedParameter =	ParameterFactory.createParameter(parameter.getDisplayName(), 
						parameter.getParaType(),
						parameter.getValue(), 
						parameter.getHelpString());	
			}
			clone.addParameter(parameter);
		}
		clone.setDisplay(isDisplay());
		clone.setColor(this.color);
		clone.setIcon(this.icon);
		clone.setHelpString(this.infoText);
		return clone;
	}



	/*
	 * Marking
	 */

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
	 * TODO: this method name may be confusing: it doesn't check if all inputs of this unit are marked
	 * @param unit
	 * @return
	 */
	public boolean hasAllInputsMarked() {

		if(hasInputs()) {
			// check each input, if it's parent has been registered
			for (Input input : getInputs()) {
				if( (input.isConnected() 
						&& !input.getFromOutput().isMarked())
						|| !input.isConnected() && input.isRequired() ) {
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

	/**
	 * Returns true, if the unit in question is positioned north of this unit.
	 * @param node
	 * @return
	 */
	public boolean isNorthOfUnit(Node node) {
		return (node.getOrigin().y > this.getOrigin().y) ? true : false;
	}

	/**
	 * Returns true, if the unit in question is positioned north of this unit.
	 * @param node
	 * @return
	 */
	public boolean isEastOfUnit(Node node) {
		return (node.getOrigin().x < this.getOrigin().x) ? true : false;
	}

	/**
	 * Returns true, if the unit in question is positioned north of this unit.
	 * @param node
	 * @return
	 */
	public boolean isSouthOfUnit(Node node) {
		return (node.getOrigin().y < this.getOrigin().y) ? true : false;
	}

	/**
	 * Returns true, if the unit in question is positioned north of this unit.
	 * @param node
	 * @return
	 */
	public boolean isWestOfUnit(Node node) {
		return (node.getOrigin().x > this.getOrigin().x) ? true : false;
	}


	/*
	 * Displayable
	 */

	/**
	 * boolean indicating if this unit is a display unit
	 * The result of DisplayUnits will be shown after executing the workflow.  
	 */
	protected boolean display = false;  
	
	
	/**
	 * If activated, the unit will display the current image.
	 * This setting is actually attached to the {@link Output}. 
	 * This is a convenience method for changing all outputs of this
	 * unit at once.
	 * @param isDisplayUnit
	 */
	public void setDisplay(final boolean isDisplay) {
		this.display = isDisplay;
		for (Output output : getOutputs()) {
			output.setDoDisplay(isDisplay);
		}
		notifyModelListeners();
	}

	/**
	 * Returns true if any {@link Output} connects to a unit that is set as displayable.
	 * @return
	 */
	public boolean hasDisplayBranch() {
		if(isDisplay())
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
	 * Returns whether or not this unit should display the current state of the image.
	 * @return 
	 */
	public boolean isDisplay() {
		return this.display;
	}


}

