/**
 * 
 */
package models;

import ij.plugin.filter.PlugInFilter;

import java.awt.Dimension;

import de.danielsenff.imageflow.models.Connection;
import de.danielsenff.imageflow.models.Input;
import de.danielsenff.imageflow.models.Output;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;

import junit.framework.TestCase;

/**
 * @author danielsenff
 *
 */
public class ConnectionTests extends TestCase {

	
	/**
	 * Test if an correctly initialized Connection returns the right status.
	 */
	public void testConnectionStatus() {
		final UnitElement source = UnitFactory.createBackgroundUnit(new Dimension(12,12));
		final UnitElement blur = UnitFactory.createGaussianBlurUnit(); 
		
		final Connection connection1 = new Connection(source, 1, blur, 1);
		connection1.connect();
		assertTrue("status check 1", (connection1.checkConnection() == Connection.Status.OK) );
		
		assertTrue("connection conncted", connection1.isConnected());
		assertEquals(source, connection1.getFromUnit());
		assertEquals(blur, connection1.getToUnit());
		
		
		
		Output output = source.getOutput(0);
		Input input = blur.getInput(0);
		assertEquals("source output parent", source, output.getParent());
		for (Connection conn : output.getConnections()) {
			assertEquals("source output to unit", blur, conn.getToUnit());	
		}
		
		assertEquals("blur input parent", blur, input.getParent());
		assertEquals("blur input from unit", source, input.getFromUnit());
		
		assertEquals("check imageTitles generated on pins", 
				output.getImageTitle(), 
				input.getImageTitle());
	}
	

	public void testHasAllInputsMarked() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		//assertion
		
		assertFalse("con has no inputs marked yet", conn.hasInputMarked());
		// the source is not yet marked, so the first filter should give false
//		assertEquals("filter1 has no inputs marked yet", false, filterUnit1.hasAllInputsMarked());
//		assertEquals("filter2 has inputs marked", false, filterUnit2.hasAllInputsMarked());
		
		//set mark on the source, now the filter next connected should find this mark
		sourceUnit.setMark(1);
		
		assertTrue("filter1 has inputs marked", conn.hasInputMarked());
	}
	
	public void testIsConnectedPin() {

		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		Output sourceOutput = sourceUnit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		Input filter1Input = filterUnit1.getInput(0); 
		Output filter1Output = filterUnit1.getOutput(0);
		
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		Input filter2Input = filterUnit2.getInput(0); 
		Output filter2Output = filterUnit2.getOutput(0);
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		//assertion
		assertTrue("Source output", conn.isConnected(sourceOutput));
		assertTrue("Filter1 input", conn.isConnected(filter1Input));
		assertFalse("Filter1 output", conn.isConnected(filter1Output));
		assertFalse("Filter2 input", conn.isConnected(filter2Input));
		assertFalse("Filter2 Output", conn.isConnected(filter2Output));
		
	}
	
	public void testIsConnectedWithUnit() {

		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		//assertion
		assertTrue("Source", conn.isConnectedToUnit(sourceUnit));
		assertTrue("Filter1", conn.isConnectedToUnit(filterUnit1));
		assertFalse("Filter2", conn.isConnectedToUnit(filterUnit2));
		
	}
	
	
	public void testCauseLoops() {
		
	}
	
	

	public void testAreImageDepthCompatible() {
		
		UnitElement unit1 = new UnitElement("unit1", "some syntax");
		unit1.addOutput("output1", "o", PlugInFilter.DOES_32, false);
		unit1.addOutput("output2", "o", PlugInFilter.DOES_ALL, false);
		unit1.addOutput("output2", "o", -1, false);
		UnitElement unit2 = new UnitElement("unit2", "some syntax");
		unit2.addInput("input1", "i", PlugInFilter.DOES_32, false);
		unit2.addInput("input2", "i", PlugInFilter.DOES_16, false);
		unit2.addInput("input3", "i", PlugInFilter.DOES_ALL, false);
		
		// conn1 32 to 16		
		Connection conn1 = new Connection(unit1,1,unit2,1);
		assertTrue("both do 32", conn1.isImageBitDepthCompatible());
		
		// conn2 32 to 16		
		Connection conn2 = new Connection(unit1,1,unit2,2);
		assertFalse("both do 32", conn2.isImageBitDepthCompatible());

		// conn3 ALL to 32
		Connection conn3 = new Connection(unit1,2,unit2, 1);
		// technically compatable, but we expect something explicit and no guesses
		assertFalse("all to 32", conn3.isImageBitDepthCompatible());
		
		// conn4 32 to all
		Connection conn4 = new Connection(unit1,1,unit2, 3);
		assertTrue("32 to all", conn4.isImageBitDepthCompatible());
		
		// now test pins, which don't care
		//TODO hm how should this react actually? needs an input set
		Connection conn5 = new Connection(unit1,3,unit2, 2);
		//assertTrue("-1 to 16", conn5.areImageBitDepthCompatible());
		
		// now test pins, which don't care
		// doesn't do much since -1 is undefined
		Connection conn6 = new Connection(unit1,3,unit2, 3);
		//assertTrue("-1 to ALL", conn6.areImageBitDepthCompatible());
	}
	
}
