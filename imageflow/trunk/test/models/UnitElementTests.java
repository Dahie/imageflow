/**
 * 
 */
package models;


import ij.plugin.filter.PlugInFilter;

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

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author danielsenff
 *
 */
public class UnitElementTests {


	@Test public void testInputsActualCount() {

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


	@Test public void testAddParameter() {
		//unit with one allowed parameter
		UnitElement unit = new UnitElement("name", "");

		//test usual adding
		boolean addFirst = unit.addParameter(ParameterFactory.createParameter("integer", "integer",1, "ein int"));
		assertTrue("add first", addFirst);

		//add one more than actually allowed
		boolean addSecond = unit.addParameter(ParameterFactory.createParameter("integer", "integer",1, "ein int"));
		assertTrue("add second", addSecond);
	}

	@Test public void testHasInputs() {

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


	@Test public void testAddInput() {
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
	
	@Test public void testAddOutput() {
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

	@Test public void testRequiredInputs() {
		//TODO
	}
	

	@Test public void testHasOutputs() {
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

	@Test public void testHasAllInputsMarked() {

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


	@Test public void testSetMark() {

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

	@Test public void testClone() {
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

	
	@Test public void testHasDisplayBranch() {
		
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

		filterUnit1.setDisplay(true);
		
		assertFalse(sourceUnit.isDisplay());
		assertTrue(sourceUnit.hasDisplayBranch());
		assertFalse(sourceUnit2.isDisplay());
		assertFalse(sourceUnit2.hasDisplayBranch());
		assertTrue(filterUnit1.isDisplay());
		assertTrue(filterUnit1.hasDisplayBranch());
		assertFalse(filterUnit2.isDisplay());
		assertFalse(filterUnit2.hasDisplayBranch());
		
		Connection conn1b = new Connection(sourceUnit2, 1, filterUnit1, 1);
		
		connList.add(conn1b);
		
		assertFalse(sourceUnit.hasDisplayBranch());
		assertFalse(sourceUnit2.isDisplay());
		assertTrue(sourceUnit2.hasDisplayBranch());
		assertTrue(filterUnit1.isDisplay());
		assertTrue(filterUnit1.hasDisplayBranch());
		assertFalse(filterUnit2.isDisplay());		
		assertFalse(filterUnit2.hasDisplayBranch());
	}
	
	

	/*
	 * Helper methods
	 */
	
	
	protected UnitElement createSourceUnit() {
		UnitElement unit1 = new UnitElement("unit1", "some syntax");
		Output output1 = new Output("output1", "o", 
				DataTypeFactory.createImage(PlugInFilter.DOES_32), unit1, 1);
		unit1.addOutput(output1);
		Output output2 = new Output("output2", "o", 
				DataTypeFactory.createImage(PlugInFilter.DOES_ALL), unit1, 2);
		unit1.addOutput(output2);
		Output output3 = new Output("output3", "o", 
				DataTypeFactory.createImage(-1), unit1, 3);
		unit1.addOutput(output3);
		Output output4 = new Output("output4", "o", 
				DataTypeFactory.createImage(PlugInFilter.DOES_16+PlugInFilter.DOES_32), unit1, 4);
		unit1.addOutput(output4);
		return unit1;
	}
	
	protected UnitElement createSinkUnit() {
		UnitElement unit2 = new UnitElement("unit2", "some syntax");
		Input input1 = new Input("input1", "i", 
				DataTypeFactory.createImage(PlugInFilter.DOES_32), unit2, 1, true, false);
		unit2.addInput(input1);
		Input input2 = new Input("input2", "i", 
				DataTypeFactory.createImage(PlugInFilter.DOES_16), unit2, 1, true, false);
		unit2.addInput(input2);
		Input input3 = new Input("input3", "i", 
				DataTypeFactory.createImage(PlugInFilter.DOES_ALL), unit2, 1, true, false);
		unit2.addInput(input3);
		Input input4 = new Input("input4", "i", 
				DataTypeFactory.createImage(PlugInFilter.DOES_32+PlugInFilter.DOES_16), 
				unit2, 1, true, false);
		unit2.addInput(input4);
		return unit2;
	}
	
	protected UnitElement createUnit(int inputImageType, int outputImageType) {
		UnitElement unit2 = new UnitElement("unit", "some syntax");
		Output output1 = new Output("output1", "o", 
				DataTypeFactory.createImage(outputImageType), unit2, 1);
		unit2.addOutput(output1);

		Input input1 = new Input("input1", "i", 
				DataTypeFactory.createImage(inputImageType), unit2, 1, true, false);
		unit2.addInput(input1);

		return unit2;
	}

}
