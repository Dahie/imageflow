package models;


import static org.junit.Assert.*;

import java.awt.Dimension;

import org.junit.Test;

import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class ConnectionListTests {

	
	@Test public void construction() {
		//assert connectionList
		ConnectionList connectionList = new ConnectionList();
		assertTrue("connection list empty", connectionList.isEmpty());
		assertEquals("connection list empty", 0, connectionList.size());
	}
	
	/**
	 * Testing the add-methods of the ConnectionList
	 * This only tests the add(connection)-methods
	 */
	@Test
	public void testAddConnection() {
		
		// test output-only
		UnitElement source1Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source1Output = source1Unit.getOutput(0);
		UnitElement source2Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source2Output = source2Unit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		Input filter1Input = filterUnit1.getInput(0); 
		Output filter1Output = filterUnit1.getOutput(0);
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		Input filter2Input = filterUnit2.getInput(0); 
		Output filter2Output = filterUnit2.getOutput(0);
		
		
		//assert connectionList
		ConnectionList connectionList = new ConnectionList();
		assertTrue("connection list empty", connectionList.isEmpty());
		assertEquals("connection list empty", 0, connectionList.size());
		assertFalse("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertFalse("filter1 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
		Connection conn1 = new Connection(source1Unit, 1, filterUnit1, 1);
		
		// adding first connection
		assertTrue("add conn1", connectionList.add(conn1));
		assertFalse("connection list first added", connectionList.isEmpty());
		assertEquals("connection list first added", 1, connectionList.size());
		assertTrue("connectin list contains conn1", connectionList.contains(conn1));
		assertTrue("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertFalse("filter2 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
		
		// adding second connection, this replaces the first
		Connection conn2 = new Connection(source2Unit, 1, filterUnit1, 1);
		assertTrue("add conn2", connectionList.add(conn2));
		
		assertFalse("connection list first replaced", connectionList.isEmpty());
		assertEquals("connection list first replaced", 1, connectionList.size());
		assertFalse("connection list contains conn1", connectionList.contains(conn1));
		assertTrue("connection list contains conn2", connectionList.contains(conn2));
		
		// assert pins
		
		assertFalse("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertTrue("filter1 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
		// now for illegal connections
		
		// connection connecting output to input on same parent
		/*TODO
		 * Connection conn3 = new Connection(source2Unit, 1, source2Unit, 1);
		 *
		assertFalse("add conn3", connectionList.add(conn3));
		assertFalse(connectionList.isEmpty());
		assertEquals( 1, connectionList.size());
		assertFalse("connection list contains conn3", connectionList.contains(conn1));*/
		
	}
	

	/**
	 * Testing the add-methods of the ConnectionList
	 * This only tests the add(connection)-methods
	 */
	@Test
	public void testAddPinPin() {
		
		// test output-only
		UnitElement source1Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source1Output = source1Unit.getOutput(0);
		UnitElement source2Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source2Output = source2Unit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		Input filter1Input = filterUnit1.getInput(0); 
		Output filter1Output = filterUnit1.getOutput(0);
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		Input filter2Input = filterUnit2.getInput(0); 
		Output filter2Output = filterUnit2.getOutput(0);
		
		
		//assert connectionList
		ConnectionList connectionList = new ConnectionList();
		assertTrue("connection list empty", connectionList.isEmpty());
		assertEquals("connection list empty", 0, connectionList.size());
		
		// assert pins
		
		assertFalse("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertFalse("filter1 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
		// adding first connection
		assertTrue("add conn1", connectionList.add(source1Output, filter1Input));
		assertFalse("connection list first added", connectionList.isEmpty());
		assertEquals("connection list first added", 1, connectionList.size());
	
		// assert pins
		
		assertTrue("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertFalse("filter1 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
		// adding second connection, this replaces the first
		assertTrue("add conn2", connectionList.add(source2Output, filter1Input));
		assertFalse("connection list first replaced", connectionList.isEmpty());
		assertEquals("connection list first replaced", 1, connectionList.size());
		
		// assert pins
		
		assertFalse("filter1 input connected with source1", 
				filter1Input.isConnectedWith(source1Output));
		assertTrue("filter1 input connected with source2", 
				filter1Input.isConnectedWith(source2Output));
		
		// now for illegal connections
		
		// TODO ...
	}
	
	public void testContains() {
		
		// test output-only
		UnitElement source1Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source1Output = source1Unit.getOutput(0);
		UnitElement source2Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source2Output = source2Unit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		Input filter1Input = filterUnit1.getInput(0); 
		Output filter1Output = filterUnit1.getOutput(0);
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		Input filter2Input = filterUnit2.getInput(0); 
		Output filter2Output = filterUnit2.getOutput(0);
		
		ConnectionList connectionList = new ConnectionList();
		
		Connection conn2 = new Connection(source1Output, filter1Input);
		assertFalse(connectionList.contains(conn2));
		assertFalse(connectionList.containsConnection(source1Output, filter1Input));
		assertFalse(connectionList.containsConnection(filter1Input, source1Output));
		assertFalse(connectionList.containsConnection(source1Output, filter2Input));
		
		
		assertTrue("add conn2", connectionList.add(conn2));
		assertTrue(connectionList.contains(conn2));
		assertTrue(connectionList.containsConnection(source1Output, filter1Input));
		assertFalse(connectionList.containsConnection(source1Output, filter2Input));

	}
	
	
	public void testLoopConnection() {
	
		UnitElement filter1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filter2 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filter3 = UnitFactoryExt.createAddNoiseUnit();
		
		ConnectionList connectionList = new ConnectionList();
		assertTrue("create conn f1 to f2", connectionList.add(filter1, 1, filter2, 1));
		assertTrue("create conn f1 to f2", connectionList.add(filter1.getOutput(0), filter2.getInput(0)));
		assertTrue("create conn f2 to f3", connectionList.add(filter2, 1, filter3, 1));
		
		// try to create loops
		assertFalse("create conn f3 to f1",  connectionList.add(filter3, 1, filter1, 1));
		assertFalse("create conn f3 to f1",  connectionList.add(filter3.getOutput(0), filter1.getInput(0)));
	}
	
	public void testRemoveConnection() {
		UnitElement filter1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filter2 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filter3 = UnitFactoryExt.createAddNoiseUnit();
		
		ConnectionList connectionList = new ConnectionList();
		assertTrue("add conn1", connectionList.add(filter1, 1, filter2, 1));
		assertTrue("add conn2", connectionList.add(filter2, 1, filter3, 1));
		
		assertFalse("filter1 inputs ", filter1.hasInputsConnected());
		assertTrue("filter2 inputs ", filter2.hasInputsConnected());
		assertTrue("filter3 inputs ", filter3.hasInputsConnected());
		
		assertTrue("filter1 outputs", filter1.hasOutputsConnected());
		assertTrue("filter2 outputs", filter2.hasOutputsConnected());
		assertFalse("filter3 outputs", filter3.hasOutputsConnected());
		
		connectionList.remove(1);
		
		assertFalse("filter1 inputs ", filter1.hasInputsConnected());
		assertTrue("filter2 inputs ", filter2.hasInputsConnected());
		assertFalse("filter3 inputs ", filter3.hasInputsConnected());
		
		assertTrue("filter1 outputs", filter1.hasOutputsConnected());
		assertFalse("filter2 outputs", filter2.hasOutputsConnected());
		assertFalse("filter3 outputs", filter3.hasOutputsConnected());
		
	}
	
	
}
