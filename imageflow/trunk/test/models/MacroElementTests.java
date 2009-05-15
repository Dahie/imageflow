/**
 * 
 */
package models;

import de.danielsenff.imageflow.models.MacroElement;
import junit.framework.TestCase;

/**
 * @author danielsenff
 *
 */
public class MacroElementTests extends TestCase {

	/**
	 * Tests, if the substitution of variables works.
	 */
	public void testVariableSubstitution() {
		final String syntaxExample = "some syntax with some variable";
		final MacroElement macro = new MacroElement(syntaxExample);
		macro.replace("variable", "code");
		
		assertEquals("original syntax", 
				syntaxExample, 
				macro.getImageJSyntax());
		assertEquals("string with substituted variable", 
				"some syntax with some code", 
				macro.getCommandSyntax());
	}
	
	
	/**
	 * Test if the output is actually added to the given macro-string.
	 */
	public void testOutput() {
		String macroLog = "log \n";
		
		MacroElement macro = new MacroElement("some syntax");
		macroLog += macro.getCommandSyntax();
		
		assertEquals("string with substituted variable", 
				"log \nsome syntax", 
				macroLog);
	}
	
	
	/**
	 * Test the parsing of StringParameters.
	 */
	public void testParseStringParameters() {
		
		final String syntaxExample = "open(\"PARA_STRING_1\");";
		final MacroElement macro = new MacroElement(syntaxExample);
		macro.replace("variable", "code");
		
		//TODO
	}
	
}
