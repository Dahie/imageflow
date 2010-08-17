package models;


import java.awt.Dimension;

import de.danielsenff.imageflow.imagej.MacroFlowRunner;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;

import org.junit.Test;

import static org.junit.Assert.*;

public class MacroFlowRunnerTests {

	@Test public void testSortAlgorithm1() {
		UnitList units = buildSampleWorkflow();
		
		UnitElement source1 = (UnitElement) units.get(0);
		UnitElement filter1 = (UnitElement) units.get(1);
		UnitElement filter2 = (UnitElement) units.get(2);
		UnitElement filter3 = (UnitElement) units.get(3);
		UnitElement source2 = (UnitElement) units.get(4);
		
		UnitList sortedList = MacroFlowRunner.sortList(units);
		
		assertEquals("source on correct position 0", source1, sortedList.get(0));
		assertEquals("source on correct position 1", filter1, sortedList.get(1));
		assertEquals("source on correct position 2", filter2, sortedList.get(2));
		
	}

	private UnitList buildSampleWorkflow() {
		UnitList unitList = new UnitList();
		
		UnitElement source1 = UnitFactoryExt.createBackgroundUnit(new Dimension(10, 10));
		UnitElement filter1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filter2 = UnitFactoryExt.createFindEdgesUnit();
		UnitElement filter3 = UnitFactoryExt.createGaussianBlurUnit();
		
		UnitElement source2 = UnitFactoryExt.createBackgroundUnit(new Dimension(10,0));
		
		unitList.add(source1);
		unitList.add(source2);
		unitList.add(filter1);
		unitList.add(filter2);
		unitList.add(filter3);
		
		assertEquals(5, unitList.size());
		assertFalse(unitList.hasUnitAsDisplay());
		filter2.setDisplay(true);
		assertTrue(unitList.hasUnitAsDisplay());
		
		
		ConnectionList connList = unitList.getConnections();
		connList.add(new Connection(source1, 1, filter1, 1));
		connList.add(new Connection(filter1, 1, filter2, 1));
		
		assertTrue("branch has display units", source1.hasDisplayBranch());
		assertTrue("branch has display units", filter1.hasDisplayBranch());
		
		connList.add(new Connection(source2, 1, filter3, 1));
		
		assertFalse("branch has display", source2.hasDisplayBranch());
		assertEquals(5, unitList.size());
		
		return unitList;
	}
	
	
}
