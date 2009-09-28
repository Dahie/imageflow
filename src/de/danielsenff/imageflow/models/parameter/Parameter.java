package de.danielsenff.imageflow.models.parameter;

/**
 * Parameter Interface
 * @author danielsenff
 *
 */
public interface Parameter {

	/**
	 * Returns the Value of this Parameter as an {@link Object}.
	 * @return
	 */
	public abstract Object getValue();
	
	public abstract Object getDefaultValue();
	
	/**
	 * Returns the name of the Parameter. 
	 * @return
	 */
	public String getDisplayName();
	
	/*
	 * Returns the type of the Parameter
	 */
	/**
	 * @return
	 */
	public String getParaType();
	
	/**
	 * Get the help description of this Parameter.
	 * @return
	 */
	public String getHelpString();
	
	/**
	 * @param parameterNumber
	 */
	public void setParameterNumber(final int parameterNumber);
	
}
