package de.danielsenff.imageflow.controller;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import visualap.Node;
import de.danielsenff.imageflow.models.SelectionList;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.ProxyInput;
import de.danielsenff.imageflow.models.connection.ProxyOutput;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory.Image;
import de.danielsenff.imageflow.models.unit.CommentNode;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
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
	 * @return the unitElements
	 */
	public UnitList getUnitElements() {
		return this.nodes;
	}


	/**
	 * Generates the executable Macro based on the current graph.
	 * @param extendedMacro determines if callback functions are put into macro code 
	 * @return
	 */
	public String generateMacro(boolean extendedMacro) {
		final MacroFlowRunner macroFlowRunner = new MacroFlowRunner(this.nodes);
		return macroFlowRunner.generateMacro(extendedMacro);
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
	 * @param node 
	 * @return
	 */
	public boolean removeNode(final Node node) {
		return nodes.remove(node);
	}
	
	/**
	 * Ungroup the contents of a GroupUnit
	 * @param group
	 */
	public void ungroup(final GroupUnitElement group) {
		ungroup(group, getUnitElements());
	}
	
	public static void ungroup(final GroupUnitElement group, UnitList units) {
		
		int deltaX = group.getOrigin().x - 25;
		int deltaY = group.getOrigin().y - 25;
		for (Node node : group.getNodes()) {
			int x = node.getOrigin().x, y = node.getOrigin().y;
			node.getOrigin().setLocation(x+deltaX, y+deltaY);
			
			for (Input input : ((UnitElement)node).getInputs()) {
				input.setLocked(false);
			}
			for (Output output : ((UnitElement)node).getOutputs()) {
				output.setLocked(false);
			}
		}
		
		
		
		units.addAll(group.getNodes());
		ConnectionList connections = units.getConnections();
		
		/*
		 * reconnect inputs
		 */
		for (Input input : group.getInputs()) {
			if(input instanceof ProxyInput) {
				ProxyInput pInput = (ProxyInput)input;
				if(pInput.isConnected()) {
					Output connectedOutput = pInput.getFromOutput();
					Input originalInput = pInput.getEmbeddedInput();
					
					Connection connection = new Connection(connectedOutput, originalInput);
					connections.add(connection);
				}
			}
		}
		
		/*
		 *  reconnect outputs
		 */
		Collection<Connection> tmpConn = new Vector<Connection>();
		for (Output output : group.getOutputs()) {
			if(output instanceof ProxyOutput) {
				ProxyOutput pOutput = (ProxyOutput)output;
				if(pOutput.isConnected()) {
					Output originalOutput = pOutput.getEmbeddedOutput();
					if(originalOutput.getDataType() instanceof DataTypeFactory.Image) {
						Image imageDataType = (DataTypeFactory.Image)originalOutput.getDataType();
						imageDataType.setParentUnitElement(originalOutput.getParent());
						imageDataType.setParentPin(originalOutput);
					}
					
					for (Connection	connection : pOutput.getConnections()) {
						Connection newConn = new Connection(originalOutput, connection.getInput());
						tmpConn.add(newConn);
					}
				}
			}
		}
		// write connections into actual connectionlist
		for (Connection connection : tmpConn) {
			connections.add(connection);	
		}
		
		/*
		 * reconnect connection within the group
		 */
		
		for (Connection connection : group.getInternalConnections()) {
			connections.add(connection);
		}
		
		units.remove(group);
	}

	public void group() throws Exception {
		if(!getSelections().isEmpty()) {
			GroupUnitElement group = new GroupUnitElement(new Point(34, 250), "Group");
			group.putUnits(getSelections(), getUnitElements());
			getUnitElements().add(group);
			selections.clear();
			selections.add(group);	
		}
	}
	
	/**
	 * Reads the contents of a flow-XML-file.
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public void read(File file) throws FileNotFoundException {
		WorkflowXMLBuilder workflowbuilder = new WorkflowXMLBuilder(nodes);
		workflowbuilder.read(file);
	}
	
	public void write(final File file) throws IOException {
		WorkflowXMLBuilder workflowbuilder = new WorkflowXMLBuilder(nodes);
		workflowbuilder.write(file);
	}
	

	public void setupExample1() {
		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////
		DelegatesController delegatesController = DelegatesController.getInstance();

		final UnitElement sourceUnit = delegatesController.getDelegate("Image Source").createUnit(new Point(30, 100));
		final UnitElement blurUnit = delegatesController.getDelegate("Gaussian Blur").createUnit(new Point(180, 50));
		final UnitElement mergeUnit = delegatesController.getDelegate("Image Calculator").createUnit(new Point(320, 100));
		final UnitElement noiseUnit = delegatesController.getDelegate("Add Noise").createUnit(new Point(450, 100));
		noiseUnit.setDisplay(true);
		
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
			read(new File("xml_flows/Example0_flow.xml"));
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
		fireUnit.setDisplay(true);

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

