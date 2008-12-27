/**
 * 
 */
package imageflow.models.parameter;

/**
 * @author danielsenff
 *
 */
public class ParameterFactory {

	/**
	 * Creates the right UnitElement based on the given parameters.
	 * @param displayName 
	 * @param parameter 
	 * @param helpString 
	 * @return 
	 */
	
	// old version should be removed
	// TODO what does truestring, why is it added?
	public static Parameter createParameter(final String displayName, 
			Object parameter, 
			String helpString) {
		if(parameter instanceof String) {
			return new StringParameter(displayName, (String) parameter, helpString);
		} else if (parameter instanceof Double ) {
			return new DoubleParameter(displayName, (Double) parameter, helpString);
		} else if ( parameter instanceof Integer) {
//			return new DoubleParameter(displayName, (Double) parameter, helpString);
			return new IntegerParameter(displayName, (Integer) parameter, helpString);
		} else if (parameter instanceof Boolean) {
			return new BooleanParameter(displayName, (Boolean) parameter, "", helpString);
		} else if (parameter instanceof String[]) {
			return new ChoiceParameter(displayName, (String[]) parameter, 
					((String[]) parameter)[0], helpString);
		}
		System.out.println("Parameter not recognized: " + parameter);
		return null;

	}
	
	/**
	 * @param displayName
	 * @param parameter
	 * @param trueString
	 * @param helpString
	 * @return
	 */
	public static Parameter createParameter(final String displayName, 
			final Object parameter,
			final String trueString,
			final String helpString) {
		if(parameter instanceof String) {
			return new StringParameter(displayName, (String) parameter, helpString);
		} else if (parameter instanceof Double ) {
			return new DoubleParameter(displayName, (Double) parameter, helpString);
		} else if ( parameter instanceof Integer) {
//			return new DoubleParameter(displayName, (Double) parameter, helpString);
			return new IntegerParameter(displayName, (Integer) parameter, helpString);
		} else if (parameter instanceof Boolean) {
			return new BooleanParameter(displayName, (Boolean) parameter, trueString, helpString);
		} else if (parameter instanceof String[]) {
			return new ChoiceParameter(displayName, (String[]) parameter, 
					((String[]) parameter)[0], helpString);
		}
		System.out.println("Parameter not recognized: " + parameter);
		return null;

	}
	
}
