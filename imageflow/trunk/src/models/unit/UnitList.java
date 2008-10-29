/**
 * 
 */
package models.unit;

import java.util.ArrayList;

import models.Input;
import graph.GList;
import graph.Node;

/**
 * List holding all {@link UnitElement}s. For the moment this is just an empty wrapper.
 * @author danielsenff
 *
 */
public class UnitList extends GList<Node> {

	

	/**
	 * Checks all Inputs if they are connected or not. 
	 * @param networkOK
	 * @return
	 */
	public boolean areAllInputsConnected() {
		for (final Object element : this) {
			final UnitElement unit = (UnitElement) element;
			
//			if (unit != null) { // just to avoid the first null element, which is kinda legacy
				if(unit.hasInputs()) {
					final ArrayList<Input> inputs = unit.getInputs();
					for (int i = 0; i < inputs.size(); i++) {
						final Input input = inputs.get(i);
						if(!input.isConnected() && input.isRequiredInput()) {
							return false;
						}
					}	
//				}
			}
		}
		return true;
	}

	
}
