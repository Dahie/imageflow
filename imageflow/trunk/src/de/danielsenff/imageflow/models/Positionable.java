package de.danielsenff.imageflow.models;

import java.awt.Dimension;
import java.awt.Point;

import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;

public interface Positionable {

	public void setOrigin(Point point);
	
	public Point getOrigin();
	
	/**
	 * 
	 * @param dimension
	 */
	public void setDimension(Dimension dimension);

	/**
	 * Returns the element dimensions.
	 * @return
	 */
	public Dimension getDimension();
	
	public void setCompontentSize(Size size);
	
	
	
}
