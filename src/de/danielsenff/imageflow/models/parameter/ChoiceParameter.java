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
 * Works like an Enumeration. Contains a List of possible values.
 * The actual value is an element of this list.
 * @author danielsenff
 *
 */
public class ChoiceParameter extends StringParameter {
	/**
	 * Delimiter that splits the StringArray
	 */
	public static final String DELIMITER = ";";
	private final ArrayList<String> choiceValues;
	
	/**
	 * @param displayName
	 * @param choices
	 * @param choiceValue
	 * @param helpString
	 */
	public ChoiceParameter(final String displayName, 
			final ArrayList<String> choices,
			final String choiceValue,
			final String helpString) {
		super(displayName, choiceValue, helpString);
		this.choiceValues = choices;
		this.paraType = "StringArray";
	}
	
	/**
	 * @param displayName
	 * @param choices
	 * @param choiceIndex
	 * @param helpString
	 */
	public ChoiceParameter(final String displayName, 
			final ArrayList<String> choices,
			final int choiceIndex,
			final String helpString) {
		super(displayName, choices.isEmpty() ? "" : choices.get(choiceIndex) , helpString);
		this.choiceValues = choices;
		this.paraType = "StringArray";
	}
	
	/**
	 * Returns the possible values available for this parameter.
	 * @return
	 */
	public ArrayList<String> getChoices() {
		return this.choiceValues;
	}
	
	@Override
	public void setValue(String stringValue) {
		if(this.choiceValues.contains(stringValue)) {
			super.setValue(stringValue);	
		} else {
			System.err.println("Tried setting a value that isn't permitted.");
		}
	}

	/**
	 * Returns an array of strings in this {@link ChoiceParameter}.
	 * @return
	 */
	public String[] getChoicesArray() {
		final String[] array = new String[getChoices().size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = getChoices().get(i);
		}
		return array;
	}
	
	/**
	 * Return the index of the selected choice
	 * @return
	 */
	public int getChoiceIndex() {
		if (this.choiceValues.isEmpty()) {
			return 0;
		}
		return this.choiceValues.indexOf(this.stringValue);
	}

	/**
	 * Returns one String with the choices delimited by the DELIMITER.
	 * @return
	 */
	public String getChoicesString() {
		String choiceString = "";
		for (String choice : getChoices()) {
			choiceString += choice+DELIMITER;
		}
		return choiceString;
	}
	
}
