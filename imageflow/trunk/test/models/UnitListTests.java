/**
 * 
 */
package models;

import graph.Node;

import imageflow.backend.GraphController;
import imageflow.models.Connection;
import imageflow.models.ConnectionList;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitFactory;
import imageflow.models.unit.UnitList;

import java.awt.Dimension;

import junit.framework.TestCase;

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
	
}
