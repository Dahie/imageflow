package models;

import junit.framework.TestCase;
import models.unit.UnitElement;
import backend.GraphController;

public class GraphControllerTests extends TestCase {

	
	public void testRemoveUnit() {
		GraphController controller = new GraphController();
		controller.setupExample1();
		
		assertEquals("number of connections", 4, controller.getConnections().size());
		
		UnitElement merge = (UnitElement) controller.getUnitElements().get(3);
		assertTrue("has removed sucessfully", controller.removeUnit(merge));
	}
	
}
