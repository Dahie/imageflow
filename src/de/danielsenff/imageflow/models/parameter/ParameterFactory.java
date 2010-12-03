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

import java.util.ArrayList;

/**
 * @author danielsenff
 *
 */
public class ParameterFactory {

	/**
	 * Creates the right UnitElement based on the given parameters.
	 * @param displayName 
	 * @param dataTypeName 
	 * @param value 
	 * @param parameter 
	 * @param helpString 
	 * @return 
	 */
	public static Parameter createParameter(final String displayName, 
			final String dataTypeName,
			Object value, 
			String helpString) {
		return createParameter(displayName, dataTypeName, value, helpString, null, 0);
	}
	
	/**
	 * Create a new ChoiceParameter.
	 * @param displayName
	 * @param dataTypeName
	 * @param value
	 * @param helpString
	 * @param choiceIndex
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Parameter createChoiceParameter(final String displayName, 
			final String dataTypeName,
			final ArrayList<String> value,
			final String helpString, 
			final int choiceIndex) throws IllegalArgumentException {
		ArrayList<String> values = (ArrayList<String>) value; // bah, what a construct
		if (values.isEmpty())
			return new ChoiceParameter(displayName, values, 
					"", helpString);
		else
			return new ChoiceParameter(displayName, values, 
					values.get(choiceIndex), helpString);
	}
	
	/**
	 * Creates a {@link Parameter}-Implementation for the Type of the value.
	 * Certain Parameters require additional arguments.
	 * BooleanParameter: String used when condition/value is true.
	 * ChoiceParameter: List of choices
	 * @param displayName
	 * @param dataTypeName 
	 * @param value
	 * @param chosenValue 
	 * @param helpString
	 * @param boolTrueString	This can be any other type that maybe required for certain parameters, like TrueString or chosenValue
	 * @param choiceIndex	Selected index from the list of choices. 
	 * @return
	 * @throws IllegalArgumentException 
	 */
	public static Parameter createParameter(final String displayName, 
			final String dataTypeName,
			final Object value,
			final String helpString, 
			final String boolTrueString,
			final int choiceIndex) throws IllegalArgumentException {
		
		// see what parameter instance has to be created
		if(value instanceof String) {
			return new StringParameter(displayName, (String) value, helpString);
		} else if(value instanceof String && dataTypeName.toLowerCase().equals("text")) {
			return new TextParameter(displayName, (String) value, helpString);
		} else if (value instanceof Double ) {
			return new DoubleParameter(displayName, (Double) value, helpString);
		} else if (value instanceof Integer) {
			return new IntegerParameter(displayName, (Integer) value, helpString);
		} else if (value instanceof Boolean && boolTrueString != null) {
			return new BooleanParameter(displayName, (Boolean) value, boolTrueString, helpString);
		} else if (value instanceof ArrayList) {
			ArrayList<String> values = (ArrayList) value; // bah, what a construct
			if (values.isEmpty()) {
				return new ChoiceParameter(displayName, values, 
						"", helpString);
			} else {
				return new ChoiceParameter(displayName, values, 
						values.get(choiceIndex), helpString);
			}
			
		} else throw new IllegalArgumentException(
				"Parameter "+value+" of type not recognized or not all required arguments");

	}
	
}
