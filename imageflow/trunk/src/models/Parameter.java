package models;

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
	 * Get the help description of this Parameter.
	 * @return
	 */
	public String getTrueString();
	
	/**
	 * @param parameterNumber
	 */
	public void setParameterNumber(final int parameterNumber);
	
}
