/**
 * 
 */
package models;

import java.awt.Dimension;

import org.junit.Test;
import static org.junit.Assert.*;

import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class InputTests {

	@Test public void testImageTitle() {
	
		// image title of style "Unit_1_Output_1"
		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
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
				"Unit_"+sourceUnit.getNodeID()+"_Output_1", filter1Input.getImageTitle());
		
		assertEquals("imagetitle for input 1 at unit 3", 
				"Unit_"+sourceUnit.getNodeID()+"_Output_1", filter2Input.getImageTitle());
	}
	
	@Test public void testIsConnected() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
		Input filterInput = filterUnit1.getInput(0);
		
		// test beforehand
		assertFalse("input not connected", filterInput.isConnected());
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		ConnectionList connList = new ConnectionList();
		assertTrue(connList.add(conn));
		
		// test after connecting
		assertTrue("input connected", filterInput.isConnected());	
	}
	
	@Test public void testIsConnectedWith() {
		// test output-only
		UnitElement source1Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source1Output = source1Unit.getOutput(0);
		UnitElement source2Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source2Output = source2Unit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
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

	@Test public void testIsDisconnected() {

		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
		Input filterInput = filterUnit1.getInput(0);
		
		ConnectionList connList = new ConnectionList();
		
		// test after connecting
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		assertTrue("add conn 1", connList.add(conn));
		assertTrue("input connected", filterInput.isConnected());
		assertTrue("conn connected", conn.isConnected());

		assertNotNull(connList.remove(conn));
		
		// test after removing connection ie disconnecting
		assertFalse("input disconnected", filterInput.isConnected());
		assertFalse("conn connected", conn.isConnected());
	}
	
	@Test public void testIsRequiredInput() {
		UnitElement source = UnitFactoryExt.createBackgroundUnit(new Dimension(10,10));
		UnitElement unit = UnitFactoryExt.createImageCalculatorUnit();
		
		Input imageCalcInput1 = unit.getInput(0);
		Input imageCalcInput2 = unit.getInput(1);
		
		assertTrue(imageCalcInput1.isRequired());
		assertTrue(imageCalcInput2.isRequired());
		
		imageCalcInput2.setRequiredInput(false);
		
		assertFalse(imageCalcInput2.isRequired());
		
		// are connected?
		
		assertFalse(imageCalcInput1.isConnected());
		assertFalse(imageCalcInput2.isConnected());
		
		Connection conn2 = new Connection(source, 1, unit, 2 );
		conn2.connect();
		
		assertFalse(imageCalcInput1.isConnected());
		assertTrue(imageCalcInput2.isConnected());
		assertFalse("required inputs connected", unit.hasRequiredInputsConnected());
		
		Connection conn1 = new Connection(source, 1, unit, 1 );
		conn1.connect();
		
		assertTrue(imageCalcInput1.isConnected());
		assertTrue(imageCalcInput2.isConnected());
		assertTrue("required inputs connected", unit.hasRequiredInputsConnected());
		
	}
	
	@Test public void testUnitConnectedInOutputBranch() {
		
		UnitElement unit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit2 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit3 = UnitFactoryExt.createAddNoiseUnit();
		
		Connection conn1 = new Connection(unit1, 1, unit2, 1);
		
		conn1.connect();
		assertTrue(conn1.isConnected());
		
		Input input2 = unit2.getInput(0);
		assertFalse("input2 knows unit1", input2.isConnectedInOutputBranch(unit1));
		assertFalse("input2 knows unit2", input2.isConnectedInOutputBranch(unit2));
		assertFalse("input2 knows unit3", input2.isConnectedInOutputBranch(unit3));
		
		Connection conn2 = new Connection(unit2, 1, unit3, 1);
		conn2.connect();
		
		Input input3 = unit3.getInput(0);
		assertFalse("input3 knows unit1", input3.isConnectedInOutputBranch(unit1));
		assertFalse("input3 knows unit2", input3.isConnectedInOutputBranch(unit2));
		assertFalse("input3 knows unit3", input3.isConnectedInOutputBranch(unit3));
		assertTrue("input2 knows unit3", input2.isConnectedInOutputBranch(unit3));
	}
	
	@Test public void testUnitConnectedInInputBranch() {
		
		UnitElement unit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit2 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit3 = UnitFactoryExt.createAddNoiseUnit();
		
		Connection conn1 = new Connection(unit1, 1, unit2, 1);
		
		conn1.connect();
		assertTrue(conn1.isConnected());
		
		Input input2 = unit2.getInput(0);
		assertTrue("input2 knows unit1", input2.isConnectedInInputBranch(unit1));
		assertFalse("input2 knows unit2", input2.isConnectedInInputBranch(unit2));
		assertFalse("input2 knows unit3", input2.isConnectedInInputBranch(unit3));
		
		Connection conn2 = new Connection(unit2, 1, unit3, 1);
		conn2.connect();
		
		Input input3 = unit3.getInput(0);
		assertTrue("input3 knows unit1", input3.isConnectedInInputBranch(unit1));
		assertTrue("input3 knows unit2", input3.isConnectedInInputBranch(unit2));
		assertFalse("input3 knows unit1", input3.isConnectedInInputBranch(unit3));
		assertFalse("input2 knows unit3", input2.isConnectedInInputBranch(unit3));
	}
	
}
