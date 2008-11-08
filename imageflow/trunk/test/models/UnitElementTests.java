/**
 * 
 */
package models;

import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;

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
		
		assertEquals("number of possible parameters",1, unit.getParametersMaxCount());
		
		//test usual adding
		boolean addFirst = unit.addParameter(ParameterFactory.createParameter("integer", 1, "ein int"));
		assertTrue("add first", addFirst);
		
		assertEquals("number of possible parameters",
				unit.getParametersMaxCount(), 
				unit.getParametersActualCount());
		
		//add one more than actually allowed
		boolean addSecond = unit.addParameter(ParameterFactory.createParameter("integer", 1, "ein int"));
		assertFalse("add second", addSecond);
	}
	
	public void testHasInputs() {
		
		// Sources have no inputs
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		assertFalse("source", sourceUnit.hasInputs());
		
		UnitElement filter = UnitFactory.createAddNoiseUnit();
		assertTrue("filter", filter.hasInputs());
		
	}
	
	
	public void testAddInput() {
		//unit with one allowed parameter
		UnitElement unit = new UnitElement("name", "", 1, 0, 1);
		
		assertEquals("number of possible inputs",1, unit.getInputsMaxCount());
		
		//test usual adding
		boolean addFirst = unit.addInput("input", "i", 4, false);
		assertTrue("add first", addFirst);
		
		assertEquals("number of possible parameters",
				unit.getInputsMaxCount(), 
				unit.getInputsActualCount());
		
		//add one more than actually allowed
		boolean addSecond = unit.addInput("input", "i", 4, false);
		assertTrue("add second", addSecond);
	}
	

	
	public void testHasAllInputsMarked() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		
		// test input/output case
		UnitElement filterUnit1 = UnitFactory.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactory.createAddNoiseUnit();
		
		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		ConnectionList connList = new ConnectionList();
		connList.add(conn);
		
		//assertion
		
		assertTrue("source has inputs marked", sourceUnit.hasAllInputsMarked());
		// the source is not yet marked, so the first filter should give false
		assertFalse("filter1 has no inputs marked yet", filterUnit1.hasAllInputsMarked());
		assertFalse("filter2 has inputs marked", filterUnit2.hasAllInputsMarked());
		
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
	
	public void testClone() {
		UnitElement mergeUnit = UnitFactory.createImageCalculatorUnit();
		
		try {
			UnitElement clone = mergeUnit.clone();

			//assertions
			
			assertFalse("object the same", mergeUnit.equals(clone));
			assertEquals("number of max inputs", 
					mergeUnit.getInputsMaxCount(), clone.getInputsMaxCount());
			assertEquals("number of max outputs", 
					mergeUnit.getOutputsMaxCount(), clone.getOutputsMaxCount());
			assertEquals("number of max parameters", 
					mergeUnit.getParametersMaxCount(), clone.getParametersMaxCount());
			assertEquals("unit name", mergeUnit.getUnitName(), clone.getUnitName());
			assertNotSame("unit id", mergeUnit.getUnitID(), clone.getUnitID());
			assertFalse("Unitid not the same",(mergeUnit.getUnitID() == clone.getUnitID()));
			
			// assert Object
			MacroElement mergeObject = (MacroElement) mergeUnit.getObject(); 
			MacroElement cloneObject = (MacroElement) clone.getObject();
			assertFalse("contained object", mergeObject.equals(cloneObject));
			assertEquals("object imagej syntax", 
					mergeObject.getImageJSyntax() , cloneObject.getImageJSyntax());
			
			// assert Inputs
			assertEquals("inputs actual", mergeUnit.getInputsActualCount(), clone.getInputsActualCount());
			for (int i = 0; i < clone.getInputsActualCount(); i++) {
				Input cloneInput = clone.getInput(i);
				Input mergeInput = mergeUnit.getInput(i);
				assertFalse("input equals", cloneInput.equals(mergeInput));
				assertFalse("input parents equals", 
						cloneInput.getParent().equals(mergeInput.getParent()));
				
			}
			
			
			// assert Outputs
//			assertEquals("outputs actual", mergeUnit.getOutputsActualCount(), clone.getOutputsActualCount());
			for (int i = 0; i < clone.getOutputsMaxCount(); i++) {
				Output cloneOutput = clone.getOutput(i);
				Output mergeOutput = mergeUnit.getOutput(i);
				assertFalse("output equals", cloneOutput.equals(mergeOutput));
				assertFalse("output parents equals", 
						cloneOutput.getParent().equals(mergeOutput.getParent()));
				
			}
			
			
			// assert Parameters
			
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public void testAreImageDepthCompatible() {
		
		UnitElement unit1 = new UnitElement("unit1", "some syntax", 1, 1, 0);
		unit1.addOutput("output1", "o", PlugInFilter.DOES_32);
		unit1.addOutput("output2", "o", PlugInFilter.DOES_ALL);
		unit1.addOutput("output2", "o", -1);
		UnitElement unit2 = new UnitElement("unit2", "some syntax", 1, 1, 0);
		unit2.addInput("input1", "i", PlugInFilter.DOES_32, false);
		unit2.addInput("input2", "i", PlugInFilter.DOES_16, false);
		unit2.addInput("input3", "i", PlugInFilter.DOES_ALL, false);
		
		// conn1 32 to 16		
		Connection conn1 = new Connection(unit1,1,unit2,1);
		assertTrue("both do 32", conn1.areImageBitDepthCompatible());
		
		// conn2 32 to 16		
		Connection conn2 = new Connection(unit1,1,unit2,2);
		assertFalse("both do 32", conn2.areImageBitDepthCompatible());

		// conn3 ALL to 32
		Connection conn3 = new Connection(unit1,2,unit2, 1);
		assertTrue("all to 32", conn3.areImageBitDepthCompatible());
		
		// conn4 32 to all
		Connection conn4 = new Connection(unit1,1,unit2, 3);
		assertTrue("32 to all", conn4.areImageBitDepthCompatible());
		
		// now test pins, which don't care
		//TODO hm how should this react actually? needs an input set
		Connection conn5 = new Connection(unit1,3,unit2, 2);
		assertTrue("-1 to 16", conn5.areImageBitDepthCompatible());
		
		// now test pins, which don't care
		Connection conn6 = new Connection(unit1,3,unit2, 3);
		assertTrue("-1 to ALL", conn6.areImageBitDepthCompatible());
	}
	
}
