/**
 * 
 */
package de.danielsenff.imageflow.models.parameter;

import java.util.ArrayList;

/**
 * @author danielsenff
 *
 */
public class ParameterFactory {

	/**
	 * Creates the right UnitElement based on the given parameters.
	 * @param displayName 
	 * @param dataTypeName 
	 * @param value 
	 * @param parameter 
	 * @param helpString 
	 * @return 
	 */
	public static Parameter createParameter(final String displayName, 
			final String dataTypeName,
			Object value, 
			String helpString) {
		return createParameter(displayName, dataTypeName, value, helpString, null, 0);
	}
	
	/**
	 * Creates a {@link Parameter}-Implemenation for the Type of the value.
	 * Certain Parameters require additional arguments.
	 * BooleanParameter: String used when condition/value is true.
	 * ChoiceParameter: List of choices
	 * @param displayName
	 * @param value
	 * @param chosenValue 
	 * @param helpString
	 * @param boolTrueString	This can be any other type that maybe required for certain parameters, like TrueString or chosenValue
	 * @param choiceIndex	Selected index from the list of choices. 
	 * @return
	 */
	public static Parameter createParameter(final String displayName, 
			final String dataTypeName,
			final Object value,
			final String helpString, 
			final String boolTrueString,
			final int choiceIndex) throws IllegalArgumentException {
		
		// see what parameter instance has to be created
		if(value instanceof String) {
			return new StringParameter(displayName, (String) value, helpString);
		} else if(value instanceof String && dataTypeName.toLowerCase().equals("text")) {
			return new TextParameter(displayName, (String) value, helpString);
		} else if (value instanceof Double ) {
			return new DoubleParameter(displayName, (Double) value, helpString);
		} else if (value instanceof Integer) {
			return new IntegerParameter(displayName, (Integer) value, helpString);
		} else if (value instanceof Boolean && boolTrueString != null) {
			return new BooleanParameter(displayName, (Boolean) value, boolTrueString, helpString);
		} else if (value instanceof ArrayList) {
			ArrayList<String> values = (ArrayList) value; // bah, what a construct
			return new ChoiceParameter(displayName, values, 
					values.get(choiceIndex), helpString);
		} else throw new IllegalArgumentException(
				"Parameter "+value+" of type not recognized or not all required arguments");

	}
	
}
