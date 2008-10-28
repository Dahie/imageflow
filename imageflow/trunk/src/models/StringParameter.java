/**
 * 
 */
package models;

/**
 * @author danielsenff
 *
 */
public class StringParameter extends Parameter {

	protected String  stringValue;	
	protected String  stringValueDefault;

	/**
	 * @param parameterNumber
	 */
	public StringParameter(int parameterNumber) {
		super(parameterNumber);
		super.paraType = 1;
	}
	
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

	}

	
	public String getStringValue() {
		return this.stringValue;
	}

	public void setStringValue(String stringValue) {
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
		this.paraType = 1;
	}
	
}
