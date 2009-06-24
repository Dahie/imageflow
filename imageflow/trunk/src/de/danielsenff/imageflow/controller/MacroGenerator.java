package de.danielsenff.imageflow.controller;

import java.util.ArrayList;

import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;



/**
 * @author danielsenff
 *
 */
public class MacroGenerator {

	// leave nodes out, which:
	// have no input connected
	// have no input marked
	// which are source, but no output and no display
	
	private UnitList unitList;
	private ArrayList<ImageJImage> openedImages;
	private String  macroText;

	public MacroGenerator(final UnitList unitElements) {
		this.unitList = unitElements;
		this.openedImages = new ArrayList<ImageJImage>();
		this.macroText = "";
	}
	
	
	/**
	 * Generates a macro.
	 * @return
	 */
	public String generateMacro() {
		// reset in case somebody has the mad idea to run this twice
		this.macroText = ""; 
		macroText += "setBatchMode(true); \n";
		
		// loop over all units
		// they have to be presorted so they are in the right order
		for (int unitIndex = 0; unitIndex < unitList.size(); unitIndex++) {
			macroText += " \n";
			
			// read the ImageJ syntax for this unit
			final UnitElement unit = (UnitElement) unitList.get(unitIndex);
			
			
			addProcessingUnit(unit);
		}

		
		// delete all images that are not to be displayed
		macroText +=  "// delete unwanted images \n";
		macroText += deleteImages();
		macroText +=  "// human understandable names \n";
		macroText += renameImages();
		
		macroText += "\nsetBatchMode(\"exit and display\"); ";
		
		return macroText;
	}

	private void addFunctionUnit(UnitElement unit) {
		final MacroElement macroElement = ((MacroElement)unit.getObject()); 
		macroElement.reset();
		
		// duplicate input images if necessary
		macroText += duplicateImages(unit);
		
		// parse the command string for wildcards, that need to be replaced
		macroElement.parseParameters(unit.getParameters());
		macroElement.parseInputs(unit.getInputs());
		macroElement.parseOutputs(unit.getOutputs());
		
		
		// parse the command string for TITLE tags that need to be replaced
		for (int in = 0; in < unit.getInputsCount(); in++) {
			final Input input = unit.getInput(in);
			final String searchString = "TITLE_" + (in+1);
			final String parameterString = 
				input.isNeedToCopyInput() ? getNeedCopyTitle(input.getImageID()) : input.getImageTitle();
//			System.out.println(input.getImageTitle());
//			System.out.println("Unit: " + unitIndex + " Input: " + in + " Title: " + parameterString);
			macroElement.replace(searchString, parameterString);
		}
		
		// andere Module brauchen manchmal die ID (dieser Teil fehlt noch)
		
		macroText += macroElement.getCommandSyntax();
	}
	
	
	/**
	 * for Processing Units
	 * @param unit
	 */
	private void addProcessingUnit(UnitElement unit) {
		addFunctionUnit(unit);
		
		// FIXME duplicates always
		// maybe use rename(name)
		// only works for one output
		for (int out = 0; out < unit.getOutputsCount(); out++) {
			Output output = unit.getOutput(out);
			final String outputTitle = output.getOutputTitle();
			final String outputID = output.getOutputID();
			
			if(output.getDataType() instanceof DataTypeFactory.Image) {
				macroText +=  
//					"ID_temp = getImageID(); \n"
//					+ "run(\"Duplicate...\", \"title=" + outputTitle  + "\"); \n"
					"rename(\"" + outputTitle  + "\"); \n"
					+ outputID + " = getImageID(); \n"
					+ "selectImage("+outputID+"); \n";
				openedImages.add(new ImageJImage(output));
			}
		}
	}


	/**
	 * Rename the remaining displayed images, so they don't have the crypting
	 * identifier-string, but a humand-readable title.
	 * @return
	 */
	private String renameImages() {
		String macroText = "";

		for (ImageJImage image : this.openedImages) {
			macroText += "selectImage("+ image.id +"); \n" 
				+ "rename(\"" + image.parentOutput.getName()  + "\"); \n";
		}
		return macroText;
	}


	private String deleteImages() {
		String macroText = "";

		ArrayList<ImageJImage> removeAfterwards = new ArrayList<ImageJImage>();
		
		for (ImageJImage image : this.openedImages) {
			if (!image.display) {
				macroText += "selectImage("+ image.id +"); \n" 
					+ "close(); \n";
				removeAfterwards.add(image);
			} 
		}
		for (ImageJImage image : removeAfterwards) {
			this.openedImages.remove(image);
		}
		
		return macroText;
	}
	
	

	private static String duplicateImages(final UnitElement unit) {
		String code = "";
		for (int in = 0; in < unit.getInputsCount(); in++) {
			final Input input = unit.getInput(in);
			if(input.isNeedToCopyInput()) {
				final String inputID = unit.getInput(in).getImageID();

				code += "selectImage(" + inputID + "); \n";
				code += "run(\"Duplicate...\", \"title="+ getNeedCopyTitle(inputID) +"\"); \n";
			}
		}
		return code;
	}
	
	private static String getNeedCopyTitle(String inputID) {
		return "Title_Temp_"+ inputID;
	}
	
	
	public class ImageJImage {
		String id;
		String title;
		Output parentOutput;
		boolean display;
		
		public ImageJImage(Output output) {
			this.id 	= output.getOutputID();
			this.title 	= output.getOutputTitle();
			this.parentOutput = output;
			this.display = output.isDoDisplay();
		}
	}
}
