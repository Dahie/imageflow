package macro;

import backend.Tools;
import Models.Connection;
import Models.DoubleParameter;
import Models.Input;
import Models.Parameter;
import Models.StringParameter;
import Models.unit.UnitElement;
import Models.unit.UnitList;



public class MacroGenerator {

	public static String generateMacrofromUnitList(UnitList unitElements) {
		
		UnitElement[] units = new UnitElement[unitElements.size()];
		for (int i = 1; i < unitElements.size(); i++) {
			units[i] = (UnitElement) unitElements.get(i);
		}
		
		return generateMacro(units);
	}
	
	
	
	public static String generateMacro(UnitElement[] unitElement) {
		
		String  macroText ="";

		macroText = "setBatchMode(true); \n";
		
		// loop over all units
		for (int unitIndex = 1; unitIndex < unitElement.length; unitIndex++) {
			macroText += " \n";
			// read the ImageJ syntax for this unit
			UnitElement unit = unitElement[unitIndex];
			
			String command = unit.getImageJSyntax();
			
			int numInputs = unit.getInputsActualCount();
			int numOutputs = unit.getOutputsCount();
			int numParas = unit.getParameters().size();

			
			// duplicate input images if necessary
			macroText += duplicateImages(unit, numInputs);
			
			// parse the command string for parameter tags that need to be replaced
			for (int p = 0; p < numParas; p++) {
				command = parseParameters(unitIndex, unit, command, p);
			}
			
			// parse the command string for TITLE tags that need to be replaced
			for (int in = 0; in < numInputs; in++) {
				Input input = unit.getInput(in);
				String searchString = "TITLE_" + (in+1);
				if(command.contains(searchString)) { 
					String parameterString = "" + input.getImageTitle();
					System.out.println("Unit: " + unitIndex + " Input: " + in + " Title: " + parameterString);
					command = Tools.replace(command, searchString, parameterString);
					System.out.println(command);
				}
			}
			
			// andere Module brauchen manchmal die ID (dieser Teil fehlt noch)
			
			macroText += command;
			
			// funktioniert nur fŸr einen Ausgang
			for (int out = 0; out < numOutputs; out++) {
				String outputTitle = unit.getOutput(out).getImageTitle();
				String outputID = unit.getOutput(out).getImageID();
				
				macroText +=  "ID_temp = getImageID(); \n" +
					"run(\"Duplicate...\", \"title=" + outputTitle  + "\"); \n" +
				outputID + " = getImageID(); \n" +
					"selectImage(ID_temp); \n" +
					"close(); \n";
			}
		}

		
		// delete all images that are not to be displayed
		macroText +=  "// delete unwanted images \n";
		for (int u = 1; u < unitElement.length; u++) {
			UnitElement unit = unitElement[u];
			macroText += deleteImages(unit);
		}

		
		macroText += "\nsetBatchMode(\"exit and display\"); ";
		
		return macroText;
	}



	private static String deleteImages(UnitElement unit) {
		String macroText = "";
		int numOutputs = unit.getOutputsCount();

		for (int out = 0; out < numOutputs; out++) {
			if (!unit.getOutput(out).isDoDisplay()) {
				String outputID = unit.getOutput(out).getImageID();
			
				macroText += "selectImage("+outputID+"); \n" +
							 "close(); \n";
			}
		}
		return macroText;
	}



	private static String parseParameters(int u, 
			UnitElement unit, 
			String command,
			int p) {
		Parameter parameter = (Parameter) unit.getParameters().get(p);

		String searchString;
		
		searchString = "PARA_DOUBLE_" + (p+1);
		if(command.contains(searchString)) { 
			String parameterString = "" + ((DoubleParameter)parameter).getDoubleValue();
			System.out.println("Unit: " + u + " Parameter: " + p + " Double Parameter: " + parameterString);
			command = Tools.replace(command, searchString, parameterString);
			System.out.println(command);
		}
		searchString = "PARA_STRING_" + (p+1);
		if(command.contains(searchString)) {
			String parameterString = "" + ((StringParameter)parameter).getStringValue();
			System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
			command = Tools.replace(command, searchString, parameterString);
			System.out.println(command);
		}
		// boolean fehlt noch
		return command;
	}



	private static String duplicateImages(UnitElement unit,	int numInputs) {
		String code = "";
		for (int in = 0; in < numInputs; in++) {
			//TODO knall peng
			Input input = unit.getInput(in);
			if(input.isNeedToCopyInput()) {
				//String inputTitle = unit.inputs[in].imageTitle;
				String inputID = unit.getInput(in).getImageID();

				code += "selectImage(" + inputID + "); \n";
				code += "run(\"Duplicate...\", \"title=Title_Temp\"); \n";
			}
		}
		return code;
	}
	
	public static String generateMacro_Sample(UnitElement[] unitElement, Connection[] connection) {
		
		String macroText = 
			
		"setBatchMode(true); \n" +

		"// open an imageÂ \n" +
		"open(\"/Users/barthel/Applications/ImageJ/_images/zange1.png\"); \n" +
		"ID_temp = getImageID(); \n" +
		"run(\"Duplicate...\", \"title=Source1\"); \n" +
		"ID_Source1 = getImageID(); \n" +
		"selectImage(ID_temp); \n" +
		"close(); \n" +

		"// blur image\n" +
		"selectImage(ID_Source1); \n" +
		"run(\"Duplicate...\", \"title=Proc1_out1\"); \n" +
		"ID_Proc1_out1 = getImageID(); \n" +
		"run(\"Gaussian Blur...\", \"sigma=2\"); \n" +

		"// close the images that are not to be displayed \n" +
		"selectImage(ID_Source1); \n" +
		"close(); \n" +

		"setBatchMode(\"exit and display\"); ";

		return macroText;
	}

}
