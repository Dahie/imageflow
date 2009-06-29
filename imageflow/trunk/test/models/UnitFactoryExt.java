package models;
import ij.plugin.filter.PlugInFilter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.parameter.ParameterFactory;
import de.danielsenff.imageflow.models.unit.SourceUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;


public class UnitFactoryExt extends UnitFactory {


	/*
	 * From here on it's kinda legacy. 
	 * They are still used for the testing framework, but not for the application.
	 */
	
	

	
	/**
	 * setup of a processing unit (Image Calculator  / Subtract)
	 * display name, syntax, 2 inputs (as titles), 1 output, 1 parameter
	 * @return
	 */
	public static UnitElement createImageCalculatorUnit() {
		return createImageCalculatorUnit(new Point(30,30));
	}
	
	
	/**
	 * setup of a processing unit (gaussian blur)
	 * display name, syntax: "run("Gaussian Blur...", "sigma=2");", 1 input, 1 output, 1 parameter
	 * @return
	 */
	public static UnitElement createAddNoiseUnit() {
		return createAddNoiseUnit(new Point(30,30));
	}
	
	/**
	 * setup of a processing unit (add noise)
	 * display name, syntax: "run("Add Noise");", 1 input, 1 output, 0 parameter
	 * @param origin
	 * @return
	 */
	@Deprecated
	public static UnitElement createAddNoiseUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Add Noise", "run(\"Add Noise\"); \n");
		
