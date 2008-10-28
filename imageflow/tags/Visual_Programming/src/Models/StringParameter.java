/**
 * 
 */
package Models;

/**
 * @author danielsenff
 *
 */
public class StringParameter extends Parameter {

	

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

	String  stringValue;
	public String getStringValue() {
		return this.stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	String  stringValueDefault;
	
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
