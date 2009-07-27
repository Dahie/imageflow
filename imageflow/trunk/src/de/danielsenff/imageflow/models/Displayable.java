package de.danielsenff.imageflow.models;

import de.danielsenff.imageflow.models.connection.Output;

public interface Displayable {

	/**
	 * Returns whether or not this unit should display the current state of the image.
	 * @return 
	 */
	public boolean isDisplay();
	
	/**
	 * If activated, the unit will display the current image.
	 * This setting is actually attached to the {@link Output}. 
	 * This is a convenience method for changing all outputs of this
	 * unit at once.
	 * @param isDisplayUnit
	 */
	public void setDisplay(boolean display);
	
}
