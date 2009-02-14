package imageflow.models.parameter;

import java.util.ArrayList;

/**
 * Works like an Enumeration. Contains a List of possible values.
 * The actual value is an element of this list.
 * @author danielsenff
 *
 */
public class ChoiceParameter extends StringParameter {

	private final ArrayList<String> choiceValues;
	
	/**
	 * @param displayName
	 * @param choices
	 * @param choiceParameter
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

	public String[] getChoicesArray() {
		String[] array = new String[getChoices().size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = getChoices().get(i);
		}
		return array;
	}
	
}
