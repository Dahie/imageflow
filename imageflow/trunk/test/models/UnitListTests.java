/**
 * 
 */
package models;

import java.awt.Dimension;

import graph.Node;
import junit.framework.TestCase;
import models.unit.UnitElement;
import models.unit.UnitFactory;
import models.unit.UnitList;
import backend.GraphController;

/**
 * @author danielsenff
 *
 */
public class UnitListTests extends TestCase {


	public void testUnitListSelectionSorting() {
		
		final GraphController controller = new GraphController();
		controller.setupExample();
		final UnitList unsortedList = controller.getUnitElements();
		int usortedListOriginalLength = unsortedList.size();
		
		final UnitList sortedList = GraphController.sortList(unsortedList);
		
		int expectedMark = 0;
		for (final Node node : sortedList) {
			expectedMark++;
			assertEquals("mark for node "+ node, expectedMark, ((UnitElement)node).getMark());
		}
		
		assertEquals("list length", usortedListOriginalLength, sortedList.size());
	}
	
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
