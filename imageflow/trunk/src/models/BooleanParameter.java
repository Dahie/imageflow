/**
 * 
 */
package models;

/**
 * @author danielsenff
 *
 */
public class BooleanParameter extends Parameter {

	/**
	 * The actual value of this Parameter;
	 */
	protected boolean booleanValue;
	/**
	 * The default value of this Parameter.
	 */
	protected boolean booleanValueDefault;
	
	

	/**
	 * @param displayName 
	 * @param boolParameter 
	 * @param helpString 
	 */
	public BooleanParameter(final String displayName, 
			final boolean boolParameter, 
			String helpString) {
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
	

	/**
	 * Get the value.
	 * @return
	 */
	public Boolean getValue() {
		return this.booleanValue;
	}

	/**
	 * Set the value.
	 * @param booleanValue
	 */
	public void setValue(final boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

}
