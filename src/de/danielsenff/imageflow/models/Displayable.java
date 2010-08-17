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
package de.danielsenff.imageflow.models;

/**
 * Interface describes Elements that can be displayed on the workflow canvas.
 * @author dahie
 *
 */
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
	 * @param display
	 */
	public void setDisplay(boolean display);
	
	/**
	 * If activate deactivate and vice versa.
	 */
	public void toggleDisplay();
	
}
