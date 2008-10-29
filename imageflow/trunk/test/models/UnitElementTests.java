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
		
		assertEquals("number of possible inputs",1, unit.getInputsMaxCount());
		
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
	

	
	public void testHasAllInputsMarked() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		
		//assertion
		
		assertEquals("source has inputs marked", true, sourceUnit.hasAllInputsMarked());
		// the source is not yet marked, so the first filter should give false
		assertEquals("filter1 has no inputs marked yet", false, filterUnit1.hasAllInputsMarked());
		assertEquals("filter2 has inputs marked", false, filterUnit2.hasAllInputsMarked());
		
		//set mark on the source, now the filter next connected should find this mark
		sourceUnit.setMark(1);
		
		assertEquals("filter1 has inputs marked", true, filterUnit1.hasAllInputsMarked());
		
	}
	
	
	public void testSetMark() {

		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		
//		assertEquals("mark on source before setting", false, sourceUnit.hasAllInputsMarked());
//		assertEquals("mark on filter before setting", false, filterUnit1.hasAllInputsMarked());
		
		assertUnitMarks(sourceUnit, 0);
		assertUnitMarks(filterUnit1, 0);
		
		//set marks
		sourceUnit.setMark(1);
		filterUnit1.setMark(1);
		
		assertUnitMarks(sourceUnit, 1);
	}

	/**
	 * @param sourceUnit
	 */
	private void assertUnitMarks(UnitElement sourceUnit, int expected) {
		for (Input input : sourceUnit.getInputs()) {
			assertEquals("mark set in "+sourceUnit+" for "+input, expected, input.getMark());
		}
		for (Output output : sourceUnit.getOutputs()) {
			assertEquals("mark set in "+sourceUnit+" for "+output, expected, output.getMark());
		}
	}
	
}
