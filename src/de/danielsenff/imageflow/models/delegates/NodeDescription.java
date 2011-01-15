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
package de.danielsenff.imageflow.models.delegates;

import java.awt.Color;

/**
 * Interface for NodeDescriptions
 * @author Daniel Senff
 *
 */
public interface NodeDescription {

	/**
	 * Returns true if the described node has any inputs.
	 * @return
	 */
	boolean hasInputs();
	/**
	 * Returns true if the described node has any outputs.
	 * @return
	 */
	boolean hasOutputs();
	/**
	 * Returns true if the described node has any parameters.
	 * @return
	 */
	boolean hasParameters();
	
	/**
	 * Returns the Help string used for displaying a description of the unit.
	 * @return
	 */
	public String getHelpString();

	/**
	 * Returns the unique unit name.
	 * @return
	 */
	String getUnitName();
	/**
	 * Returns the xml file name of the unit definition.
	 * @return
	 */
	String getXMLName();

	/**
	 * 
	 * @return
	 */
	boolean getIsDisplayUnit();

	/**
	 * Returns the Color predefined for the node.
	 * @return
	 */
	Color getColor();
}
