/**
 * 
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import visualap.Node;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.gui.GraphPanel;
import de.danielsenff.imageflow.models.Displayable;
import de.danielsenff.imageflow.models.NodeListener;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.ParameterFactory;
import de.danielsenff.imageflow.models.unit.UnitDescription.Para;



/**
 * Factory for creating {@link UnitElement}s.
 * @author danielsenff
 *
 */
public class UnitFactory {
	
	/**
	 * Creates a {@link UnitElement} based on the given Descriptions.
	 * @param unitDescription
	 * @param origin
	 * @return
	 */
	public static UnitElement createProcessingUnit(
			final UnitDescription unitDescription, 
			final Point origin) {
		return createProcessingUnit(unitDescription, origin, null);
	}
	
	/**
	 * Creates a {@link UnitElement} based on the given Descriptions.
	 * @param unitDescription
	 * @param origin
	 * @return
	 */
	public static UnitElement createProcessingUnit(
			final UnitDescription unitDescription, 
			final Point origin, 
			String[] args) {
		
		String unitName = unitDescription.unitName;
		String imageJSyntax = unitDescription.imageJSyntax;

		
		
		// usual case, we deal with a UnitElement
		
		// if we have a SourceUnit, we have to take the according class
		UnitElement unitElement;
		if(unitName.equals("Image Source")) {
			unitElement = new SourceUnitElement(new Point(origin), unitName, imageJSyntax);
		} else if(unitName.equals("Background")) {
			unitElement = new BackgroundUnitElement(new Point(origin), unitName, imageJSyntax);
		} else 
			unitElement = new UnitElement(new Point(origin), unitName, imageJSyntax);
		
		unitElement.setHelpString(unitDescription.helpString);
		unitElement.setColor(unitDescription.color);
		
		// add an icon if there is one mentioned and found
		File iconFile = new File(unitDescription.pathToIcon);
		if(iconFile.exists()) {
			try {
				if(unitElement instanceof SourceUnitElement) {
					SourceUnitElement sourceUnit = (SourceUnitElement)unitElement;
					unitElement.setIcon(ImageIO.read(sourceUnit.getFile()));
				} else 
					unitElement.setIcon(ImageIO.read(iconFile));	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(unitDescription.icon != null) {
			unitElement.setIcon(unitDescription.icon);
			unitElement.setIconFile(unitDescription.iconFile);
			unitElement.setIconPath(unitDescription.pathToIcon);
		}

		if(unitDescription.componentSize != null)
			unitElement.setCompontentSize(unitDescription.componentSize);
		
		// setup of the Parameters
		addParameters(unitDescription, unitElement);
		
		// setup of the inputs
		addInputs(unitDescription, unitElement);
		
		// setup of the output(s)
		addOutputs(unitDescription,unitElement);
		unitElement.setDisplay(unitDescription.isDisplayUnit);
		
		// setup the PinTolerance
		setPinTolerance(unitElement);
		
		//special cases
		if(unitElement instanceof BackgroundUnitElement) {
			String imageType = (String) unitElement.getParameter(2).getValue();
			((BackgroundUnitElement)unitElement).setOutputImageType(imageType);	
		} else if(unitElement instanceof SourceUnitElement) {
			SourceUnitElement sourceUnit = (SourceUnitElement)unitElement;
			if(args != null && args[0] != null) {
				((SourceUnitElement)unitElement).setFile(args[0]);
			}
			if(!sourceUnit.hasFilePath()) {
//				sourceUnit.showIJOpenDialog();
				sourceUnit.showOpenFileChooser();	
			}
			sourceUnit.updateImageType();
		}
		
		return unitElement;
	}

	public static void createGroupUnit() {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * sets pinTolerance accordings to number of inputs and outputs
	 * @param unitElement current {@link UnitElement}, which contains the number of inputs and outputs
	 */
	private static void setPinTolerance(UnitElement unitElement) {
		// get maxiumum of pins and adjust PIN_TOLERANCE to number of pins
		int maxPins = Math.max(unitElement.inputs.size(), unitElement.outputs.size()); 
		if (maxPins > 2) {
			int pinTolerance = (100 / (2*maxPins)) - 2;
			unitElement.setPinTolerance(pinTolerance);
		}
	}
	
	private static void addParameters(final UnitDescription unitDescription,
			UnitElement unitElement) {
		int numParas = unitDescription.numParas;
		for (int i = 1; i <= numParas; i++) {
			Para para = unitDescription.para[i];
			
			unitElement.addParameter(
					ParameterFactory.createParameter(para.name, 
													para.dataTypeString,
													para.value, 
													para.helpString, 
													para.trueString, 
													para.choiceIndex));
		}
	}

	private static void addOutputs(final UnitDescription unitDescription,
			UnitElement unitElement) {
		int numOutputs = unitDescription.numOutputs;
		for (int i = 1; i <= numOutputs; i++) {
			UnitDescription.Output outputDescription = unitDescription.output[i];
			Output output = createOutput(outputDescription, unitElement, i);
			
			unitElement.addOutput(output); 
			if(unitElement instanceof Displayable) {
				boolean doDisplay = outputDescription.doDisplay;
				unitElement.setDisplay(doDisplay);
			}
		}
	}
	
	public static Output createOutput(UnitDescription.Output outputDescription, 
			UnitElement unitElement, int i) {
		String name = outputDescription.name;
		String shortName = outputDescription.shortName;
		DataType dataType = outputDescription.dataType;
		
		if(unitElement instanceof SourceUnitElement
				&& dataType instanceof DataTypeFactory.Image) {
			int imageType = ((SourceUnitElement)unitElement).getImageType();
			((DataTypeFactory.Image)dataType).setImageBitDepth(imageType);
		} 
		// imagetype -1 means output will be the same type as the input
		
		Output output = new Output(dataType, unitElement, i);
		output.setupOutput(name, shortName);
		return output;
	}
	

	private static void addInputs(final UnitDescription unitDescription,
			UnitElement unitElement) {
		int numInputs = unitDescription.numInputs;
		for (int i = 1; i <= numInputs; i++) {
			Input input = createInput(unitDescription.input[i], unitElement, i);
			unitElement.addInput(input);
		}
	}

	public static Input createInput(final UnitDescription.Input inputDescription,
			UnitElement unitElement, int i) {
		String name = inputDescription.name;
		String shortName = inputDescription.shortName;
		boolean needToCopyInput = inputDescription.needToCopyInput;
		boolean required = inputDescription.required;
		DataType dataType = inputDescription.dataType;
		if(dataType instanceof DataTypeFactory.Image) {
			((DataTypeFactory.Image)dataType).setImageBitDepth(inputDescription.imageType);
		}
		
		Input input = new Input(dataType, unitElement, i, required);
		input.setupInput(name, shortName, needToCopyInput);
		return input;
	}

	/**
	 * Creates a new {@link CommentNode}. 
	 * @param string
	 * @param point
	 * @return
	 */
	public static CommentNode createComment(final String string, final Point point) {
		CommentNode commentNode = new CommentNode(point, string);
		return commentNode;
	}
	
	
	/**
	 * Node is registered to the GraphPanel
	 * @param node
	 */
	public static void registerModelListener(Node node) {
		final ImageFlowView ifView = ((ImageFlowView)ImageFlow.getApplication().getMainView());
		final GraphPanel graphPanel = ifView.getGraphPanel();
		if(node instanceof CommentNode) {
			((CommentNode)node).addModelListener(new NodeListener(graphPanel, ifView));
		} else if (node instanceof UnitElement) 
		{
			((UnitElement)node).addModelListener(new NodeListener(graphPanel, ifView));	
		}
	}
}
