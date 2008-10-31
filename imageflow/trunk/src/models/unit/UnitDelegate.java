/**
 * 
 */
package models.unit;

import java.awt.Point;

import visualap.Delegate;

/**
 * This class is a Meta-Class for the {@link UnitElement}.
 * Basically what this does is describe a UnitElement 
 * and give the interface to instantiate a new UnitElement-Object. 
 * the GPanel requires it, but nothing else
 * @author danielsenff
 *
 */
public abstract class UnitDelegate extends Delegate {

	/**
	 * @param unitName 
	 * @param tooltipText 
	 */
	public UnitDelegate(final String unitName, final String tooltipText) {
		this.name = unitName;
		this.toolTipText = tooltipText;
	}
	
	/**
	 * Instantiates an object of this {@link UnitElement}
	 * @param origin 
	 * @return 
	 */
	public abstract UnitElement createUnit(Point origin);
	

	/**
	 * Set the name.
	 * @param name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the tooltip.
	 * @param toolTipText
	 */
	public void setToolTipText(final String toolTipText) {
		this.toolTipText = toolTipText;
	}

	/**
	 * Get the ToolTip.
	 * @return
	 */
	public String getToolTipText() {
		return toolTipText;
	}
	
}
