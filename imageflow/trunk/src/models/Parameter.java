package models;

/**
 * Parameter is a defined variable in the ImageJ-syntax, which has an expected type
 * @author danielsenff
 *
 */
public abstract class Parameter {
	
	/**
	 * Index of the parameter in the Unit.
	 */
	protected int parameterNumber;
	/**
	 * parameter name that is shown in the unit
	 */
	protected String displayName; 
	
	protected boolean textArea = false;


	public boolean isTextArea() {
		return textArea;
	}

	public void setTextArea(boolean textfield) {
		this.textArea = textfield;
	}

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

	public abstract Object getValue();
	
	/**
	 * Returns the name of the Parameter. 
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
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