package macro;

import models.Connection;
import models.Input;
import models.MacroElement;
import models.unit.UnitElement;
import models.unit.UnitList;



public class MacroGenerator {

	//TODO leave nodes out, which:
	// have no input connected
	// have no input marked
	// which are source, but no output and no display
	
	
	
	public static String generateMacrofromUnitList(UnitList unitElements) {
		
		UnitElement[] units = new UnitElement[unitElements.size()+1];
		for (int i = 0; i < unitElements.size(); i++) {
			units[i+1] = (UnitElement) unitElements.get(i);
		}
		
		return generateMacro(units);
	}
	
	
	
	public static String generateMacro(UnitElement[] unitElement) {
		
		String  macroText ="";

		macroText = "setBatchMode(true); \n";
		
		System.out.println("number of units: "+unitElement.length);
		
		// loop over all units
		// they have to be presorted so they are in the right order
		for (int unitIndex = 1; unitIndex < unitElement.length; unitIndex++) {
			macroText += " \n";
			// read the ImageJ syntax for this unit
			UnitElement unit = unitElement[unitIndex];
			
//			String command = unit.getImageJSyntax();
			MacroElement macroElement = ((MacroElement)unit.getObject()); 
			macroElement.reset();
			
			
			int numInputs = unit.getInputsActualCount();
			int numOutputs = unit.getOutputsMaxCount();
			int numParas = unit.getParameters().size();

			
			// duplicate input images if necessary
			macroText += duplicateImages(unit, numInputs);
			
			// parse the command string for parameter tags that need to be replaced
			for (int p = 0; p < numParas; p++) {
//				command = parseParameters(unitIndex, unit, command, p);
				macroElement.parseParameter(unit, p);
			}
			
			System.out.println("parse Inputs \n");
			// parse the command string for TITLE tags that need to be replaced
			for (int in = 0; in < numInputs; in++) {
				Input input = unit.getInput(in);
				String searchString = "TITLE_" + (in+1);
				String parameterString = "" + input.getImageTitle();
				System.out.println(input.getImageTitle());
				System.out.println("Unit: " + unitIndex + " Input: " + in + " Title: " + parameterString);
				macroElement.replace(searchString, parameterString);
			}
			
			// andere Module brauchen manchmal die ID (dieser Teil fehlt noch)
			
//			macroText += command;
			macroText = macroElement.output(macroText);
			
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
		int numOutputs = unit.getOutputsMaxCount();

		for (int out = 0; out < numOutputs; out++) {
			if (!unit.isDisplayUnit()) {
				String outputID = unit.getOutput(out).getImageID();
			
				macroText += "selectImage("+outputID+"); \n" +
							 "close(); \n";
			}
		}
		return macroText;
	}






	private static String duplicateImages(UnitElement unit,	int numInputs) {
		String code = "";
		for (int in = 0; in < numInputs; in++) {
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
	
	/**
	 * TODO legacy
	 * @param unitElement
	 * @param connection
	 * @return
	 */
	public static String generateMacro_Sample(UnitElement[] unitElement, Connection[] connection) {
		
		String macroText = 
			
		"setBatchMode(true); \n" +

		"// open an image \n" +
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
