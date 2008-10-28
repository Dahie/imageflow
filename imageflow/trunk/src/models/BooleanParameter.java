/**
 * 
 */
package models;

/**
 * @author danielsenff
 *
 */
public class BooleanParameter extends Parameter {

	protected boolean booleanValue;
	protected boolean booleanValueDefault;
	
	
	public boolean getBooleanValue() {
		return this.booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	/**
	 * @param parameterNumber
	 */
	public BooleanParameter(int parameterNumber) {
		super(parameterNumber);
		this.paraType = 2;
	}

	/**
	 * @param string
	 * @param b
	 * @param string2
	 */
	public BooleanParameter(String displayName, boolean boolParameter, String helpString) {
		this.displayName = displayName;
		this.booleanValue = boolParameter;
		this.booleanValueDefault = boolParameter;
		this.helpString = helpString;
	}

	/**
	 * @param displayName
	 * @param boolParameter
	 * @param helpString
	 */
	public void setParameter(String displayName, boolean boolParameter, String helpString) {
		this.displayName = displayName;
		this.booleanValue = boolParameter;
		this.booleanValueDefault = boolParameter;
		this.helpString = helpString;
	}
	
	
}
