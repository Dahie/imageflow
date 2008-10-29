package backend;
import graph.Edges;
import graph.Node;
import ij.IJ;
import ij.ImageJ;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import macro.MacroGenerator;
import models.Connection;
import models.ConnectionList;
import models.Input;
import models.unit.UnitElement;
import models.unit.UnitFactory;
import models.unit.UnitList;



/**
 * @author danielsenff
 *
 */
public class GraphController {

	
	
	private UnitList unitElements;
	private final ConnectionList connectionMap;

	/**
	 * 
	 */
	public GraphController() {

		/*
		 * Wurzelbaum
		 */
		
		
		
		
		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////
		
		unitElements = new UnitList();
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
		
		connectionMap = new ConnectionList();
		
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
		
		unitElements = sortList(unitElements);
		
		final boolean networkOK = checkNetwork(connectionMap);
		
		/*try {
			System.out.println(new Check(unitElements, connectionMap).checkSystem());
		} catch (CheckException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		////////////////////////////////////////////////////////
		// generation of the ImageJ macro
		////////////////////////////////////////////////////////
		
		// unitElements has to be ordered according to the correct processing sequence
		if(networkOK) {
			final String macro = MacroGenerator.generateMacrofromUnitList(unitElements);
			
			new ImageJ();
			IJ.log(macro);
			IJ.runMacro(macro, "");
		} else {
			System.out.println("Error in node network.");
		}
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
			networkOK = false;
		}
		
		
		//FIXME check if units got all the inputs they need
		networkOK = unitElements.areAllInputsConnected();

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
		controller.generateMacro();
	}

	/**
	 * @return
	 */
	public Edges getConnections() {
		return this.connectionMap;
	}
	
	
	public static UnitList sortList(UnitList unitElements) {
		
		UnitList orderedList = new UnitList();
		int sortedElementsCount = 0;
		int mark = 1;
		
		//TODO reset all marks
		
		int i = 0;
		int index = 0;
		//loop over all units, selection sort, levelorder
		//FIXME this breaks, if connections are missing!
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
				orderedList.add(unitElements.remove(index));
			}
			
			// Selection Sort
			// each time an element whose previous nodes have already been registered
			// is found the next loop over the element list is one element shorter.
			// thereby having O(n^2) :/ maybe this can be done better later
			i++;
		}
		return orderedList;
	}



}
