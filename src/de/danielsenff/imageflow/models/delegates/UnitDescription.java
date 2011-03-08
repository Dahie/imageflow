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
package de.danielsenff.imageflow.models.delegates;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import org.jdom.Element;
import org.jdom.JDOMException;

import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.datatype.ImageDataType;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.unit.NodeIcon;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;
import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;
import de.danielsenff.imageflow.utils.Tools;


/**
 * UnitDescription stores the single pieces a UnitElement is made of from an
 * XML-file. The {@link UnitFactory} can construct an {@link UnitElement}-Instance based
 * on this description.
 * @author Daniel Senff
 *
 */
public class UnitDescription implements NodeDescription {

	public URL unitURL;

	public String unitName;
	public String helpString;
	public String pathToIcon;
	public String colorString;
	public String componentSizeString;
	public Size componentSize;
	public Color color;
	public String imageJSyntax;
	public int    argbDefault =  (0xFF<<24)|(128<<16)|(128<<8)|128;

	public int numParas;
	public Para[] para;

	public int numInputs;
	public Input[] input;

	public int numOutputs;
	public Output[] output;
	public boolean isDisplayUnit = false;
	public boolean isDisplaySilentUnit = false;
	public BufferedImage icon = null;
	public URL iconURL;


	public UnitDescription(URL url) {
		this.unitURL = url;
	}

	/** 
	 * Read Unit Properties from XML-Element 
	 * @param root
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws DataFormatException 
	 */
	public void readXML() throws JDOMException, IOException, DataFormatException {
		final Element root = Tools.getXMLRoot(this.unitURL);
		readXML(root);
	}

	/** 
	 * Read Unit Properties from XML URL
	 * @param root
	 * @throws DataFormatException 
	 */
	public void readXML(final Element root) throws DataFormatException {
		// TODO save XML in model
		// read general info about this unit
		Element elementGeneral = root.getChild("General");
		unitName = elementGeneral.getChild("UnitName").getValue();
		pathToIcon = elementGeneral.getChild("PathToIcon").getValue();
		helpString = elementGeneral.getChild("HelpString").getValue();
		colorString = elementGeneral.getChild("Color").getValue();
		if(elementGeneral.getChild("DoDisplay") != null)
			isDisplayUnit = elementGeneral.getChild("DoDisplay").getValue().equalsIgnoreCase("true") ? true : false;
		if(elementGeneral.getChild("DoDisplaySilent") != null)
			isDisplaySilentUnit = elementGeneral.getChild("DoDisplaySilent").getValue().equalsIgnoreCase("true") ? true : false;
		
		try {
			color = Color.decode(colorString);
			if (color == null)
				color = new Color(argbDefault);
		} catch (NumberFormatException e) {
			System.out.println("Wrong color string ");
		}


		if(elementGeneral.getChild("IconSize") != null) {
			componentSizeString = elementGeneral.getChild("IconSize").getValue();
			componentSize = NodeIcon.getSizeFromString(componentSizeString);
		}

		imageJSyntax = elementGeneral.getChild("ImageJSyntax").getValue() + "\n";

		try {
			// get icon
			iconURL = getIconURL(this.unitURL, pathToIcon);
			icon = ImageIO.read(iconURL.openStream());
		} catch (Exception e) {
			// no exception handling is needed here, since it is often
			// the case that icons are missing. Most of the units are
			// not even intended to have icons.
		}

		// parameters
		Element parametersElement = root.getChild("Parameters");
		if (parametersElement != null) {
			parseParameter(parametersElement);
		}

		// Inputs
		Element inputsElement = root.getChild("Inputs");
		if (inputsElement != null) {
			parseInputs(inputsElement);
		}

		// Outputs
		Element outputsElement = root.getChild("Outputs");
		if (outputsElement != null) {
			parseOutputs(outputsElement);
		}
	}


	/**
	 * @param parametersElement
	 * @throws DataFormatException 
	 */
	private void parseParameter(Element parametersElement) throws DataFormatException {
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
	private void parseInputs(Element inputsElement) {
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

			if(actInput.dataType instanceof ImageDataType) {
				int imageType = Integer.valueOf(inputElement.getChild("ImageType").getValue());
				actInput.imageType = imageType;
				((ImageDataType)actInput.dataType).setImageBitDepth(imageType);
			}

			num++;
		}
	}


