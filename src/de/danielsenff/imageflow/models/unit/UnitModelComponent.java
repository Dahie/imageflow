/**
 * Copyright (C) 2008-2010 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Interface for a view-class for {@link UnitElement}-Models. 
 * This Class gives the necessary method definitions required
 * for representing units on the workspace.
 * @author Daniel Senff
 *
 */
public interface UnitModelComponent {

	public enum Size {BIG, MEDIUM, SMALL};
	
	/**
	 * Draws the unit-element in an image.
	 * @param size
	 * @return
	 */
	public BufferedImage getImage(final Size size);
	public BufferedImage getImage();
	
	
	public Graphics2D paintBigIcon(final Graphics2D g2);
	public Graphics2D paintMediumIcon(Graphics2D g2);
	public Graphics2D paintSmallIcon(Graphics2D g22);
	
	public Image getIcon();
	public void setIcon(final Image icon);
}
