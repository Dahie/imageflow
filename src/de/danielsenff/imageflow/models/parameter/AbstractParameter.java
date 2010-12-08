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
 * Parameter is a defined variable in the ImageJ-syntax, which has an expected type
 * @author Daniel Senff
 *
 */
public abstract class AbstractParameter implements Parameter {
	
	/**
	 * Index of the parameter in the Unit.
	 */
	protected int parameterNumber;
	/**
	 * parameter name that is shown in the unit
	 */
	protected String displayName; 
	
	/**
	 * Name of the parameter type
	 */
	protected String paraType;

	/**
	 * help text describing the functionality of this parameter
	 */
	protected String helpString;


	/**
	 * 
	 */
	public AbstractParameter(String paraType, String displayName, String helpString) {
		this.displayName = displayName;
		this.paraType = paraType;
		this.helpString = helpString;
	}

	/**
	 * @param parameterNumber
	 */
	public AbstractParameter(final int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}

	
	
	//public void onChange();
	
	/**
	 * Returns the name of the Parameter. 
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	public String getParaType() {
		return paraType;
	}
	/**
	 * Get the help description of this Parameter.
	 * @return
	 */
	public String getHelpString() {
		return helpString;
	}

	
	/**
	 * @param parameterNumber
	 */
	public void setParameterNumber(final int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}
}