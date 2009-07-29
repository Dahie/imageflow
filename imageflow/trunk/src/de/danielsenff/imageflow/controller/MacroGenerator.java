package de.danielsenff.imageflow.controller;

import java.util.ArrayList;
import java.util.Collection;

import visualap.Node;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;
import de.danielsenff.imageflow.models.unit.UnitElement.Type;



/**
 * Based on a cleaned {@link UnitList} the MacroGenerator creates 
 * the Macro-Code for ImageJ.
 * @author danielsenff
 *
 */
public class MacroGenerator {

	// leave nodes out, which:
	// have no input connected
	// have no input marked
	// which are source, but no output and no display
	
	private Collection<Node> unitList;
	private ArrayList<ImageJImage> openedImages;
	private String  macroText;

	public MacroGenerator(final Collection<Node> unitElements) {
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
		for (Node node : this.unitList) {
		
			System.out.println(node);
			
			macroText += " \n";
			macroText += "// " + node.getLabel() + "\n";
			macroText += " \n";
			
			if(node instanceof UnitElement) {
				final UnitElement unit = (UnitElement) node;
				addProcessingUnit(unit);	
			} else if (node instanceof GroupUnitElement) {
				final GroupUnitElement unit = (GroupUnitElement) node;
			} 
			
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
		macroElement.parseAttributes(unit.getInputs(), unit.getParameters());
		
		
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
		/*
		 * now we iterate over all outputs of this unit. Each output creates 
		 * a unique image/data, which can be read multiple times by later units. 
		 */
		for (Output output : unit.getOutputs()) {
			final String outputTitle = output.getOutputTitle();
			final String outputID = output.getOutputID();
			
			 if(output.getDataType() instanceof DataTypeFactory.Image) {
				macroText +=  
					"rename(\"" + outputTitle  + "\"); \n"
					+ outputID + " = getImageID(); \n"
					+ "selectImage("+outputID+"); \n";
				openedImages.add(new ImageJImage(output));
			}
		}
		
		/*
		 * Most units require the needCopy-flag true. So when they read their input
		 * and beginn processing, they duplicate their input in order not to 
		 * change the original output data.
		 * However if we work with a sink, ie unit without outputs. The image taken 
		 * by the unit needs to be closed again.  
		 */
		if(unit.getUnitType() == Type.SINK) {
			for (final Input input : unit.getInputs()) {
				if(input.isNeedToCopyInput()) {
					final String inputID = input.getImageID();

					macroText += "selectImage(\"" + getNeedCopyTitle(inputID) + "\"); \n";
					macroText += "close(); \n";
				}
			}
		}
	}


	/**
	 * Rename the remaining displayed images, so they don't have the crypty
	 * identifier-string, but a human-readable title.
	 * @return
	 */
	private String renameImages() {
		String macroText = "";

		for (ImageJImage image : this.openedImages) {
			macroText += "selectImage("+ image.id +"); \n" 
				+ "rename(\"" + image.parentOutput.getDisplayName()  + "\"); \n";
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
		for (final Input input : unit.getInputs()) {
			if(input.isNeedToCopyInput()) {
				final String inputID = input.getImageID();

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
