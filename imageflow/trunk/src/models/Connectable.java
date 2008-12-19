package models;

import models.unit.UnitElement;
import graph.Pin;

/**
 * Interface for {@link Pin} to connect to other pins
 * @author danielsenff
 *
 */
public interface Connectable {

	
	/**
	 * Sets the connection between this input and an output.
	 * @param unit 
	 * @param pinNumber 
	 */
	public void connectTo(final UnitElement unit, final int pinNumber);
	
	public void connectTo(final UnitElement unit, final Pin pin);
	
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
	public void disconnect();
	
}
