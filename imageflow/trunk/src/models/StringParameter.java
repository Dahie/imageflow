/**
 * 
 */
package models;

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
	 * @param stringParameter
	 * @param helpString
	 */
	public StringParameter(String displayName, String stringParameter, String helpString) {
		this.displayName = displayName;
		this.stringValue = stringParameter;
		this.stringValueDefault = stringParameter;
		this.helpString = helpString;
		this.paraType = "String";
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
	
}
