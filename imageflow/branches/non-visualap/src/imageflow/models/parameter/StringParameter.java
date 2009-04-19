/**
 * 
 */
package imageflow.models.parameter;

/**
 * @author danielsenff
 *
 */
public class StringParameter extends AbstractParameter {

	/**
	 * Actual String value
	 */
	protected String  stringValue;
	/**
	 * Default String value
	 */
	protected String  stringValueDefault;

	

	/**
	 * @param displayName
	 * @param stringValue 
	 * @param defaultValue 
	 * @param helpString
	 */
	public StringParameter(String displayName, 
			String stringValue, 
			String defaultValue, 
			String helpString) {
		this.displayName = displayName;
		this.stringValue = stringValue;
		this.stringValueDefault = defaultValue;
		this.helpString = helpString;
		this.paraType = "String";
	}

	/**
	 * @param displayName
	 * @param stringValue
	 * @param helpString
	 */
	public StringParameter(final String displayName, 
			final String stringValue, 
			final String helpString) {
		this(displayName, stringValue, stringValue, helpString);
	}
	
	
	/**
	 * Get the String parameter value
	 * @return
	 */
	public String getValue() {
		return this.stringValue;
	}

	/**
	 * Set String parameter.
	 * @param stringValue
	 */
	public void setValue(final String stringValue) {
		this.stringValue = stringValue;
	}

	
	/**
	 * @param displayName
	 * @param stringParameter
	 * @param helpString
	 */
	public void setParameter(String displayName, String stringParameter, String helpString) {
		this.displayName = displayName;
		this.stringValue = stringParameter;
		this.stringValueDefault = stringParameter;
		this.helpString = helpString;
	}

	public String getDefaultValue() {
		return this.stringValueDefault;
	}
	
}
