/**
 * 
 */
package models;

import junit.framework.TestCase;
import models.unit.UnitElement;
import models.unit.UnitList;
import backend.GraphController;

/**
 * @author danielsenff
 *
 */
public class UnitListTests extends TestCase {


	public void testUnitListSelectionSorting() {
		
		final GraphController controller = new GraphController();
		final UnitList unsortedList = controller.getUnitElements();
		int usortedListOriginalLength = unsortedList.size();
		
		final UnitList sortedList = GraphController.sortList(unsortedList);
		
		int expectedMark = 1;
		for (final UnitElement unit : sortedList) {
			expectedMark++;
			assertEquals("mark for node "+ unit, expectedMark, unit.getMark());
		}
		
		assertEquals("list length", usortedListOriginalLength, sortedList.size());
	}
	
//	public void test
	
}
