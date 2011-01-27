/**
 * Copyright (C) 2008-2010 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.danielsenff.imageflow.imagej;

import ij.IJ;
import ij.ImagePlus;

import java.util.ArrayList;
import java.util.Collection;

import visualap.Node;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.unit.ForGroupUnitElement;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;
import de.danielsenff.imageflow.models.unit.UnitElement.Type;



/**
 * Based on a cleaned {@link UnitList} the MacroGenerator creates 
 * the Macro-Code for ImageJ.
 * @author Daniel Senff
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
	private int unitAmount;
	private int currentUnit;

	/**
	 * MacroGenerator based on a List of unitElements.
	 * @param unitElements
	 */
	public MacroGenerator(final Collection<Node> unitElements) {
		this.unitList = unitElements;
		this.unitAmount = unitList.size();
		this.openedImages = new ArrayList<ImageJImage>();
		this.macroText = "";
	}
	
	
	/**
	 * @return the openedImages
	 */
	public final ArrayList<ImageJImage> getOpenedImages() {
		return openedImages;
	}


	/**
	 * Generates a macro.
	 * @param extendedMacro determines if callback functions are put into macro code
	 * @return
	 */
	public String generateMacro(boolean extendedMacro) {
		// reset in case somebody has the mad idea to run this twice
		this.macroText = ""; 
		this.macroText += "setBatchMode(true); \n";
		// reset progressBar to 0%
		if (extendedMacro) {
			this.macroText += "call(\"de.danielsenff.imageflow.tasks.RunMacroTask.setProgress\", \"0\")";
		}
		
		// loop over all units
		// they have to be presorted so they are in the right order
		for (Node node : this.unitList) {
			generateUnitMacroCode(node, 0, extendedMacro); 
		}

		
		// delete all images that are not to be displayed
		this.macroText +=  "\n";
		this.macroText +=  "// delete unwanted images \n";
		this.macroText += deleteImages();
		this.macroText +=  "// human understandable names \n";
		this.macroText += renameImages();
		
		this.macroText += "\nsetBatchMode(\"exit and display\"); ";
		
		return macroText;
	}

	/**
	 * Generates the macro code for one node.
	 * @param node
	 * @param i
	 * @param extendedMacro
	 */
	private void generateUnitMacroCode(Node node, int i, boolean extendedMacro) {
		
		macroText += " \n";
		macroText += "// " + node.getLabel() + "\n";
		macroText += " \n";
		
		try {
			if (node instanceof GroupUnitElement) {
				final ForGroupUnitElement unit = (ForGroupUnitElement) node;
				addForUnitElement(unit, i, extendedMacro);
			} else if(node instanceof UnitElement) {
				final UnitElement unit = (UnitElement) node;
				addProcessingUnit(unit, i);	
			}
		} catch (Exception e) {
			macroText += "// An Error has occured, this unit will not be processed";
			e.printStackTrace();
		}
		
		// update progressBar
		if (extendedMacro) {
			currentUnit++;
			Double currentProgress = (1.0*currentUnit) / unitAmount;
			macroText += "call(\"de.danielsenff.imageflow.tasks.RunMacroTask.setProgress\", \""+ currentProgress +"\")";
		}
		
	}

	/**
	 * we assume embedded units are already in the correct order
	 * @param unit
	 */
	private void addForUnitElement(ForGroupUnitElement unit, int i, boolean extendedMacro) {
		
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
		
//		renameOutputImages(unit, i);
		
		/*
		 * content middle section
		 */
		
		// iterate over all units i this for-group
		for (Node node : unit.getNodes()) {
			System.out.println("process nodes in loop");
			// process unit as usual
			generateUnitMacroCode(node, i, extendedMacro);
			
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

	private void addFunctionUnit(UnitElement unit, int i) throws Exception {
		final MacroElement macroElement = ((MacroElement)unit.getObject()); 
		macroElement.reset();
		
		// duplicate input images if necessary
		macroText += duplicateImages(unit);
		
		// parse the command string for wildcards, that need to be replaced
		// int i is needed for correct generation of identifiers according to loops
		macroElement.parseParameters(unit.getParameters());
		macroElement.parseStack(unit.getInputs());
		if(!unit.hasRequiredInputsConnected()) throw new Exception("not all required Inputs connected");
		macroElement.parseInputs(unit.getInputs(), i);
		macroElement.parseOutputs(unit.getOutputs(), i);
		macroElement.parseAttributes(unit.getInputs(), unit.getParameters(), i);
		
		
		// parse the command string for TITLE_x tags that need to be replaced
		String parameterString, searchString;
		Input input;
		for (int in = 0; in < unit.getInputsCount(); in++) {
			input = unit.getInput(in);
			searchString = "TITLE_" + (in+1);
			parameterString = input.isNeedToCopyInput() ? getNeedCopyTitle(input.getImageID()+"_"+i) : input.getImageTitle()+"_"+i;
//			System.out.println("Unit: " + unit.getUnitID() + " Input: " + in + " Title: " + parameterString);
			macroElement.replace(searchString, parameterString);
		}
		// if a module needs the image id, just use ID_TITLE_x
		
		macroText += macroElement.getCommandSyntax();
	}
	
	
	/**
	 * for Processing Units
	 * @param unit
	 * @throws Exception 
	 */
	private void addProcessingUnit(UnitElement unit, int i) throws Exception {
		addFunctionUnit(unit, i);
		
		printNumbers(unit, i);
		
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
		String inputID;
		if(unit.getUnitType() == Type.SINK) {
			for (final Input input : unit.getInputs()) {
				if(input.isNeedToCopyInput()) {
					inputID = input.getImageID()+"_"+i;

					macroText += "selectImage(" + getNeedCopyTitle(inputID) + "); \n";
					macroText += "close(); \n";
				}
			}
		}
	}


	private void renameOutputImages(UnitElement unit, int i) {
		String outputTitle, outputID;
		for (Output output : unit.getOutputs()) {
			outputTitle = output.getOutputTitle()+"_"+0;
			outputID = output.getOutputID()+"_"+i;
			
			 if((output.getDataType() instanceof DataTypeFactory.Image)) {
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
		String inputID;
		int binaryComparison;
		for (final Input input : unit.getInputs()) {
			if (input.getDataType() instanceof DataTypeFactory.Image) {
				inputID = input.getImageID()+"_"+0;
				code += "selectImage(" + inputID + "); \n";
				if(input.isNeedToCopyInput()) {
					// Stacks need an additional Parameter 'duplicate' in the Duplicate-command
					binaryComparison = ((DataTypeFactory.Image)input.getFromOutput().getDataType()).getImageBitDepth() 
						& (ij.plugin.filter.PlugInFilter.DOES_STACKS);
					if (binaryComparison != 0)
						code += "run(\"Duplicate...\", \"title="+ getNeedCopyTitle(inputID) +" duplicate\"); \n";
					else
						code += "run(\"Duplicate...\", \"title="+ getNeedCopyTitle(inputID) +"\"); \n";
				}
			}
		}
		return code;
	}
	
	private static String getNeedCopyTitle(String inputID) {
		return "Title_Temp_"+ inputID;
	}
	
	/**
	 * If Units have an {@link Output} of Type Double/Integer/Number and are set to Display
	 * their result values are printed to the Log. There is no need for an extra Print-Unit.   
	 * @param unit
	 * @param i
	 */
	private void printNumbers(UnitElement unit, int i) {
		for (Output output : unit.getOutputs()) {
			if ( (output.getDataType() instanceof DataTypeFactory.Double
					|| output.getDataType() instanceof DataTypeFactory.Integer
					|| output.getDataType() instanceof DataTypeFactory.Number)
					&& output.getParent().isDisplay()) {
				macroText += "print (" + output.getOutputTitle() + "_" + i + "); \n";
			}
		}
	}
	
	/**
	 * 
	 * @author dahie
	 *
	 */
	public class ImageJImage {
		public String id;
		public String title;
		public Output parentOutput;
		public Node node;
		public boolean display;
		
		public ImageJImage(Output output, int i) {
			this.id 	= output.getOutputID()+"_"+i;
			this.title 	= output.getOutputTitle()+"_"+i;
			this.parentOutput = output;
			this.display = output.isDoDisplay();
			this.node = output.getParent().getOriginalUnit();
		}
		
	}
}
