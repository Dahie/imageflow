package models;

import javax.xml.datatype.DatatypeFactory;

import ij.plugin.filter.PlugInFilter;
import junit.framework.TestCase;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class DataTypeTests extends TestCase {

	
	public void testDataTypeInteger() {
		DataType integer1 = DataTypeFactory.createInteger();
		DataType integer2 = DataTypeFactory.createInteger();
		DataType image1 = DataTypeFactory.createImage(PlugInFilter.DOES_32);
		
		assertTrue("int on int", integer1.isCompatible(integer2));
		assertFalse("int on img", integer1.isCompatible(image1));
	}
	
	
	/*
	 * Image
	 */

	public void testImageBitDepthCompatible() {

		UnitElement unit1 = new UnitElement("unit1", "some syntax");
		DataType img32 = DataTypeFactory.createImage(PlugInFilter.DOES_32);
		DataType imgAll = DataTypeFactory.createImage(PlugInFilter.DOES_ALL);
		DataType img_1 = DataTypeFactory.createImage(-1);
		unit1.addOutput("output1", "o", PlugInFilter.DOES_32, false);
		unit1.addOutput("output2", "o", PlugInFilter.DOES_ALL, false);
		unit1.addOutput("output2", "o", -1, false);
		Output output1 = unit1.getOutput(0);
		Output output2 = unit1.getOutput(1);
		Output output3 = unit1.getOutput(2);
		
		UnitElement unit2 = new UnitElement("unit2", "some syntax");
		unit2.addInput("input1", "i", PlugInFilter.DOES_32, false);
		unit2.addInput("input2", "i", PlugInFilter.DOES_16, false);
		unit2.addInput("input3", "i", PlugInFilter.DOES_ALL, false);
		Input input1 = unit2.getInput(0);
		Input input2 = unit2.getInput(1);
		Input input3 = unit2.getInput(2);

		assertTrue("both do 32", input1.isCompatible(output1));
		assertFalse("32 to 16", input2.isCompatible(output1));
		assertFalse("all to 32", input1.isCompatible(output2));
		assertTrue("32 to all", input3.isCompatible(output1));

	}
	
	
	public void testImageBitDepthCompatible() {

		UnitElement unit1 = new UnitElement("unit1", "some syntax");
		unit1.addOutput("output1", "o", PlugInFilter.DOES_32, false);
		unit1.addOutput("output2", "o", PlugInFilter.DOES_ALL, false);
		unit1.addOutput("output3", "o", -1, false);
		Output output1 = unit1.getOutput(0);
		Output output2 = unit1.getOutput(1);
		Output output3 = unit1.getOutput(2);
		
		UnitElement unit2 = new UnitElement("unit2", "some syntax");
		unit2.addInput("input1", "i", PlugInFilter.DOES_32, false);
		unit2.addInput("input2", "i", PlugInFilter.DOES_16, false);
		unit2.addInput("input3", "i", PlugInFilter.DOES_ALL, false);
		Input input1 = unit2.getInput(0);
		Input input2 = unit2.getInput(1);
		Input input3 = unit2.getInput(2);

		/*assertTrue("both do 32", input1.isImageBitDepthCompatible(output1.getImageBitDepth()));
		assertFalse("32 to 16", input2.isImageBitDepthCompatible(output1.getImageBitDepth()));
		assertTrue("all to 32", input1.isImageBitDepthCompatible(output2.getImageBitDepth()));
		assertTrue("32 to all", input3.isImageBitDepthCompatible(output1.getImageBitDepth()));*/
		
		assertTrue("both do 32", output1.isImageBitDepthCompatible(input1.getImageBitDepth()));
		// ok, technically ALL to 32 does work, but since the input tests for a concrete type, 
		// and not for a could-be, this is false
		assertFalse("All to 32", output2.isCompatible(input1));
		assertFalse("32 to 16", output1.isCompatible(input2));
		assertFalse("-1 to 16", output3.isCompatible(input2));
		assertFalse("32 to 16", output1.isCompatible(input2));

	}
	
}
