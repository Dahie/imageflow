/**
 * 
 */
package models;

import java.awt.Dimension;

import models.unit.UnitElement;
import models.unit.UnitFactory;
import junit.framework.TestCase;

/**
 * @author danielsenff
 *
 */
public class InputTests extends TestCase {

	public void testIsConnected() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Input filterInput = filterUnit1.getInput(0);
		
		// test beforehand
		assertEquals("input not connected", false, filterInput.isConnected());
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		// test after connecting
		assertEquals("input connected", true, filterInput.isConnected());	
	}
	
	
}
