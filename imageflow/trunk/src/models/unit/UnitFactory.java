/**
 * 
 */
package models.unit;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import models.ParameterFactory;


/**
 * @author danielsenff
 *
 */
public class UnitFactory {

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
				"run(\"Image Calculator...\", \"image1=TITLE_1 operation=Subtract image2=TITLE_2 create 32-bit\"); \n",2,1,1);
		// setup of the parameter
		mergeUnit.addParameter(
				ParameterFactory.createParameter("32-bit", true, "generate a floating point result image"));
		// setup of the inputs
		mergeUnit.addInput("Input1", "I1", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		mergeUnit.addInput("Input2", "I2", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		// setup of the first output of unit 1 
		mergeUnit.addOutput("Output", "O", 32); // 32 means output will be floatingpoint
		mergeUnit.setDisplayUnit(true);
		mergeUnit.updateUnitIcon();
		return mergeUnit;
	}
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @return 
	 */
	public static UnitElement createSourceUnit() {
		return createSourceUnit(new Point(30,30));
	}
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @param origin
	 * @return
	 */
	public static UnitElement createSourceUnit(Point origin) {

		UnitElement sourceUnit = new UnitElement(origin, "Source1", "open(\"PARA_STRING_1\");\n", 0, 1, 1);
		// setup of the first parameter
		sourceUnit.addParameter(
				ParameterFactory.createParameter("Input image file",	// parameter description
					"/Users/danielsenff/zange1.png", // parameter value
					"The source unit needs the path of an image file." // help text for this parameter
					));
		
		// setup of the output of the (source) unit 0
		int bitDepth = sourceUnit.getBitDepth();
		sourceUnit.addOutput("Output", "O", bitDepth);
		sourceUnit.updateUnitIcon();
		return sourceUnit;
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
			blurUnit.setIcon(ImageIO.read(new File("res"+File.separator+"blur.png")));
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
		blurUnit.setDisplayUnit(true);
		blurUnit.updateUnitIcon();
		return blurUnit;
	}
	
}
