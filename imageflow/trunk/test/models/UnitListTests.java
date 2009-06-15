/**
 * 
 */
package models;

import java.awt.Dimension;

import junit.framework.TestCase;
import de.danielsenff.imageflow.models.Connection;
import de.danielsenff.imageflow.models.ConnectionList;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;
import de.danielsenff.imageflow.models.unit.UnitList;

/**
 * @author danielsenff
 *
 */
public class UnitListTests extends TestCase {


	
	public void testAreAllInputsConnected() {

		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		ConnectionList connList = new ConnectionList();
		connList.add(conn);
		
		// adding to UnitList
		
		UnitList units = new UnitList();
		units.add(sourceUnit);
		units.add(filterUnit1);
		
		
		//assertion
		assertTrue("only nodes added, which are connected", units.areAllInputsConnected());
		
		// add one more which is not connected
		units.add(filterUnit2);
		
		//assertion
		assertFalse("contains nodes, which are not connected", units.areAllInputsConnected());
	}
	

	public void testHasDisplayUnit() {
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		// adding to UnitList
		
		UnitList units = new UnitList();
		units.add(sourceUnit);
		units.add(filterUnit1);
		units.add(filterUnit2);
		
		assertFalse(units.isEmpty());
		assertFalse("has no displayunits", units.hasUnitAsDisplay());
		
		filterUnit1.setDisplayUnit(true);
		
		assertTrue("has displayunits", units.hasUnitAsDisplay());
		
	}
	
	public void testIsEmpty() {
		UnitList units = new UnitList();
		
		assertTrue(units.isEmpty());
		
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		units.add(filterUnit1);
		units.add(filterUnit2);
		
		assertFalse(units.isEmpty());
		assertEquals("elements counts",2, units.size());
		
		units.remove(filterUnit1);
		
		assertFalse(units.isEmpty());
		assertEquals("elements counts",1, units.size());
		assertFalse(units.contains(filterUnit1));
		assertTrue(units.contains(filterUnit2));
		
		units.remove(filterUnit2);
		
		assertTrue(units.isEmpty());
		assertEquals("elements counts",0, units.size());
		assertFalse(units.contains(filterUnit2));
		
	}
	
}
