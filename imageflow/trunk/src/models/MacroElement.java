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

	/**
	 * the syntax that is to be used for this unit
	 */
	protected String imageJSyntax;
	/**
	 * syntax with replaced variables
	 */
	protected String commandSyntax;
//	private String inputMacroLog;
	
	
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
	 * @param searchString 
	 * @param parameterString 
	 */
	public void replace(final String searchString, final String parameterString) {
		if(commandSyntax.contains(searchString)) { 
			commandSyntax = Tools.replace(commandSyntax, searchString, parameterString);
		}
	}
	
	
	
	/**
	 * @param inputMacroLog 
	 * @return 
	 */
	public String output(final String inputMacroLog) {
		String outputMacroLog = inputMacroLog + this.commandSyntax;
		
		return outputMacroLog;
	}

	
	/**
	 * @param unit 
	 * @param p 
	 */
	public void parseParameter(UnitElement unit, final int p) {
		this.commandSyntax = parseParameters(unit, this.commandSyntax, p);
	}
	
	public void parseParameters(UnitElement unit, final int p) {
		this.commandSyntax = parseParameters(unit, this.commandSyntax, p);
	}

	private static String parseParameters(UnitElement unit, 
			String command,
			int numParas) {
		int u = unit.getUnitID();

		int p = 0, pc = 0, pd = 0, ps = 0, pi = 0, pb = 0;

		String searchString;

		while (p < numParas) {
			Parameter parameter = (Parameter) unit.getParameters().get(p);

			searchString = "PARA_DOUBLE_" + (pd+1);
			if(command.contains(searchString) && parameter.getParaType().equals("double")) { 
				String parameterString = "" + ((DoubleParameter)parameter).getValue();
				System.out.println("Unit: " + u + " Parameter: " + p + " Double Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				System.out.println(command);
				pd++;
				p++;
			}
			searchString = "PARA_STRING_" + (ps+1);
			if(command.contains(searchString) && (parameter.getParaType().equals("String") || parameter.getParaType().equals("StringArray"))) {
				String parameterString = "" + ((StringParameter)parameter).getValue();
				System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				System.out.println(command);
				ps++;
				p++;
			}
			searchString = "PARA_INTEGER_" + (pi+1);
			if(command.contains(searchString) && parameter.getParaType().equals("int")) {
				String parameterInteger = "" + ((IntegerParameter)parameter).getValue();
				System.out.println("Unit: " + u + " Parameter: " + p + " Integer Parameter: " + parameterInteger);
				command = Tools.replace(command, searchString, parameterInteger);
				System.out.println(command);
				pi++;
				p++;
			}
			searchString = "PARA_BOOLEAN_" + (pb+1);
			if(command.contains(searchString) && parameter.getParaType().equals("boolean")) {
				boolean bool = ((BooleanParameter)parameter).getValue();
				String parameterString =  (bool == true) ? ((BooleanParameter)parameter).getTrueString():""; 
				//String parameterString = "" + ((BooleanParameter)parameter).getValue();
				System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				System.out.println(command);
				pb++;
				p++;
			}
			pc++;
			if (p != pc) {
				System.err.println("Error in parameters or ImageJ-syntax");
				return command;
			}
				
		}
		// choiceParameter uses the PARA_STRING_x
		return command;
	}

//	private static String parseParameters(UnitElement unit, 
//			String command,
//			int p) {
//		Parameter parameter = (Parameter) unit.getParameters().get(p);
//		int u = unit.getUnitID();
//
//		String searchString;
//		
//		searchString = "PARA_DOUBLE_" + (p+1);
//		if(command.contains(searchString)) { 
//			String parameterString = "" + ((DoubleParameter)parameter).getValue();
//			System.out.println("Unit: " + u + " Parameter: " + p + " Double Parameter: " + parameterString);
//			command = Tools.replace(command, searchString, parameterString);
//			System.out.println(command);
//		}
//		searchString = "PARA_STRING_" + (p+1);
//		if(command.contains(searchString)) {
//			String parameterString = "" + ((StringParameter)parameter).getValue();
//			System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
//			command = Tools.replace(command, searchString, parameterString);
//			System.out.println(command);
//		}
//		searchString = "PARA_INTEGER_" + (p+1);
//		if(command.contains(searchString)) {
//			String parameterInteger = "" + ((IntegerParameter)parameter).getValue();
//			System.out.println("Unit: " + u + " Parameter: " + p + " Integer Parameter: " + parameterInteger);
//			command = Tools.replace(command, searchString, parameterInteger);
//			System.out.println(command);
//		}
//		searchString = "PARA_BOOLEAN_" + (p+1);
//		if(command.contains(searchString)) {
//			boolean bool = ((BooleanParameter)parameter).getValue();
//			String parameterString =  (bool == true) ? ((BooleanParameter)parameter).getTrueString():""; 
//			//String parameterString = "" + ((BooleanParameter)parameter).getValue();
//			System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
//			command = Tools.replace(command, searchString, parameterString);
//			System.out.println(command);
//		}
//		// choiceParameter uses the PARA_STRING_x
//		return command;
//	}



	/**
	 * Returns the command-syntax with substituted variables.
	 * @return
	 */
	public String getCommandSyntax() {
		return this.commandSyntax;
	}



	public void reset() {
		this.commandSyntax = this.imageJSyntax;
	}
	
}
