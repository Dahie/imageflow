/**
 * 
 */
package models;

/**
 * @author danielsenff
 *
 */
public class IntegerParameter extends Parameter {

	protected int  integerdoubleValue;
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
		this.integerdoubleValue = integerParameter;
		this.integerValueDefault = integerParameter;
		this.helpString = helpString;
	}

	public void setParameter(String displayName, int integerParameter, String helpString) {
		this.displayName = displayName;
		this.integerdoubleValue = integerParameter;
		this.integerValueDefault = integerParameter;
		this.helpString = helpString;
	}
	
	public double getDoubleValue() {
		return this.integerdoubleValue;
	}

	public void setIntegerValue(int value) {
		this.integerdoubleValue = value;
	}
	
}