		// setup of the first input of unit
		Input input = new Input(DataTypeFactory.createImage(PlugInFilter.DOES_ALL), unit,1, true);
		input.setupInput("Input", "I", false);
		unit.addInput(input);
		// setup of the first output of unit 
		Output output = new Output(DataTypeFactory.createImage(-1), unit, 1);
		output.setupOutput("Output", "O");
		unit.addOutput(output);
		unit.updateUnitIcon();
		return unit;
	}
	
	
	@Deprecated
	public static UnitElement createHistogramUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Histogram", "run(\"Histogram\"); \n");
		
		// setup of the first input of unit 2
		Input input = new Input(DataTypeFactory.createImage(PlugInFilter.DOES_ALL), unit,1, true);
		input.setupInput("Input", "I", false);
		unit.addInput(input);
		// setup of the first output of unit 2 
		unit.updateUnitIcon();
		return unit;
	}
	
	
	/**
	 * setup of a processing unit (gaussian blur)
	 * display name, syntax: "run("Find Edges");", 1 input, 1 output, 1 parameter
	 * @return
	 */
	@Deprecated
	public static UnitElement createFindEdgesUnit() {
		return createFindEdgesUnit(new Point(30,30));
	}
	
	/**
	 * setup of a processing unit (add noise)
	 * display name, syntax: "run("Find Edges");", 1 input, 1 output, 0 parameter
	 * @param origin
	 * @return
	 */
	public static UnitElement createFindEdgesUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Find Edges", "	run(\"Find Edges\");\n");
		
		// setup of the first input of unit 2
		
		Input input = new Input(DataTypeFactory.createImage(ij.plugin.filter.PlugInFilter.DOES_ALL), 
				unit, 1, true);
		input.setupInput("Input", "i", true);
		unit.addInput(input);
		// setup of the first output of unit 2 
		Output output = new Output(DataTypeFactory.createImage(-1), unit, 1);
		output.setupOutput("Output", "O");
		unit.addOutput(output);
		unit.updateUnitIcon();
		return unit;
	}

	
	public static UnitElement createInvertUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Invert", "	run(\"Invert\");\n");
		
		// setup of the first input of unit 2
		Input input = new Input(DataTypeFactory.createImage(ij.plugin.filter.PlugInFilter.DOES_ALL), 
				unit, 1, true);
		input.setupInput("Input", "i", true);
		unit.addInput(input);
		// setup of the first output of unit 2 
		Output output = new Output(DataTypeFactory.createImage(-1), unit, 1);
		output.setupOutput("Output", "O");
		unit.addOutput(output);
		unit.updateUnitIcon();
		return unit;
	}
	

	/**
	 * setup of a processing unit (Image Calculator  / Subtract)
	 * display name, syntax, 2 inputs (as titles), 1 output, 1 parameter
	 * @param origin
	 * @return
	 */
	@Deprecated
	public static UnitElement createImageCalculatorUnit(Point origin) {
		
		UnitElement unit = new UnitElement(origin, "Image Calculator", 
				"run(\"Image Calculator...\", \"image1=TITLE_1 operation=PARA_STRING_1 image2=TITLE_2 create 32-bit\"); \n");
		ArrayList<String> mathChoices = new ArrayList<String>();
		mathChoices.add("Add");
		mathChoices.add("Subtract");
		mathChoices.add("Multiply");
		mathChoices.add("Devide");
		mathChoices.add("AND"); 
		mathChoices.add("OR");
		mathChoices.add("XOR");
		
		
		unit.addParameter(
				new ChoiceParameter("Math", mathChoices, "Add",
				"Defines what math should be used to merge both images"));
		
		// setup of the inputs
		
		Input input = new Input(DataTypeFactory.createImage(ij.plugin.filter.PlugInFilter.DOES_ALL), 
				unit, 1, true);
		input.setupInput("Input", "i", true);
		unit.addInput(input);
		Input input2 = new Input(DataTypeFactory.createImage(ij.plugin.filter.PlugInFilter.DOES_ALL), 
				unit, 2, true);
		input2.setupInput("Input", "i", true);
		unit.addInput(input2);
		// setup of the first output of unit 1 
		Output output = new Output(DataTypeFactory.createImage(32), unit, 1);
		output.setupOutput("Output", "O");
		unit.addOutput(output);
		unit.updateUnitIcon();
		return unit;
	}
	
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @return 
	 */
	@Deprecated
	public static SourceUnitElement createSourceUnit() {
		// load filechooser, get path
		JFileChooser imageFileChooser = new JFileChooser();
		
		imageFileChooser.setMultiSelectionEnabled(false);
		imageFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		final int res = imageFileChooser.showOpenDialog(null);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			final File file = imageFileChooser.getSelectedFile();
			System.out.println(file);
			return createSourceUnit(file.getAbsolutePath());
		}
		return null;
	}
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @return 
	 */
	@Deprecated
	public static UnitElement createSourceUnit(Point origin) {
		// load filechooser, get path
		JFileChooser imageFileChooser = new JFileChooser();
		
		imageFileChooser.setMultiSelectionEnabled(false);
		imageFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		final int res = imageFileChooser.showOpenDialog(null);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			final File file = imageFileChooser.getSelectedFile();
			System.out.println(file);
			return createSourceUnit(file.getAbsolutePath(), origin);
		}
		return null;
	}
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @return 
	 */
	@Deprecated
	public static SourceUnitElement createSourceUnit(String path) {
		return createSourceUnit(path, new Point(30,100));
	}
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @param origin
	 * @return
	 */
	@Deprecated
	public static SourceUnitElement createSourceUnit(String path, Point origin) {

		SourceUnitElement unit = 
			new SourceUnitElement(origin, "Image Source", "open(\"PARA_STRING_1\");\n");
		// setup of the first parameter
		unit.addParameter(
				ParameterFactory.createParameter("Input image file",	// parameter description
					"String",
					path, // parameter value
					"The source unit needs the path of an image file." // help text for this parameter
					));
		
		// setup of the output of the (source) unit 0
		int bitDepth = unit.getBitDepth();
		unit.setColor(Color.decode("0x9cba92"));
		Output output = new Output(DataTypeFactory.createImage(bitDepth), unit, 1);
		output.setupOutput("Output", "O");
		unit.addOutput(output);
		unit.updateUnitIcon();
		return unit;
	}
	

	/**
	 * @param dimension
	 * @return
	 */
	@Deprecated
	public static UnitElement createBackgroundUnit(Dimension dimension) {
		return createBackgroundUnit(dimension, new Point(30,30));
	}

	
	/**
	 * setup of a source (input) background unit
	 * this creates an empty image with a defined color
	 * @param origin
	 * @return
	 */
	public static UnitElement createBackgroundUnit(Dimension dimension, Point origin) {

		UnitElement unit = new UnitElement(origin, "Background", 
				"newImage(\"Background\", \"8-bit White\", PARA_INTEGER_1, PARA_INTEGER_2, 1);\n"); 
		// setup of the first parameter
		unit.addParameter(
				ParameterFactory.createParameter("Background width", 
						"Integer",
						dimension.width, "Width of the background image"));
		unit.addParameter(
				ParameterFactory.createParameter("Background height", 
						"Integer", dimension.height, "Height of the background image"));
		
		// setup of the output of the (source) unit 0
		Output output = new Output(DataTypeFactory.createImage(-1), unit, 1);
		output.setupOutput("Output", "O");
		unit.addOutput(output);
		unit.setColor(new Color(0x9cba92));
		unit.updateUnitIcon();
		return unit;
	}
	
	
	
	/**
	 * setup of a processing unit (gaussian blur)
	 * display name, syntax: "run("Gaussian Blur...", "sigma=2");", 1 input, 1 output, 1 parameter
	 * @return
	 */
	public static UnitElement createGaussianBlurUnit() {
		return createGaussianBlurUnit(new Point(30,30));
	}
	
	/**
	 * setup of a processing unit (gaussian blur)
	 * display name, syntax: "run("Gaussian Blur...", "sigma=2");", 1 input, 1 output, 1 parameter
	 * @param origin
	 * @return
	 */
	@Deprecated
	public static UnitElement createGaussianBlurUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Gaussian Blur", "run(\"Gaussian Blur...\", \"sigma=PARA_DOUBLE_1\");\n");
		// setup of the parameter
		unit.addParameter(
				ParameterFactory.createParameter("Radius", 
						"Double",
						4.0, "Radius of the gaussian kernel"));
		// attention, 4.0 as value 4 will be cast as integer and will lead to ClassCastExceptions
		
		// setup of the first input of unit 2
		Input input = new Input(DataTypeFactory.createImage(ij.plugin.filter.PlugInFilter.DOES_ALL), 
				unit, 1, true);
		input.setupInput("Input", "i", true);
		unit.addInput(input);
		// setup of the first output of unit 2 
		Output output = new Output(DataTypeFactory.createImage(-1), unit, 1);
		output.setupOutput("Output", "O");
		unit.addOutput(output);
		unit.updateUnitIcon();
		return unit;
	}



	
}
