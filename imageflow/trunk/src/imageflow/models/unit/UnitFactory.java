/**
 * 
 */
package imageflow.models.unit;

import graph.Node;
import imageflow.ImageFlow;
import imageflow.ImageFlowView;
import imageflow.gui.GraphPanel;
import imageflow.models.Model;
import imageflow.models.ModelListener;
import imageflow.models.parameter.ChoiceParameter;
import imageflow.models.parameter.ParameterFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;



/**
 * @author danielsenff
 *
 */
public class UnitFactory {
	
	/**
	 * Creates a {@link UnitElement} based on the given Descriptions.
	 * @param unitDescription
	 * @param origin
	 * @return
	 */
	public static UnitElement createProcessingUnit(
			final UnitDescription unitDescription, 
			final Point origin) {
		
		String unitName = unitDescription.unitName;
		String imageJSyntax = unitDescription.imageJSyntax;
		int numParas = unitDescription.numParas;
		int numInputs = unitDescription.numInputs;
		int numOutputs = unitDescription.numOutputs;
		Color color = unitDescription.color;
		
		// usual case, we deal with a UnitElement
		
		// if we have a SourceUnit, we have to take the according class
		UnitElement unitElement;
		if(unitName.equals("Image Source")) {
			unitElement = new SourceUnitElement(new Point(origin), unitName, imageJSyntax);
		} else if(unitName.equals("Background")) {
			unitElement = new BackgroundUnitElement(new Point(origin), unitName, imageJSyntax);
		} else 
			unitElement = new UnitElement(new Point(origin), unitName, imageJSyntax);
		
		unitElement.setColor(color);
		
		// add an icon if there is one mentioned and found
		File iconFile = new File(unitDescription.pathToIcon);
		if(iconFile.exists()) {
			try {
				if(unitElement instanceof SourceUnitElement) 
					unitElement.setIcon(ImageIO.read(((SourceUnitElement) unitElement).getFile()));
				else 
					unitElement.setIcon(ImageIO.read(iconFile));	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 1; i <= numParas; i++) {
			Para para = unitDescription.para[i];
			
			unitElement.addParameter(
					ParameterFactory.createParameter(para.name, 
													para.value, 
													para.helpString, 
													para.trueString, 
													para.choiceIndex));
		}
		
		// setup of the inputs
		for (int i = 1; i <= numInputs; i++) {
			Input input = unitDescription.input[i];
			String name = input.name;
			String shortName = input.shortName;
			int imageType = input.imageType;
			boolean needToCopyInput = input.needToCopyInput;
			
			unitElement.addInput(name, shortName, imageType, needToCopyInput);
		}
		
		// setup of the output(s)
		for (int i = 1; i <= numOutputs; i++) {
			Output output = unitDescription.output[i];
			String name = output.name;
			String shortName = output.shortName;
			int imageType = output.imageType;
			boolean doDisplay = output.doDisplay;
			
			if(unitElement instanceof SourceUnitElement) {
				imageType = ((SourceUnitElement)unitElement).getImageType();
			} else if(unitElement instanceof SourceUnitElement) {
				imageType = ((SourceUnitElement)unitElement).getImageType();
			}
			// imagetype -1 means output will be the same type as the input
			unitElement.addOutput(name, shortName, imageType, doDisplay); 
			
		}
		
//		unitElement.updateUnitIcon();
		if(ImageFlow.getApplication() != null) 
			registerModelListener(unitElement);
		
		return unitElement;
	}

	/**
	 * Creates a new {@link CommentNode}. 
	 * @param string
	 * @param point
	 * @return
	 */
	public static CommentNode createComment(final String string, final Point point) {
		CommentNode commentNode = new CommentNode(point, string);
		registerModelListener(commentNode);
		return commentNode;
	}
	
	

	public static void registerModelListener(Node node) {
		final ImageFlowView ifView = ((ImageFlowView)ImageFlow.getApplication().getMainView());
		final GraphPanel graphPanel = ifView.getGraphPanel();
		if(node instanceof CommentNode) {
			((CommentNode)node).addModelListener(
					new ModelListener () {
						public void modelChanged (final Model hitModel)	{
							graphPanel.invalidate();
							graphPanel.repaint();
							ifView.setModified(true);
						}
					});	
		} else //if(node instanceof UnitElement) 
		{
			((UnitElement)node).addModelListener(
					new ModelListener () {
						public void modelChanged (final Model hitModel)	{
							graphPanel.invalidate();
							graphPanel.repaint();
							ifView.setModified(true);
						}
					});	
		}
	}
	
	
	
	
	
	
	
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
		UnitElement noiseUnit = new UnitElement(origin, "Add Noise", "run(\"Add Noise\"); \n");
		
		// setup of the first input of unit 2
		noiseUnit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		noiseUnit.addOutput("Output", "O", -1, false); // -1 means output will be the same type as the input
		noiseUnit.updateUnitIcon();
		return noiseUnit;
	}
	
	
	@Deprecated
	public static UnitElement createHistogramUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Histogram", "run(\"Histogram\"); \n");
		
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
		UnitElement noiseUnit = new UnitElement(origin, "Find Edges", "	run(\"Find Edges\");\n");
		
