package de.danielsenff.imageflow.models.parameter;

public class TextParameter extends StringParameter {

	public TextParameter(String displayName, String stringValue,
			String helpString) {
		super(displayName, stringValue, helpString);
		this.paraType = "Text";
	}

}
