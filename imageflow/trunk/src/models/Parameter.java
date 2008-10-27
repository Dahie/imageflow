package Models;

public class Parameter {
	
	protected int parameterNumber;
	protected String displayName;  // parameter name that is shown in the unit
	
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

	public Parameter(int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}


	/**
	 * @param parameterNumber
	 */
	public void setParameterNumber(int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}
}