		// setup of the first input of unit 2
		noiseUnit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		noiseUnit.addOutput("Output", "O", -1, false); // -1 means output will be the same type as the input
		noiseUnit.updateUnitIcon();
		return noiseUnit;
	}

	
	public static UnitElement createInvertUnit(Point origin) {
		// 
		UnitElement unit = new UnitElement(origin, "Invert", "	run(\"Invert\");\n");
		
		// setup of the first input of unit 2
		unit.addInput("Input", "I", ij.plugin.filter.PlugInFilter.DOES_ALL, true);
		// setup of the first output of unit 2 
		unit.addOutput("Output", "O", -1, false); // -1 means output will be the same type as the input
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
		
		UnitElement mergeUnit = new UnitElement(origin, "Image Calculator", 
				"run(\"Image Calculator...\", \"image1=TITLE_1 operation=PARA_STRING_1 image2=TITLE_2 create 32-bit\"); \n");
		ArrayList<String> mathChoices = new ArrayList<String>();
		mathChoices.add("Add");
		mathChoices.add("Subtract");
		mathChoices.add("Multiply");
		mathChoices.add("Devide");
		mathChoices.add("AND"); 
		mathChoices.add("OR");
		mathChoices.add("XOR");
		
		
		mergeUnit.addParameter(
				new ChoiceParameter("Math", mathChoices, "Add",
				"Defines what math should be used to merge both images"));
		
		// setup of the inputs
		mergeUnit.addInput("Input1", "I1", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		mergeUnit.addInput("Input2", "I2", ij.plugin.filter.PlugInFilter.DOES_ALL, false);
		// setup of the first output of unit 1 
		mergeUnit.addOutput("Output", "O", 32, false); // 32 means output will be floatingpoint
		mergeUnit.updateUnitIcon();
		return mergeUnit;
	}
	
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @return 
	 */
	@Deprecated
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
	public static UnitElement createSourceUnit(String path) {
		return createSourceUnit(path, new Point(30,100));
	}
	
	/**
	 * setup of a source (input) unit
	 * display name, syntax: "open("path");", 0 inputs, 1 output, 1 parameter
	 * @param origin
	 * @return
	 */
	@Deprecated
	public static UnitElement createSourceUnit(String path, Point origin) {

		SourceUnitElement sourceUnit = new SourceUnitElement(origin, "Image Source", "open(\"PARA_STRING_1\");\n");
		// setup of the first parameter
		sourceUnit.addParameter(
				ParameterFactory.createParameter("Input image file",	// parameter description
					path, // parameter value
					"The source unit needs the path of an image file." // help text for this parameter
					));
		
		// setup of the output of the (source) unit 0
		int bitDepth = sourceUnit.getBitDepth();
		sourceUnit.setColor(Color.decode("0x9cba92"));
		sourceUnit.addOutput("Output", "O", bitDepth, false);
		sourceUnit.updateUnitIcon();
		return sourceUnit;
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

		UnitElement sourceUnit = new UnitElement(origin, "Background", 
				"newImage(\"Background\", \"8-bit White\", PARA_INTEGER_1, PARA_INTEGER_2, 1);\n"); 
		// setup of the first parameter
		sourceUnit.addParameter(
				ParameterFactory.createParameter("Background width", dimension.width, "Width of the background image"));
		sourceUnit.addParameter(
				ParameterFactory.createParameter("Background height", dimension.height, "Height of the background image"));
		
		// setup of the output of the (source) unit 0
		sourceUnit.addOutput("Output", "O", ij.plugin.filter.PlugInFilter.DOES_RGB, false);
		sourceUnit.setColor(new Color(0x9cba92));
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
	@Deprecated
	public static UnitElement createGaussianBlurUnit(Point origin) {
		// 
		UnitElement blurUnit = new UnitElement(origin, "Gaussian Blur", "run(\"Gaussian Blur...\", \"sigma=PARA_DOUBLE_1\");\n");
		try {
			blurUnit.setIcon(ImageIO.read(new File("bin/imageflow/resources/blur.png")));
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
		blurUnit.addOutput("Output", "O", -1, false); // -1 means output will be the same type as the input
		blurUnit.updateUnitIcon();
		return blurUnit;
	}





	
}
