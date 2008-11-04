/**
 * 
 */
package models.unit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import models.ChoiceParameter;
import models.ParameterFactory;


/**
 * @author danielsenff
 *
 */
public class UnitFactory {

	public static UnitElement buildUnitElement(String name, String imageJsyntax, Point origin) {
		
		// 
		UnitElement unit = new UnitElement(origin, name, imageJsyntax+" \n",1,1,0);
		
		// setup of the first input of unit 2
		unit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		unit.addOutput("Output", "O", -1); // -1 means output will be the same type as the input
		unit.updateUnitIcon();
		return unit;
		
	}
	
	
	/**
	 * setup of a processing unit (Image Calculator  / Subtract)
	 * display name, syntax, 2 inputs (as titles), 1 output, 1 parameter
	 * @return
	 */
	public static UnitElement createImageCalculatorUnit() {
		return createImageCalculatorUnit(new Point(30,30));
	}
	
	/**
	 * setup of a processing unit (Image Calculator  / Subtract)
	 * display name, syntax, 2 inputs (as titles), 1 output, 1 parameter
	 * @param origin
	 * @return
	 */
	public static UnitElement createImageCalculatorUnit(Point origin) {
		
		UnitElement mergeUnit = new UnitElement(origin, "Image Calculator", 
				"run(\"Image Calculator...\", \"image1=TITLE_1 operation=PARA_STRING_1 image2=TITLE_2 create 32-bit\"); \n",2,1,1);
		// setup of the parameter
//		mergeUnit.addParameter(
//				ParameterFactory.createParameter("32-bit", true, "generate a floating point result image"));
		String[] mathChoices = {"Add", "Subtract", "Multiply", "Devide", "AND", "OR", "XOR"};
		mergeUnit.addParameter(
				new ChoiceParameter("Math", mathChoices, "Add",
				"Defines what math should be used to merge both images"));
		
		// setup of the inputs
		mergeUnit.addInput("Input1", "I1", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		mergeUnit.addInput("Input2", "I2", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		// setup of the first output of unit 1 
		mergeUnit.addOutput("Output", "O", 32); // 32 means output will be floatingpoint
		mergeUnit.updateUnitIcon();
		return mergeUnit;
	}
	
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @return 
	 */
	public static UnitElement createSourceUnit() {
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
	public static UnitElement createSourceUnit(String path) {
		return createSourceUnit(path, new Point(30,30));
	}
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @param origin
	 * @return
	 */
	public static UnitElement createSourceUnit(String path, Point origin) {

		UnitElement sourceUnit = new UnitElement(origin, "File Source", "open(\"PARA_STRING_1\");\n", 0, 1, 1);
		// setup of the first parameter
		sourceUnit.addParameter(
				ParameterFactory.createParameter("Input image file",	// parameter description
					path, // parameter value
					"The source unit needs the path of an image file." // help text for this parameter
					));
		
		// setup of the output of the (source) unit 0
		int bitDepth = sourceUnit.getBitDepth();
		sourceUnit.addOutput("Output", "O", bitDepth);
		sourceUnit.updateUnitIcon();
		return sourceUnit;
	}
	

	/**
	 * @param dimension
	 * @return
	 */
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

		UnitElement sourceUnit = new UnitElement(origin, "Background", 
				"newImage(\"Background\", \"8-bit White\", PARA_INTEGER_1, PARA_INTEGER_2, 1);\n", 0, 1, 2); 
		// setup of the first parameter
		sourceUnit.addParameter(
				ParameterFactory.createParameter("Background width", dimension.width, "Width of the background image"));
		sourceUnit.addParameter(
				ParameterFactory.createParameter("Background height", dimension.height, "Height of the background image"));
		
		// setup of the output of the (source) unit 0
		sourceUnit.addOutput("Output", "O", ij.plugin.filter.PlugInFilter.DOES_RGB);
		sourceUnit.updateUnitIcon();
		return sourceUnit;
	}
	
	
	
	public static UnitElement createFillBackground(Color color, Point origin){
		UnitElement fillUnit = new UnitElement(origin, "Source2", 
				"setForegroundColor(PARA_INTEGER_1, PARA_INTEGER_2, PARA_INTEGER_3); \n"+
				"floodFill(1, 1); \n", 0, 1, 3); 
		
		fillUnit.addParameter(
				ParameterFactory.createParameter("Red value", color.getRed(), "Red value"));
		fillUnit.addParameter(
				ParameterFactory.createParameter("Green value", color.getGreen(), "Green value"));
		fillUnit.addParameter(
				ParameterFactory.createParameter("Blue value", color.getBlue(), "Blue value"));
		fillUnit.addOutput("Output", "O", ij.plugin.filter.PlugInFilter.DOES_RGB);
		return fillUnit;
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
	public static UnitElement createGaussianBlurUnit(Point origin) {
		// 
		UnitElement blurUnit = new UnitElement(origin, "Blur", "run(\"Gaussian Blur...\", \"sigma=PARA_DOUBLE_1\");\n",1,1,1);
		try {
			blurUnit.setIcon(ImageIO.read(new File("bin/res/blur.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// setup of the parameter
		blurUnit.addParameter(
				ParameterFactory.createParameter("Radius", 4.0, "Radius of the gaussian kernel"));
		// attention, 4.0 as value 4 will be cast as integer and will lead to ClassCastExceptions
		
		
		// setup of the first input of unit 2
		blurUnit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		blurUnit.addOutput("Output", "O", -1); // -1 means output will be the same type as the input
		blurUnit.updateUnitIcon();
		return blurUnit;
	}
	
	
	String[] unitDescription = {
			
	};
	
	public static UnitElement createProcessingUnit(Point origin) {
		// 
		UnitElement blurUnit = new UnitElement(origin, "Gaussian Blur", "run(\"Gaussian Blur...\", \"sigma=PARA_DOUBLE_1\");\n",1,1,1);
		try {
			blurUnit.setIcon(ImageIO.read(new File("bin/res/blur.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// setup of the parameter
//		unitElements[2].parameters[1].setParameter("Radius", 4, "Radius of the gaussian kernel");
		blurUnit.addParameter(
				ParameterFactory.createParameter("Radius", 4, "Radius of the gaussian kernel"));
		
		// setup of the first input of unit 2
		blurUnit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		blurUnit.addOutput("Output", "O", -1); // -1 means output will be the same type as the input
		blurUnit.updateUnitIcon();
		return blurUnit;
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
	public static UnitElement createAddNoiseUnit(Point origin) {
		// 
		UnitElement noiseUnit = new UnitElement(origin, "Add Noise", "run(\"Add Noise\"); \n",1,1,0);
		
		// setup of the first input of unit 2
		noiseUnit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		noiseUnit.addOutput("Output", "O", -1); // -1 means output will be the same type as the input
		noiseUnit.updateUnitIcon();
		return noiseUnit;
	}
	
	
	public static UnitElement createHistogramUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Histogram", "run(\"Histogram\"); \n",1,0,0);
		
		// setup of the first input of unit 2
		unit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		unit.updateUnitIcon();
		return unit;
	}
	
	
	/**
	 * setup of a processing unit (gaussian blur)
	 * display name, syntax: "run("Find Edges");", 1 input, 1 output, 1 parameter
	 * @return
	 */
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
		UnitElement noiseUnit = new UnitElement(origin, "Find Edges", "	run(\"Find Edges\");\n",1,1,0);
		
		// setup of the first input of unit 2
		noiseUnit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		noiseUnit.addOutput("Output", "O", -1); // -1 means output will be the same type as the input
		noiseUnit.updateUnitIcon();
		return noiseUnit;
	}

	
	public static UnitElement createInvertUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Invert", "	run(\"Invert\");\n",1,1,0);
		
		// setup of the first input of unit 2
		unit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		unit.addOutput("Output", "O", -1); // -1 means output will be the same type as the input
		unit.updateUnitIcon();
		return unit;
	}
	
}
