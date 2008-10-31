package models;

/**
 * Parameter is a defined variable in the ImageJ-syntax, which has an expected type
 * @author danielsenff
 *
 */
public class Parameter {
	
	/**
	 * Index of the parameter in the Unit.
	 */
	protected int parameterNumber;
	/**
	 * parameter name that is shown in the unit
	 */
	protected String displayName; 

	/* 
	 * legacy, unused
	 */
	String  choiceStringValue;
	String  choiceStringValueDefault;

	/**
	 * help text describing the functionality of this parameter
	 */
	protected String helpString;

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