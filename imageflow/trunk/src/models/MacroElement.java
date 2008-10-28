/**
 * 
 */
package models;

import helper.Tools;
import models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class MacroElement {

	protected String imageJSyntax;		// the syntax that is to be used for this unit
	protected String commandSyntax; 	// syntax with replaced variables
	
	
	/**
	 * 
	 */
	public MacroElement(String unitsImageJSyntax) {
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
	 * 
	 */
	public void replace(final String searchString, final String parameterString) {
		if(commandSyntax.contains(searchString)) { 
			commandSyntax = Tools.replace(commandSyntax, searchString, parameterString);
			System.out.println(commandSyntax);
		}
	}
	
	
	
	/**
	 * 
	 */
	public String output(String inputMacroLog) {
		String outputMacroLog = inputMacroLog + this.commandSyntax;
		
		return outputMacroLog;
	}

	
	/**
	 * 
	 */
	public void parseParameter(UnitElement unit, int p) {
		this.commandSyntax = parseParameters(unit, this.commandSyntax, p);
	}
	

	private static String parseParameters(UnitElement unit, 
			String command,
			int p) {
		Parameter parameter = (Parameter) unit.getParameters().get(p);
		int u = unit.getUnitID();

		String searchString;
		
		searchString = "PARA_DOUBLE_" + (p+1);
		if(command.contains(searchString)) { 
			String parameterString = "" + ((DoubleParameter)parameter).getDoubleValue();
			System.out.println("Unit: " + u + " Parameter: " + p + " Double Parameter: " + parameterString);
			command = Tools.replace(command, searchString, parameterString);
			System.out.println(command);
		}
		searchString = "PARA_STRING_" + (p+1);
		if(command.contains(searchString)) {
			String parameterString = "" + ((StringParameter)parameter).getStringValue();
			System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
			command = Tools.replace(command, searchString, parameterString);
			System.out.println(command);
		}
		searchString = "PARA_BOOL_" + (p+1);
		if(command.contains(searchString)) {
			String parameterString = "" + ((BooleanParameter)parameter).getBooleanValue();
			System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
			command = Tools.replace(command, searchString, parameterString);
			System.out.println(command);
		}
		// boolean fehlt noch
		return command;
	}



	/**
	 * Returns the command-syntax with substituted variables.
	 * @return
	 */
	public String getCommandSyntax() {
		return this.commandSyntax;
	}
	
}
