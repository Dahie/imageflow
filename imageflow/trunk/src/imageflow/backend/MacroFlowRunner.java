package imageflow.backend;

import graph.Node;
import imageflow.models.Connection;
import imageflow.models.ConnectionList;
import imageflow.models.Input;
import imageflow.models.parameter.Parameter;
import imageflow.models.parameter.StringParameter;
import imageflow.models.unit.CommentNode;
import imageflow.models.unit.SourceUnitElement;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitList;
import imageflow.models.unit.UnitElement.Type;

import java.io.File;
import java.util.Iterator;

/**
 * MacroRunner takes the current workflow and processes it, so that a clean Macro can be 
 * created. Since for example unconnected units shall be discarded by the {@link MacroGenerator}
 * but not deleted, this creates a temporary {@link UnitList} with only the 
 * units and connections valid and usefull without destroying the original workflow.
 * @author danielsenff
 *
 */
public class MacroFlowRunner {

	UnitList macroUnitList;
	
	public MacroFlowRunner(UnitList units) {
		this.macroUnitList = sortList((UnitList) units.clone());
	}
	
	/**
	 * verification and generation of the ImageJ macro for the full graph
	 * @return 
	 */
	public String generateMacro() {
		////////////////////////////////////////////////////////
		// analysis and 
		// verification of the connection network
		////////////////////////////////////////////////////////

		if (!checkNetwork()) {
			System.out.println("Error in node network.");
			return null;
		}

		// unitElements has to be ordered according to the correct processing sequence
		

		////////////////////////////////////////////////////////
		// generation of the ImageJ macro
		////////////////////////////////////////////////////////

		MacroGenerator generator = new MacroGenerator();
		final String macro = generator.generateMacrofromUnitList(macroUnitList);

		return macro;
	}
	
	/**
	 * Is true if a {@link UnitElement} exists in the {@link UnitList} that is cleaned for
	 * running as a macro. If the unit exists, it means, that the subgraph up untill this 
	 * unit is valid for running too.  
	 * @param unit 
	 * @return 
	 */
	public boolean contains(UnitElement unit) {
		return this.macroUnitList.contains(unit);
	}
	
	
	/**
	 * Returns a subset of a workflow. It only includes all elements to 
	 * calculate the final {@link UnitElement} specified.
	 * @param endUnit 
	 * @return
	 */
	public UnitList getSubUnitList(UnitElement endUnit) {
		
		UnitList subgraph = new UnitList();
		subgraph.add(endUnit);
		
		traverseGraph(endUnit, subgraph);
		
		return subgraph;
	}
	
	public MacroFlowRunner getSubMacroFlowRunner(UnitElement endUnit) {
		return new MacroFlowRunner(getSubUnitList(endUnit));
	}

	private void traverseGraph(UnitElement unit, UnitList subgraph) {
		for (Input input : unit.getInputs()) {
			UnitElement parent = input.getParent();
			if(!subgraph.contains(parent)) {
				subgraph.add(parent);
				traverseGraph(parent, subgraph);
			}
		}
	}
	
	
	/**
	 * check if all connections have in and output
	 * @param connectionMap
	 * @return
	 */
	public boolean checkNetwork() {

		if(!macroUnitList.hasUnitAsDisplay()) {
			System.err.println("The flow has no displayable units, running it doesn't do anything.");
			return false; 
		}

		
		// sources -> file exists
		for (Node node : macroUnitList) {
			if(node instanceof SourceUnitElement) {
				UnitElement unit = (UnitElement) node;
				for (Parameter parameter : unit.getParameters()) {
					if(parameter instanceof StringParameter) {
						File file = new File((String) parameter.getValue());
						return file.exists();
					}	
				}
			}
		}





		ConnectionList connections = macroUnitList.getConnections();
		if(connections.size() > 0) {
			System.out.println("Number of connections: "+ connections.size());
			for (Iterator iterator = connections.iterator(); iterator.hasNext();) {
				Connection connection = (Connection) iterator.next();

				if (!connection.areImageBitDepthCompatible()) {
					System.err.println("Faulty connection, image type not compatible");
					return false;
				}
					

				switch(connection.checkConnection()) {
				case MISSING_BOTH:
				case MISSING_FROM_UNIT:
				case MISSING_TO_UNIT:
					System.err.println("Faulty connection, no input or output unit found.");
					System.err.println(connection.toString() 
							+ " with status " + connection.checkConnection());
					return false;				
				}
			}
		} else if (macroUnitList.hasSourcesAsDisplay()) {
			// ok, we got no connections, but we have Source-units, 
			// which are set to display.

			//do nothing
		} else {
			System.err.println("no existing connections");
			return false;
		}


		//FIXME check if units got all the inputs they need
		if (!macroUnitList.areAllInputsConnected()) {
			System.err.println("not all required inputs are connected");
			return false;
		}


		//TODO check parameters
		return true;
	}
	
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
			// TODO I don't like this condition
			while(!unitElements.isEmpty()) {
				index = i % unitElements.size();
				Node node =  unitElements.get(index); 

				// find out what kind of node is stored
				if(node instanceof CommentNode) {
					//if comment then remove and ignore, we don't need it
					unitElements.remove(index);
				} else if (node instanceof UnitElement) {
					UnitElement unit = (UnitElement) node;
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

			// TODO replacing here causes deletion of none-used nodes 
			/*for (Node node : orderedList) {
				unitElements.add(node);
			}*/
		} catch(Exception ex) {
			// restore list, without damaging it
		}

//		return unitElements;
		return orderedList;
	}
	
}
