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
 * @author Daniel Senff
 *
 */
public class BooleanParameter extends AbstractParameter {

	/**
	 * The actual value of this Parameter;
	 */
	protected boolean booleanValue;
	/**
	 * The default value of this Parameter.
	 */
	protected boolean booleanValueDefault;
	
	/**
	 * The string that is inserted in the imagej-syntax if the condidation is true.
	 */
	protected String trueString;

	/**
	 * @param displayName 
	 * @param boolParameter 
	 * @param trueString 
	 * @param helpString 
	 */
	public BooleanParameter(final String displayName, 
			final boolean boolParameter, 
			final String trueString,
			final String helpString) {
		this.displayName = displayName;
		this.booleanValue = boolParameter;
		this.booleanValueDefault = boolParameter;
		this.trueString = trueString;
		this.helpString = helpString;
		this.paraType = "boolean";
	}

	/**
	 * @param displayName
	 * @param boolParameter
	 * @param helpString
	 */
	public void setParameter(String displayName, boolean boolParameter, String helpString) {
		this.displayName = displayName;
		this.booleanValue = boolParameter;
		this.booleanValueDefault = boolParameter;
		this.helpString = helpString;
		this.paraType = "boolean";
	}
	

	/**
	 * Get the value.
	 * @return
	 */
	public Boolean getValue() {
		return this.booleanValue;
	}

	/**
	 * Set the value.
	 * @param booleanValue
	 */
	public void setValue(final boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	
	
	/**
	 * This is the string to insert in the macro-syntax, if the condition is true
	 * @return
	 */
	public String getTrueString() {
		return trueString;
	}

	public Boolean getDefaultValue() {
		return this.booleanValueDefault;
	}

}
