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
package de.danielsenff.imageflow.models;

import java.util.ArrayList;

import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.DoubleParameter;
import de.danielsenff.imageflow.models.parameter.IntegerParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.StringParameter;
import de.danielsenff.imageflow.utils.Tools;

/**
 * MacroElement is a processor class. It contains the ImageJ-syntax template.
 * Placeholders in this template will be replaced by concrete values during macro generation.
 * @author danielsenff
 *
 */
public class MacroElement {

	/**
	 * the syntax that is to be used for this unit
	 */
	protected String imageJSyntax;
	/**
	 * syntax with replaced variables
	 */
	protected String commandSyntax;
	
	
	/**
	 * @param unitsImageJSyntax 
	 * 
	 */
	public MacroElement(final String unitsImageJSyntax) {
		this.imageJSyntax = unitsImageJSyntax;
		this.commandSyntax = unitsImageJSyntax;
	}
	
	/**
	 * Returns the ImageJ-Macro syntax of this unit. 
	 * This syntax is inserted in the macro and constructs the working flow.
	 * @return
	 */
	public String getImageJSyntax() {
		return this.imageJSyntax;
	}

	/**
	 * Returns the command-syntax with substituted variables.
	 * @return
	 */
	public String getCommandSyntax() {
		return this.commandSyntax;
	}

	/**
	 * Resets the MacroElement. All parsed commands will be removed.
	 */
	public void reset() {
		this.commandSyntax = this.imageJSyntax;
	}
	
	/**
	 * @param searchString 
	 * @param parameterString 
	 */
	public void replace(final String searchString, final String parameterString) {
		if(commandSyntax.contains(searchString)) { 
			commandSyntax = Tools.replace(commandSyntax, searchString, parameterString);
		}
	}
	
	/**
	 * Writes the values of the Parameters into the syntax.
	 * @param parameters
	 */
	public void parseParameters(final ArrayList<Parameter> parameters) {
		this.commandSyntax = parseParameters(parameters, this.commandSyntax);
	}

	private static String parseParameters(ArrayList<Parameter> parameters, String command) {
//		int unitID = unit.getUnitID();

		int parameterIndex = 0, pc = 0, pd = 0, ps = 0, pi = 0, pb = 0;
		String searchString, paraType;
		String parameterString;
		Parameter parameter;
		while (parameterIndex < parameters.size()) {
			
			parameter = parameters.get(parameterIndex);

			searchString = "PARA_DOUBLE_" + (pd+1);
			paraType = parameter.getParaType().toLowerCase();
			if(command.contains(searchString) && paraType.equals("double")) { 
				parameterString = "" + ((DoubleParameter)parameter).getValue();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " Double Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				pd++;
				parameterIndex++;
			}
			searchString = "PARA_STRING_" + (ps+1);
			if(command.contains(searchString) && (paraType.equals("string") || paraType.equals("stringarray"))) {
				parameterString = "" + ((StringParameter)parameter).getValue();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				ps++;
				parameterIndex++;
			}
			searchString = "PARA_INTEGER_" + (pi+1);
			if(command.contains(searchString) && paraType.equals("integer")) {
				String parameterInteger = "" + ((IntegerParameter)parameter).getValue();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " Integer Parameter: " + parameterInteger);
				command = Tools.replace(command, searchString, parameterInteger);
				pi++;
				parameterIndex++;
			}
			searchString = "PARA_BOOLEAN_" + (pb+1);
			if(command.contains(searchString) && paraType.equals("boolean")) {
				boolean bool = ((BooleanParameter)parameter).getValue();
				parameterString =  (bool) ? ((BooleanParameter)parameter).getTrueString() : ""; 
				//String parameterString = "" + ((BooleanParameter)parameter).getValue();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				pb++;
				parameterIndex++;
			}
			pc++;
			if (parameterIndex != pc) {
				searchString = "_PARAMETER_" + (parameterIndex+1);
				if(command.contains(searchString) && paraType.equals("boolean")) {
					// This parameter is used as an "attibute" somewhere
					// Go ahead and increment parameter index, so subsequent parameters are still read
					parameterIndex++;
				}
			} 
			if (parameterIndex != pc) {
//				System.err.println("Error in parameters or ImageJ-syntax");
				return command;
			}
				
		}
		// choiceParameter uses the PARA_STRING_x
		return command;
	}
	
	/**
	 * Writes values of input-DataTypes into the command-string.
	 * @param inputs
	 * @param i 
	 */
	public void parseInputs(ArrayList<Input> inputs, final int i) {
		this.commandSyntax = parseInputs(inputs, this.commandSyntax, i);
	}
	
	/**
	 * Writes values of output-DataTypes into the command-string.
	 * @param outputs 
	 * @param i 
	 */
	public void parseOutputs(ArrayList<Output> outputs, int i) {
		this.commandSyntax = parseOutputs(outputs, this.commandSyntax, i);
	}

	/**
	 * Writes values of output-DataTypes into the command-string.
	 * @param inputs
	 * @param parameters
	 * @param i
	 */
	public void parseAttributes(ArrayList<Input> inputs, ArrayList<Parameter> parameters, int i) {
		this.commandSyntax = parseAttributes(inputs, parameters, this.commandSyntax, i);
	}
	
	/**
	 * Writes stack-command in the command-string, if input-DataType is stack
	 * @param inputs 
	 */
	public void parseStack(ArrayList<Input> inputs) {
		this.commandSyntax = parseStack(inputs, this.commandSyntax);
	}
	
	private static String parseOutputs(ArrayList<Output> outputs, String command, int i) {
		int index = 0, oDbl = 0, oInt = 0, oNbr = 0;
		String searchString, uniqueOutputName;
		Output output;
		while (index < outputs.size()) {
			
			output = outputs.get(index);
			uniqueOutputName = output.getOutputTitle() + "_" + i;

			searchString = "OUTPUT_DOUBLE_" + (oDbl+1);
			if(command.contains(searchString) 
					&& output.getDataType() instanceof DataTypeFactory.Double) { 
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " Double Parameter: " + parameterString);
				command = Tools.replace(command, searchString, uniqueOutputName);
				oDbl++;
//				inputIndex++;
			}
			searchString = "OUTPUT_INTEGER_" + (oInt+1);
			if(command.contains(searchString) 
					&& output.getDataType() instanceof DataTypeFactory.Integer) {
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, uniqueOutputName);
				oInt++;
//				index++;
			}
			searchString = "OUTPUT_NUMBER_" + (oNbr+1);
			if(command.contains(searchString) 
					&& output.getDataType() instanceof DataTypeFactory.Number) {
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, uniqueOutputName);
				oNbr++;
