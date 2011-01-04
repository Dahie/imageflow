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
public class BooleanParameter extends AbstractParameter<Boolean> {

	/**
	 * The actual value of this Parameter;
	 */
	//protected boolean booleanValue;
	/**
	 * The default value of this Parameter.
	 */
	//protected boolean booleanValueDefault;
	
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
		super("boolean", displayName, helpString);
		this.value = boolParameter;
		this.defaultValue = boolParameter;
		this.trueString = trueString;
	}

	/**
	 * @param displayName
	 * @param boolParameter
	 * @param helpString
	 */
	public void setParameter(String displayName, boolean boolParameter, String helpString) {
		this.displayName = displayName;
		this.value = boolParameter;
		this.defaultValue = boolParameter;
		this.helpString = helpString;
		this.paraType = "boolean";
	}
	

	/**
	 * This is the string to insert in the macro-syntax, if the condition is true
	 * @return
	 */
	public String getTrueString() {
		return trueString;
	}
}
