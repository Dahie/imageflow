package de.danielsenff.imageflow.controller;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;

import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import visualap.Node;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.SelectionList;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.unit.CommentNode;
import de.danielsenff.imageflow.models.unit.UnitDescription;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;
import de.danielsenff.imageflow.models.unit.UnitList;




/**
 * Controller for Workflows. 
 * @author danielsenff
 *
 */
public class GraphController{

	private UnitList nodes;
	/**
	 * List which stores copied Nodes.
	 */
	protected ArrayList<Node> copyNodesList;

	/**
	 * List of selected units
	 */
	private SelectionList selections;


	
	/**
	 * 
	 */
	public GraphController() {
		this.nodes = new UnitList();
		this.copyNodesList = new ArrayList<Node>();
		this.selections = new SelectionList();
	}


	/**
	 * Starts an imagej instance and executes the macro.
	 * @param macro
	 * @param showLog
	 */
	public void runImageJMacro(final String macro, final boolean showLog) {
		ImageJ imagej = ((ImageFlow)ImageFlow.getInstance()).getImageJInstance();
		if(showLog)
			IJ.log(macro);

//		imagej.setVisible(false);
		IJ.runMacro(macro, "");
		
		/* beginning for new functions, but not today, daniel */
		/*int[] imageIDs = WindowManager.getIDList();
		for (int i = 0; i < WindowManager.getImageCount(); i++) {
			ImagePlus image = WindowManager.getImage(imageIDs[i]);
			String imagetitle = image.getTitle(); 
			System.out.println(imagetitle);
			
			if(imagetitle.contains("-"))
				imagetitle.substring(0, imagetitle.indexOf('-'));
				
			String[] titleStrings = imagetitle.split("_");
			int unitID = Integer.valueOf(titleStrings[1]);
			int outputID = Integer.valueOf(titleStrings[3]);;			
			
			if(nodes.getUnit(unitID) instanceof UnitElement) {
				UnitElement unit = (UnitElement) nodes.getUnit(unitID);
				unit.setIcon(image.getImage().getScaledInstance(48, 48, Image.SCALE_FAST));
			}
			
			System.out.println("unit "+unitID+ " and output "+ outputID);
		}*/
		
	}


	/**
	 * @return the unitElements
	 */
	public UnitList getUnitElements() {
		return this.nodes;
	}


	/**
	 * Generates the executable Macro based on the current graph.
	 * @return
	 */
	public String generateMacro() {
		final MacroFlowRunner macroFlowRunner = new MacroFlowRunner(this.nodes);
		return macroFlowRunner.generateMacro();
	}

	/**
	 * Returns current the {@link ConnectionList}
	 * @return
	 */
	public ConnectionList getConnections() {
		return this.nodes.getConnections();
	}


	/**
	 * Selections
	 * @return
	 */
	public SelectionList getSelections() {
		return this.selections;
	}
	
	/**
	 * Get the List of copied {@link Node};
	 * @return
	 */
	public ArrayList<Node> getCopyNodesList() {
		return copyNodesList;
	}

	/**
	 * Removes the {@link UnitElement} from the unitList and its Connections.
	 * @param unit
	 * @return
	 */
	public boolean removeNode(final Node node) {
		return nodes.remove(node);
	}

	/**
	 * Convenience method for calling ConnectionList.write() to save the workflow
	 * into a XML-file.
	 * @param file
	 * @throws IOException
	 */
	public void write(File file) throws IOException {
		nodes.write(file);
	}


