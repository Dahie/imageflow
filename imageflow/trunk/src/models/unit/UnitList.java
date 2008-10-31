/**
 * 
 */
package models.unit;

import graph.GList;
import graph.Node;

import java.util.ArrayList;

import models.Input;

/**
 * List holding all {@link UnitElement}s. For the moment this is just an empty wrapper.
 * @author danielsenff
 *
 */
public class UnitList extends GList<Node> {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8204689428123811757L;

	/**
	 * Checks all Inputs if they are connected or not. 
	 * @param networkOK
	 * @return
	 */
	public boolean areAllInputsConnected() {
		for (final Object element : this) {
			final UnitElement unit = (UnitElement) element;
			
			if(unit.hasInputs()) {
				final ArrayList<Input> inputs = unit.getInputs();
				for (int i = 0; i < inputs.size(); i++) {
					final Input input = inputs.get(i);
					if (!input.isConnected() && input.isRequiredInput()) {
						System.out.println(input + " is not connected");
						return false;
					}
				}	
			}
		}
		return true;
	}

	/**
	 * Remove this Unit from the workflow.
	 * @param unitElement 
	 * @return 
	 */
	public boolean remove(final UnitElement unitElement) {
		
		// remove any connections
		
		// remove unit itself
		
		return super.remove(unitElement);
		
	}
	
}
