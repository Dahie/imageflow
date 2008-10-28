/**
 * 
 */
package models;

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
		final UnitElement source = UnitFactory.createSourceUnit();
		final UnitElement blur = UnitFactory.createGaussianBlurUnit(); 
		
		final Connection connection1 = new Connection(source, 1, blur, 1);
		assertEquals("status check 1", true, (connection1.checkConnection() == Connection.Status.OK) );
	}
	
}
