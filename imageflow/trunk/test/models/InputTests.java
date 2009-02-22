/**
 * 
 */
package models;

import ij.plugin.filter.PlugInFilter;
import imageflow.models.Connection;
import imageflow.models.ConnectionList;
import imageflow.models.Input;
import imageflow.models.Output;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitFactory;

import java.awt.Dimension;

import junit.framework.TestCase;

/**
 * @author danielsenff
 *
 */
public class InputTests extends TestCase {

	public void testImageTitle() {
	
		// image title of style "Unit_1_Output_1"
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Input filter1Input = filterUnit1.getInput(0);
		Input filter2Input = filterUnit2.getInput(0);

		Connection conn1 = new Connection(sourceUnit, 1, filterUnit1, 1);
		Connection conn2 = new Connection(sourceUnit, 1, filterUnit2, 1);
		ConnectionList connList= new ConnectionList();
		assertTrue(connList.add(conn1));
		assertTrue(connList.add(conn2));
		
		
		// the imagetitle is constructed from the unit and pin the 
		// connection comes from and the 
		assertEquals("imagetitle for input 1 at unit 2", 
				"Unit_"+sourceUnit.getUnitID()+"_Output_1", filter1Input.getImageTitle());
		
		assertEquals("imagetitle for input 1 at unit 3", 
				"Unit_"+sourceUnit.getUnitID()+"_Output_1", filter2Input.getImageTitle());
	}
	
	public void testIsConnected() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Input filterInput = filterUnit1.getInput(0);
		
		// test beforehand
		assertFalse("input not connected", filterInput.isConnected());
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		ConnectionList connList = new ConnectionList();
		assertTrue(connList.add(conn));
		
		// test after connecting
		assertTrue("input connected", filterInput.isConnected());	
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
		
		Input filterInput = filterUnit1.getInput(0);
		
		// test beforehand
		assertFalse("input not connected with source1Output", 
				filterInput.isConnectedWith(source1Output));
		assertFalse("input not connected with source2Output", 
				filterInput.isConnectedWith(source2Output));
		
		Connection conn = new Connection(source1Unit, 1, filterUnit1, 1);
		ConnectionList connList = new ConnectionList();
		connList.add(conn);
		
		
		// test after connecting
		assertTrue("input not connected with source1Output", 
				filterInput.isConnectedWith(source1Output));
		assertFalse("input not connected with source2Output", 
				filterInput.isConnectedWith(source2Output));
	}

	public void testIsDisconnected() {

		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Input filterInput = filterUnit1.getInput(0);
		
		ConnectionList connList = new ConnectionList();
		
		// test after connecting
		assertTrue("add conn 1", connList.add(sourceUnit, 1, filterUnit1, 1));
		assertTrue("input connected", filterInput.isConnected());			

		assertNotNull(connList.remove(0));
		
		// test after removing connection ie disconnecting
		assertFalse("input disconnected", filterInput.isConnected());
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

		assertTrue("both do 32", input1.isImageBitDepthCompatible(output1.getImageBitDepth()));
		assertFalse("32 to 16", input2.isImageBitDepthCompatible(output1.getImageBitDepth()));
		assertFalse("all to 32", input1.isImageBitDepthCompatible(output2.getImageBitDepth()));
		assertTrue("32 to all", input3.isImageBitDepthCompatible(output1.getImageBitDepth()));

	}

	void testForwardingImageBitDepth() {
		// -1 requires to check the imagebitdepth of the input
		
		UnitElement unit1 = new UnitElement("unit1", "some syntax");
		unit1.addInput("input1", "i", PlugInFilter.DOES_32, false);
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
		
		
		// now test pins, which don't care
		//TODO hm how should this react actually? needs an input set
//		assertTrue("-1 to 16", input2.isImageBitDepthCompatible(output3.getImageBitDepth()));
//		assertTrue("-1 to ALL", input3.isImageBitDepthCompatible(output3.getImageBitDepth()));
		
	}
	
	
	public void testUnitConnectedInBranch() {
		
		UnitElement unit1 = UnitFactory.createAddNoiseUnit();
		UnitElement unit2 = UnitFactory.createAddNoiseUnit();
		UnitElement unit3 = UnitFactory.createAddNoiseUnit();
		
		Connection conn1 = new Connection(unit1, 1, unit2, 1);
		
		ConnectionList connList = new ConnectionList();
		assertTrue(connList.add(conn1));
		
		Input input2 = unit2.getInput(0);
		assertTrue("input2 knows unit1", input2.isConnectedInInputBranch(unit1));
		assertFalse("input2 knows unit2", input2.isConnectedInInputBranch(unit2));
		assertFalse("input2 knows unit3", input2.isConnectedInInputBranch(unit3));
		
		Connection conn2 = new Connection(unit2, 1, unit3, 1);
		assertTrue(connList.add(conn2));
		
		Input input3 = unit3.getInput(0);
		assertTrue("input3 knows unit1", input3.isConnectedInInputBranch(unit1));
		assertTrue("input3 knows unit2", input3.isConnectedInInputBranch(unit2));
		assertFalse("input3 knows unit1", input3.isConnectedInInputBranch(unit3));
		assertFalse("input2 knows unit3", input2.isConnectedInInputBranch(unit3));
	}
	
	
}
