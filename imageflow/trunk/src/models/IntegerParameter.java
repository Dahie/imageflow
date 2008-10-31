/**
 * 
 */
package models;

/**
 * @author danielsenff
 *
 */
public class IntegerParameter extends Parameter {

	/**
	 * Actual Integer value
	 */
	protected int  integerValue;
	/**
	 * Default Integer value
	 */
	protected int  integerValueDefault;

	
	/**
	 * @param parameterNumber
	 */
	public IntegerParameter(int parameterNumber) {
		super(parameterNumber);
//		super.paraType = 0;
	}

	/**
	 * @param string
	 * @param integerParameter
	 * @param string2
	 */
	public IntegerParameter(String displayName, int integerParameter, String helpString) {
		this.displayName = displayName;
		this.integerValue = integerParameter;
		this.integerValueDefault = integerParameter;
		this.helpString = helpString;
	}

	public void setParameter(String displayName, int integerParameter, String helpString) {
		this.displayName = displayName;
		this.integerValue = integerParameter;
		this.integerValueDefault = integerParameter;
		this.helpString = helpString;
	}
	
	public Integer getValue() {
		return this.integerValue;
	}

	public void setValue(int value) {
		this.integerValue = value;
	}

	
}
