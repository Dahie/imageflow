package de.danielsenff.imageflow.models.parameter;

import java.awt.Color;

/**
 * TODO
 * @author danielsenff
 *
 */
public class ColorParameter extends StringParameter {

	private Color color;
	
	public ColorParameter(final String displayName, final String stringParameter,
			final String helpString) {
		super(displayName, stringParameter, helpString);
		
	}
	
	/**
	 * Returns sets the color.
	 * @param color
	 */
	public void setValue(final Color color) {
		this.color = color;
		super.setValue(color.getRed()+", "+color.getGreen()+", "+color.getBlue());
	}
	

}
