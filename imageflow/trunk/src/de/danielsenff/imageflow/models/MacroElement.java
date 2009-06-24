/**
 * 
 */
package de.danielsenff.imageflow.models;

import java.util.ArrayList;

import de.danielsenff.imageflow.helper.Tools;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.DoubleParameter;
import de.danielsenff.imageflow.models.parameter.IntegerParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.StringParameter;

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
		String searchString;

		while (parameterIndex < parameters.size()) {
			
			Parameter parameter = parameters.get(parameterIndex);

			searchString = "PARA_DOUBLE_" + (pd+1);
			String paraType = parameter.getParaType().toLowerCase();
			if(command.contains(searchString) && paraType.equals("double")) { 
				String parameterString = "" + ((DoubleParameter)parameter).getValue();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " Double Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				pd++;
				parameterIndex++;
			}
			searchString = "PARA_STRING_" + (ps+1);
			if(command.contains(searchString) && (paraType.equals("string") || paraType.equals("stringarray"))) {
				String parameterString = "" + ((StringParameter)parameter).getValue();
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
				String parameterString =  (bool) ? ((BooleanParameter)parameter).getTrueString() : ""; 
				//String parameterString = "" + ((BooleanParameter)parameter).getValue();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				pb++;
				parameterIndex++;
			}
			pc++;
			if (parameterIndex != pc) {
				System.err.println("Error in parameters or ImageJ-syntax");
				return command;
			}
				
		}
		// choiceParameter uses the PARA_STRING_x
		return command;
	}
	
	/**
	 * Writes values of input-DataTypes into the command-string.
	 * @param inputs
	 */
	public void parseInputs(ArrayList<Input> inputs) {
		this.commandSyntax = parseInputs(inputs, this.commandSyntax);
	}
	

	/**
	 * Writes values of output-DataTypes into the command-string.
	 * @param inputs
	 */
	public void parseOutputs(ArrayList<Output> outputs) {
		this.commandSyntax = parseOutputs(outputs, this.commandSyntax);
	}

	private static String parseOutputs(ArrayList<Output> outputs, String command) {
		int index = 0, oDbl = 0, oInt = 0;
		String searchString;

		while (index < outputs.size()) {
			
			Output output = outputs.get(index);

			System.out.println(output.getParent());
			System.out.println(outputs.size());
			System.out.println(output.getDataType());
			
			searchString = "OUTPUT_DOUBLE_" + (oDbl+1);
			/*String paraType = input.getParaType().toLowerCase();
			if(command.contains(searchString) && paraType.equals("double")) { 
				String parameterString = "" + ((DoubleParameter)input).getValue();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " Double Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				oDbl++;
				inputIndex++;
			}*/
			searchString = "OUTPUT_INTEGER_" + (oInt+1);
			if(command.contains(searchString) 
					&& output.getDataType() instanceof DataTypeFactory.Integer ) {
				String uniqueOutputName = output.getOutputTitle();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, uniqueOutputName);
				oInt++;
//				index++;
			}
			index++;
	
		}
		return command;
	}
	

	private static String parseInputs(ArrayList<Input> inputs, String command) {
		int index = 0, oDbl = 0, oInt = 0;
		String searchString;

		while (index < inputs.size()) {
			
			Input input = inputs.get(index);

			searchString = "INPUT_DOUBLE_" + (oDbl+1);
			/*String paraType = input.getParaType().toLowerCase();
			if(command.contains(searchString) && paraType.equals("double")) { 
				String parameterString = "" + ((DoubleParameter)input).getValue();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " Double Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				oDbl++;
				inputIndex++;
			}*/
			searchString = "INPUT_INTEGER_" + (oInt+1);
			if(command.contains(searchString) 
					&& input.getDataType() instanceof DataTypeFactory.Integer ) {
				String uniqueOutputName = input.getFromOutput().getOutputTitle();
//				System.out.println("Unit: " + unitID + " Parameter: " + parameterIndex + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, uniqueOutputName);
				oInt++;
			}
			index++;
				
		}
		return command;
	}
	
}
