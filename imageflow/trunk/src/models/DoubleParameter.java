/**
 * 
 */
package models;

/**
 * @author danielsenff
 *
 */
public class DoubleParameter extends Parameter {

	protected double  doubleValue;
	protected double  doubleValueDefault;


	/**
	 * @param string
	 * @param doubleParameter
	 * @param string2
	 */
	public DoubleParameter(String displayName, double doubleParameter, String helpString) {
		this.displayName = displayName;
		this.doubleValue = doubleParameter;
		this.doubleValueDefault = doubleParameter;
		this.helpString = helpString;
	}

	public void setParameter(String displayName, double doubleParameter, String helpString) {
		this.displayName = displayName;
		this.doubleValue = doubleParameter;
		this.doubleValueDefault = doubleParameter;
		this.helpString = helpString;
	}
	
	public double getDoubleValue() {
		return this.doubleValue;
	}

	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	
}
