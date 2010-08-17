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
package de.danielsenff.imageflow.models.datatype;

import visualap.Pin;

/**
 * A DataType only describes what kind of data can be used on a {@link Pin}
 * It does not actually store the value passed along in the workflow.
 * @author Daniel Senff
 *
 */
public interface DataType extends Cloneable {

	/**
	 * Compares two DataTypes to see if they are compatible.
	 * @param compareType
	 * @return
	 */
	public boolean isCompatible(DataType compareType);
	
	/**
	 * Clone this DataType.
	 * @return
	 */
	public DataType clone();
	
	/**
	 * Returns the simple Class name of this DataType. 
	 * @return
	 */
	public String getName();
}
