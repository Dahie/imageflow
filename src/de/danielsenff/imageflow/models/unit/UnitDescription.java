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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jdom.Element;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;
import de.danielsenff.imageflow.utils.Tools;


/**
 * UnitDescription reads the single pieces a UnitElement is made of from an
 * XML-file. The {@link UnitFactory} can construct an {@link UnitElement}-Instance based
 * on this description.
 * @author Daniel Senff
 *
 */
public class UnitDescription implements NodeDescription {

	public UnitDescription() {
	}

	protected String unitName;
	public String helpString;
	protected String pathToIcon;
	protected String colorString;
	protected String componentSizeString;
	protected Size componentSize;
	protected Color color;
	protected String imageJSyntax;
	protected int    argbDefault =  (0xFF<<24)|(128<<16)|(128<<8)|128;

	protected int numParas;
	protected Para[] para;

	protected int numInputs;
	protected Input[] input;

	protected int numOutputs;
	protected Output[] output;
	protected boolean isDisplayUnit;
	protected BufferedImage icon = null;
	protected URL iconURL;
	
	
	public UnitDescription(URL url) {
		this(url, Tools.getXMLRoot(url));
	}
	
	public UnitDescription(URL url, Element root) {
		try {
			// read general infos about this unit
			Element elementGeneral = root.getChild("General");
			unitName = elementGeneral.getChild("UnitName").getValue();
			pathToIcon = elementGeneral.getChild("PathToIcon").getValue();
			helpString = elementGeneral.getChild("HelpString").getValue();
			colorString = elementGeneral.getChild("Color").getValue();
			if(elementGeneral.getChild("DoDisplay") != null)
				isDisplayUnit = elementGeneral.getChild("DoDisplay").getValue().equalsIgnoreCase("true") ? true : false;
			
			try {
				color = Color.decode(colorString);
			} catch (NumberFormatException e) {
				System.out.println("Wrong color string ");
			}

			if (color == null)
				color = new Color(argbDefault);

			if(elementGeneral.getChild("IconSize") != null) {
				componentSizeString = elementGeneral.getChild("IconSize").getValue();
				componentSize = NodeIcon.getSizeFromString(componentSizeString);
			}
		    
			imageJSyntax = elementGeneral.getChild("ImageJSyntax").getValue() + "\n";

			try {
				// get icon
				iconURL = getIconURL(url, pathToIcon);
				icon = ImageIO.read(iconURL.openStream());
			}
			catch (Exception e) {
				// no exception handling is needed here, since it is often
				// the case that icons are missing. Most of the units are
				// not even intended to have icons.
			}
			
			// parameters
			Element parametersElement = root.getChild("Parameters");
			if (parametersElement != null) {
				processParameter(parametersElement);
			}

			// Inputs
			Element inputsElement = root.getChild("Inputs");
			if (inputsElement != null) {
				processInputs(inputsElement);
			}

			// Outputs
			Element outputsElement = root.getChild("Outputs");
			if (outputsElement != null) {
				processOutputs(outputsElement);
			}
			
		}

		catch (Exception e) {
			System.err.println("Invalid XML-File!");
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"There has been a problem loading a XML unit description." +'\n' 
					+ "The error ocures in " + url + "." + '\n'
					+ "The programm start will continue without this unit."
					,
					"Missing connections", 
					JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}


	/**
	 * @param parametersElement
	 * @throws Exception
	 */
	private void processParameter(Element parametersElement) throws Exception {
		List<Element> parametersList = parametersElement.getChildren();
		Iterator<Element> parametersIterator = parametersList.iterator();
		
		numParas = parametersList.size();
		para = new Para[numParas + 1];

		// loop for all Parameter
		int num = 1;
		while (parametersIterator.hasNext()) {
			Element actualParameterElement = (Element) parametersIterator.next();
			
			processParameters(num, actualParameterElement);
			num++;
		}
	}


	/**
	 * process a single input
	 * @param inputsElement
	 */
	private void processInputs(Element inputsElement) {
		List<Element> inputsList = inputsElement.getChildren();
		Iterator<Element> inputsIterator = inputsList.iterator();
		
		numInputs = inputsList.size();
		input = new Input[numInputs+1];
		// loop over all Inputs
		int num = 1;
		while (inputsIterator.hasNext()) {
			Element inputElement = (Element) inputsIterator.next();
			
			Input actInput = input[num] = new Input();
			actInput.name = inputElement.getChild("Name").getValue();
			if(inputElement.getChild("Required") != null)
				actInput.required = inputElement.getChild("Required").getValue().equalsIgnoreCase("true") ? true : false;
			actInput.shortName = inputElement.getChild("ShortName").getValue();
			
			if(inputElement.getChild("Required") != null)
				actInput.required = inputElement.getChild("Required").getValue().equalsIgnoreCase("true") ? true : false;
			
			
			// legacy: in case no type is given, assume DataTypeFactory.Image
			if(inputElement.getChild("DataType") != null) {
				actInput.dataType = DataTypeFactory.createDataType(inputElement.getChild("DataType").getValue());	
			} else 
				actInput.dataType = DataTypeFactory.createDataType("Image");
			
			actInput.needToCopyInput = inputElement.getChild("NeedToCopyInput").getValue().equalsIgnoreCase("true") ? true : false;
			
			if(actInput.dataType instanceof DataTypeFactory.Image) {
				int imageType = Integer.valueOf(inputElement.getChild("ImageType").getValue());
				actInput.imageType = imageType;
				((DataTypeFactory.Image)actInput.dataType).setImageBitDepth(imageType);
			}
					
			num++;
		}
	}


	/**
	 * @param outputsElement
	 */
	private void processOutputs(Element outputsElement) {
		List<Element> outputsList = outputsElement.getChildren();
		Iterator<Element> outputIterator = outputsList.iterator();
		numOutputs = outputsList.size();
		output = new Output[outputsList.size()+1];
		// loop over all Inputs
		int num = 1;
		while (outputIterator.hasNext()) {
			Element outputElement = (Element) outputIterator.next();
			Output actOutput = output[num] = new Output();
			actOutput.name = outputElement.getChild("Name").getValue();
			actOutput.shortName = outputElement.getChild("ShortName").getValue();
			
			// legacy: in case no type is given, assume DataTypeFactory.Image
			if(outputElement.getChild("DataType") != null) {
				actOutput.dataType = DataTypeFactory.createDataType(outputElement.getChild("DataType").getValue());
			} else {
				actOutput.dataType = DataTypeFactory.createDataType("Image");
			}
			
			if(actOutput.dataType instanceof DataTypeFactory.Image) {
				int imageType = Integer.valueOf(outputElement.getChild("ImageType").getValue());
				actOutput.imageType = imageType;
				((DataTypeFactory.Image)actOutput.dataType).setImageBitDepth(imageType);
			}
			
			
			actOutput.doDisplay = outputElement.getChild("DoDisplay").getValue().equalsIgnoreCase("true")? true : false;
			isDisplayUnit = actOutput.doDisplay;
			num++;
		}
	}


	private void processParameters(int num, Element actualParameterElement)
			throws Exception {
		Para actPara = para[num] = new Para();
		
		actPara.name = actualParameterElement.getChild("Name").getValue();
		if(actualParameterElement.getChild("HelpString") != null)
			actPara.helpString = actualParameterElement.getChild("HelpString").getValue();
		else
			actPara.helpString = actualParameterElement.getChild("Name").getValue();
		String dataTypeString = actPara.dataTypeString = actualParameterElement.getChild("DataType").getValue();
		String valueString = actualParameterElement.getChild("Value").getValue();
		
		if (dataTypeString.toLowerCase().equals("double")) 
			actPara.value = Double.valueOf(valueString);
		else if (dataTypeString.toLowerCase().equals("file"))
			actPara.value = valueString;
		else if (dataTypeString.toLowerCase().equals("string")) 
			actPara.value = valueString;
		else if (dataTypeString.toLowerCase().equals("text")) 
			actPara.value = valueString;
		else if (dataTypeString.toLowerCase().equals("integer")) 
			actPara.value = Integer.valueOf(valueString);
		else if (dataTypeString.toLowerCase().equals("stringarray")) { 
			int choiceNumber = Integer.valueOf(actualParameterElement.getChild("ChoiceNumber").getValue());
			String[] strings = valueString.split(ChoiceParameter.DELIMITER);
			ArrayList<String> choicesList;
			if(strings.length > 1) {
				choicesList = new ArrayList<String>(strings.length);
				for (int i = 0; i < strings.length; i++) {
					choicesList.add(strings[i]);
				}	
			} else {
				// in the first beta the delimiter was a space, 
				// so to be able to read old workflows, this construct exists
				strings = valueString.split(" ");
				choicesList = new ArrayList<String>(strings.length);
				for (int i = 0; i < strings.length; i++) {
					choicesList.add(strings[i]);
				}
			}
			
			actPara.value = choicesList;
			actPara.choiceIndex = Integer.valueOf(choiceNumber);
		}
		else if (dataTypeString.toLowerCase().equals("boolean")) {
			actPara.value = Boolean.valueOf(valueString);
			actPara.trueString = actualParameterElement.getChild("TrueString").getValue();
		} else 
			throw new Exception("invalid datatype");
	}

	/**
	 * Creates a new URL based on a given context URL.
	 * If a path is given, the new URL is dependent of the protocol
	 * of the context URL. If not, the context URL with an extension
	 * replacement from "xml" to "png" is returned.
	 */
	private URL getIconURL(URL context, String relativeIconPath) throws MalformedURLException {
		String path;
		if(relativeIconPath != null && relativeIconPath.length() > 0) {
			String iconFolder = DelegatesController.getUnitIconFolder();

			if (context.getProtocol().equals("jar"))
				path = "/" + iconFolder + "/" + relativeIconPath;
			else
				path = System.getProperty("user.dir") + File.separator 
					+ iconFolder + File.separator + relativeIconPath;
		} else {
			// search for unitname.png in same directory as xml
			path = context.getPath().replace(".xml", ".png");
		}
		return new URL(context, path);
	}

	public boolean hasHelpString() {
		return (helpString != null);
	}
	
	public String getHelpString() {
		return helpString;
	}

	public String getUnitName() {
		return unitName;
	}


	public boolean getIsDisplayUnit() {
		return isDisplayUnit;
	}

	public boolean hasInputs() {
		return this.input != null && this.input.length > 0;
	}
	
	public boolean hasOutputs() {
		return this.output != null && this.output.length > 0;
	}
	
	public boolean hasParameters() {
		return this.para != null && this.para.length > 0;
	}
	
	/**
	 * Returns the previously read in icon
	 * or null if no icon was found
	 * @return 
	 */
	public BufferedImage  getUnitIcon() {
		return icon;
	}

	public class Para {
		String name;

		String dataTypeString;
		/*double doubleValue;
	int integerValue;
	String stringValue;
	String[] comboStringValues;
	int choiceNumber;
	boolean booleanValue;*/

		/**
		 * can be
		 * ArrayList
		 * Integer
		 * Double
		 * String
		 * Boolean
		 */
		Object value;

		/**
		 * Enumeration of possible values, the actual value has to be 
		 * element in this list.
		 */
		int choiceIndex;

		/**
		 * String used when value true for {@link BooleanParameter}
		 */
		public String trueString;

		String helpString;
	}

	public class Input {
		String name;
		String shortName;
		DataType dataType;
		boolean required = true;
		int imageType;
		boolean needToCopyInput;
	}

	public class Output {
		String name;
		String shortName;
		DataType dataType;
		int imageType;
		boolean doDisplay;
	}

	public Color getColor() {
		return this.color;
	}

}
