/**
 * 
 */
package models;

import junit.framework.TestCase;
import models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class UnitElementTests extends TestCase {

	
	/**
	 *  
	 */
	public void testAddParameter() {
		//unit with one allowed parameter
		UnitElement unit = new UnitElement("name", "", 0, 0, 1);
		
		assertEquals("number of possible parameters",1, unit.getParametersPossibleCount());
		
		//test usual adding
		boolean addFirst = unit.addParameter(new Parameter());
		assertEquals("add first", true, addFirst);
		
		assertEquals("number of possible parameters",
				unit.getParametersPossibleCount(), 
				unit.getParametersActualCount());
		
		//add one more than actually allowed
		boolean addSecond = unit.addParameter(new Parameter());
		assertEquals("add second",false, addSecond);
	}
	
	public void testAddInput() {
		//unit with one allowed parameter
		UnitElement unit = new UnitElement("name", "", 1, 0, 1);
		
		assertEquals("number of possible inputs",1, unit.getInputsActualCount());
		
		//test usual adding
		boolean addFirst = unit.addInput("input", "i", 4, false);
		assertEquals("add first", true, addFirst);
		
		assertEquals("number of possible parameters",
				unit.getInputsMaxCount(), 
				unit.getInputsActualCount());
		
		//add one more than actually allowed
		boolean addSecond = unit.addInput("input", "i", 4, false);
		assertEquals("add second",true, addSecond);
	}
	
}
