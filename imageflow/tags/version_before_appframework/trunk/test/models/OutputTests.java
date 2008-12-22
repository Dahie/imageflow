package models;

import ij.plugin.filter.PlugInFilter;

import java.awt.Dimension;

import models.unit.UnitElement;
import models.unit.UnitFactory;
import junit.framework.TestCase;

public class OutputTests extends TestCase {

	
	public void testImageTitle() {
		
		// image title of style "Unit_1_Output_1"
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Output sourceOutput = sourceUnit.getOutput(0);
		Input filter1Input = filterUnit1.getInput(0);
		Output filter1Output = filterUnit1.getOutput(0);

		Connection conn1 = new Connection(sourceUnit, 1, filterUnit1, 1);
		Connection conn2 = new Connection(sourceUnit, 1, filterUnit2, 1);
		conn1.connect();
		assertTrue(conn1.isConnected());
		conn2.connect();
		assertTrue(conn2.isConnected());
		
		// the imagetitle is constructed from the unit and pin the 
		// connection comes from and the 
		assertEquals("imagetitle for output 1 at unit 1", 
				"Unit_"+sourceUnit.getUnitID()+"_Output_1", sourceOutput.getImageTitle());
		assertEquals("imagetitle for input 1 at unit 2", 
				"Unit_"+sourceUnit.getUnitID()+"_Output_1", filter1Input.getImageTitle());
		
		assertNotNull("imagetitle for output 1 at unit 2", filter1Output.getImageTitle());
	}
	

	public void testIsConnected() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Output sourceOutput = sourceUnit.getOutput(0);
		
		// test beforehand
		assertFalse("output not connected", sourceOutput.isConnected());
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		ConnectionList connList = new ConnectionList();
		connList.add(conn);
		
		// test after connecting
		assertTrue("output connected", sourceOutput.isConnected());	
		assertFalse("output connected", filterUnit2.getOutput(0).isConnected());	
	}
	
	public void testIsConnectedWith() {
		// test output-only
		UnitElement source1Unit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		Output source1Output = source1Unit.getOutput(0);
		UnitElement source2Unit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		Output source2Output = source2Unit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Input filter1Input = filterUnit1.getInput(0);
		Input filter2Input = filterUnit2.getInput(0);
		
		// test beforehand
		assertFalse("output not connected with filter1Input", 
				source1Output.isConnectedWith(filter1Input));
		assertFalse("output not connected with filter2Input", 
				source1Output.isConnectedWith(filter2Input));
		
		Connection conn = new Connection(source1Unit, 1, filterUnit1, 1);
		ConnectionList connList = new ConnectionList();
		connList.add(conn);
		
		
		// test after connecting
		assertTrue("output connected with filter1Input", 
				source1Output.isConnectedWith(filter1Input));
		assertFalse("output not connected with filter2Input", 
				source1Output.isConnectedWith(filter2Input));
	}
	
	public void testIsDisconnected() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Output sourceOutput = sourceUnit.getOutput(0);
		
		
		ConnectionList connList = new ConnectionList();
		
		// test after connecting
		assertTrue("connection added to list", connList.add(sourceUnit, 1, filterUnit1, 1));
		assertTrue("output connected", sourceOutput.isConnected());	
		assertFalse("output connected", filterUnit2.getOutput(0).isConnected());
		
		connList.remove(0);
		
		//test after disconnecting
		assertFalse("output disconnected", sourceOutput.isConnected());
		assertFalse("output connected", filterUnit2.getOutput(0).isConnected());
	}
	
	public void testImageBitDepthCompatible() {

		UnitElement unit1 = new UnitElement("unit1", "some syntax");
		unit1.addOutput("output1", "o", PlugInFilter.DOES_32, false);
		unit1.addOutput("output2", "o", PlugInFilter.DOES_ALL, false);
		unit1.addOutput("output2", "o", -1, false);
		Output output1 = unit1.getOutput(0);
		Output output2 = unit1.getOutput(1);
		Output output3 = unit1.getOutput(2);
		
		UnitElement unit2 = new UnitElement("unit2", "some syntax");
		unit2.addInput("input1", "i", PlugInFilter.DOES_32, false);
		unit2.addInput("input2", "i", PlugInFilter.DOES_16, false);
		unit2.addInput("input3", "i", PlugInFilter.DOES_ALL, false);
		Input input1 = unit2.getInput(0);
		Input input2 = unit2.getInput(1);
		Input input3 = unit2.getInput(2);

		/*assertTrue("both do 32", input1.isImageBitDepthCompatible(output1.getImageBitDepth()));
		assertFalse("32 to 16", input2.isImageBitDepthCompatible(output1.getImageBitDepth()));
		assertTrue("all to 32", input1.isImageBitDepthCompatible(output2.getImageBitDepth()));
		assertTrue("32 to all", input3.isImageBitDepthCompatible(output1.getImageBitDepth()));*/
		
		assertTrue("both do 32", output1.isImageBitDepthCompatible(input1.getImageBitDepth()));
		assertTrue("All to 16", output2.isImageBitDepthCompatible(input1.getImageBitDepth()));
		assertFalse("32 to 16", output1.isImageBitDepthCompatible(input2.getImageBitDepth()));
		assertTrue("32 to all", output3.isImageBitDepthCompatible(input1.getImageBitDepth()));
		
		
		// now test pins, which don't care
		//TODO hm how should this react actually? needs an input set
		/*assertTrue("-1 to 16", input2.isImageBitDepthCompatible(output3.getImageBitDepth()));
		assertTrue("-1 to ALL", input3.isImageBitDepthCompatible(output3.getImageBitDepth()));*/


	}
	
	
	public void testUnitConnectedInBranch() {
		
		UnitElement unit1 = UnitFactory.createAddNoiseUnit();
		UnitElement unit2 = UnitFactory.createAddNoiseUnit();
		UnitElement unit3 = UnitFactory.createAddNoiseUnit();
		
		Connection conn1 = new Connection(unit1, 1, unit2, 1);
		
		ConnectionList connList = new ConnectionList();
		connList.add(conn1);
		
		Output output2 = unit2.getOutput(0);
		assertFalse("output2 knows unit1", output2.knows(unit1));
		assertTrue("output2 knows unit2", output2.knows(unit2));
		assertFalse("output2 knows unit3", output2.knows(unit3));
		
		Connection conn2 = new Connection(unit2, 1, unit3, 1);
//		connList.add(conn2);
		
		
		assertFalse("output2 knows unit1", output2.knows(unit1));
		assertTrue("output2 knows unit2", output2.knows(unit2));
		assertFalse("output2 knows unit3", output2.knows(unit3));
		
		Output output1 = unit1.getOutput(0);
		assertTrue("output1 knows unit1", output1.knows(unit1));
		assertTrue("output1 knows unit2", output2.knows(unit2));
		assertFalse("output1 knows unit3", output2.knows(unit3));
		
		
		connList.add(conn2);
		
		
		assertTrue("output1 knows unit1", output1.knows(unit1));
		assertTrue("output1 knows unit2", output1.knows(unit2));
		assertTrue("output1 knows unit3", output2.knows(unit3));
		
	}
	
}
