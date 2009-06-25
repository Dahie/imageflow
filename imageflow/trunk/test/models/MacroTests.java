package models;


import java.awt.Dimension;
import java.util.ArrayList;

import junit.framework.TestCase;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.ParameterFactory;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;

public class MacroTests extends TestCase {

	
	public void testMatchingImageTitles() {
		
		final UnitElement source = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		final UnitElement blur = UnitFactoryExt.createGaussianBlurUnit();
		final UnitElement noise = UnitFactoryExt.createAddNoiseUnit();
		
		final Connection connection1 = new Connection(source, 1, blur, 1);
		final Connection connection2 = new Connection(blur, 1, noise, 1);
		ConnectionList connList = new ConnectionList();
		assertTrue(connList.add(connection1));
		assertTrue(connList.add(connection2));
		
		assertTrue("is conn1 connected", connection1.isConnected());
		assertTrue("is conn2 connected", connection2.isConnected());
		
		assertTrue("status check 1", (connection1.checkConnection() == Connection.Status.OK) );
		String outputImageTitleSource = source.getOutput(0).getOutputTitle();
		System.out.println(outputImageTitleSource);
		String inputImageTitleBlur = blur.getInput(0).getImageTitle();
//		System.out.println(inputImageTitleBlur);
		assertEquals("check ImageTitles generated on pins", 
				outputImageTitleSource, inputImageTitleBlur);
		
		
		
		assertTrue("status check 2", (connection2.checkConnection() == Connection.Status.OK) );
		String outputimageTitleBlur = blur.getOutput(0).getOutputTitle();
		System.out.println(outputimageTitleBlur);
		String InputImageTitleNoise = noise.getInput(0).getImageTitle();
		System.out.println(InputImageTitleNoise);
		assertEquals("check ImageTitles generated on pins", 
				outputimageTitleBlur, InputImageTitleNoise);
		
	}
	
	
	public void testParsingCommadsInteger() {
		
		String exampleIntSyntax = "PARA_INTEGER_1";
		MacroElement macroElement = new MacroElement(exampleIntSyntax);
		
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ParameterFactory.createParameter("Integer", new Integer(11), "testinteger"));
		
		assertTrue(macroElement.getImageJSyntax().equals(exampleIntSyntax));
		assertTrue(macroElement.getCommandSyntax().equals(exampleIntSyntax));
		assertFalse(macroElement.getCommandSyntax().equals("11"));
		assertFalse(macroElement.getCommandSyntax().equals("12"));
		
		macroElement.parseParameters(parameters);
		
		assertNotNull(macroElement.getImageJSyntax());
		assertTrue(macroElement.getImageJSyntax().equals(exampleIntSyntax));
		assertFalse(macroElement.getCommandSyntax().equals(exampleIntSyntax));
		assertNotNull(macroElement.getCommandSyntax());
		assertTrue(macroElement.getCommandSyntax().equals("11"));
		assertFalse(macroElement.getCommandSyntax().equals("12"));
	}
	
	public void testParsingCommadsString() {
		
		String exampleIntSyntax = "PARA_STRING_1";
		MacroElement macroElement = new MacroElement(exampleIntSyntax);
		
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ParameterFactory.createParameter("String", new String("Hallo"), "test"));
		
		assertTrue(macroElement.getImageJSyntax().equals(exampleIntSyntax));
		assertTrue(macroElement.getCommandSyntax().equals(exampleIntSyntax));
		assertFalse(macroElement.getCommandSyntax().equals("Hallo"));
		assertFalse(macroElement.getCommandSyntax().equals("Bienvenue"));
		
		macroElement.parseParameters(parameters);
		
		assertNotNull(macroElement.getImageJSyntax());
		assertTrue(macroElement.getImageJSyntax().equals(exampleIntSyntax));
		assertFalse(macroElement.getCommandSyntax().equals(exampleIntSyntax));
		assertNotNull(macroElement.getCommandSyntax());
		assertTrue(macroElement.getCommandSyntax().equals("Hallo"));
		assertFalse(macroElement.getCommandSyntax().equals("Bienvenue"));
	}
	
	public void testParsingCommadsDouble() {
		
		String exampleIntSyntax = "PARA_DOUBLE_1";
		MacroElement macroElement = new MacroElement(exampleIntSyntax);
		
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ParameterFactory.createParameter("Double", new Double("3.141596"), "test"));
		
		assertTrue(macroElement.getImageJSyntax().equals(exampleIntSyntax));
		assertTrue(macroElement.getCommandSyntax().equals(exampleIntSyntax));
		assertFalse(macroElement.getCommandSyntax().equals("3.141596"));
		assertFalse(macroElement.getCommandSyntax().equals("9.81"));
		
		macroElement.parseParameters(parameters);
		
		assertNotNull(macroElement.getImageJSyntax());
		assertTrue(macroElement.getImageJSyntax().equals(exampleIntSyntax));
		assertFalse(macroElement.getCommandSyntax().equals(exampleIntSyntax));
		assertNotNull(macroElement.getCommandSyntax());
		assertTrue(macroElement.getCommandSyntax().equals("3.141596"));
		assertFalse(macroElement.getCommandSyntax().equals("9.81"));
	}
	
}
