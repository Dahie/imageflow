package models;

import static org.junit.Assert.*;
import ij.plugin.filter.PlugInFilter;

import java.awt.Dimension;

import org.junit.Test;

import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class OutputTests {

	
	@Test public void testImageTitle() {
		
		// image title of style "Unit_1_Output_1"
		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
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
				"Unit_"+sourceUnit.getUnitID()+"_Output_1", sourceOutput.getOutputTitle());
		assertEquals("imagetitle for input 1 at unit 2", 
				"Unit_"+sourceUnit.getUnitID()+"_Output_1", filter1Input.getImageTitle());
		
		assertNotNull("imagetitle for output 1 at unit 2", filter1Output.getOutputTitle());
	}
	

	@Test public void testIsConnected() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
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
	
	@Test public void testIsConnectedWith() {
		// test output-only
		UnitElement source1Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source1Output = source1Unit.getOutput(0);
		UnitElement source2Unit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		Output source2Output = source2Unit.getOutput(0);
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		Input filter1Input = filterUnit1.getInput(0);
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		Input filter2Input = filterUnit2.getInput(0);
		
		// test beforehand
		assertFalse("output not connected with filter1Input", 
				source1Output.isConnectedWith(filter1Input));
		assertFalse("output not connected with filter2Input", 
				source1Output.isConnectedWith(filter2Input));
		
		Connection conn = new Connection(source1Unit, 1, filterUnit1, 1);
//		ConnectionList connList = new ConnectionList();
//		connList.add(conn);
		conn.connect();
		
		
		// test after connecting
		assertTrue("Connection connected correctly", conn.isConnected());
		assertTrue("output connected with filter1Input", 
				source1Output.isConnectedWith(filter1Input));
		assertFalse("output not connected with filter2Input", 
				source1Output.isConnectedWith(filter2Input));
	}
	
	@Test public void testIsDisconnected() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();
		
		Output sourceOutput = sourceUnit.getOutput(0);
		
		
		ConnectionList connList = new ConnectionList();
		
		// test after connecting
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		assertTrue("connection added to list", connList.add(conn));
		assertTrue("output connected", sourceOutput.isConnected());	
		assertFalse("output connected", filterUnit2.getOutput(0).isConnected());
		
		connList.remove(conn);
		
		//test after disconnecting
		assertFalse("output disconnected", sourceOutput.isConnected());
		assertFalse("output connected", filterUnit2.getOutput(0).isConnected());
	}

	
	public String verboseBitDepth(int imagetype) {
		switch(imagetype) {
		case PlugInFilter.DOES_16:
			return "DOES_16";
		case PlugInFilter.DOES_32:
			return "DOES_32";
		case PlugInFilter.DOES_8G:
			return "DOES_8G";
		case PlugInFilter.DOES_8C:
			return "DOES_8C";
		case PlugInFilter.DOES_RGB:
			return "DOES_RGB";
		case PlugInFilter.DOES_ALL:
			return "DOES_ALL";
		case PlugInFilter.DOES_STACKS:
			return "DOES_STACKS";
		case -1:
			return "predecessor type";
		}
		return "unknown";
	}
	
	public void traverseImageBitDepth(final int unit1Obitdepth, 
			final int unit2Ibitdepth, final int unit2Obitdepth, 
			final int unit3Ibitdepth,
			final boolean expFirstConn, final boolean expScndConn) {
		UnitElement unit1 = new UnitElement("unit1", "some syntax");
		DataType dataType = DataTypeFactory.createImage(unit1Obitdepth);
		unit1.addOutput(new Output(dataType, unit1, 1));
//		unit1.addOutput("output1", "o", unit1Obitdepth, false);
		
		UnitElement unit2 = new UnitElement("unit2", "some syntax");
		unit2.addInput(new Input("input1", "i",DataTypeFactory.createImage(unit2Ibitdepth),unit2, 1, true, false));
		unit2.addOutput(new Output("output1", "o", DataTypeFactory.createImage(unit2Obitdepth), unit2, 1));

		UnitElement unit3 = new UnitElement("unit3", "some syntax");
		unit3.addInput(new Input("input1", "i",DataTypeFactory.createImage(unit3Ibitdepth),unit3, 1, true, false));

		Connection conn1 = new Connection(unit1, 1, unit2, 1); // 32 to 32
		conn1.connect();
		assertEquals("Conn1: "+verboseBitDepth(unit1Obitdepth)+" to "+ verboseBitDepth(unit2Ibitdepth), 
				expFirstConn, conn1.isCompatible());
		Connection conn2 = new Connection(unit2, 1, unit3, 1); // 32 to 32
		conn2.connect();
		assertEquals("Conn2: "+verboseBitDepth(unit2Obitdepth)+" to "+ verboseBitDepth(unit3Ibitdepth), 
				expScndConn, conn2.isCompatible());
	}
	
	@Test public void testImageBitDepthTraversing() {
		
		traverseImageBitDepth(PlugInFilter.DOES_32, //unit 1 output
				PlugInFilter.DOES_32, PlugInFilter.DOES_32,  //unit2 input, output
				PlugInFilter.DOES_32, true, true); // unit 3, expectations
		
		traverseImageBitDepth(PlugInFilter.DOES_32, //unit 1 output
				PlugInFilter.DOES_ALL, PlugInFilter.DOES_32,  //unit2 input, output
				PlugInFilter.DOES_ALL, true, true); // unit 3, expectations
		
		
		
		traverseImageBitDepth(PlugInFilter.DOES_32, //unit 1 output
				PlugInFilter.DOES_ALL, PlugInFilter.DOES_32,  //unit2 input, output
				PlugInFilter.DOES_16, true, false); // unit 3, expectations
		traverseImageBitDepth(PlugInFilter.DOES_32, //unit 1 output
				PlugInFilter.DOES_16, PlugInFilter.DOES_32,  //unit2 input, output
				PlugInFilter.DOES_32, false, true); // unit 3, expectations
		
		
		traverseImageBitDepth(PlugInFilter.DOES_32, //unit 1 output
				PlugInFilter.DOES_ALL, -1,  //unit2 input, output
				PlugInFilter.DOES_32, true, true); // unit 3, expectations
		
		traverseImageBitDepth(PlugInFilter.DOES_16, //unit 1 output
				PlugInFilter.DOES_ALL, -1,  //unit2 input, output
				PlugInFilter.DOES_32, true, false); // unit 3, expectations

		traverseImageBitDepth(PlugInFilter.DOES_16, //unit 1 output
				PlugInFilter.DOES_ALL, -1,  //unit2 input, output
				PlugInFilter.DOES_ALL, true, true); // unit 3, expectations	
	}
	
	
	@Test public void testTraversingImageBitDepth() {
		UnitElement unit1 = new UnitElement("unit1", "some syntax");
		Output output1 = new Output("output1", "o", DataTypeFactory.createImage(PlugInFilter.DOES_ALL), unit1, 1);
		unit1.addOutput(output1);
		
		UnitElement unit2 = new UnitElement("unit2", "some syntax");
		Input input1 = new Input("input1", "i",DataTypeFactory.createImage(PlugInFilter.DOES_ALL),unit2, 1, true, false);
		unit2.addInput(input1);
		Output output2 = new Output("output1", "o", DataTypeFactory.createImage(-1), unit2, 1);
		unit2.addOutput(output2);
		
//		UnitElement unit3 = new UnitElement("unit3", "some syntax");
//		unit3.addInput("input1", "i", unit3Ibitdepth, false);

		Connection conn1 = new Connection(unit1, 1, unit2, 1);
		conn1.connect();
		
		DataTypeFactory.Image o1dT = ((DataTypeFactory.Image)output1.getDataType());
		assertTrue("travers from DOES_32", 
				o1dT.isImageBitDepthCompatible(PlugInFilter.DOES_32));
		assertFalse("travers from DOES_32", 
				o1dT.isImageBitDepthCompatible(PlugInFilter.DOES_16));
		
		DataTypeFactory.Image o2dT = ((DataTypeFactory.Image)output2.getDataType());
		assertTrue("travers from DOES_32 via DOES_ALL to -1",
				o2dT.isImageBitDepthCompatible(PlugInFilter.DOES_32));
		
		assertFalse("travers from DOES_32 via DOES_ALL to -1", 
				o2dT.isImageBitDepthCompatible(PlugInFilter.DOES_16));

	}
	
	
	@Test public void testLoopScenario1() {
		
		UnitElement unit1 = UnitFactoryExt.createFindEdgesUnit();
		UnitElement unit2 = UnitFactoryExt.createImageCalculatorUnit();
		UnitElement unit3 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit4 = UnitFactoryExt.createGaussianBlurUnit();
		
		Connection conn1 = new Connection(unit1, 1, unit2, 1);
		conn1.connect();
		assertTrue(conn1.isConnected());
		Connection conn2 = new Connection(unit3, 1, unit2, 2);
		conn2.connect();
		assertTrue(conn2.isConnected());
		
		Output outputU2 = unit2.getOutput(0);
		Input inputU4 = unit4.getInput(0);
		
		assertFalse(outputU2.existsInInputSubgraph(unit4));
		assertFalse(inputU4.isConnectedInInputBranch(unit2));
		
		// now we create a loop
		
		Connection conn3 = new Connection(unit4, 1, unit1, 1);
		conn3.connect();
		assertTrue(conn3.isConnected());
		
		assertTrue(outputU2.existsInInputSubgraph(unit4));
		assertTrue(inputU4.isConnectedInInputBranch(unit2));
	}
	

	@Test public void testLoopScenario2() {
		
		UnitElement unit1 = UnitFactoryExt.createFindEdgesUnit();
		UnitElement unit2 = UnitFactoryExt.createImageCalculatorUnit();
		UnitElement unit4 = UnitFactoryExt.createGaussianBlurUnit();
		
		Connection conn1 = new Connection(unit1, 1, unit2, 1);
		conn1.connect();
		Connection conn2 = new Connection(unit1, 1, unit2, 2);
		conn2.connect();
		assertTrue(conn2.isConnected());
		
		Output outputU2 = unit2.getOutput(0);
		Input inputU4 = unit4.getInput(0);
		
		assertFalse(outputU2.existsInInputSubgraph(unit4));
		assertFalse(inputU4.isConnectedInOutputBranch(unit2));
		
		// now we create a loop
		
		Connection conn3 = new Connection(unit4, 1, unit1, 1);
		conn3.connect();
		assertTrue(conn3.isConnected());
		
		assertTrue(outputU2.existsInInputSubgraph(unit4));
		assertTrue(inputU4.isConnectedInOutputBranch(unit2));
	}
	
	@Test public void testLoopScenario3() {
		UnitElement unit1 = UnitFactoryExt.createFindEdgesUnit();
		UnitElement unit4 = UnitFactoryExt.createGaussianBlurUnit();
		
		Output outputU1 = unit1.getOutput(0);
		Input inputU4 = unit4.getInput(0);
		
		assertFalse(outputU1.existsInInputSubgraph(unit4));
		assertFalse(inputU4.isConnectedInOutputBranch(unit1));
		
		//connecting first time
		
		Connection conn1 = new Connection(unit1, 1, unit4, 1);
		conn1.connect();
		assertTrue(conn1.isConnected());
		
		assertFalse(outputU1.existsInInputSubgraph(unit4));
		assertFalse(inputU4.isConnectedInOutputBranch(unit1));
		
		// creating the same connection a second time
		
		Connection conn3 = new Connection(unit4, 1, unit1, 1);
		conn3.connect();
		assertTrue(conn3.isConnected());
//		assertFalse(conn1.isConnected());
	}
	
	
	@Test public void testUnitConnectedInBranch() {
		
		UnitElement unit1 = UnitFactoryExt.createAddNoiseUnit();
		Output output1 = unit1.getOutput(0);
		UnitElement unit2 = UnitFactoryExt.createAddNoiseUnit();
		Output output2 = unit2.getOutput(0);
		UnitElement unit3 = UnitFactoryExt.createAddNoiseUnit();
		
		Connection conn1 = new Connection(unit1, 1, unit2, 1);
		
		ConnectionList connList = new ConnectionList();
		connList.add(conn1);
		
		
		assertTrue("output2 knows unit1", output2.existsInInputSubgraph(unit1));
		assertTrue("output2 knows unit2", output2.existsInInputSubgraph(unit2));
		assertFalse("output2 knows unit3", output2.existsInInputSubgraph(unit3));
		
		Connection conn2 = new Connection(unit2, 1, unit3, 1);
		
		
		assertTrue("output2 knows unit1", output2.existsInInputSubgraph(unit1));
		assertTrue("output2 knows unit2", output2.existsInInputSubgraph(unit2));
		assertFalse("output2 knows unit3", output2.existsInInputSubgraph(unit3));
		
		/*
		 * output 1 is on a source, so it has no input branch
		 */
		
		assertTrue("output1 knows unit1", output1.existsInInputSubgraph(unit1));
		assertFalse("output1 knows unit2", output1.existsInInputSubgraph(unit2));
		assertFalse("output1 knows unit3", output2.existsInInputSubgraph(unit3));
		
		
		connList.add(conn2);
		
		
		assertTrue("output1 knows unit1", output1.existsInInputSubgraph(unit1));
		assertFalse("output1 knows unit2", output1.existsInInputSubgraph(unit2));
		assertFalse("output1 knows unit3", output1.existsInInputSubgraph(unit3));
		
	}
}
