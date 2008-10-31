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
		assertEquals("status check 1", true, (connection1.checkConnection() == Connection.Status.OK) );
	}
	

	public void testHasAllInputsMarked() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		//assertion
		
		assertEquals("con has no inputs marked yet", false, conn.hasInputMarked());
		// the source is not yet marked, so the first filter should give false
//		assertEquals("filter1 has no inputs marked yet", false, filterUnit1.hasAllInputsMarked());
//		assertEquals("filter2 has inputs marked", false, filterUnit2.hasAllInputsMarked());
		
		//set mark on the source, now the filter next connected should find this mark
		sourceUnit.setMark(1);
		
		assertEquals("filter1 has inputs marked", true, conn.hasInputMarked());
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
		assertEquals("Source output", true, conn.isConnected(sourceOutput));
		assertEquals("Filter1 input", true, conn.isConnected(filter1Input));
		assertEquals("Filter1 output", false, conn.isConnected(filter1Output));
		assertEquals("Filter2 input", false, conn.isConnected(filter2Input));
		assertEquals("Filter2 Output", false, conn.isConnected(filter2Output));
		
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
		
		Connection conn1 = new Connection(source1Unit, 1, filterUnit1, 1);
		Connection conn2 = new Connection(source2Unit, 1, filterUnit1, 1);
		
		ConnectionList connectionList = new ConnectionList();
		
		//assert connectionList
		
		assertEquals("connection list empty", true, connectionList.isEmpty());
		assertEquals("connection list empty", 0, connectionList.size());
		
		// assert pins
		
		assertEquals("filter1 input connected with source1", 
				false, filter1Input.isConnectedWith(source1Output));
		assertEquals("filter1 input connected with source2", 
				false, filter1Input.isConnectedWith(source2Output));
		
		
		connectionList.add(conn1);
		
		assertEquals("connection list first added", false, connectionList.isEmpty());
		assertEquals("connection list first added", 1, connectionList.size());
		assertEquals("connectin list contains conn1", true, connectionList.contains(conn1));
		assertEquals("connectin list contains conn2", false, connectionList.contains(conn2));
	
		// assert pins
		
		assertEquals("filter1 input connected with source1", 
				true, filter1Input.isConnectedWith(source1Output));
		assertEquals("filter1 input connected with source2", 
				false, filter1Input.isConnectedWith(source2Output));
		
		
		
		connectionList.add(conn2);
		
		assertEquals("connection list first replaced", false, connectionList.isEmpty());
		assertEquals("connection list first replaced", 1, connectionList.size());
		assertEquals("connectin list contains conn1", false, connectionList.contains(conn1));
		assertEquals("connectin list contains conn2", true, connectionList.contains(conn2));
		
		// assert pins
		
		assertEquals("filter1 input connected with source1", 
				false, filter1Input.isConnectedWith(source1Output));
		assertEquals("filter1 input connected with source2", 
				true, filter1Input.isConnectedWith(source2Output));
		
	}
	
}
