/**
 * 
 */
package Models.unit;

import visualap.Delegate;

/**
 * @author danielsenff
 *
 */
public class UnitDelegate extends Delegate {

	protected UnitElement unit;

	/**
	 * 
	 */
	public UnitDelegate() {
		this.unit = UnitFactory.createSourceUnit();
	}
	
	
	/**
	 * @return the unit
	 */
	public UnitElement getUnit() {
		return this.unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(UnitElement unit) {
		this.unit = unit;
	}
	
}
