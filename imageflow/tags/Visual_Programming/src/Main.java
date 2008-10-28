import java.util.HashMap;

import ij.IJ;
import ij.ImageJ;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////
		int numUnits = 6;
		
		UnitElements[] unitElements = new UnitElements[numUnits+1]; // unit 0 will never be used 
		
		// setup of a source (input) unit
		// display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
		unitElements[1] = new UnitElements("Source1", "open(\"PARA_STRING_1\");\n", 0, 1, 1);
				
		// setup of the first parameter
		unitElements[1].parameters[1].setParameter(
				"Input image file",										 // parameter description
				"/Users/barthel/Applications/ImageJ/_images/zange1.png", // parameter value
				"The source unit needs the path of an image file." 	 	 // help text for this parameter
				);

		// setup of the output of the (source) unit 0
		int bitDepth = unitElements[1].getBitDepth();
		unitElements[1].outputs[1].setupOutput("Output", "O", bitDepth);
		
			
		// setup of a processing unit (gaussian blur)
		// display name, syntax: "run("Gaussian Blur...", "sigma=2");", 1 input, 1 output, 1 parameter
		unitElements[2] = new UnitElements("Blur", "run(\"Gaussian Blur...\", \"sigma=PARA_DOUBLE_1\");\n",1,1,1);
		
		// setup of the parameter
		unitElements[2].parameters[1].setParameter("Radius", 4, "Radius of the gaussian kernel");
		
		// setup of the first input of unit 2
		unitElements[2].inputs[1].setupInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		unitElements[2].outputs[1].setupOutput("Output", "O", -1); // -1 means output will be the same type as the input

		
		// setup of a processing unit (Image Calculator  / Subtract)
		// display name, syntax, 2 inputs (as titles), 1 output, 1 parameter
		unitElements[3] = new UnitElements("Image Calculator", 
				"run(\"Image Calculator...\", \"image1=TITLE_1 operation=Subtract image2=TITLE_2 create 32-bit\"); \n",2,1,1);
		
		// setup of the parameter
		unitElements[3].parameters[1].setParameter("32-bit", true, "generate a floating point result image");
		
		// setup of the inputs
		unitElements[3].inputs[1].setupInput("Input1", "I1", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		unitElements[3].inputs[2].setupInput("Input2", "I2", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		// setup of the first output of unit 1 
		unitElements[3].outputs[1].setupOutput("Output", "O", 32); // 32 means output will be floatingpoint

		
				
		// setup of a display unit, 1 input, 0 outputs, no parameters
		unitElements[4] = new UnitElements("Display", "", 1, 0, 0);
		unitElements[4].inputs[1].setupInput("Input1", "I1", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		unitElements[4].isDisplayUnit = true;
		
		unitElements[5] = new UnitElements("Display", "", 1, 0, 0);
		unitElements[5].inputs[1].setupInput("Input1", "I1", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		unitElements[5].isDisplayUnit = true;
		
		unitElements[6] = new UnitElements("Display", "", 1, 0, 0);
		unitElements[6].inputs[1].setupInput("Input1", "I1", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		unitElements[6].isDisplayUnit = true;
		
		////////////////////////////////////////////////////////
		// setup the connections
		////////////////////////////////////////////////////////
		
		HashMap<Integer,Connection> connectionMap = new HashMap<Integer,Connection>();
		
		// add six connections
		// fromUnitNumber, fromOutputNumber, toUnitNumber, toInputNumber
		Connection con;
		con = new Connection(1,1,2,1);
		connectionMap.put(con.id, con);
		con = new Connection(2,1,3,1);
		connectionMap.put(con.id, con);
		con = new Connection(1,1,3,2);
		connectionMap.put(con.id, con);
		con = new Connection(1,1,4,1); // display output 1 of unit 1 
		connectionMap.put(con.id, con);
		con = new Connection(2,1,5,1); // display output 1 of unit 2 
		connectionMap.put(con.id, con);
		con = new Connection(3,1,6,1); // display output 1 of unit 3 
		connectionMap.put(con.id, con);
		
		// remove one connection
		//connectionMap.remove( Connection.getID(2,1,5,1) );
		
		
		// apply the connections
		for (Connection connection : connectionMap.values()) {
			connection.connect(unitElements);
		}
		
		////////////////////////////////////////////////////////
		// analysis and 
		// verification of the connection network
		////////////////////////////////////////////////////////
		

		////////////////////////////////////////////////////////
		// generation of the ImageJ macro
		////////////////////////////////////////////////////////
		
		// unitElements has to be ordered according to the correct processing sequence
		String macro = MacroGenerator.generateMacro(unitElements);
		
		new ImageJ();
		IJ.log(macro);
		IJ.runMacro(macro, "");
	}

}
