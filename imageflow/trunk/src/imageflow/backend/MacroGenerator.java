package imageflow.backend;

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
	
	public static String generateMacrofromUnitList(final UnitList unitElements) {
		
		String  macroText ="";

		macroText = "setBatchMode(true); \n";
		
		// loop over all units
		// they have to be presorted so they are in the right order
//		for (int unitIndex = 1; unitIndex < unitElement.length; unitIndex++) {
		for (int unitIndex = 1; unitIndex < unitElements.size()+1; unitIndex++) {
			macroText += " \n";
			// read the ImageJ syntax for this unit
			final UnitElement unit = (UnitElement) unitElements.get(unitIndex-1);
			
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
		for (int u = 1; u < unitElements.size()+1; u++) {
			final UnitElement unit = (UnitElement) unitElements.get(u-1);
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
}
