/**
 * 
 */
package models;

import java.awt.Dimension;

import junit.framework.TestCase;
import models.unit.UnitElement;
import models.unit.UnitFactory;

/**
 * @author danielsenff
 *
 */
public class ConnectionTests extends TestCase {

	
	/**
	 * Test if an correctly initialized Connection returns the right status.
	 */
	public void testConnectionStatus() {
		final UnitElement source = UnitFactory.createSourceUnit("/Users/danielsenff/zange1.png");
		final UnitElement blur = UnitFactory.createGaussianBlurUnit(); 
		
		final Connection connection1 = new Connection(source, 1, blur, 1);
		assertTrue("status check 1", (connection1.checkConnection() == Connection.Status.OK) );
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
	
	public void testIsConnectedToUnit() {

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
	
	public void testAddConnection() {
		

		// test output-only
		UnitElement source1Unit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		Output source1Output = source1Unit.getOutput(0);
		UnitElement source2Unit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		Output source2Output = source2Unit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		Input filter1Input = filterUnit1.getInput(0); 
		Output filter1Output = filterUnit1.getOutput(0);
		
		
		
		ConnectionList connectionList = new ConnectionList();
		
		//assert connectionList
		
		assertTrue("connection list empty", connectionList.isEmpty());
		assertEquals("connection list empty", 0, connectionList.size());
		
		// assert pins
		
		assertFalse("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertFalse("filter1 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
		Connection conn1 = new Connection(source1Unit, 1, filterUnit1, 1);
		Connection conn2 = new Connection(source2Unit, 1, filterUnit1, 1);
		
		connectionList.add(conn1);
		
		assertFalse("connection list first added", connectionList.isEmpty());
		assertEquals("connection list first added", 1, connectionList.size());
		assertTrue("connectin list contains conn1", connectionList.contains(conn1));
		assertFalse("connectin list contains conn2", connectionList.contains(conn2));
	
		// assert pins
		
		assertTrue("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertFalse("filter1 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
		
		
		connectionList.add(conn2);
		
		assertFalse("connection list first replaced", connectionList.isEmpty());
		assertEquals("connection list first replaced", 1, connectionList.size());
		assertFalse("connectin list contains conn1", connectionList.contains(conn1));
		assertTrue("connectin list contains conn2", connectionList.contains(conn2));
		
		// assert pins
		
		assertFalse("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertTrue("filter1 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
	}
	
}
