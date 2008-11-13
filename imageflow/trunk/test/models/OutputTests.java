package models;

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
		Output filter1Input = filterUnit1.getOutput(0);

		Connection conn1 = new Connection(sourceUnit, 1, filterUnit1, 1);
		Connection conn2 = new Connection(sourceUnit, 1, filterUnit2, 1);
		
		// the imagetitle is constructed from the unit and pin the 
		// connection comes from and the 
		assertEquals("imagetitle for output 1 at unit 1", 
				"Unit_"+sourceUnit.getUnitID()+"_Output_1", sourceOutput.getImageTitle());
		
		assertEquals("imagetitle for input 1 at unit 2", 
				"Unit_"+filterUnit1.getUnitID()+"_Output_1", filter1Input.getImageTitle());
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
	
}
