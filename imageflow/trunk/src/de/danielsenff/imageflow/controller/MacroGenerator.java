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
			generateUnitMacroCode(node, 0); 
		}

		
		// delete all images that are not to be displayed
		macroText +=  "// delete unwanted images \n";
		macroText += deleteImages();
		macroText +=  "// human understandable names \n";
		macroText += renameImages();
		
		macroText += "\nsetBatchMode(\"exit and display\"); ";
		
		return macroText;
	}


	private void generateUnitMacroCode(Node node, int i) {
		System.out.println(node);
		
		macroText += " \n";
		macroText += "// " + node.getLabel() + "\n";
		macroText += " \n";
		
		if(node instanceof UnitElement) {
			final UnitElement unit = (UnitElement) node;
			addProcessingUnit(unit, i);	
		} else if (node instanceof GroupUnitElement) {
			final GroupUnitElement unit = (GroupUnitElement) node;
			addForUnitElement(unit, i);
		}
	}

	/**
	 * we assume embedded units are already in the correct order
	 * @param unit
	 */
	private void addForUnitElement(GroupUnitElement unit, int i) {
		
		int begin = 0;
		int step = 1;
		int end = 10;
		
		/*
		 * for-header 
		 */
		
		macroText += "// For-Loop /n";
		
		// duplicate input images if necessary
		macroText += duplicateImages(unit);
		
		macroText += "for (i="+begin+"; i<"+end+"; i+="+step+") { Unit_7_Output_2 = i;"
			+ "rename(\"Unit_7_Output_1_\"+"+i+");" 
			+ "ID_Unit_7_Output_1 = getImageID();" 
			+ "selectImage(ID_Unit_7_Output_1); ";
		
		renameOutputImages(unit, i);
		
		/*
		 * content middle section
		 */
		
		// iterate over all units i this for-group
		for (Node node : unit.getNodes()) {
			
			// process unit as usual
			generateUnitMacroCode(node, i);
			
			for (int j = begin; j < end; i+= step) {

				/*
				 * now we iterate over all outputs of this unit. Each output creates 
				 * a unique image/data, which can be read multiple times by later units. 
				 */
				renameOutputImages(unit, i);
//				openedImages.add(new ImageJImage(output, j));
			}
			
		}
		
		/*
		 * closing footer 
		 */
		
		// close all images create in the loop
		
		
		/*selectImage(ID_Unit_9_Output_1); 
		//run("Duplicate...", "title=Title_Temp_ID_Unit_7_Output_1"); 
		//rename("Unit_7_Output_1"); 
		*/
		
		// close for loop
		macroText += "} \n";
		
		
		
		macroText += "// close all loop images here"
		+"for (i=0; i<"+end+"; i+=1) { "
		+"selectImage(\"Unit_7_Output_1_\"+"+i+");" 
		+"close();"
		+"}";
		
		
		//prepare output
		renameOutputImages(unit, end+1);
	}

	
	

	private void addFunctionUnit(UnitElement unit, int i) {
		final MacroElement macroElement = ((MacroElement)unit.getObject()); 
		macroElement.reset();
		
		// duplicate input images if necessary
		macroText += duplicateImages(unit);
		
		// parse the command string for wildcards, that need to be replaced
		// int i is needed for correct generation identifiers according to loops
		macroElement.parseParameters(unit.getParameters(), i);
		macroElement.parseInputs(unit.getInputs(), i);
		macroElement.parseOutputs(unit.getOutputs(), i);
		macroElement.parseAttributes(unit.getInputs(), unit.getParameters(), i);
		
		
		// parse the command string for TITLE tags that need to be replaced
		for (int in = 0; in < unit.getInputsCount(); in++) {
			final Input input = unit.getInput(in);
			final String searchString = "TITLE_" + (in+1);
			final String parameterString = 
				input.isNeedToCopyInput() ? getNeedCopyTitle(input.getImageID()+"_"+i) : input.getImageTitle()+"_"+i;
//			System.out.println(input.getImageTitle());
			System.out.println("Unit: " + unit.getUnitID() + " Input: " + in + " Title: " + parameterString);
			macroElement.replace(searchString, parameterString);
		}
		
		// andere Module brauchen manchmal die ID (dieser Teil fehlt noch)
		
		macroText += macroElement.getCommandSyntax();
	}
	
	
	/**
	 * for Processing Units
	 * @param unit
	 */
	private void addProcessingUnit(UnitElement unit, int i) {
		addFunctionUnit(unit, i);
		
		// FIXME duplicates always
		// maybe use rename(name)
		// only works for one output
		/*
		 * now we iterate over all outputs of this unit. Each output creates 
		 * a unique image/data, which can be read multiple times by later units. 
		 */
		renameOutputImages(unit, i);
		
		/*
		 * Most units require the needCopy-flag true. So when they read their input
		 * and begin processing, they duplicate their input in order not to 
		 * change the original output data.
		 * However if we work with a sink, ie unit without outputs. The image taken 
		 * by the unit needs to be closed again.  
		 */
		if(unit.getUnitType() == Type.SINK) {
			for (final Input input : unit.getInputs()) {
				if(input.isNeedToCopyInput()) {
					final String inputID = input.getImageID()+"_"+i;

					macroText += "selectImage(" + getNeedCopyTitle(inputID) + "); \n";
					macroText += "close(); \n";
				}
			}
		}
	}


	private void renameOutputImages(UnitElement unit, int i) {
		for (Output output : unit.getOutputs()) {
			final String outputTitle = output.getOutputTitle()+"_"+0;
			final String outputID = output.getOutputID()+"_"+i;
			
			 if((output.getDataType() instanceof DataTypeFactory.Image) ||
					 (unit.getUnitType() != Type.SOURCE)) {
				macroText +=  
					"rename(\"" + outputTitle  + "\"); \n"
					+ outputID + " = getImageID(); \n"
					+ "selectImage("+outputID+"); \n";
				openedImages.add(new ImageJImage(output, 0));
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
			final String inputID = input.getImageID()+"_"+0;
			code += "selectImage(" + inputID + "); \n";
			if(input.isNeedToCopyInput()) {

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
		
		/*private ImageJImage(Output output) {
			this.id 	= output.getOutputID();
			this.title 	= output.getOutputTitle();
			this.parentOutput = output;
			this.display = output.isDoDisplay();
		}*/
		
		public ImageJImage(Output output, int i) {
			this.id 	= output.getOutputID()+"_"+i;
			this.title 	= output.getOutputTitle()+"_"+i;
			this.parentOutput = output;
			this.display = output.isDoDisplay();
		}
		
	}
}
