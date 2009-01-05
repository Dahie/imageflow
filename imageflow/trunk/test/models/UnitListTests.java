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


	public void testUnitListSelectionSorting() {
		
		final GraphController controller = new GraphController();
		controller.setupExample1();
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
	
	
/*public void testUnitListSelSortExceptionHandling() {
	
		final GraphController controller = new GraphController();
		controller.setupExample1();
		final UnitList unsortedList = controller.getUnitElements();
		int usortedListOriginalLength = unsortedList.size();
		
		UnitElement addNoiseUnit = (UnitElement)unsortedList.get(4);
		UnitElement blurUnit = (UnitElement)unsortedList.get(2);
		Connection connLoop = new Connection(addNoiseUnit, 1, blurUnit, 1);
		controller.getConnections().add(connLoop);
		
		final UnitList sortedList = GraphController.sortList(unsortedList);
		
		
		
		int expectedMark = 0;
		for (final Node node : sortedList) {
			expectedMark++;
			assertEquals("mark for node "+ node, expectedMark, ((UnitElement)node).getMark());
		}
		
		assertEquals("list length", usortedListOriginalLength, sortedList.size());
	}
	*/	
	
	
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
