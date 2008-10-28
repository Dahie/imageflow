package models;

/**
 * Parameter is a defined variable in the ImageJ-syntax, which has an expected type
 * @author danielsenff
 *
 */
public class Parameter {
	
	protected int parameterNumber;
	protected String displayName;  // parameter name that is shown in the unit
	
	//TODO this is legacy, the type is now derived from Inheritance
	protected int 	paraType;    // 0 = double, 1 = String, 2 = boolean 
	
	// the actual value (can be a double, string or boolean)

	String  choiceStringValue;
	String  choiceStringValueDefault;

	protected String helpString;     // help text describing the functionality of this parameter

	/**
	 * 
	 */
	public Parameter() {
	}

	/**
	 * @param parameterNumber
	 */
	public Parameter(final int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}


	/**
	 * @param parameterNumber
	 */
	public void setParameterNumber(final int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}
}