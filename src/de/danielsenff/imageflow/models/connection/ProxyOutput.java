/**
 * 
 */
package de.danielsenff.imageflow.models.connection;

import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class ProxyOutput extends Output {

	/**
	 * encapsulated Output
	 */
	private Output embeddedOutput;
	
	/**
	 * @param embeddedOutput
	 * @param parentNode
	 * @param i
	 */
	public ProxyOutput(final Output embeddedOutput, 
			final UnitElement parentNode, 
			final int i) {
		super(embeddedOutput.getName(), 
				embeddedOutput.getShortDisplayName(), 
				embeddedOutput.getDataType(), 
				parentNode, i);
		setDoDisplay(embeddedOutput.isDoDisplay());
		setDoDisplaySilent(embeddedOutput.isDoDisplaySilent());
		this.embeddedOutput = embeddedOutput;
	}
	
	/**
	 * Returns the embedded {@link Input}.
	 * @return
	 */
	public Output getEmbeddedOutput() {
		return this.embeddedOutput;
	}
}
