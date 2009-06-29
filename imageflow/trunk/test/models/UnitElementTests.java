/**
 * 
 */
package models;


import java.awt.Dimension;
import java.awt.Point;

import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.ParameterFactory;
import de.danielsenff.imageflow.models.unit.SourceUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;
import de.danielsenff.imageflow.models.unit.UnitElement.Type;

import junit.framework.TestCase;

/**
 * @author danielsenff
 *
 */
public class UnitElementTests extends TestCase {


	public void testInputsActualCount() {

		UnitElement unit = UnitFactoryExt.createAddNoiseUnit();

		assertEquals("number outputs",1, unit.getOutputsCount());
		assertEquals("number inputs",1, unit.getInputsCount());

		UnitElement source = UnitFactoryExt.createBackgroundUnit(new Dimension(100,100));
		assertEquals("number outputs",1, source.getOutputsCount());
		assertEquals("number inputs",0, source.getInputsCount());

		UnitElement merge = UnitFactoryExt.createImageCalculatorUnit();
		assertEquals("number outputs",1, merge.getOutputsCount());
		assertEquals("number inputs",2, merge.getInputsCount());

		UnitElement sink = UnitFactoryExt.createHistogramUnit(new Point(0,0));
		assertEquals("number outputs",0, sink.getOutputsCount());
		assertEquals("number inputs",1, sink.getInputsCount());
	}


	/**
	 *  
	 */
	public void testAddParameter() {
		//unit with one allowed parameter
		UnitElement unit = new UnitElement("name", "");

		//test usual adding
		boolean addFirst = unit.addParameter(ParameterFactory.createParameter("integer", "integer",1, "ein int"));
		assertTrue("add first", addFirst);


		//add one more than actually allowed
		boolean addSecond = unit.addParameter(ParameterFactory.createParameter("integer", "integer",1, "ein int"));
		assertTrue("add second", addSecond);
	}

	public void testHasInputs() {

		// Sources have no inputs
		UnitElement source = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		assertFalse("source", source.hasInputs());
		assertFalse("source", source.hasInputsConnected());
		assertTrue(source.hasRequiredInputsConnected());

		UnitElement filter = UnitFactoryExt.createAddNoiseUnit();
		assertTrue("filter", filter.hasInputs());
		assertFalse("filter", filter.hasInputsConnected());
		assertFalse(filter.hasRequiredInputsConnected());

		Connection conn1 = new Connection(source, 1, filter, 1);
		conn1.connect();
		assertTrue(conn1.isConnected());
		assertTrue("filter", filter.hasInputsConnected());
		
		Input filter1Input1 = filter.getInput(0);
		assertTrue(filter1Input1.isRequired());
		assertTrue(filter.hasRequiredInputsConnected());
		
		
		
	}


	public void testAddInput() {
		//unit with one allowed parameter
		UnitElement unit = new UnitElement("name", "");

		//test usual adding
		Input input = new Input("input", "i", DataTypeFactory.createInteger(), unit, 4, true, false);
		boolean addFirst = unit.addInput(input);
		assertTrue("add first", addFirst);
		assertEquals(1, unit.getInputsCount());

		//add one more
		Input scndInput = new Input(DataTypeFactory.createInteger(), unit, 1);
		boolean addSecond = unit.addInput(scndInput);
		assertTrue("add second", addSecond);
		assertEquals(2, unit.getInputsCount());
	}
	
	public void testAddOutput() {
		//unit with one allowed parameter
		UnitElement unit = new UnitElement("name", "");

		//test usual adding
		Output output = new Output("Output", "i", DataTypeFactory.createInteger(),unit, 4);
		boolean addFirst = unit.addOutput(output);
		assertTrue("add first", addFirst);
		assertEquals(1, unit.getOutputsCount());

		//add one more
		Output scndOutput = new Output(DataTypeFactory.createInteger(), unit, 1);
		boolean addSecond = unit.addOutput(scndOutput);
		assertTrue("add second", addSecond);
		assertEquals(2, unit.getOutputsCount());
	}

	public void testRequiredInputs() {
		//TODO
	}
	

	public void testHasOutputs() {
		UnitElement sink = UnitFactoryExt.createHistogramUnit(new Point(0,0));
		assertTrue("is a sink", sink.getUnitType() == Type.SINK);
		assertFalse(sink.hasOutputsConnected());
		assertFalse(sink.hasOutputs());
		assertEquals(0, sink.getOutputsCount());
		
		
		UnitElement filter = UnitFactoryExt.createFindEdgesUnit();
		assertTrue("is a filter", filter.getUnitType() == Type.FILTER);
		assertFalse(filter.hasOutputsConnected());
		assertTrue(filter.hasOutputs());
		assertEquals(1, filter.getOutputsCount());;
		
	}

