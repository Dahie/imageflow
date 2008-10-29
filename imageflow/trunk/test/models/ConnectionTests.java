/**
 * 
 */
package models;

import java.awt.Dimension;

import junit.framework.TestCase;
import models.unit.UnitElement;
import models.unit.UnitFactory;

/**
 * @author danielsenff
 *
 */
public class ConnectionTests extends TestCase {

	
	/**
	 * Test if an correctly initialized Connection returns the right status.
	 */
	public void testConnectionStatus() {
		final UnitElement source = UnitFactory.createSourceUnit("/Users/danielsenff/zange1.png");
		final UnitElement blur = UnitFactory.createGaussianBlurUnit(); 
		
		final Connection connection1 = new Connection(source, 1, blur, 1);
		assertEquals("status check 1", true, (connection1.checkConnection() == Connection.Status.OK) );
	}
	

	public void testHasAllInputsMarked() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		//assertion
		
		assertEquals("con has no inputs marked yet", false, conn.hasInputMarked());
		// the source is not yet marked, so the first filter should give false
//		assertEquals("filter1 has no inputs marked yet", false, filterUnit1.hasAllInputsMarked());
//		assertEquals("filter2 has inputs marked", false, filterUnit2.hasAllInputsMarked());
		
		//set mark on the source, now the filter next connected should find this mark
		sourceUnit.setMark(1);
		
		assertEquals("filter1 has inputs marked", true, conn.hasInputMarked());
		
	}
	
}
