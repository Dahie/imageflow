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

/**
 * @author danielsenff
 *
 */
public class IntegerParameter extends AbstractParameter {

	/**
	 * Actual Integer value
	 */
	protected int  integerValue;
	/**
	 * Default Integer value
	 */
	protected int  integerValueDefault;

	
	/**
	 * @param parameterNumber
	 */
	public IntegerParameter(int parameterNumber) {
		super(parameterNumber);
//		super.paraType = 0;
	}

	/**
	 * @param string
	 * @param integerParameter
	 * @param string2
	 */
	public IntegerParameter(String displayName, 
			int integerParameter, 
			String helpString) {
		super("integer", displayName, helpString);
		this.integerValue = integerParameter;
		this.integerValueDefault = integerParameter;
	}

	public void setParameter(String displayName, int integerParameter, String helpString) {
		this.displayName = displayName;
		this.integerValue = integerParameter;
		this.integerValueDefault = integerParameter;
		this.helpString = helpString;
		this.paraType = "integer";
	}
	
	public Integer getValue() {
		return this.integerValue;
	}

	public void setValue(int value) {
		this.integerValue = value;
	}

	public Integer getDefaultValue() {
		return this.integerValueDefault;
	}

	
}
