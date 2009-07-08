package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import ij.plugin.filter.PlugInFilter;

import java.awt.Point;

import org.junit.Test;

import visualap.Node;
import de.danielsenff.imageflow.models.SelectionList;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.ProxyOutput;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;

public class DataTypeTests extends UnitElementTests{

	@Test
	public void testDataTypeInteger() {
		DataType integer1 = DataTypeFactory.createInteger();
		DataType integer2 = DataTypeFactory.createInteger();
		DataType image1 = DataTypeFactory.createImage(PlugInFilter.DOES_32);
		
		assertTrue("int on int", integer1.isCompatible(integer2));
		assertTrue("int on int", integer2.isCompatible(integer1));
		assertFalse("int on img", integer1.isCompatible(image1));
		assertFalse("img on int", image1.isCompatible(integer1));
	}
	
	
	/*
	 * Image
	 */
	
	@Test public void testInputImageBitDepthCompatible() {

		UnitElement unit1 = createSourceUnit();
		Output output_32 = unit1.getOutput(0); // 32
		Output output_all = unit1.getOutput(1); // all
		Output output_m1 = unit1.getOutput(2); // -1
		Output output_3216 = unit1.getOutput(3); // -1
		
		UnitElement unit2 = createSinkUnit();
		Input input_32 = unit2.getInput(0); // 32
		Input input_16 = unit2.getInput(1); // 16
		Input input_all = unit2.getInput(2); // all
		Input input_3216 = unit2.getInput(3); // 32/16

		// strictly determined
		assertTrue("both do 32", input_32.isCompatible(output_32));
		assertFalse("32 to 16", input_16.isCompatible(output_32));
		assertTrue("32 to all", input_all.isCompatible(output_32));
		assertTrue("32 to 31/16", input_3216.isCompatible(output_32));
		
		
		
		// undetermined output
		// ok, technically ALL to 32 does work, but since the input tests for a concrete type, 
		// and not for a could-be, this is false
		
		//FIXME technically it is always possible to assign an output as all, this just breaks the system
//		assertFalse("all to 32", input_32.isCompatible(output_all));
//		assertFalse("all to 32/16", input_32.isCompatible(output_3216));
		assertFalse("-1 to 32", input_32.isCompatible(output_m1));
		assertFalse("-1 to all", input_16.isCompatible(output_m1));
		assertFalse("-1 to 31/16", input_3216.isCompatible(output_m1));
		
		// this are also undetermined
		assertTrue("32/16 to 16", input_16.isCompatible(output_3216));
		assertTrue("32/16 to 16", input_32.isCompatible(output_3216));
		
	}
	

	@Test public void testOutputImageBitDepthCompatible() {

		UnitElement unit1 = createSourceUnit();
		Output output_32 = unit1.getOutput(0); // 32
		Output output_all = unit1.getOutput(1); // all
		Output output_m1 = unit1.getOutput(2); // -1
		Output output_3216 = unit1.getOutput(3); // -1
		
		UnitElement unit2 = createSinkUnit();
		Input input_32 = unit2.getInput(0); // 32
		Input input_16 = unit2.getInput(1); // 16
		Input input_all = unit2.getInput(2); // all
		Input input_3216 = unit2.getInput(3); // all

		// output determined
		assertTrue("both do 32", output_32.isCompatible(input_32));
		assertTrue("32 to all", output_32.isCompatible(input_all));
		assertTrue("32 to 32/16", output_32.isCompatible(input_3216));
		assertFalse("32 to 16", output_32.isCompatible(input_16));
		assertFalse("32 to 16", output_32.isCompatible(input_16));
		
		// undetermined
		// ok, technically ALL to 32 does work, but since the input tests for a concrete type, 
		// and not for a could-be, this is false
//		assertFalse("All to 32", output_all.isCompatible(input_32));
//		assertFalse("32 to 32/16", output_3216.isCompatible(input_3216));
//		assertFalse("32 to 32/16", output_3216.isCompatible(input_32));
		assertFalse("-1 to 16", output_m1.isCompatible(input_16));
		assertFalse("-1 to 16", output_m1.isCompatible(input_all));
		assertFalse("-1 to 16", output_m1.isCompatible(input_32));
		
		
	}
	
