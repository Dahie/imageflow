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
public class StringParameter extends AbstractParameter<String> {

	/**
	 * @param displayName
	 * @param stringValue 
	 * @param defaultValue 
	 * @param helpString
	 */
	public StringParameter(String displayName, 
			String stringValue, 
			String defaultValue, 
			String helpString,
			final HashMap<String, Object> options) {
		super("String", displayName, helpString, options);
		this.value = stringValue;
		this.defaultValue = defaultValue;
	}

	/**
	 * @param displayName
	 * @param stringValue
	 * @param helpString
	 */
	public StringParameter(final String displayName, 
			final String stringValue, 
			final String helpString,
			final HashMap<String, Object> options) {
		this(displayName, stringValue, stringValue, helpString, options);
	}
	
	
	/**
	 * @param displayName
	 * @param stringParameter
	 * @param helpString
	 */
	public void setParameter(String displayName, String stringParameter, String helpString) {
		this.displayName = displayName;
		this.value = stringParameter;
		this.defaultValue = stringParameter;
		this.helpString = helpString;
	}

}
