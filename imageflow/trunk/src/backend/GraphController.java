package backend;
import graph.Edges;
import graph.Node;
import ij.IJ;
import ij.ImageJ;
import ij.plugin.Macro_Runner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import macro.MacroGenerator;
import models.Connection;
import models.ConnectionList;
import models.unit.UnitElement;
import models.unit.UnitFactory;
import models.unit.UnitList;
import models.unit.UnitElement.Type;
import application.ApplicationController;



/**
 * @author danielsenff
 *
 */
public class GraphController extends ApplicationController {

	private ApplicationController controller;
	
	private UnitList unitElements;
	private final ConnectionList connectionMap;
	private ImageJ imagej;
	/**
	 * List which stores copied Nodes.
	 */
	protected ArrayList<Node> copyNodesList;

	
	
	/**
	 * 
	 */
	public GraphController() {

		this.unitElements = new UnitList();
		this.connectionMap = new ConnectionList();
		this.copyNodesList = new ArrayList<Node>();
	}

	public void setupExample() {

		/*
		 * Wurzelbaum
		 */
		
		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////
		
		
//		unitElements.add(null);
		
		final UnitElement sourceUnit = UnitFactory.createSourceUnit("/Users/danielsenff/zange1.png");

		final UnitElement blurUnit = UnitFactory.createGaussianBlurUnit(new Point(150, 150));
		
		final UnitElement mergeUnit = UnitFactory.createImageCalculatorUnit(new Point(320, 30));
		
		final UnitElement noiseUnit = UnitFactory.createAddNoiseUnit(new Point(450, 30));
		noiseUnit.setDisplayUnit(true);
		
		
		// some mixing, so they are not in order
		unitElements.add(noiseUnit);
		unitElements.add(blurUnit);
		unitElements.add(sourceUnit);
		unitElements.add(mergeUnit);
		
		
		////////////////////////////////////////////////////////
		// setup the connections
		////////////////////////////////////////////////////////
		
		
		
		// add six connections
		// the conn is established on adding
		// fromUnit, fromOutputNumber, toUnit, toInputNumber
		Connection con;
		con = new Connection(sourceUnit,1,blurUnit,1);
		connectionMap.add(con);
		con = new Connection(blurUnit,1,mergeUnit,1);
		connectionMap.add(con);
		con = new Connection(sourceUnit,1,mergeUnit,2);
		connectionMap.add(con);
		con = new Connection(mergeUnit,1,noiseUnit,1);
		connectionMap.add(con);
		
		// remove one connection
		//connectionMap.remove( Connection.getID(2,1,5,1) );
		
		
	}
	

	/**
	 * verification and generation of the ImageJ macro
	 */
	public void generateMacro() {
		////////////////////////////////////////////////////////
		// analysis and 
		// verification of the connection network
		////////////////////////////////////////////////////////
		
		final boolean networkOK = checkNetwork(connectionMap);
		if (!networkOK) {
			return;
		}
		
		unitElements = sortList(unitElements);
		
		
		
		/*try {
			System.out.println(new Check(unitElements, connectionMap).checkSystem());
		} catch (CheckException e) {
			e.printStackTrace();
		}*/
		
		////////////////////////////////////////////////////////
		// generation of the ImageJ macro
		////////////////////////////////////////////////////////
		
		// unitElements has to be ordered according to the correct processing sequence
		if(networkOK) {
			final String macro = MacroGenerator.generateMacrofromUnitList(unitElements);
			
			if(imagej == null)
				imagej = new ImageJ(null, ImageJ.EMBEDDED);
			IJ.log(macro);
			IJ.runMacro(macro, "");
//			runMacro(macro, "");
		} else {
			System.out.println("Error in node network.");
		}
	}

	public static String runMacro(String macro, String arg) {
		Macro_Runner mr = new Macro_Runner();
		return mr.runMacroFromIJJar(macro, arg);
	}
	
	
	/**
	 * check if all connections have in and output
	 * @param connectionMap
	 * @return
	 */
	public boolean checkNetwork(final ConnectionList connectionMap) {
		boolean networkOK = true;
		

		
//		for (final Connection (Connection)connection : connectionMap) {
		if(connectionMap.size() > 0) {
			for (Iterator iterator = connectionMap.iterator(); iterator.hasNext();) {
				Connection connection = (Connection) iterator.next();
			
				/*if (!connection.areImageBitDepthCompatible())
					return false;*/
				
				switch(connection.checkConnection()) {
					case MISSING_BOTH:
					case MISSING_FROM_UNIT:
					case MISSING_TO_UNIT:
						networkOK = false;
						System.out.println(connection.checkConnection());
						System.out.println(connection.toString());
						break;					
				}
			}
		} else {
			System.out.println("no existing connections ");
			return false;
		}
		
		
		//FIXME check if units got all the inputs they need
		if (!unitElements.areAllInputsConnected()) {
			System.out.println("not all required inputs are connected");
			return false;
		}
			

		//TODO check parameters
		return networkOK;
	}

	/**
	 * @return the unitElements
	 */
	public UnitList getUnitElements() {
		return this.unitElements;
	}
	
	public static void main(final String[] args) {
		final GraphController controller = new GraphController();
		controller.setupExample();
		controller.generateMacro();
	}

	/**
	 * @return
	 */
	public Edges getConnections() {
		return this.connectionMap;
	}
	
	
	public static UnitList sortList(UnitList unitElements) {
		
		// temporary list, discarded after this method call
		UnitList orderedList = new UnitList();
		
		// reset all marks
		unmarkUnits(unitElements);
		
		int mark = 0;
		int i = 0;
		int index = 0;
		//loop over all units, selection sort, levelorder
		while(!unitElements.isEmpty()) {
			index = i % unitElements.size();
			UnitElement unit = (UnitElement) unitElements.get(index); 
			
			// check if all inputs of this node are marked
			// if so, this unit is moved from the old list to the new one
			if(unit.hasAllInputsMarked()) {
				mark++;	
				
				// increment mark
				// mark outputs
				unit.setMark(mark);
				
				// remove from the old list and
				// move this to the new ordered list
				Node remove = unitElements.remove(index);
				
				// if unit has no inputs, is not source, then discard as well
//				if((unit.getType() != Type.SOURCE && !unit.hasInputs())) 
					orderedList.add(remove);
			} else if (!unit.hasInputsConnected() && unit.getType() != Type.SOURCE) {
				// if unit has no connections actually, it can be discarded right away
				unitElements.remove(index);
				// TODO if there is a branch with two units connected, the first one will be discarded, 
				// the second will still exist 
			}
			
			// Selection Sort
			// each time an element whose previous nodes have already been registered
			// is found the next loop over the element list is one element shorter.
			// thereby having O(n^2) :/ maybe this can be done better later
			i++;
		}
		
		for (Node node : orderedList) {
			unitElements.add(node);
		}
		
		return unitElements;
	}

	/**
	 * Resets all marks to zero.
	 * @param units
	 */
	public static void unmarkUnits(UnitList units) {
		
		for (Node node : units) {
			UnitElement unit = (UnitElement) node;
			unit.setMark(0);
		}
		
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
	public boolean removeUnit(final UnitElement unit) {

		// find connections which are attached to this unit
		
		for (int i = 0; i < connectionMap.size(); i++) {
			Connection connection = (Connection) connectionMap.get(i);
			if(connection.isConnectedToUnit(unit)) {
				// delete connections
				connectionMap.remove(connection);
				i--;
			}
			
		}
		
		
		// delete Unit
		return unitElements.remove(unit);
	}
	
}
