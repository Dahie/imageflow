package models;

import static org.junit.Assert.*;
import ij.plugin.filter.PlugInFilter;

import java.awt.Point;

import org.junit.Test;

import de.danielsenff.imageflow.models.SelectionList;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;

public class GroupUnitTests extends UnitElementTests {

	/**
	 * We create a unit and after object construction we add
	 * units to the unit.
	 */
	@Test public void testLateGroupAdding() {
		UnitList units = new UnitList();
		
		UnitElement unit1 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);

		Connection conn1_2 = new Connection(unit1, 1, unit2, 1);
		units.getConnections().add(conn1_2);
		Connection conn2_3 = new Connection(unit2, 1, unit3, 1);
		units.getConnections().add(conn2_3);
		
		/*
		 * construct empty group
		 */
		
		GroupUnitElement group = new GroupUnitElement(new Point(33, 55), "group");
		
		assertEquals("inputs count",0 , group.getInputsCount());
		assertFalse("has inputs", group.hasInputs());
		assertEquals("outputs count",0 ,  group.getOutputsCount());
		assertFalse("has outputs", group.hasOutputs());
		assertEquals("units count", 0, group.getNodes().size());
		assertEquals("internal connections count", 0, group.getInternalConnections().size());
		
		SelectionList selections = new SelectionList();
		selections.add(unit1);
		selections.add(unit2);
		
		/* 
		 * add units to group
		 */
		
		try {
			group.putUnits(selections, units);
			
			assertEquals("inputs count",0 , group.getInputsCount());
			assertFalse("has inputs", group.hasInputs());
			assertEquals("outputs count",1 , group.getOutputsCount());
			assertTrue("has outputs", group.hasOutputs());
			assertEquals("units count", 2, group.getNodes().size());
			assertEquals("internal connections count", 1, group.getInternalConnections().size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 1 unit1 selected for group
	 * 2 unit2
	 * 3 unit3 selected for group
	 * create group
	 */
	@Test public void testGroupWithUnselectedUnitsInGraph() {
		UnitList units = new UnitList();
		
		UnitElement unit1 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		UnitElement unit2 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);
		UnitElement unit3 = createUnit(PlugInFilter.DOES_32, PlugInFilter.DOES_32);

		Connection conn1_2 = new Connection(unit1, 1, unit2, 1);
		units.getConnections().add(conn1_2);
		Connection conn2_3 = new Connection(unit2, 1, unit3, 1);
		units.getConnections().add(conn2_3);
		
		assertEquals("number of connections", 2, units.getConnections().size());
		for (Connection connection : units.getConnections()) {
			assertTrue(connection.isConnected());
			assertTrue(connection.isCompatible());
		}
		
		SelectionList selections = new SelectionList();
		selections.add(unit1);
		selections.add(unit3);
		
		GroupUnitElement group = new GroupUnitElement(new Point(33, 55), "group", selections, units);
		
		assertEquals("inputs count",0 , group.getInputsCount());
		assertFalse("has inputs", group.hasInputs());
		assertEquals("outputs count",1 , group.getOutputsCount());
		assertTrue("has outputs", group.hasOutputs());
		assertEquals("units count", 2, group.getNodes().size());
		assertEquals("internal connections count", 1, group.getInternalConnections().size());
	}
	
}