	/*
	 * We have 3 units
	 * the first one has a determined output datatype
	 * the second takes it and passes it through
	 * the third has the actual comparison
	 */
	@Test public void testBubbleInput_32_m1_32() {
		UnitElement unit1 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_32);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_32, -1);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		
		Connection conn_1_2 = new Connection(unit1, 1, unit2, 1);
		conn_1_2.connect();
		Connection conn_2_3 = new Connection(unit2, 1, unit3, 1);
		conn_2_3.connect();
		
		assertTrue(conn_1_2.isConnected());
		assertTrue(conn_2_3.isConnected());
		assertTrue(conn_1_2.isCompatible());
		assertTrue(conn_2_3.isCompatible());
	}
	
	@Test public void testBubbleInput_32_all_all() {
		UnitElement unit1 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_32);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_ALL, -1);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_ALL, PlugInFilter.DOES_32);
		
		Connection conn_1_2 = new Connection(unit1, 1, unit2, 1);
		conn_1_2.connect();
		Connection conn_2_3 = new Connection(unit2, 1, unit3, 1);
		conn_2_3.connect();
		
		assertTrue(conn_1_2.isConnected());
		assertTrue(conn_2_3.isConnected());
		assertTrue(conn_1_2.isCompatible());
		assertTrue(conn_2_3.isCompatible());
	}
	
	@Test public void testBubbleInput_32_m1_16() {
		UnitElement unit1 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_32);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_32, -1);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_32);
		
		Connection conn_1_2 = new Connection(unit1, 1, unit2, 1);
		conn_1_2.connect();
		Connection conn_2_3 = new Connection(unit2, 1, unit3, 1);
		conn_2_3.connect();
		
		assertTrue(conn_1_2.isConnected());
		assertTrue(conn_2_3.isConnected());
		assertTrue(conn_1_2.isCompatible());
		assertFalse(conn_2_3.isCompatible());
	}
	
	@Test public void testBubbleInput_16_1632_32() {
		UnitElement unit1 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_16);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_32);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		
		Connection conn_1_2 = new Connection(unit1, 1, unit2, 1);
		conn_1_2.connect();
		Connection conn_2_3 = new Connection(unit2, 1, unit3, 1);
		conn_2_3.connect();
		
		assertTrue(conn_1_2.isConnected());
		assertTrue(conn_2_3.isConnected());
		assertTrue(conn_1_2.isCompatible());
		assertTrue(conn_2_3.isCompatible());
	}
	
	@Test public void testBubbleInput_8c_1632_ALL() {
		UnitElement unit1 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_8C);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_32);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_ALL, PlugInFilter.DOES_32);
		
		Connection conn_1_2 = new Connection(unit1, 1, unit2, 1);
		conn_1_2.connect();
		Connection conn_2_3 = new Connection(unit2, 1, unit3, 1);
		conn_2_3.connect();
		
		assertTrue(conn_1_2.isConnected());
		assertTrue(conn_2_3.isConnected());
		assertFalse(conn_1_2.isCompatible());
		assertTrue(conn_2_3.isCompatible());
	}
	
	@Test public void testBubbleInput_8c_1632_1632() {
		UnitElement unit1 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_8C);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_16&PlugInFilter.DOES_8G, PlugInFilter.DOES_32);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_16&PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		
		Connection conn_1_2 = new Connection(unit1, 1, unit2, 1);
		conn_1_2.connect();
		Connection conn_2_3 = new Connection(unit2, 1, unit3, 1);
		conn_2_3.connect();
		
		assertTrue(conn_1_2.isConnected());
		assertTrue(conn_2_3.isConnected());
		assertFalse(conn_1_2.isCompatible());
		assertFalse(conn_2_3.isCompatible());
	}
	
	
	/**
	 * test bubbling through a group
	 * 1 unit outputting a determined value
	 * 2 group
	 *   - unit taking input
	 *   - unit taking input
	 * 3 unit taking input
	 * 
	 */
	@Test public void testBubbleGroupInput_8c_1632_1632() {
		UnitList units = new UnitList();	
		
		UnitElement unit1 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_32);
		units.add(unit1);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_ALL, -1);
		units.add(unit2);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_ALL, -1);
		units.add(unit3);
		UnitElement unit4 = createUnit(PlugInFilter.DOES_ALL, -1);
		units.add(unit4);
		UnitElement unit5 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		units.add(unit5);
		
		ConnectionList connList = units.getConnections();
		Connection conn_1_2 = new Connection(unit1, 1, unit2, 1);
		connList.add(conn_1_2);
		Connection conn_2_3 = new Connection(unit2, 1, unit3, 1);
		connList.add(conn_2_3);
		Connection conn_3_4 = new Connection(unit3, 1, unit4, 1);
		connList.add(conn_3_4);
		Connection conn_4_5 = new Connection(unit4, 1, unit5, 1);
		connList.add(conn_4_5);
		
		for (Connection connection : connList) {
			connection.isConnected();
			connection.isCompatible();
		}
				
		SelectionList selections = new SelectionList();
		selections.add(unit2);
		selections.add(unit3);
		
		GroupUnitElement group = new GroupUnitElement(new Point(33, 55), "group", selections, units);
		units.add(group);
		
		assertEquals("number of inputs", 1, group.getInputsCount());
		assertEquals("number of outputs", 1, group.getOutputsCount());
		assertEquals("number of internal connection", 1, group.getInternalConnections().size());
		
		for (Connection connection : units.getConnections()) {
			assertTrue(connection.isConnected());
			assertTrue(connection.isCompatible());
		}
		
		for (Connection connection : units.getConnections()) {
			assertTrue(connection.isConnected());
			assertTrue(connection.isCompatible());
		}
		
		Connection connToGroup = group.getInput(0).getConnection();
		assertTrue(connToGroup.isConnected());
		assertTrue(connToGroup.isCompatible());
		
		
		for (Connection connFromGroup : group.getOutput(0).getConnections()) {
			assertTrue(connFromGroup.isConnected());
			assertTrue(connFromGroup.isCompatible());
		}
	}
	
	@Test public void testBubbleSourceGroupInput_1632_1632() {
		UnitList units = new UnitList();	
		
		UnitElement unit1 = createUnit(PlugInFilter.DOES_16, PlugInFilter.DOES_32);
		units.add(unit1);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_ALL, -1);
		units.add(unit2);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_ALL, -1);
		units.add(unit3);
		UnitElement unit4 = createUnit(PlugInFilter.DOES_ALL, -1);
		units.add(unit4);
		UnitElement unit5 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		units.add(unit5);
		
		ConnectionList connList = units.getConnections();
		Connection conn_1_2 = new Connection(unit1, 1, unit2, 1);
		connList.add(conn_1_2);
		Connection conn_2_3 = new Connection(unit2, 1, unit3, 1);
		connList.add(conn_2_3);
		Connection conn_3_4 = new Connection(unit3, 1, unit4, 1);
		connList.add(conn_3_4);
		Connection conn_4_5 = new Connection(unit4, 1, unit5, 1);
		connList.add(conn_4_5);
		
		for (Connection connection : connList) {
			connection.isConnected();
			connection.isCompatible();
		}
		
		SelectionList selections = new SelectionList();
		selections.add(unit1);
		selections.add(unit2);
		selections.add(unit3);
		
		GroupUnitElement group = new GroupUnitElement(new Point(33, 55), "group", selections, units);
		
		for (Connection connection : group.getInternalConnections()) {
			assertTrue(connection.isConnected());
		}
		
		assertEquals("number of inputs", 0, group.getInputsCount());
		assertEquals("number of outputs", 1, group.getOutputsCount());
		assertEquals("number of internal connection", 2, group.getInternalConnections().size());
		assertEquals("number of connections", 2, units.getConnections().size());
		assertEquals("number of units",2 , units.size());
		assertEquals("number of grouped units",3 , group.getNodes().size());
		
		
		ProxyOutput groupOutput = (ProxyOutput)group.getOutput(0);
		DataTypeFactory.Image dtImage = (DataTypeFactory.Image)groupOutput.getDataType();
		assertEquals(8, dtImage.getImageBitDepth());
		assertTrue(dtImage.isImageBitDepthCompatible(8));
		units.add(group);

	
		
		
		for (Connection connection : units.getConnections()) {
			assertTrue(connection.isConnected());
			assertTrue(connection.isCompatible());
		}
		
		for (Connection connFromGroup : groupOutput.getConnections()) {
			assertTrue(connFromGroup.isConnected());
			assertTrue("compatibility for "+connFromGroup, connFromGroup.isCompatible());
		}
	}
	
	
	
	
	
}
