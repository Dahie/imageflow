package models.unit;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class UnitDescription {

	public UnitDescription() {
	}

	String unitName;
	String helpString;
	String pathToIcon;
	String colorString;
	Color color;
	String imageJSyntax;
	int    argbDefault =  (0xFF<<24)|(128<<16)|(128<<8)|128;

	int numParas;
	Para[] para;

	int numInputs;
	Input[] input;

	int numOutputs;
	Output[] output;

	public void setBlurValues() {
		unitName = "Gaussian Blur";
		pathToIcon = "";
		imageJSyntax = "run(\"Gaussian Blur...\", \"sigma=PARA_DOUBLE_1\");\n";

		numParas = 1;
		para = new Para[numParas + 1];
		para[1] = new Para();
		para[1].name = "Radius";
		para[1].stringDataType = "double";
		para[1].doubleValue = 4;
		para[1].helpString = "Radius of the gaussian kernel";

		numInputs = 1;
		input = new Input[numInputs + 1];
		input[1] = new Input();
		input[1].name = "Input";
		input[1].shortName = "I";
		input[1].imageType = ij.plugin.filter.PlugInFilter.DOES_ALL;
		input[1].needToCopyInput = true;

		numOutputs = 1;
		output = new Output[numOutputs + 1];
		output[1] = new Output();
		output[1].name = "Output";
		output[1].shortName = "O";
		output[1].imageType = -1; // same as the input
	}

	public UnitDescription(File file) {
		try {
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(file);

			Element root = doc.getRootElement();

			// read general infos about this unit
			Element elementGeneral = root.getChild("General");
			unitName = elementGeneral.getChild("UnitName").getValue();
			pathToIcon = elementGeneral.getChild("PathToIcon").getValue();
			helpString = elementGeneral.getChild("HelpString").getValue();
			colorString = elementGeneral.getChild("Color").getValue();
			try {
				color = Color.decode(colorString);
		    } catch (NumberFormatException e) {
		          System.out.println("Wrong color string ");
		    }
		    if (color == null)
		    	color = new Color(argbDefault);

			imageJSyntax = elementGeneral.getChild("ImageJSyntax").getValue() + "\n";

			
			// parameters
			Element elementParameters = root.getChild("Parameters");
			if (elementParameters != null) {	
				List<Element> parameterList = elementParameters.getChildren();
				Iterator<Element> parameterIterator = parameterList.iterator();
				
				numParas = parameterList.size();
				para = new Para[numParas + 1];

				// loop Ÿber alle Parameter
				int num = 1;
				while (parameterIterator.hasNext()) {
					Element childP = (Element) parameterIterator.next();
					List<Element> parameterElements = childP.getChildren();
					Iterator<Element> parameterIterator2 = parameterElements.iterator();

					String valueString = "";
					String stringDataType = "";
					para[num] = new Para();
					while (parameterIterator2.hasNext()) {
						Element childP2 = (Element) parameterIterator2.next();
						String string = childP2.getName();
						if (string.equals("Name"))
							para[num].name = childP2.getValue();
						else if (string.equals("DataType")) 
							stringDataType = para[num].stringDataType = childP2.getValue();
						else if (string.equals("Value"))
							valueString = childP2.getValue();
						else if (string.equals("HelpString"))
							para[num].helpString = childP2.getValue();
					}
					
					if (stringDataType.equals("double")) // double
						para[num].doubleValue = Double.valueOf(valueString);
					else if (stringDataType.equals("String")) // String
						para[num].stringValue = valueString;
					else if (stringDataType.equals("StringArray")) { // String array
						para[num].comboStringValues = valueString.split(" ");
						// initialize with first element
						para[num].stringValue = para[num].comboStringValues[0];
					}
					else if (stringDataType.equals("boolean")) { // boolean
						para[num].booleanValue = valueString.equals("true") ? true : false;
					}
//					else 
//						throw ("invalid datatype");
					num++;
				}
			}

			// Inputs
			Element inputElement = root.getChild("Inputs");
			if (inputElement != null) {
				List<Element> inputList = inputElement.getChildren();
				Iterator<Element> inputIterator = inputList.iterator();
				
				numInputs = inputList.size();
				input = new Input[numInputs+1];
				// loop Ÿber alle Inputs
				int num = 1;
				while (inputIterator.hasNext()) {
					Element childI = (Element) inputIterator.next();
					Iterator<Element> inputIterator2 = childI.getChildren().iterator();

					input[num] = new Input();
					while (inputIterator2.hasNext()) {
						Element childI2 = (Element) inputIterator2.next();
						String string = childI2.getName();
						if (string.equals("Name"))
							input[num].name = childI2.getValue();
						else if (string.equals("ShortName"))
							input[num].shortName = childI2.getValue();
						else if (string.equals("ImageType"))
							input[num].imageType = Integer.valueOf(childI2.getValue());
						else if (string.equals("NeedToCopyInput"))
							input[num].needToCopyInput = childI2.getValue().equals("true") ? true : false;
					}
					num++;
				}
			}

			// Outputs
			Element outputElement = root.getChild("Outputs");
			if (outputElement != null) {
				List<Element> outputList = outputElement.getChildren();
				Iterator<Element> outputIterator = outputList.iterator();
				// loop Ÿber alle Inputs
				numOutputs = outputList.size();
				output = new Output[outputList.size()+1];
				int num = 1;
				while (outputIterator.hasNext()) {
					Element child = (Element) outputIterator.next();
					Iterator<Element> outputIterator2 = child.getChildren().iterator();

					output[num] = new Output();
					while (outputIterator2.hasNext()) {
						Element childO2 = (Element) outputIterator2.next();
						String string = childO2.getName();
						if (string.equals("Name"))
							output[num].name = childO2.getValue();
						else if (string.equals("ShortName"))
							output[num].shortName = childO2.getValue();
						else if (string.equals("ImageType"))
							output[num].imageType = Integer.valueOf(childO2.getValue());
					}
					num++;
				}
			}
		}

		catch (Exception e) {
			System.err.println("Invalid XML-File!");
			e.printStackTrace();
		}
	}

	
	public void setImageCalculatorValues() {
		unitName = "Image Calculator";
		pathToIcon = "";
		imageJSyntax = "run(\"Image Calculator...\", \"image1=TITLE_1 operation=PARA_STRING_1 image2=TITLE_2 create 32-bit\"); \n";

		numParas = 1;
		para = new Para[1 + 1];
		para[1] = new Para();
		para[1].name = "Math";
		para[1].stringDataType = "StringArray";
		para[1].stringValue = "Add";
		para[1].comboStringValues = new String[7];
		para[1].comboStringValues[0] = "Add";
		para[1].comboStringValues[1] = "Subtract";
		para[1].comboStringValues[2] = "Multiply";
		para[1].comboStringValues[3] = "Devide";
		para[1].comboStringValues[4] = "AND";
		para[1].comboStringValues[5] = "OR";
		para[1].comboStringValues[6] = "XOR";

		para[1].helpString = "Defines what math should be used to merge both images";

		numInputs = 2;
		input = new Input[numInputs + 1];
		input[1] = new Input();
		input[1].name = "Input1";
		input[1].shortName = "I1";
		input[1].imageType = ij.plugin.filter.PlugInFilter.DOES_ALL;
		input[1].needToCopyInput = false;

		input[2] = new Input();
		input[2].name = "Input2";
		input[2].shortName = "I2";
		input[2].imageType = ij.plugin.filter.PlugInFilter.DOES_ALL;
		input[2].needToCopyInput = false;

		numOutputs = 1;
		output = new Output[numOutputs + 1];
		output[1] = new Output();
		output[1].name = "Output";
		output[1].shortName = "O";
		output[1].imageType = 32; // same as the input
	}

//	public void parseUnitValuesFromXmlFile(String uri) {
//		try {
//			SAXBuilder sb = new SAXBuilder();
//			Document doc = sb.build(new File(uri));
//
//			Element root = doc.getRootElement();
//
//			// read general infos about this unit
//			System.out.println("General:");
//			Element eleGeneral = root.getChild("General");
//			String strUnitName = eleGeneral.getChild("UnitName").getValue();
//			System.out.println("UnitName = " + strUnitName);
//			String strPathToIcon = eleGeneral.getChild("PathToIcon").getValue();
//			System.out.println("PathToIcon = " + strPathToIcon);
//			String strImageJSyntax = eleGeneral.getChild("ImageJSyntax")
//					.getValue();
//			System.out.println("ImageJSyntax = " + strImageJSyntax);
//
//			System.out.println();
//			Element eleParameters = root.getChild("Parameters");
//			List<Element> parameterList = eleParameters.getChildren();
//			Iterator<Element> parameterIterator = parameterList.iterator();
//			int numParas = parameterList.size();
//			System.out.println(numParas + " Parameter(s):");
//			// loop Ÿber alle Parameter
//			int num = 1;
//			while (parameterIterator.hasNext()) {
//				System.out.println("Parameter " + num + ":");
//
//				Element child = (Element) parameterIterator.next();
//				List<Element> parameterElements = child.getChildren();
//				Iterator<Element> iterator2 = parameterElements.iterator();
//
//				while (iterator2.hasNext()) {
//					Element child2 = (Element) iterator2.next();
//					System.out.print(child2.getName() + "=");
//					System.out.println(child2.getValue());
//				}
//				System.out.println();
//				num++;
//			}
//
//			System.out.println();
//			Element eleInputs = root.getChild("Inputs");
//			List<Element> inputList = eleInputs.getChildren();
//			Iterator<Element> inputIterator = inputList.iterator();
//			int numInputs = inputList.size();
//			System.out.println(numInputs + " Inputs(s):");
//			// loop Ÿber alle Inputs
//			num = 1;
//			while (inputIterator.hasNext()) {
//				System.out.println("Input " + num + ":");
//
//				Element child = (Element) inputIterator.next();
//				List<Element> inputElements = child.getChildren();
//				Iterator<Element> iterator2 = inputElements.iterator();
//
//				while (iterator2.hasNext()) {
//					Element child2 = (Element) iterator2.next();
//					System.out.print(child2.getName() + "=");
//					System.out.println(child2.getValue());
//				}
//				System.out.println();
//				num++;
//			}
//
//			System.out.println();
//			Element eleOutputs = root.getChild("Outputs");
//			List<Element> outputList = eleOutputs.getChildren();
//			Iterator<Element> outputIterator = outputList.iterator();
//			int numOutputs = outputList.size();
//			System.out.println(numOutputs + " Outputs(s):");
//			// loop Ÿber alle Inputs
//			num = 1;
//			while (outputIterator.hasNext()) {
//				System.out.println("Input " + num + ":");
//
//				Element child = (Element) outputIterator.next();
//				List<Element> outputElements = child.getChildren();
//				Iterator<Element> iterator2 = outputElements.iterator();
//
//				while (iterator2.hasNext()) {
//					Element child2 = (Element) iterator2.next();
//					System.out.print(child2.getName() + "=");
//					System.out.println(child2.getValue());
//				}
//				System.out.println();
//				num++;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public String getHelpString() {
		return helpString;
	}

	public String getUnitName() {
		return unitName;
	}
}

class Para {
	String name;
	String stringDataType;
	double doubleValue;
	String stringValue;
	boolean booleanValue;
	String[] comboStringValues;

	String helpString;
}

class Input {
	String name;
	String shortName;
	int imageType;
	boolean needToCopyInput;
}

class Output {
	String name;
	String shortName;
	int imageType;
}