	/**
	 * @param outputsElement
	 */
	private void parseOutputs(Element outputsElement) {
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

			if(actOutput.dataType instanceof ImageDataType) {
				int imageType = Integer.valueOf(outputElement.getChild("ImageType").getValue());
				actOutput.imageType = imageType;
				((ImageDataType)actOutput.dataType).setImageBitDepth(imageType);
			}

			actOutput.doDisplay = outputElement.getChild("DoDisplay").getValue().equalsIgnoreCase("true")? true : false;
			isDisplayUnit = actOutput.doDisplay;
			
			if (outputElement.getChild("DoDisplaySilent") != null) {
				actOutput.doDisplaySilent = outputElement.getChild("DoDisplaySilent").getValue().equalsIgnoreCase("true")? true : false;
				isDisplaySilentUnit = actOutput.doDisplaySilent;
			}
			num++;
		}
	}

	/** 
	 * Read parameter definition
	 * @param num
	 * @param actualParameterElement
	 * @throws DataFormatException
	 */
	private void processParameters(int num, Element actualParameterElement)
	throws DataFormatException {
		Para actPara = para[num] = new Para();
		actPara.options = new HashMap<String, Object>();

		actPara.name = actualParameterElement.getChild("Name").getValue();
		if(actualParameterElement.getChild("HelpString") != null)
			actPara.helpString = actualParameterElement.getChild("HelpString").getValue();
		else
			actPara.helpString = actualParameterElement.getChild("Name").getValue();
		
		Element dataTypeElement = actualParameterElement.getChild("DataType");
		String dataTypeString = actPara.dataTypeString = dataTypeElement.getValue();
		
		Element valueElement = actualParameterElement.getChild("Value");
		String valueString = valueElement.getValue();

		Element readOnlyElement = actualParameterElement.getChild("ReadOnly");
		if (readOnlyElement != null)
			actPara.readOnly = readOnlyElement.getValue().equalsIgnoreCase("true")? true : false;;
		
		Element hiddenElement = actualParameterElement.getChild("Hidden");
		if (hiddenElement != null)
			actPara.hidden = hiddenElement.getValue().equalsIgnoreCase("true")? true : false;;
		
		if (dataTypeString.toLowerCase().equals("double"))
			processDoubleDataType(actPara, dataTypeElement, valueElement,	valueString);
		else if (dataTypeString.equalsIgnoreCase("file")) {
			actPara.value = valueString;

			if (hasAttribute(dataTypeElement, "as", "openfilechooser") 
					|| hasAttribute(dataTypeElement, "as", "savefilechooser") ) {
				actPara.options.put("as", dataTypeElement.getAttribute("as").getValue());
			} else {
				actPara.options.put("as", "textfield");
			}		
		} else if (dataTypeString.equalsIgnoreCase("string")) 
			actPara.value = valueString;
		else if (dataTypeString.equalsIgnoreCase("text")) { 
			actPara.value = valueString;
		} else if (dataTypeString.equalsIgnoreCase("integer")) {
			processIntegerDataType(actPara, dataTypeElement, valueElement,	valueString);
		} else if (dataTypeString.equalsIgnoreCase("stringarray")) {
			int choiceNumber = Integer.valueOf(actualParameterElement.getChild("ChoiceNumber").getValue());
			String[] strings = valueString.split(ChoiceParameter.DELIMITER);
			ArrayList<String> choicesList;
			choicesList = new ArrayList<String>(strings.length);
			for (int i = 0; i < strings.length; i++) {
				choicesList.add(strings[i]);
			}	

			actPara.value = choicesList;
			//actPara.choiceIndex = Integer.valueOf(choiceNumber);
			actPara.options.put("choiceIndex", choiceNumber);
			if (hasAttribute(dataTypeElement, "as", "radio") ){
				actPara.options.put("as", "radio");
			}

		}
		else if (dataTypeString.equalsIgnoreCase("boolean")) {
			actPara.value = Boolean.valueOf(valueString);
			actPara.trueString = actualParameterElement.getChild("TrueString").getValue();
		} else 
			throw new DataFormatException("invalid datatype");
	}

	private void processDoubleDataType(final Para actPara, 
			final Element dataTypeElement, 
			final Element valueElement, 
			final String valueString) {
		actPara.value = Double.valueOf(valueString);
		if (hasAttribute(dataTypeElement, "as", "slider") 
				&& (valueElement.getAttribute("min") != null
						&& valueElement.getAttribute("max") != null) ) {
			actPara.options.put("as", "slider");
			actPara.options.put("min", Double.valueOf(valueElement.getAttribute("min").getValue()));
			actPara.options.put("max", Double.valueOf(valueElement.getAttribute("max").getValue()));
		} else {
			actPara.options.put("as", "textfield");
		}
	}

	private void processIntegerDataType(final Para actPara, 
			final Element dataTypeElement,
			final Element valueElement, 
			final String valueString) {
		actPara.value = Integer.valueOf(valueString);

		if (hasAttribute(dataTypeElement, "as", "slider") 
				&& (valueElement.getAttribute("min") != null
						&& valueElement.getAttribute("max") != null) ) {
			actPara.options.put("as", "slider");
			actPara.options.put("min", Integer.valueOf(valueElement.getAttribute("min").getValue()));
			actPara.options.put("max", Integer.valueOf(valueElement.getAttribute("max").getValue()));
		} else {
			actPara.options.put("as", "textfield");
		}
	}

	private boolean hasAttribute(Element dataTypeElement, String attributeName, String value) {
		return dataTypeElement.getAttribute(attributeName) != null 
		&& dataTypeElement.getAttribute(attributeName).getValue().equalsIgnoreCase(value);
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
			String iconFolder = DelegatesController.getUnitIconFolderName();

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

	public String getXMLName() {
		return this.unitURL.getFile();
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
		public String name;

		public String dataTypeString;
		/*
		 * for storing special options, 
		 * in the long run the help class could be replaced by this, we'll see - ds
		 */
		public HashMap<String, Object> options;

		/**
		 * can be
		 * ArrayList
		 * Integer
		 * Double
		 * String
		 * Boolean
		 */
		public Object value;


		/**
		 * String used when value true for {@link BooleanParameter}
		 */
		public String trueString;

		/**
		 * String used as description for the Parameter.
		 */
		public String helpString;
		
		public boolean readOnly;
		public boolean hidden;
	}

	public class Input {
		public String name;
		public String shortName;
		public DataType dataType;
		public boolean required = true;
		public int imageType;
		public boolean needToCopyInput;
	}

	public class Output {
		public String name;
		public String shortName;
		public DataType dataType;
		public int imageType;
		public boolean doDisplay;
		public boolean doDisplaySilent;
	}

	public Color getColor() {
		return this.color;
	}

}