	public void testHasAllInputsMarked() {

		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		UnitElement sourceUnit2 = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));

		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createImageCalculatorUnit();

		Connection conn = new Connection(sourceUnit, 1, filterUnit1, 1);
		ConnectionList connList = new ConnectionList();
		connList.add(conn);

		//assertion

		assertTrue("source has inputs marked", sourceUnit.hasAllInputsMarked());
		// the source is not yet marked, so the first filter should give false
		assertFalse("filter1 has inputs marked yet", filterUnit1.hasAllInputsMarked());
		assertTrue("filter1 is connected", filterUnit1.hasRequiredInputsConnected());
		assertFalse("filter2 has all input marled", filterUnit2.hasAllInputsMarked());
		assertFalse("filter2 is connected", filterUnit2.hasInputsConnected());
		
		Connection conn2 = new Connection(sourceUnit, 1, filterUnit2, 1);
		connList.add(conn2);
		
		assertTrue("input of filter2 connected", filterUnit2.getInput(0).isConnected());
		assertTrue("filter2 is connected", filterUnit2.hasInputsConnected());
		assertFalse("filter2 has no inputs marked yet", filterUnit2.hasAllInputsMarked());
		assertFalse("filter2 is connected", filterUnit2.hasRequiredInputsConnected());

		//set mark on the source, now the filter next connected should find this mark
		sourceUnit.setMark(1);

		assertTrue("input of filter1 connected", filterUnit1.getInput(0).isConnected());
		assertTrue("filter1 has all inputs marked", filterUnit1.hasAllInputsMarked());
		assertFalse("filter2 has all inputs marked", filterUnit2.hasAllInputsMarked());
		
		Connection conn3 = new Connection(sourceUnit2, 1, filterUnit2, 2);
		connList.add(conn3);

		assertFalse("filter2 has no inputs marked yet", filterUnit2.hasAllInputsMarked());
		assertTrue("filter2 is connected", filterUnit2.hasRequiredInputsConnected());
		
		sourceUnit2.setMark(2);
		
		assertTrue("filter2 has all inputs marked", filterUnit2.hasAllInputsMarked());
	}


	public void testSetMark() {

		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));

		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();

		assertTrue("mark on source before setting", sourceUnit.hasAllInputsMarked());
		assertFalse("mark on filter before setting", filterUnit1.hasAllInputsMarked());

		assertUnitMarks(sourceUnit, 0, true);
		assertUnitMarks(filterUnit1, 0, false);
		

		//set marks
		sourceUnit.setMark(1);
		filterUnit1.setMark(1);

		assertUnitMarks(sourceUnit, 1, true);
		assertUnitMarks(filterUnit1, 1, true);
	}

	/**
	 * @param sourceUnit
	 */
	private void assertUnitMarks(UnitElement sourceUnit, int expected, boolean expectedMark) {
		for (Input input : sourceUnit.getInputs()) {
			assertEquals("mark set in "+sourceUnit+" for "+input, expected, input.getMark());
			assertEquals(expectedMark, input.isMarked());
			assertEquals(!expectedMark, input.isUnmarked());
		}
		for (Output output : sourceUnit.getOutputs()) {
			assertEquals("mark set in "+sourceUnit+" for "+output, expected, output.getMark());
		}
	}

	public void testClone() {
		UnitElement mergeUnit = UnitFactoryExt.createImageCalculatorUnit();


		UnitElement clone = mergeUnit.clone();

		//assertions

		assertFalse("object the same", mergeUnit.equals(clone));
		assertEquals("number of max inputs", 
				mergeUnit.getInputsCount(), clone.getInputsCount());
		assertEquals("number of max outputs", 
				mergeUnit.getOutputsCount(), clone.getOutputsCount());
		assertEquals("number of max parameters", 
				mergeUnit.getParametersCount(), clone.getParametersCount());
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
		assertEquals("inputs actual", mergeUnit.getInputsCount(), clone.getInputsCount());
		for (int i = 0; i < clone.getInputsCount(); i++) {
			Input cloneInput = clone.getInput(i);
			Input mergeInput = mergeUnit.getInput(i);
			assertFalse("input equals", cloneInput.equals(mergeInput));
			assertFalse("input parents equals", 
					cloneInput.getParent().equals(mergeInput.getParent()));
		}


		// assert Outputs
		//			assertEquals("outputs actual", mergeUnit.getOutputsActualCount(), clone.getOutputsActualCount());
		for (int i = 0; i < clone.getOutputsCount(); i++) {
			Output cloneOutput = clone.getOutput(i);
			Output mergeOutput = mergeUnit.getOutput(i);
			assertFalse("output equals", cloneOutput.equals(mergeOutput));
			assertFalse("output parents equals", 
					cloneOutput.getParent().equals(mergeOutput.getParent()));
		}

	}

	
	public void testHasDisplayBranch() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		UnitElement sourceUnit2 = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));;

		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();

		ConnectionList connList = new ConnectionList();
		Connection conn1 = new Connection(sourceUnit, 1, filterUnit1, 1);
		connList.add(conn1);
		Connection conn2 = new Connection(filterUnit1, 1, filterUnit2, 1);
		connList.add(conn2);
		
		assertFalse(sourceUnit.hasDisplayBranch());
		assertFalse(sourceUnit2.hasDisplayBranch());
		assertFalse(filterUnit1.hasDisplayBranch());
		assertFalse(filterUnit2.hasDisplayBranch());

		filterUnit1.setDisplayUnit(true);
		
		assertFalse(sourceUnit.isDisplayUnit());
		assertTrue(sourceUnit.hasDisplayBranch());
		assertFalse(sourceUnit2.isDisplayUnit());
		assertFalse(sourceUnit2.hasDisplayBranch());
		assertTrue(filterUnit1.isDisplayUnit());
		assertTrue(filterUnit1.hasDisplayBranch());
		assertFalse(filterUnit2.isDisplayUnit());
		assertFalse(filterUnit2.hasDisplayBranch());
		
		Connection conn1b = new Connection(sourceUnit2, 1, filterUnit1, 1);
		
		connList.add(conn1b);
		
		assertFalse(sourceUnit.hasDisplayBranch());
		assertFalse(sourceUnit2.isDisplayUnit());
		assertTrue(sourceUnit2.hasDisplayBranch());
		assertTrue(filterUnit1.isDisplayUnit());
		assertTrue(filterUnit1.hasDisplayBranch());
		assertFalse(filterUnit2.isDisplayUnit());		
		assertFalse(filterUnit2.hasDisplayBranch());
	}
	
	
	

}
