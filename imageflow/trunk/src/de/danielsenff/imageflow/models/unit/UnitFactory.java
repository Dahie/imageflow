/**
 * 
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import visualap.Node;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.gui.GraphPanel;
import de.danielsenff.imageflow.models.Model;
import de.danielsenff.imageflow.models.ModelListener;
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
		
		String unitName = unitDescription.unitName;
		String imageJSyntax = unitDescription.imageJSyntax;
		Color color = unitDescription.color;
		
		// usual case, we deal with a UnitElement
		
		// if we have a SourceUnit, we have to take the according class
		UnitElement unitElement;
		if(unitName.equals("Image Source")) {
			unitElement = new SourceUnitElement(new Point(origin), unitName, imageJSyntax);
		} else if(unitName.equals("Background")) {
			unitElement = new BackgroundUnitElement(new Point(origin), unitName, imageJSyntax);
		} else 
			unitElement = new UnitElement(new Point(origin), unitName, imageJSyntax);
		
		
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

		unitElement.setColor(color);
		if(unitDescription.icon != null) {
			unitElement.setIcon(unitDescription.icon);
			unitElement.setIconFile(unitDescription.iconFile);
			unitElement.setIconPath(unitDescription.pathToIcon);
		}
			
		if(unitDescription.componentSize != null)
			unitElement.setCompontentSize(unitDescription.componentSize);
		unitElement.setHelpString(unitDescription.helpString);
		
		// setup of the Parameters
		addParameters(unitDescription, unitElement);
		
		// setup of the inputs
		addInputs(unitDescription, unitElement);
		
		// setup of the output(s)
		addOutputs(unitDescription,unitElement);
		
		
		//special cases
		if(unitElement instanceof BackgroundUnitElement) {
			String imageType = (String) unitElement.getParameter(2).getValue();
			((BackgroundUnitElement)unitElement).setOutputImageType(imageType);	
		} else if(unitElement instanceof SourceUnitElement) {
			SourceUnitElement sourceUnit = (SourceUnitElement)unitElement;
			if(!sourceUnit.hasFilePath()) {
//				sourceUnit.showIJOpenDialog();
				sourceUnit.showOpenFileChooser();	
			}
			sourceUnit.updateImageType();
		}
		
		// in case we want to create a unit without an running application
		// eg for testing
		if(ImageFlow.getApplication() != null) 
			registerModelListener(unitElement);
		
		return unitElement;
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
			String name = outputDescription.name;
			String shortName = outputDescription.shortName;
			boolean doDisplay = outputDescription.doDisplay;
			DataType dataType = outputDescription.dataType;
			
			if(unitElement instanceof SourceUnitElement
					&& dataType instanceof DataTypeFactory.Image) {
				int imageType = ((SourceUnitElement)unitElement).getImageType();
				((DataTypeFactory.Image)dataType).setImageBitDepth(imageType);
			} 
			// imagetype -1 means output will be the same type as the input
			
			Output output = new Output(dataType, unitElement, i);
			output.setupOutput(name, shortName);
			
			unitElement.addOutput(output); 
			unitElement.setDisplayUnit(doDisplay);
		}
	}

	private static void addInputs(final UnitDescription unitDescription,
			UnitElement unitElement) {
		int numInputs = unitDescription.numInputs;
		for (int i = 1; i <= numInputs; i++) {
			UnitDescription.Input inputDescription = unitDescription.input[i];
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
			
			unitElement.addInput(input);
		}
	}

	/**
	 * Creates a new {@link CommentNode}. 
	 * @param string
	 * @param point
	 * @return
	 */
	public static CommentNode createComment(final String string, final Point point) {
		CommentNode commentNode = new CommentNode(point, string);
		registerModelListener(commentNode);
		return commentNode;
	}
	
	

	public static void registerModelListener(Node node) {
		final ImageFlowView ifView = ((ImageFlowView)ImageFlow.getApplication().getMainView());
		final GraphPanel graphPanel = ifView.getGraphPanel();
		if(node instanceof CommentNode) {
			((CommentNode)node).addModelListener(
					new ModelListener () {
						public void modelChanged (final Model hitModel)	{
							graphPanel.invalidate();
							graphPanel.repaint();
							ifView.setModified(true);
						}
					});	
		} else //if(node instanceof UnitElement) 
		{
			((UnitElement)node).addModelListener(
					new ModelListener () {
						public void modelChanged (final Model hitModel)	{
							graphPanel.invalidate();
							graphPanel.repaint();
							ifView.setModified(true);
						}
					});	
		}
	}
}
