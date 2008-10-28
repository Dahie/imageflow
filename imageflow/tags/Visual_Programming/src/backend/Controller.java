package backend;
import ij.IJ;
import ij.ImageJ;

import java.util.ArrayList;
import java.util.HashMap;

import macro.MacroGenerator;

import Models.Connection;
import Models.Input;
import Models.unit.UnitElement;
import Models.unit.UnitFactory;
import Models.unit.UnitList;



public class Controller {

	
	private UnitList unitElements;
	private HashMap<Integer,Connection> connectionMap;

	/**
	 * 
	 */
	public Controller() {

		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////
		
		unitElements = new UnitList();
		unitElements.add(null);
		
		UnitElement sourceUnit = UnitFactory.createSourceUnit();
		unitElements.add(sourceUnit);

		UnitElement blurUnit = UnitFactory.createGaussianBlurUnit();
//		unitElements.add(blurUnit);
		
		UnitElement mergeUnit = UnitFactory.createImageCalculatorUnit();
//		unitElements.add(mergeUnit);
		
		

		
		////////////////////////////////////////////////////////
		// setup the connections
		////////////////////////////////////////////////////////
		
		connectionMap = new HashMap<Integer,Connection>();
		
		// add six connections
		// fromUnitNumber, fromOutputNumber, toUnitNumber, toInputNumber
		Connection con;
		con = new Connection(sourceUnit,1,blurUnit,1);
		connectionMap.put(con.id, con);
		con = new Connection(blurUnit,1,mergeUnit,1);
		connectionMap.put(con.id, con);
		con = new Connection(sourceUnit,1,mergeUnit,2);
		connectionMap.put(con.id, con);

		
		// remove one connection
		//connectionMap.remove( Connection.getID(2,1,5,1) );
		
		
		// apply the connections
		for (Connection connection : connectionMap.values()) {
			connection.connect(unitElements);
		}
		
	}

	/**
	 * verfication and generation of the ImageJ macro
	 */
	public void generateMacro() {
		////////////////////////////////////////////////////////
		// analysis and 
		// verification of the connection network
		////////////////////////////////////////////////////////
		
		boolean networkOK = checkNetwork(connectionMap);
		
		
		////////////////////////////////////////////////////////
		// generation of the ImageJ macro
		////////////////////////////////////////////////////////
		
		// unitElements has to be ordered according to the correct processing sequence
		if(networkOK) {
			String macro = MacroGenerator.generateMacrofromUnitList(unitElements);
			
			new ImageJ();
			IJ.log(macro);
			IJ.runMacro(macro, "");
		} else {
			System.out.println("Error in node network.");
		}
	}
	

	private boolean checkNetwork(HashMap<Integer, Connection> connectionMap) {
		boolean networkOK = true;
		
		//TODO check if all connections have in and output
		
		for (Connection connection : connectionMap.values()) {
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
		
		//TODO check if units got all the inputs they need
		for (Object element : unitElements) {
			UnitElement unit = (UnitElement) element;
			
			if (unit != null) { // just to avoid the first null element, which is kinda legacy
				if(unit.hasInputs()) {
					ArrayList<Input> inputs = unit.getInputs();
					for (int i = 0; i < inputs.size(); i++) {
						Input input = inputs.get(i);
						if(!input.isConnected() && input.isRequiredInput()) {
							networkOK = false;
						}
					}	
				}
			}
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

}
