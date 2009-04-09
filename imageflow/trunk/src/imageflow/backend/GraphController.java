package imageflow.backend;
import graph.Node;
import ij.IJ;
import ij.ImageJ;
import imageflow.ImageFlow;
import imageflow.models.Connection;
import imageflow.models.ConnectionList;
import imageflow.models.unit.CommentNode;
import imageflow.models.unit.UnitDescription;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitFactory;
import imageflow.models.unit.UnitList;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.jdesktop.application.View;




/**
 * Controller for Workflows. 
 * @author danielsenff
 *
 */
public class GraphController{

	//	private ApplicationController controller;

	private UnitList nodes;
	/**
	 * List which stores copied Nodes.
	 */
	protected ArrayList<Node> copyNodesList;


	/**
	 * 
	 */
	public GraphController() {
		this.nodes = new UnitList();
		this.copyNodesList = new ArrayList<Node>();
	}


	/**
	 * Starts an imagej instance and executes the macro.
	 * @param macro
	 * @param showLog
	 */
	public void runImageJMacro(final String macro, final boolean showLog) {
		ImageJ imagej = ((ImageFlow)ImageFlow.getInstance()).getImageJInstance();
		if(imagej == null)
			imagej = new ImageJ(null);

		if(showLog)
			IJ.log(macro);

//		imagej.setVisible(false);
		IJ.runMacro(macro, "");
	}


	/**
	 * @return the unitElements
	 */
	public UnitList getUnitElements() {
		return this.nodes;
	}
//
//	public static void main(final String[] args) {
//		final GraphController controller = new GraphController();
//		controller.setupExample1();
//		controller.generateMacro();
//	}

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

/*
	public static UnitList sortList(UnitList unitElements) {

		// temporary list, discarded after this method call
		UnitList orderedList = new UnitList();

		// reset all marks
		unitElements.unmarkUnits();

		int mark = 0;	// nth element, that has been sorted
		int i = 0; 		// nth lap in the loop
		int index = 0; 	// index 0 < i < unitElements.size()

		try {
			//loop over all units, selection sort, levelorder
			// I don't like this condition
			while(!unitElements.isEmpty()) {
				index = i % unitElements.size();
				Node node =  unitElements.get(index); 

				// find out what kind of node is stored
				if(node instanceof CommentNode) {
					//if comment then remove and ignore, we don't need it
					unitElements.remove(index);
				} else if (node instanceof UnitElement) {
					UnitElement unit =(UnitElement) node;
					// check if all inputs of this node are marked
					// if so, this unit is moved from the old list to the new one

					if(unit.hasMarkedOutput()) throw new Exception("Unit has Output marked, " +
					"although the unit itself is not marked. This suggests an infinited loop.");
					if(unit.hasAllInputsMarked()) {
						mark++;	

						// increment mark
						// mark outputs
						unit.setMark(mark);

						// remove from the old list and
						// move this to the new ordered list
						Node remove = unitElements.remove(index);
						orderedList.add(remove);

					} else if (!unit.hasInputsConnected() 
							&& unit.getUnitType() != Type.SOURCE) {
						// if unit has no connections actually, it can be discarded right away
						unitElements.remove(index);
						// if there is a branch with two units connected, the first one will be discarded, 
						// the second will still exist, but as the input is now missing, it will 
						// be deleted in the next lap
					} else if (!unit.hasOutputsConnected() 
							&& unit.getUnitType() == Type.SOURCE 
							&& !unit.isDisplayUnit()) {
						// if source has no connected outputs and is not visible
						unitElements.remove(index);
					}
				}
				// Selection Sort
				// each time an element whose previous nodes have already been registered
				// is found the next loop over the element list is one element shorter.
				// thereby having O(n^2) maybe this can be done better later
				i++;
			}

			//  replacing here causes deletion of none-used nodes 
			for (Node node : orderedList) {
				unitElements.add(node);
			}
		} catch(Exception ex) {
			// restore list, without damaging it
		}

		return unitElements;
	}*/


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

	public void write(File file) throws IOException {
			nodes.write(file);
	}


	public void setupExample1() {

		/*
		 * Wurzelbaum
		 */

		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////

		UnitDescription sourceUnitDescription = new UnitDescription(new File("xml_units/ImageSource_Unit.xml"));
		final UnitElement sourceUnit = UnitFactory.createProcessingUnit(sourceUnitDescription, new Point(30,100));

		UnitDescription blurUnitDescription = new UnitDescription(new File("xml_units/Process/GaussianBlur_Unit.xml"));
		final UnitElement blurUnit = UnitFactory.createProcessingUnit(blurUnitDescription, new Point(180, 50));

		UnitDescription mergeUnitDescription = new UnitDescription(new File("xml_units/Process/ImageCalculator_Unit.xml"));
		final UnitElement mergeUnit = UnitFactory.createProcessingUnit(mergeUnitDescription,new Point(320, 100));

		UnitDescription noiseUnitDescription = new UnitDescription(new File("xml_units/Process/AddNoise_Unit.xml"));
		final UnitElement noiseUnit = UnitFactory.createProcessingUnit(noiseUnitDescription,new Point(450, 100));
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

		// remove one connection
		//connectionMap.remove( Connection.getID(2,1,5,1) );


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

		UnitDescription unitConvolveDescription = new UnitDescription(new File("xml_units/Process/Convolver_Unit.xml"));
		final UnitElement convUnit = UnitFactory.createProcessingUnit(unitConvolveDescription, new Point(400, 50));
		final UnitElement convUnit2 = UnitFactory.createProcessingUnit(unitConvolveDescription, new Point(400, 160));

		UnitDescription unitSquareDescription = new UnitDescription(new File("xml_units/Process/Square_Unit.xml"));
		final UnitElement squareUnit = UnitFactory.createProcessingUnit(unitSquareDescription, new Point(510, 50));
		final UnitElement squareUnit2 = UnitFactory.createProcessingUnit(unitSquareDescription, new Point(510, 160));

		final UnitElement addUnit = UnitFactory.createProcessingUnit(new UnitDescription(new File("xml_units/Process/Add_Unit.xml")), new Point(650, 100));
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

