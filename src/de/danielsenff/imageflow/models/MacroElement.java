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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.datatype.ImageDataType;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
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
		/**
		 * my gut says, this should be refactored
		 */
		int parameterIndex = 0; // index of parameter in parameters collection
		int parameterCount = 0; // count of processed parameters
		int pd = 0, ps = 0, pi = 0, pb = 0;
		String searchString;
		String parameterString;
		Parameter parameter;
		Matcher matcher;
		while (parameterIndex < parameters.size()) {
			parameter = parameters.get(parameterIndex);

			//paraType = parameter.getParaType().toLowerCase();
			searchString = "(PARA_DOUBLE_" + (pd+1) + ")(\\D)";
			matcher = compileMatcherBy(searchString, command);
			if(matcher.find() && parameter instanceof DoubleParameter) { 
				parameterString = "" + ((DoubleParameter)parameter).getValue();
				command = matcher.replaceAll(parameterString+"$2"); // making sure, that we catch full numbers and not just single digits
				pd++;
				parameterIndex++;
			}
			// choiceParameter uses the PARA_STRING_x
			searchString = "(PARA_STRING_" + (ps+1) + ")(\\D)";
			matcher = compileMatcherBy(searchString, command);
			boolean find = matcher.find();
			if(find && (parameter instanceof StringParameter || parameter instanceof ChoiceParameter)) {
				parameterString = fixPath("" + ((StringParameter)parameter).getValue());
				command = matcher.replaceAll(parameterString+"$2"); // making sure, that we catch full numbers and not just single digits
				ps++;
				parameterIndex++;
			}
			searchString = "(PARA_INTEGER_" + (pi+1) + ")(\\D)";
			matcher = compileMatcherBy(searchString, command);
			if(matcher.find() && parameter instanceof IntegerParameter) {
				parameterString = "" + ((IntegerParameter)parameter).getValue();
				command = matcher.replaceAll(parameterString+"$2"); // making sure, that we catch full numbers and not just single digits
				pi++;
				parameterIndex++;
			}
			searchString = "(PARA_BOOLEAN_" + (pb+1) + ")(\\D)";
			matcher = compileMatcherBy(searchString, command);
			if(matcher.find() && parameter instanceof BooleanParameter) {
				boolean bool = ((BooleanParameter)parameter).getValue();
				parameterString =  (bool) ? ((BooleanParameter)parameter).getTrueString() : ""; 
				command = matcher.replaceAll(parameterString+"$2"); // making sure, that we catch full numbers and not just single digits
				pb++;
				parameterIndex++;
			}
			parameterCount++;
			if (parameterIndex != parameterCount) {
				searchString = "_PARAMETER_" + (parameterIndex+1);
				matcher = compileMatcherBy(searchString, command);
				if(matcher.find() && knownParameterType(parameter)) {
					// This parameter is used as an "attibute" somewhere
					// Go ahead and increment parameter index, so subsequent parameters are still read
					parameterIndex++;
				}
			} 
			if (parameterIndex != parameterCount) {
				// means, we have a parameter, that wasn't used
//				System.err.println("Error in parameters or ImageJ-syntax");
				return command;
			}
				
		}
		return command;
	}
	
	private static boolean knownParameterType(final Parameter parameter) {
		return parameter instanceof BooleanParameter 
			|| parameter instanceof IntegerParameter 
			|| parameter instanceof StringParameter 
			|| parameter instanceof ChoiceParameter 
			|| parameter instanceof DoubleParameter;
	}

	/*
	 * from ImageJ.Recorder
	 */
	static String fixPath (String path) {
		StringBuffer sb = new StringBuffer();
		char c;
		for (int i=0; i<path.length(); i++) {
			sb.append(c=path.charAt(i));
			if (c=='\\')
				sb.append("\\\\\\");
		}
		return new String(sb);
	}
	
	/**
	 * Compile a Matcher based on a SearchString and the Region which is to search.
	 * @param searchString
	 * @param region
	 * @return
	 */
	private static Matcher compileMatcherBy(final String searchString, final String region) {
		return Pattern.compile(searchString).matcher(region);
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

					if (command.contains(searchString)) {

						if (input.isConnected()) {
							String uniqueOutputName = input.getFromOutput().getOutputTitle() + "_" + i;
							command = Tools.replace(command, searchString, uniqueOutputName);
						} else {
							String parameterValue = "";				
							if (parameter instanceof IntegerParameter) 
								parameterValue += ((IntegerParameter)parameter).getValue(); 
							if (parameter instanceof DoubleParameter) 
								parameterValue += ((DoubleParameter)parameter).getValue();
							if (parameter instanceof BooleanParameter) 
								parameterValue += ((BooleanParameter)parameter).getTrueString();
							if (parameter instanceof StringParameter) 
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
			if (input.isConnected() && (input.getDataType() instanceof ImageDataType) ) {
				bitdepth = ((ImageDataType)input.getFromOutput().getDataType()).getImageBitDepth();
				binaryComparison = bitdepth	& (ij.plugin.filter.PlugInFilter.DOES_STACKS);
				if (binaryComparison != 0) 
					stackParameter = "stack";
			}
		}
		command = Tools.replace(command, searchString, stackParameter);
		return command;
	}
}
