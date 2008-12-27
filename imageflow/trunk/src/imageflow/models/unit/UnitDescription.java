package imageflow.models.unit;

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

	protected String unitName;
	protected String helpString;
	protected String pathToIcon;
	protected String colorString;
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
	

//	public UnitDescription(File file) {
//		try {
//			System.out.println("Reading xml-description");
//			SAXBuilder sb = new SAXBuilder();
//			Document doc = sb.build(file);
//
//			Element root = doc.getRootElement();
//
//			// read general infos about this unit
//			Element elementGeneral = root.getChild("General");
//			unitName = elementGeneral.getChild("UnitName").getValue();
//			pathToIcon = elementGeneral.getChild("PathToIcon").getValue();
//			helpString = elementGeneral.getChild("HelpString").getValue();
//			colorString = elementGeneral.getChild("Color").getValue();
//			try {
//				color = Color.decode(colorString);
//		    } catch (NumberFormatException e) {
//		          System.out.println("Wrong color string ");
//		    }
//		    if (color == null)
//		    	color = new Color(argbDefault);
//
//			imageJSyntax = elementGeneral.getChild("ImageJSyntax").getValue() + "\n";
//
//			
//			// parameters
//			Element parametersElement = root.getChild("Parameters");
//			if (parametersElement != null) {	
//				List<Element> parametersList = parametersElement.getChildren();
//				Iterator<Element> parametersIterator = parametersList.iterator();
//				
//				numParas = parametersList.size();
//				para = new Para[numParas + 1];
//
//				// loop Ÿber alle Parameter
//				int num = 1;
//				while (parametersIterator.hasNext()) {
//					Element actualParameterElement = (Element) parametersIterator.next();
//					
//					Para actPara = para[num] = new Para();
//					
//					actPara.name = actualParameterElement.getChild("Name").getValue();
//					actPara.helpString = actualParameterElement.getChild("HelpString").getValue();
//					String dataTypeString = actPara.dataTypeString = actualParameterElement.getChild("DataType").getValue();
//					String valueString = actualParameterElement.getChild("Value").getValue();
//					
//					if (dataTypeString.equals("double")) 
//						actPara.doubleValue = Double.valueOf(valueString);
//					else if (dataTypeString.equals("String")) 
//						actPara.stringValue = valueString;
//					else if (dataTypeString.equals("StringArray")) { 
//						actPara.choiceNumber = Integer.valueOf(actualParameterElement.getChild("ChoiceNumber").getValue());
//						actPara.comboStringValues = valueString.split(" ");
//						actPara.stringValue = actPara.comboStringValues[actPara.choiceNumber]; 
//					}
//					else if (dataTypeString.equals("boolean")) { 
//						actPara.trueString = actualParameterElement.getChild("TrueString").getValue();
//						actPara.booleanValue = valueString.equals("true") ? true : false;
//					}
//					else 
//						throw new Exception("invalid datatype");
//					num++;
//				}
//			}
//
//			// Inputs
//			Element inputsElement = root.getChild("Inputs");
//			if (inputsElement != null) {
//				List<Element> inputsList = inputsElement.getChildren();
//				Iterator<Element> inputsIterator = inputsList.iterator();
//				
//				numInputs = inputsList.size();
//				input = new Input[numInputs+1];
//				// loop Ÿber alle Inputs
//				int num = 1;
//				while (inputsIterator.hasNext()) {
//					Element actualInputElement = (Element) inputsIterator.next();
//					
//					Input actInput = input[num] = new Input();
//					actInput.name = actualInputElement.getChild("Name").getValue();
//					actInput.shortName = actualInputElement.getChild("ShortName").getValue();
//					actInput.imageType = Integer.valueOf(actualInputElement.getChild("ImageType").getValue());
//					actInput.needToCopyInput = actualInputElement.getChild("NeedToCopyInput").getValue().equals("true") ? true : false;
//					num++;
//				}
//			}
//
//			// Outputs
//			Element outputsElement = root.getChild("Outputs");
//			if (outputsElement != null) {
//				List<Element> outputsList = outputsElement.getChildren();
//				Iterator<Element> outputIterator = outputsList.iterator();
//				numOutputs = outputsList.size();
//				output = new Output[outputsList.size()+1];
//				// loop Ÿber alle Inputs
//				int num = 1;
//				while (outputIterator.hasNext()) {
//					Element actualOutputElement = (Element) outputIterator.next();
//					Output actOutput = output[num] = new Output();
//					actOutput.name = actualOutputElement.getChild("Name").getValue();
//					actOutput.shortName = actualOutputElement.getChild("ShortName").getValue();
//					actOutput.imageType = Integer.valueOf(actualOutputElement.getChild("ImageType").getValue());
//					actOutput.doDisplay = actualOutputElement.getChild("DoDisplay").equals("true")?true:false;
//					num++;
//				}
//			}
//		}
//
//		catch (Exception e) {
//			System.err.println("Invalid XML-File!");
//			e.printStackTrace();
//		}
//	}
	

	public UnitDescription(Element root) {
		try {

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
			Element parametersElement = root.getChild("Parameters");
			if (parametersElement != null) {	
				List<Element> parametersList = parametersElement.getChildren();
				Iterator<Element> parametersIterator = parametersList.iterator();
				
				numParas = parametersList.size();
				para = new Para[numParas + 1];

				// loop Ÿber alle Parameter
				int num = 1;
				while (parametersIterator.hasNext()) {
					Element actualParameterElement = (Element) parametersIterator.next();
					
					Para actPara = para[num] = new Para();
					
					actPara.name = actualParameterElement.getChild("Name").getValue();
					actPara.helpString = actualParameterElement.getChild("HelpString").getValue();
					String dataTypeString = actPara.dataTypeString = actualParameterElement.getChild("DataType").getValue();
					String valueString = actualParameterElement.getChild("Value").getValue();
					
					if (dataTypeString.toLowerCase().equals("double")) 
						actPara.doubleValue = Double.valueOf(valueString);
					else if (dataTypeString.toLowerCase().equals("file"))
						actPara.stringValue = valueString;
					else if (dataTypeString.toLowerCase().equals("string")) 
						actPara.stringValue = valueString;
					else if (dataTypeString.toLowerCase().equals("integer")) 
						actPara.integerValue = Integer.valueOf(valueString);
					else if (dataTypeString.toLowerCase().equals("stringarray")) { 
						actPara.choiceNumber = Integer.valueOf(actualParameterElement.getChild("ChoiceNumber").getValue());
						actPara.comboStringValues = valueString.split(" ");
						actPara.stringValue = actPara.comboStringValues[actPara.choiceNumber]; 
					}
					else if (dataTypeString.toLowerCase().equals("boolean")) { 
						actPara.trueString = actualParameterElement.getChild("TrueString").getValue();
						actPara.booleanValue = valueString.equals("true") ? true : false;
					}
					else 
						throw new Exception("invalid datatype");
					num++;
				}
			}

			// Inputs
			Element inputsElement = root.getChild("Inputs");
			if (inputsElement != null) {
				List<Element> inputsList = inputsElement.getChildren();
				Iterator<Element> inputsIterator = inputsList.iterator();
				
				numInputs = inputsList.size();
				input = new Input[numInputs+1];
				// loop Ÿber alle Inputs
				int num = 1;
				while (inputsIterator.hasNext()) {
					Element actualInputElement = (Element) inputsIterator.next();
					
					Input actInput = input[num] = new Input();
					actInput.name = actualInputElement.getChild("Name").getValue();
					actInput.shortName = actualInputElement.getChild("ShortName").getValue();
					actInput.imageType = Integer.valueOf(actualInputElement.getChild("ImageType").getValue());
					actInput.needToCopyInput = actualInputElement.getChild("NeedToCopyInput").getValue().equals("true") ? true : false;
					num++;
				}
			}

			// Outputs
			Element outputsElement = root.getChild("Outputs");
			if (outputsElement != null) {
				List<Element> outputsList = outputsElement.getChildren();
				Iterator<Element> outputIterator = outputsList.iterator();
				numOutputs = outputsList.size();
				output = new Output[outputsList.size()+1];
				// loop Ÿber alle Inputs
				int num = 1;
				while (outputIterator.hasNext()) {
					Element actualOutputElement = (Element) outputIterator.next();
					Output actOutput = output[num] = new Output();
					actOutput.name = actualOutputElement.getChild("Name").getValue();
					actOutput.shortName = actualOutputElement.getChild("ShortName").getValue();
					actOutput.imageType = Integer.valueOf(actualOutputElement.getChild("ImageType").getValue());
					actOutput.doDisplay = actualOutputElement.getChild("DoDisplay").getValue().equals("true")?true:false;
					isDisplayUnit = actOutput.doDisplay;
					num++;
				}
			}
		}

		catch (Exception e) {
			System.err.println("Invalid XML-File!");
			e.printStackTrace();
		}
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
}

class Para {
	String name;

	String dataTypeString;
	double doubleValue;
	int integerValue;
	String stringValue;
	String[] comboStringValues;
	int choiceNumber;
	boolean booleanValue;
	public String trueString;

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
	boolean doDisplay;
}
