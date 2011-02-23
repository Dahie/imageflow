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
package de.danielsenff.imageflow.models.parameter;

import java.util.HashMap;

/**
 * @author danielsenff
 *
 */
public class IntegerParameter extends AbstractParameter<Integer> {

	/**
	 * @param displayName
	 * @param integerParameter 
	 * @param helpString 
	 * @param options 
	 */
	public IntegerParameter(String displayName, 
			final int integerParameter, 
			String helpString, 
			HashMap<String, Object> options) {
		super("integer", displayName, helpString, options);
		this.value = integerParameter;
		this.defaultValue = integerParameter;
	}

	/**
	 * @param displayName
	 * @param integerParameter
	 * @param helpString
	 */
	public void setParameter(final String displayName, 
			final int integerParameter, 
			final String helpString) {
		this.displayName = displayName;
		this.value = integerParameter;
		this.defaultValue = integerParameter;
		this.helpString = helpString;
		this.paraType = "integer";
	}
	
}