//				index++;
			}
			index++;
	
		}
		return command;
	}
	

	private static String parseInputs(ArrayList<Input> inputs, String command, int i) {
		int index = 0, oDbl = 0, oInt = 0, oNbr = 0;
		String searchString, uniqueOutputName;
		while (index < inputs.size()) {
			
			Input input = inputs.get(index);
			if(input.isRequired() && input.isConnected()) {				
				uniqueOutputName = input.getFromOutput().getOutputTitle() + "_" + i;
				
				searchString = "INPUT_DOUBLE_" + (oDbl+1);
				if(command.contains(searchString) 
						&& input.getDataType() instanceof DataTypeFactory.Double) { 
					command = Tools.replace(command, searchString, uniqueOutputName);
					oDbl++;
				}
				searchString = "INPUT_INTEGER_" + (oInt+1);
				if(command.contains(searchString) 
						&& input.getDataType() instanceof DataTypeFactory.Integer ) {
					
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " String Parameter: " + parameterString);
					command = Tools.replace(command, searchString, uniqueOutputName);
					oInt++;
				}
				searchString = "INPUT_NUMBER_" + (oNbr+1);
				if(command.contains(searchString) 
						&& input.getDataType() instanceof DataTypeFactory.Number) { 
					command = Tools.replace(command, searchString, uniqueOutputName);
					oNbr++;
				}
			}
			index++;
				
		}
		return command;
	}
	
	private static String parseAttributes(ArrayList<Input> inputs, ArrayList<Parameter> parameters, String command, int i) {
		int inputIndex, parameterIndex;
		String searchString;
		Parameter parameter;
		Input input;
		for (inputIndex = 0; inputIndex < inputs.size(); inputIndex++) {
			for (parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
			
				input = inputs.get(inputIndex);
				if(!input.isRequired()) {
					parameter = parameters.get(parameterIndex);

					searchString = "ATTRIBUTE_INPUT_" + (inputIndex+1) + "_PARAMETER_" + (parameterIndex+1);

					if (command.contains(searchString) /*
							&& input.getDataType().getClass().getSimpleName().toLowerCase().equals(parameter.getParaType())*/) {

						if (input.isConnected()) {
							String uniqueOutputName = input.getFromOutput().getOutputTitle() + "_" + i;
							command = Tools.replace(command, searchString, uniqueOutputName);
						} else {
							String parameterValue = "";				
							if (parameter.getParaType().toLowerCase().equals("integer")) 
								parameterValue += ((IntegerParameter)parameter).getValue(); 
							if (parameter.getParaType().toLowerCase().equals("double")) 
								parameterValue += ((DoubleParameter)parameter).getValue();
							if (parameter.getParaType().toLowerCase().equals("boolean")) 
								parameterValue += ((BooleanParameter)parameter).getTrueString();
							if (parameter.getParaType().toLowerCase().equals("string")) 
								parameterValue += ((StringParameter)parameter).getValue();
							command = Tools.replace(command, searchString, parameterValue);
						}
					}
				}
			}
		}
		
		return command;
	}
	
	/**
	 * @param inputs
	 * @param command
	 * @return
	 */
	public static String parseStack(final ArrayList<Input> inputs, String command) {
		String searchString = "STACK";
		String stackParameter = "";
		int binaryComparison, bitdepth;
		for (Input input : inputs) {
			if (input.isConnected() && (input.getDataType() instanceof DataTypeFactory.Image) ) {
				bitdepth = ((DataTypeFactory.Image)input.getFromOutput().getDataType()).getImageBitDepth();
				binaryComparison = bitdepth	& (ij.plugin.filter.PlugInFilter.DOES_STACKS);
				if (binaryComparison != 0) 
					stackParameter = "stack";
			}
		}
		command = Tools.replace(command, searchString, stackParameter);
		return command;
	}
}
