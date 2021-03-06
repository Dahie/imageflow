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
 * @author dahie
 *
 */
public interface Selectable {

	/**
	 * Set the selection state of this object.
	 * @param sel
	 */
	public void setSelected(boolean sel);
	/**
	 * Return the selection state of this object.
	 * @return
	 */
	public boolean isSelected();
	
	/*public void addSelectionListener(final ChangeListener listener);

	public void notifySelectionListener(final ChangeListener listener);

	public void notifySelectionListeners();

	public void removeSelectionListener(final ChangeListener listener);*/

}
