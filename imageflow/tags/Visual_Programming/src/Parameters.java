
class Parameters {
	
	int parameterNumber;
	String 	displayName;  // parameter name that is shown in the unit
	
	int 	paraType;    // 0 = double, 1 = String, 2 = boolean 
	
	// the actual value (can be a double, string or boolean)
	double  doubleValue;
	double  doubleValueDefault;

	String  stringValue;
	String  stringValueDefault;

	String  choiceStringValue;
	String  choiceStringValueDefault;

	boolean booleanValue;
	boolean booleanValueDefault;
		
	String helpString;     // help text describing the functionality of this parameter

	public void setParameter(String displayName, double doubleParameter, String helpString) {
		this.displayName = displayName;
		this.doubleValue = doubleParameter;
		this.doubleValueDefault = doubleParameter;
		this.helpString = helpString;
		this.paraType = 0;
	}

	public void setParameter(String displayName, String stringParameter, String helpString) {
		this.displayName = displayName;
		this.stringValue = stringParameter;
		this.stringValueDefault = stringParameter;
		this.helpString = helpString;
		this.paraType = 1;
	}
	
	public void setParameter(String displayName, boolean boolParameter, String helpString) {
		this.displayName = displayName;
		this.booleanValue = boolParameter;
		this.booleanValueDefault = boolParameter;
		this.helpString = helpString;
		this.paraType = 2;
	}

	public Parameters(int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}
}