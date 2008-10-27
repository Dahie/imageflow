/* class ToneGeneratorEditor

This class provides a custom editor to select a specific name

version 0.1, 17-11-2007, first release

javalc6
*/

package test.tonegenerator;
public class ToneGeneratorEditor extends java.beans.PropertyEditorSupport {

    public String[] getTags() {
		String result[] = {
			"Sine",
			"Square",
			"Triangle"};
		return result;
    }

    public String getJavaInitializationString() {
		return (String)getValue();
    }

}

