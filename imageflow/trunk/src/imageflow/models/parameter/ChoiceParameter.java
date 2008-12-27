package imageflow.models.parameter;

/**
 * @author danielsenff
 *
 */
public class ChoiceParameter extends StringParameter {

	private final String[] choiceValues;
	
	
	/**
	 * @param displayName
	 * @param choices
	 * @param choiceParameter
	 * @param helpString
	 */
	public ChoiceParameter(final String displayName, 
			final String[] choices,
			final String choiceParameter,
			final String helpString) {
		super(displayName, choiceParameter, helpString);
		this.choiceValues = choices;
		this.paraType = "StringArray";
	}
	
	/**
	 * Returns the possible values available for this parameter.
	 * @return
	 */
	public String[] getChoices() {
		return this.choiceValues;
	}
	

}
