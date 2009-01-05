package imageflow;

import imageflow.backend.GraphController;
import imageflow.models.Connection;
import imageflow.models.Input;
import imageflow.models.MacroElement;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitList;



/**
 * @author danielsenff
 *
 */
public class MacroGenerator {

	//TODO leave nodes out, which:
	// have no input connected
	// have no input marked
	// which are source, but no output and no display
	
//	GraphController 
	
	public String generateMacrofromUnitList(final UnitList unitElements) {
		
		final UnitElement[] units = new UnitElement[unitElements.size()+1];
		for (int i = 0; i < unitElements.size(); i++) {
			units[i+1] = (UnitElement) unitElements.get(i);
		}
		
		return generateMacro(units);
	}
	
	
	
	public String generateMacro(final UnitElement[] unitElement) {
		
		String  macroText ="";

		macroText = "setBatchMode(true); \n";
		
		System.out.println("number of units: "+unitElement.length);
		
		// loop over all units
		// they have to be presorted so they are in the right order
		for (int unitIndex = 1; unitIndex < unitElement.length; unitIndex++) {
			macroText += " \n";
			// read the ImageJ syntax for this unit
			final UnitElement unit = unitElement[unitIndex];
			
//			String command = unit.getImageJSyntax();
			final MacroElement macroElement = ((MacroElement)unit.getObject()); 
			macroElement.reset();
			
			
			final int numInputs = unit.getInputsCount();
			final int numOutputs = unit.getOutputsCount();
			final int numParas = unit.getParameters().size();

			
			// duplicate input images if necessary
			macroText += duplicateImages(unit, numInputs);
			
			// parse the command string for parameter tags that need to be replaced
			macroElement.parseParameters(unit, numParas);
			
			
			// parse the command string for TITLE tags that need to be replaced
			for (int in = 0; in < numInputs; in++) {
				final Input input = unit.getInput(in);
				final String searchString = "TITLE_" + (in+1);
				final String parameterString = "" + input.getImageTitle();
				System.out.println(input.getImageTitle());
				System.out.println("Unit: " + unitIndex + " Input: " + in + " Title: " + parameterString);
				macroElement.replace(searchString, parameterString);
			}
			
			// andere Module brauchen manchmal die ID (dieser Teil fehlt noch)
			
//			macroText += command;
			macroText = macroElement.output(macroText);
			
			// funktioniert nur fŸr einen Ausgang
			for (int out = 0; out < numOutputs; out++) {
				final String outputTitle = unit.getOutput(out).getImageTitle();
				final String outputID = unit.getOutput(out).getImageID();
				
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
			final UnitElement unit = unitElement[u];
			macroText += deleteImages(unit);
		}

		
		macroText += "\nsetBatchMode(\"exit and display\"); ";
		
		return macroText;
	}



	private static String deleteImages(final UnitElement unit) {
		String macroText = "";
		final int numOutputs = unit.getOutputsCount();

		for (int out = 0; out < numOutputs; out++) {
			if (!unit.isDisplayUnit()) {
				final String outputID = unit.getOutput(out).getImageID();
			
				macroText += "selectImage("+outputID+"); \n" +
							 "close(); \n";
			}
		}
		return macroText;
	}

	private static String duplicateImages(final UnitElement unit,	final int numInputs) {
		String code = "";
		for (int in = 0; in < numInputs; in++) {
			final Input input = unit.getInput(in);
			if(input.isNeedToCopyInput()) {
				final String inputID = unit.getInput(in).getImageID();

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
	public static String generateMacro_Sample(final UnitElement[] unitElement, final Connection[] connection) {
		
		final String macroText = 
			
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
