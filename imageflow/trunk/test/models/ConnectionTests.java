/**
 * 
 */
package models;

import ij.plugin.filter.PlugInFilter;

import java.awt.Dimension;

import junit.framework.TestCase;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class ConnectionTests extends TestCase {

	
	/**
	 * Test if an correctly initialized Connection returns the right status.
	 */
	public void testConnectionStatus() {
		final UnitElement source = UnitFactoryExt.createBackgroundUnit(new Dimension(12,12));
		final UnitElement blur = UnitFactoryExt.createGaussianBlurUnit(); 
		
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
				output.getOutputTitle(), 
				input.getImageTitle());
	}
	

	public void testHasAllInputsMarked() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
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
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output sourceOutput = sourceUnit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		Input filter1Input = filterUnit1.getInput(0); 
		Output filter1Output = filterUnit1.getOutput(0);
		
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
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
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
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
		unit1.addOutput(
				new Output("Output", "o", 
						DataTypeFactory.createImage(PlugInFilter.DOES_ALL), unit1, 1));
		unit1.addOutput(
				new Output("Output", "o", 
						DataTypeFactory.createImage(-1), unit1, 2));
		UnitElement unit2 = new UnitElement("unit2", "some syntax");
		unit2.addInput(
				new Input("input1", "i", 
						DataTypeFactory.createImage(PlugInFilter.DOES_32), 
						unit2, 1, true, false));
		unit2.addInput(
				new Input("input2", "i", 
						DataTypeFactory.createImage(PlugInFilter.DOES_16), 
						unit2, 2, true, false));
		unit2.addInput(
				new Input("input3", "i", 
						DataTypeFactory.createImage(PlugInFilter.DOES_ALL), 
						unit2, 3, true, false));
		
		// conn1 all to 32		
		Connection conn1 = new Connection(unit1,1,unit2,1);
		assertTrue("all to 32", conn1.isCompatible());
		
		// conn2 all to all
		Connection conn2 = new Connection(unit1,1,unit2,2);
		assertTrue("all to 16", conn2.isCompatible());

		// conn3 any to 32
		Connection conn3 = new Connection(unit1,2,unit2, 1);
		// technically compatable, but we expect something explicit and no guesses
		assertFalse("all to 32", conn3.isCompatible());
		
		// conn4 all to all
		Connection conn4 = new Connection(unit1,1,unit2, 3);
		assertTrue("all to all", conn4.isCompatible());
		
		// now test pins, which don't care
		//TODO hm how should this react actually? needs an input set
//		Connection conn5 = new Connection(unit1,3,unit2, 2);
		//assertTrue("-1 to 16", conn5.areImageBitDepthCompatible());
		
		// now test pins, which don't care
		// doesn't do much since -1 is undefined
//		Connection conn6 = new Connection(unit1,3,unit2, 3);
		//assertTrue("-1 to ALL", conn6.areImageBitDepthCompatible());
	}
	
}
