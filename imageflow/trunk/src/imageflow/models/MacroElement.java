/**
 * 
 */
package imageflow.models;

import imageflow.models.parameter.AbstractParameter;
import imageflow.models.parameter.BooleanParameter;
import imageflow.models.parameter.DoubleParameter;
import imageflow.models.parameter.IntegerParameter;
import imageflow.models.parameter.StringParameter;
import imageflow.models.unit.UnitElement;
import helper.Tools;

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
	public void parseParameters(final UnitElement unit, final int p) {
		this.commandSyntax = parseParameters(unit, this.commandSyntax, p);
	}

	private static String parseParameters(UnitElement unit, 
			String command,
			int numParas) {
		int u = unit.getUnitID();

		int p = 0, pc = 0, pd = 0, ps = 0, pi = 0, pb = 0;

		String searchString;

		while (p < numParas) {
			AbstractParameter parameter = (AbstractParameter) unit.getParameters().get(p);

			searchString = "PARA_DOUBLE_" + (pd+1);
			String paraType = parameter.getParaType().toLowerCase();
			if(command.contains(searchString) && paraType.equals("double")) { 
				String parameterString = "" + ((DoubleParameter)parameter).getValue();
				System.out.println("Unit: " + u + " Parameter: " + p + " Double Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				System.out.println(command);
				pd++;
				p++;
			}
			searchString = "PARA_STRING_" + (ps+1);
			if(command.contains(searchString) && (paraType.equals("string") || paraType.equals("stringarray"))) {
				String parameterString = "" + ((StringParameter)parameter).getValue();
				System.out.println("Unit: " + u + " Parameter: " + p + " String Parameter: " + parameterString);
				command = Tools.replace(command, searchString, parameterString);
				System.out.println(command);
				ps++;
				p++;
			}
			searchString = "PARA_INTEGER_" + (pi+1);
			if(command.contains(searchString) && paraType.equals("integer")) {
				String parameterInteger = "" + ((IntegerParameter)parameter).getValue();
				System.out.println("Unit: " + u + " Parameter: " + p + " Integer Parameter: " + parameterInteger);
				command = Tools.replace(command, searchString, parameterInteger);
				System.out.println(command);
				pi++;
				p++;
			}
			searchString = "PARA_BOOLEAN_" + (pb+1);
			if(command.contains(searchString) && paraType.equals("boolean")) {
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



	/**
	 * Resets the MacroElement. All parsed commands will be removed.
	 */
	public void reset() {
		this.commandSyntax = this.imageJSyntax;
	}
	
}