	public void setupExample1() {
		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////
		DelegatesController delegatesController = DelegatesController.getInstance();

//		UnitDescription sourceUnitDescription = new UnitDescription(new File("xml_units/ImageSource_Unit.xml"));
//		final UnitElement sourceUnit = UnitFactory.createProcessingUnit(sourceUnitDescription, new Point(30,100));
		final UnitElement sourceUnit = delegatesController.getDelegate("Image Source").createUnit(new Point(30, 100));
		
		
//		UnitDescription blurUnitDescription = new UnitDescription(new File("xml_units/Process/Filters/GaussianBlur_Unit.xml"));
//		final UnitElement blurUnit = UnitFactory.createProcessingUnit(blurUnitDescription, new Point(180, 50));
		final UnitElement blurUnit = delegatesController.getDelegate("Gaussian Blur").createUnit(new Point(180, 50));
		
//		UnitDescription mergeUnitDescription = new UnitDescription(new File("xml_units/Process/ImageCalculator_Unit.xml"));
//		final UnitElement mergeUnit = UnitFactory.createProcessingUnit(mergeUnitDescription,new Point(320, 100));
		final UnitElement mergeUnit = delegatesController.getDelegate("Image Calculator").createUnit(new Point(320, 100));
		
//		UnitDescription noiseUnitDescription = new UnitDescription(new File("xml_units/Process/Noise/AddNoise_Unit.xml"));
//		final UnitElement noiseUnit = UnitFactory.createProcessingUnit(noiseUnitDescription,new Point(450, 100));
		final UnitElement noiseUnit = delegatesController.getDelegate("Add Noise").createUnit(new Point(450, 100));
		noiseUnit.setDisplayUnit(true);
		
		CommentNode comment = UnitFactory.createComment("my usual example", new Point(30, 40));

		// some mixing, so they are not in order
		nodes.add(noiseUnit);
		nodes.add(blurUnit);
		nodes.add(sourceUnit);
		nodes.add(mergeUnit);
		nodes.add(comment);


		////////////////////////////////////////////////////////
		// setup the connections
		////////////////////////////////////////////////////////



		// add six connections
		// the conn is established on adding
		// fromUnit, fromOutputNumber, toUnit, toInputNumber
		Connection con;
		con = new Connection(sourceUnit,1,blurUnit,1);
		nodes.addConnection(con);
		con = new Connection(blurUnit,1,mergeUnit,1);
		nodes.addConnection(con);
		con = new Connection(sourceUnit,1,mergeUnit,2);
		nodes.addConnection(con);
		con = new Connection(mergeUnit,1,noiseUnit,1);
		nodes.addConnection(con);
	}

	public void setupExample0_XML() {

		nodes.clear();
		try {
			nodes.read(new File("xml_flows/Example0_flow.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setupExample2() {


		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////


		UnitDescription sourceUnitDescription = new UnitDescription(new File("xml_units/ImageSource_Unit.xml"));
		final UnitElement sourceUnit = UnitFactory.createProcessingUnit(sourceUnitDescription, new Point(30,100));

		final UnitElement to8BitUnit = UnitFactory.createProcessingUnit(new UnitDescription(new File("xml_units/Image/8Bit_Unit.xml")), new Point(150, 100));
		final UnitElement to32BitUnit = UnitFactory.createProcessingUnit(new UnitDescription(new File("xml_units/Image/32Bit_Unit.xml")), new Point(260, 100));

		UnitDescription unitConvolveDescription = new UnitDescription(new File("xml_units/Process/Filters/Convolver_Unit.xml"));
		final UnitElement convUnit = UnitFactory.createProcessingUnit(unitConvolveDescription, new Point(400, 50));
		final UnitElement convUnit2 = UnitFactory.createProcessingUnit(unitConvolveDescription, new Point(400, 160));

		UnitDescription unitSquareDescription = new UnitDescription(new File("xml_units/Process/Math_unit.xml"));
		final UnitElement squareUnit = UnitFactory.createProcessingUnit(unitSquareDescription, new Point(510, 50));
		final UnitElement squareUnit2 = UnitFactory.createProcessingUnit(unitSquareDescription, new Point(510, 160));

		final UnitElement addUnit = UnitFactory.createProcessingUnit(new UnitDescription(new File("xml_units/Process/Add_unit.xml")), new Point(650, 100));
		final UnitElement fireUnit = UnitFactory.createProcessingUnit(new UnitDescription(new File("xml_units/Lookup Tables/Fire_Unit.xml")), new Point(770, 100));

		// some mixing, so they are not in order
		nodes.add(sourceUnit);
		nodes.add(to8BitUnit);
		nodes.add(to32BitUnit);
		nodes.add(convUnit);
		nodes.add(squareUnit);
		nodes.add(convUnit2);
		nodes.add(squareUnit2);
		nodes.add(addUnit);
		nodes.add(fireUnit);
		fireUnit.setDisplayUnit(true);

		////////////////////////////////////////////////////////
		// setup the connections
		////////////////////////////////////////////////////////

		// add six connections
		// the conn is established on adding
		// fromUnit, fromOutputNumber, toUnit, toInputNumber

		nodes.addConnection(new Connection(sourceUnit,1,to8BitUnit,1));
		nodes.addConnection(new Connection(to8BitUnit,1,to32BitUnit,1));
		nodes.addConnection(new Connection(to32BitUnit,1,convUnit,1));
		nodes.addConnection(new Connection(to32BitUnit,1,convUnit2,1));
		nodes.addConnection(new Connection(convUnit,1,squareUnit,1));
		nodes.addConnection(new Connection(convUnit2,1,squareUnit2,1));
		nodes.addConnection(new Connection(squareUnit,1,addUnit,1));
		nodes.addConnection(new Connection(squareUnit2,1,addUnit,2));
		nodes.addConnection(new Connection(addUnit,1,fireUnit,1));

	}


}

