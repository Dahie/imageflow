package models;

import imageflow.backend.MacroFlowRunner;
import imageflow.models.Connection;
import imageflow.models.ConnectionList;
import imageflow.models.unit.AbstractUnit;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitFactory;
import imageflow.models.unit.UnitList;

import java.awt.Dimension;

import junit.framework.TestCase;

public class MacroFlowRunnerTests extends TestCase {

	
	public void testSortAlgorithm1() {

		
		
		
		UnitList units = buildSampleWorkflow();
		
		UnitElement source1 = (UnitElement) units.get(0);
		UnitElement filter1 = (UnitElement) units.get(1);
		UnitElement filter2 = (UnitElement) units.get(2);
		UnitElement filter3 = (UnitElement) units.get(3);
		UnitElement source2 = (UnitElement) units.get(4);
		
		MacroFlowRunner.sortList(units);
		
		assertEquals("source on correct position 0", source1, units.get(0));
		assertEquals("source on correct position 1", filter1, units.get(1));
		assertEquals("source on correct position 2", filter2, units.get(2));
		
	}

	private UnitList buildSampleWorkflow() {
		UnitList unitList = new UnitList();
		
		UnitElement source1 = UnitFactory.createBackgroundUnit(new Dimension(10, 10));
		UnitElement filter1 = UnitFactory.createAddNoiseUnit();
		UnitElement filter2 = UnitFactory.createFindEdgesUnit();
		UnitElement filter3 = UnitFactory.createGaussianBlurUnit();
		
		UnitElement source2 = UnitFactory.createBackgroundUnit(new Dimension(10,0));
		
		unitList.add(source1);
		unitList.add(source2);
		unitList.add(filter1);
		unitList.add(filter2);
		unitList.add(filter3);
		
		assertEquals(5, unitList.size());
		assertFalse(unitList.hasUnitAsDisplay());
		filter2.setDisplayUnit(true);
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
