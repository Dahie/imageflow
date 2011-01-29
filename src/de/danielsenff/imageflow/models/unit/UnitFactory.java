/**
 * Copyright (C) 2008-2010 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
import de.danielsenff.imageflow.models.datatype.ImageDataType;
import de.danielsenff.imageflow.models.delegates.NodeDescription;
import de.danielsenff.imageflow.models.delegates.UnitDescription;
import de.danielsenff.imageflow.models.delegates.UnitDescription.Para;
import de.danielsenff.imageflow.models.parameter.ParameterFactory;



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
			final NodeDescription unitDescription, 
			final Point origin) {
		return createProcessingUnit(unitDescription, origin, null);
	}
	
	/**
	 * Creates a {@link UnitElement} based on the given Descriptions.
	 * @param nodeDescription 
	 * @param unitDescription
	 * @param origin
	 * @param args 
	 * @return
	 */
	public static UnitElement createProcessingUnit(
			final NodeDescription nodeDescription, 
			final Point origin, 
			String[] args) {
		
		UnitElement unitElement = null;
		
		if(nodeDescription instanceof UnitDescription) {
			UnitDescription unitDescription = (UnitDescription)nodeDescription;
			unitElement = manufactureUnitElement(origin, args, unitDescription);
		}
		return unitElement;
	}

	private static UnitElement manufactureUnitElement(final Point origin,
			String[] args, UnitDescription unitDescription) {
		UnitElement unitElement;
		String unitName = unitDescription.getUnitName();
		String imageJSyntax = unitDescription.imageJSyntax;

		// usual case, we deal with a UnitElement

		// if we have a SourceUnit, we have to take the according class
		if(unitName.equals("Import from Window")) {
			unitElement = new ImportUnitElement(new Point(origin), unitName, imageJSyntax);
		} else if(unitName.equals("Image Source")) {
			unitElement = new SourceUnitElement(new Point(origin), unitName, imageJSyntax);
		} else if(unitName.equals("Background")) {
			unitElement = new BackgroundUnitElement(new Point(origin), unitName, imageJSyntax);
		} else 
			unitElement = new UnitElement(new Point(origin), unitName, imageJSyntax);

		unitElement.setHelpString(unitDescription.helpString);
		unitElement.setColor(unitDescription.getColor());

		// add an icon if there is one mentioned and found
		File iconFile = new File(unitDescription.pathToIcon);
		if(iconFile.exists()) {
			try {
				unitElement.setIcon(ImageIO.read(iconFile));	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(unitDescription.icon != null) {
			unitElement.setIcon(unitDescription.icon);
			unitElement.setIconURL(unitDescription.iconURL);
		}

		if(unitDescription.componentSize != null)
			unitElement.setCompontentSize(unitDescription.componentSize);

		// setup of the Parameters
		if(unitDescription.hasParameters())
			addParameters(unitDescription, unitElement);

		// setup of the inputs
		if(unitDescription.hasInputs())
			addInputs(unitDescription, unitElement);

		// setup of the output(s)
		if(unitDescription.hasOutputs())
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
				((SourceUnitElement)unitElement).setFilePath(args[0]);
			}
			if(!sourceUnit.hasFilePath()) {
				sourceUnit.showOpenFileChooser();	
			}
			sourceUnit.updateImageType();
		}
		return unitElement;
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
													para.options));
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
	
	/**
	 * Create a {@link IOutput} for the given {@link UnitElement}.
	 * @param outputDescription
	 * @param unitElement
	 * @param i
	 * @return
	 */
	public static Output createOutput(UnitDescription.Output outputDescription, 
			UnitElement unitElement, int i) {
		String name = outputDescription.name;
		String shortName = outputDescription.shortName;
		DataType dataType = outputDescription.dataType;
		
		if(unitElement instanceof ImageSourceUnit
				&& dataType instanceof ImageDataType) {
			int imageType = ((ImageSourceUnit)unitElement).getImageType();
			((ImageDataType)dataType).setImageBitDepth(imageType);
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

	/**
	 * Create a {@link Input} for the given {@link UnitElement}.
	 * @param inputDescription
	 * @param unitElement
	 * @param i
	 * @return
	 */
	public static Input createInput(final UnitDescription.Input inputDescription,
			final UnitElement unitElement, final int i) {
		String name = inputDescription.name;
		String shortName = inputDescription.shortName;
		boolean needToCopyInput = inputDescription.needToCopyInput;
		boolean required = inputDescription.required;
		DataType dataType = inputDescription.dataType;
		if(dataType instanceof ImageDataType) {
			((ImageDataType)dataType).setImageBitDepth(inputDescription.imageType);
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
		// if we ever go multi-document, this will have to be addressed here
		final ImageFlowView ifView = ((ImageFlowView)ImageFlow.getApplication().getMainView());
		final GraphPanel graphPanel = ifView.getGraphPanel();
		if(node instanceof CommentNode) {
			((CommentNode)node).addModelListener(new NodeListener(graphPanel, ifView));
		} else if (node instanceof UnitElement) {
			((UnitElement)node).addModelListener(new NodeListener(graphPanel, ifView));	
		}
	}
}
