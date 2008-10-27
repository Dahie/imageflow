/**
 * 
 */
package models.unit;

import visualap.Delegate;

/**
 * TODO for the moment this class is pretty much unused and a placeholder, 
 * the GPanel requires it, but nothing else
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
