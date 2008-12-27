/**
 * 
 */
package imageflow.models.parameter;

/**
 * @author danielsenff
 *
 */
public class DoubleParameter extends AbstractParameter {

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
		this.paraType = "double";
	}

	public void setParameter(String displayName, double doubleParameter, String helpString) {
		this.displayName = displayName;
		this.doubleValue = doubleParameter;
		this.doubleValueDefault = doubleParameter;
		this.helpString = helpString;
	}
	
	public Double getValue() {
		return this.doubleValue;
	}

	public void setValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	
}
