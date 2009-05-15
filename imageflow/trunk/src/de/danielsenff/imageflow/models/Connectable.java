package de.danielsenff.imageflow.models;

import de.danielsenff.imageflow.models.unit.UnitElement;
import graph.Pin;

/**
 * Interface for {@link Pin} to connect to other pins
 * @author danielsenff
 *
 */
public interface Connectable {

	
	/**
	 * Connects to this {@link UnitElement} and the given pinNumber.
	 * The implementation handles whether this is for {@link Input} or {@link Output}
	 * @param unit 
	 * @param pinNumber 
	 */
	public void connectTo(final UnitElement unit, final int pinNumber);
	
	/**
	 * Connects to this {@link UnitElement} and the given {@link Pin}.
	 * @param unit 
	 * @param pin 
	 */
	public void connectTo(final Pin pin);
	
	/**
	 * Returns whether or not this Input is connected.
	 * @return 
	 */
	public boolean isConnected();
	
	
	/**
	 * Checks if this Input is connected with the given {@link Output}
	 * @param pin 
	 * @return
	 */
	public boolean isConnectedWith(Pin pin);


	/**
	 * Resets the this Input, so that it is unconnected.
	 */
	public void disconnectAll();
	
}
