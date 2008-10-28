
public class MacroGenerator {

	public static String generateMacro(UnitElements[] unitElement) {
		
		String macroText = "setBatchMode(true); \n";

		for (int u = 1; u < unitElement.length; u++) {
			macroText += " \n";
			// read the ImageJ syntax for this unit
			UnitElements unit = unitElement[u];
			
			String command = unit.unitsImageJSyntax;
			
			int numInputs = unit.inputs.length-1;
			int numOutputs = unit.outputs.length-1;
			int numParas = unit.parameters.length-1;

			
			// duplicate input images if necessary
			for (int in = 1; in <= numInputs; in++) {
				Inputs input = unit.inputs[in];
				if(input.needToCopyInput) {
					//String inputTitle = unit.inputs[in].imageTitle;
					String inputID = unit.inputs[in].imageID;

					macroText += "selectImage(" + inputID + "); \n";
					macroText += "run(\"Duplicate...\", \"title=Title_Temp\"); \n";
				}
			}
			
			// parse the command string for parameter tags that need to be replaced
			for (int p = 1; p <= numParas; p++) {
				Parameters parameter = unit.parameters[p];

				String searchString;
				
				searchString = "PARA_DOUBLE_" + p;
				if(command.contains(searchString)) { 
					String parameterString = "" + parameter.doubleValue;
					System.out.println("Unit: " + u + " Parameter: " + p + " Double Parameter: " + parameterString);
					command = Tools.replace(command, searchString, parameterString);
					System.out.println(command);
				}
				searchString = "PARA_STRING_" + p;
				if(command.contains(searchString)) {
					String parameterString = "" + parameter.stringValue;
					System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
					command = Tools.replace(command, searchString, parameterString);
					System.out.println(command);
				}
				// boolean fehlt noch
			}
			
			// parse the command string for TITLE tags that need to be replaced
			for (int in = 1; in <= numInputs; in++) {
				Inputs input = unit.inputs[in];

				String searchString = "TITLE_" + in;
				
				if(command.contains(searchString)) { 
					String parameterString = "" + input.imageTitle;
					System.out.println("Unit: " + u + " Input: " + in + " Title: " + parameterString);
					command = Tools.replace(command, searchString, parameterString);
					System.out.println(command);
				}
			}
			
			// andere Module brauchen manchmal die ID (dieser Teil fehlt noch)
			
			macroText += command;
			
			// funktioniert nur für einen Ausgang
			for (int out = 1; out <= numOutputs; out++) {
				String outputTitle = unit.outputs[out].imageTitle;
				String outputID = unit.outputs[out].imageID;
				
				macroText +=  "ID_temp = getImageID(); \n" +
				"run(\"Duplicate...\", \"title=" + outputTitle  + "\"); \n" +
				outputID + " = getImageID(); \n" +
				"selectImage(ID_temp); \n" +
				"close(); \n";
			}
		}

		
		// delete all images that are not to be displayed
		macroText +=  "// delete unwanted images \n";
		for (int u = 1; u < unitElement.length; u++) {
			UnitElements unit = unitElement[u];
			
			int numOutputs = unit.outputs.length-1;

			for (int out = 1; out <= numOutputs; out++) {
				if (!unit.outputs[out].doDisplay) {
					String outputID = unit.outputs[out].imageID;
				
					macroText += "selectImage("+outputID+"); \n" +
								 "close(); \n";
				}
			}
		}

		
		macroText += "\nsetBatchMode(\"exit and display\"); ";
		
		return macroText;
	}
	
	public static String generateMacro_Sample(UnitElements[] unitElement, Connection[] connection) {
		
		String macroText = 
			
		"setBatchMode(true); \n" +

		"// open an image \n" +
		"open(\"/Users/barthel/Applications/ImageJ/_images/zange1.png\"); \n" +
		"ID_temp = getImageID(); \n" +
		"run(\"Duplicate...\", \"title=Source1\"); \n" +
		"ID_Source1 = getImageID(); \n" +
		"selectImage(ID_temp); \n" +
		"close(); \n" +

		"// blur image\n" +
		"selectImage(ID_Source1); \n" +
		"run(\"Duplicate...\", \"title=Proc1_out1\"); \n" +
		"ID_Proc1_out1 = getImageID(); \n" +
		"run(\"Gaussian Blur...\", \"sigma=2\"); \n" +

		"// close the images that are not to be displayed \n" +
		"selectImage(ID_Source1); \n" +
		"close(); \n" +

		"setBatchMode(\"exit and display\"); ";

		return macroText;
	}

}
