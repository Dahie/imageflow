package de.danielsenff.imageflow.models;

/**
 * Interface for locking objects thereby making them write protected.
 * @author senff
 *
 */
public interface Lockable {

	/**
	 * Is this object write protected?
	 * @return
	 */
	public boolean isLocked();
	
	/**
	 * Sets this object write protected.
	 * @param isLocked
	 */
	public void setLocked(final boolean isLocked);
	
}
