package Models.unit;
import graph.Node;
import graph.NodeAbstract;
import helper.PaintUtil;
import ij.IJ;
import ij.ImagePlus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;

import Models.Input;
import Models.MacroElement;
import Models.Output;
import Models.Parameter;
import Models.StringParameter;


/**
 * Backend-unit logic of a single node-element.
 * @author danielsenff
 *
 */
public class UnitElement extends NodeAbstract {
	
	static int ids;
	
	protected int unitID;						// the id of this unit	
	protected String unitName;       			// name of this unit (will be shown) 
	protected File iconfile;
	protected Color color;				  	// unit color
	protected String infoText;			  	// help text, describing the unit's functionality
	protected BufferedImage unitIcon;			// unitIcon on the graphpanel
	protected BufferedImage icon;			// function icon
	
	/**
	 * status of this unit
	 */
	public enum Status {OK, ERROR, WAITING };
	public enum Type {SOURCE, FILTER, SINK};
	protected Status status;
	
	
	private boolean isDisplayUnit = false;			// flag indicating if this unit is a display unit 

	// all arrays start at 1, this will make it easy to detect unconnected inputs and outputs
	protected ArrayList<Input> inputs;				// input array
	protected ArrayList<Output> outputs;				// output array
//	Parameter[] parameters;       	// parameters that control the functionality of the unit
	protected ArrayList<Parameter> parameters;



 

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
	public UnitElement  (
			final String unitName,
			final String unitsImageJSyntax,
			final int numInputs, 
			final int numOutputs, 
			final int numParameters) {
		super(new Point(30, 30), new MacroElement(unitsImageJSyntax));
		setDimension(new Dimension(100,100));
		
		
		ids++;
		this.unitID = ids;
		this.unitName = unitName;
		this.numMaxParameters = numParameters;
		this.numMaxInputs = numInputs;
		this.numMaxOutputs = numOutputs;
		
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
		
		unitIcon = new NodeIcon(this).getImage();
		
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
		Output newOutput = new Output(unitID,this.outputs.size()+1, this);
		newOutput.setupOutput(name, shortname, outputBitDepth);
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
	
	/**
	 * 
	 */
	public int getOutputsMaxCount() {
		return this.numMaxOutputs;
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
		Input newInput = new Input(this.unitID, this.inputs.size()+1, this);
		newInput.setupInput(displayName, shortDisplayName, inputImageBitDepth, needToCopyInput);
		return this.inputs.add(newInput);
	}



	/**
	 * How many {@link Input}s are actually registered.
	 * @return
	 */
	public int getInputsActualCount() {
		return this.inputs.size();
	}
	

	/**
	 * How many {@link Input}s can be registered.
	 * @return
	 */
	public int getInputsMaxCount() {
		return this.numMaxInputs;
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
	 * Returns how many {@link Parameter}s this can unit have.
	 * @return
	 */
	public int getParametersPossibleCount() {
		return this.numMaxParameters;
	}
	
	/**
	 * Returns how many assigned {@link Parameter}s this unit actually has.
	 * @return
	 */
	public int getParametersActualCount() {
		return this.parameters.size();
	}

	/**
	 * List of all {@link Parameter}s available for this unit
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
		String string = super.toString() + " Name:"+this.unitName + " Type:" + this.getType();
		return string;
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
	}
	
	public void updateUnitIcon() {
		this.unitIcon = new NodeIcon(this).getImage();
	}
	

	/**
	 * Checks if there is an input or an output at this mouse coordinates. 
	 */
	public Object contains(int x, int y) {
		int tolerance = 4;
		System.out.println(this.origin);
		System.out.println(x);
		if ((x >= origin.x - tolerance)
				&&(x < origin.x + tolerance))	{
			System.out.println("hey close");
			int inputsActualCount = getInputsActualCount();
			for (int i = 0; i < inputsActualCount; i++) {
//				int lower_y = origin.y-3 +(dimension.height*i+dimension.height/2)/inputsActualCount;
				int lower_y = PaintUtil.alignY(inputsActualCount, i, getDimension().height, NodeIcon.pinSize);
				if ((y >= lower_y)&&(y <= lower_y + tolerance*2)) {
					return getInput(i);
				}
			}
		}
		if ((x >= origin.x + getDimension().width - tolerance)
				&&(x < origin.x + getDimension().width + tolerance)) {
			int outputsCount = getOutputsMaxCount();
			for (int i = 0; i < outputsCount; i++) {
//				int lower_y = origin.y-3 +(dimension.height*i+dimension.height/2)/outputsCount;
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
			/*g.setFont(new Font("Arial", Font.PLAIN, 14));
			FontMetrics fm = g.getFontMetrics();*/
			g.setColor(selected ? Color.red : new Color(250, 220, 100));
//			dimension.setSize(fm.stringWidth(label) + 10, fm.getHeight() + 4);
			g.fillRect(origin.x, origin.y, getDimension().width, getDimension().height);
			g.setColor(Color.black);
			g.drawRect(origin.x, origin.y, getDimension().width-1, getDimension().height-1);
//			g.drawString(label, origin.x + 5, (origin.y + 2) + fm.getAscent());
		} else {
			
			// obj != null
			if (selected) {
				g.setColor(Color.red);
				g.drawRect(origin.x-2, origin.y-2, getDimension().width+4, getDimension().height+4);
			}
			g.drawImage(unitIcon, origin.x, origin.y, getDimension().width, getDimension().height, io);
		}
		
		
		//draw inputs
		int numberInputs = getInputsActualCount();
		for (int i = 0; i < numberInputs; i++) {
			g.setColor(Color.BLACK);
			int y =  PaintUtil.alignY(numberInputs, i, unitIcon.getHeight(null), NodeIcon.pinSize);
			g.fillRect(origin.x, origin.y+y, NodeIcon.pinSize, NodeIcon.pinSize);
		}

		//draw outputs
		int numberOutputs = getOutputsMaxCount();
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
			g.setColor(Color.black);
			g.drawRect(dragging.x, dragging.y, dragging.width-1, dragging.height-1);
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
	public Node clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}

