package de.danielsenff.imageflow.models.parameter;

/**
 * Parameter is a defined variable in the ImageJ-syntax, which has an expected type
 * @author danielsenff
 *
 */
public abstract class AbstractParameter implements Parameter {
	
	/**
	 * Index of the parameter in the Unit.
	 */
	protected int parameterNumber;
	/**
	 * parameter name that is shown in the unit
	 */
	protected String displayName; 
	
	/**
	 * Name of the parameter type
	 */
	protected String paraType;

	/**
	 * help text describing the functionality of this parameter
	 */
	protected String helpString;


	/**
	 * 
	 */
	public AbstractParameter() {
	}

	/**
	 * @param parameterNumber
	 */
	public AbstractParameter(final int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}

	
	/**
	 * Returns the name of the Parameter. 
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	public String getParaType() {
		return paraType;
	}
	/**
	 * Get the help description of this Parameter.
	 * @return
	 */
	public String getHelpString() {
		return helpString;
	}

	
	/**
	 * @param parameterNumber
	 */
	public void setParameterNumber(final int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}
}