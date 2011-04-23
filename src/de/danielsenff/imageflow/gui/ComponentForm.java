/**
 * Copyright (C) 2008-2011 Daniel Senff
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

package de.danielsenff.imageflow.gui;

import java.awt.Component;

import javax.swing.JSeparator;

import de.danielsenff.imageflow.models.parameter.Parameter;

public interface ComponentForm {

	public Component add(final Component component);
	
	/**
	 * Add a message line.
	 * @param message
	 */
	public void addMessage(final String message);
	
	/**
	 * Add a {@link JSeparator} to the dialog.
	 */
	public void addSeparator();
	
	/**
	 * Adds a new Form-Element using the label and the component.
	 * @param label
	 * @param component
	 */
	public void addForm(final String label, final Component component);
	
	public void add(final Parameter parameter);
	
}
