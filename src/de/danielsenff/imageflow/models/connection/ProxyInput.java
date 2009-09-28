package de.danielsenff.imageflow.models.connection;

import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * This is a proxy for Inputs.
 * This can embed an existing input, but attach it someplace else.
 * @author danielsenff
 *
 */
public class ProxyInput extends Input {

	/**
	 * encapsulated Input
	 */
	private Input embeddedInput;
	
	/**
	 * 
	 * @param embeddedInput
	 * @param unit
	 * @param i
	 */
	public ProxyInput(Input embeddedInput, UnitElement unit, int i) {
		super(embeddedInput.getDataType(), unit, i, true);
		setupInput(embeddedInput.getName(), embeddedInput.getShortDisplayName(), embeddedInput.isNeedToCopyInput());
		this.embeddedInput = embeddedInput;
	}
	
	/**
	 * Returns the embedded {@link Input}.
	 * @return
	 */
	public Input getEmbeddedInput() {
		return this.embeddedInput;
	}

	
}
