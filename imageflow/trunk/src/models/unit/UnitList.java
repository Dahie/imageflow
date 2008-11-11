/**
 * 
 */
package models.unit;

import graph.GList;
import graph.Node;

import java.util.ArrayList;

import models.Input;
import backend.Model;
import backend.ModelListener;

/**
 * List holding all {@link UnitElement}s. For the moment this is just an empty wrapper.
 * @author danielsenff
 *
 */
public class UnitList extends GList<Node> implements Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8204689428123811757L;
	private ArrayList<ModelListener> listeners;

	public UnitList() {
		this.listeners = new ArrayList<ModelListener>();
	}
	
	@Override
	public boolean add(Node node, String label) {
		notifyModelListeners();
		return super.add(node, label);
	}
	
	@Override
	public boolean add(Node o) {
		notifyModelListeners();
		return super.add(o);
	}
	
	@Override
	public boolean remove(Node node) {
		notifyModelListeners();
		return super.remove(node);
	}
	
	@Override
	public Node remove(int index) {
		notifyModelListeners();
		return super.remove(index);
	}
	
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
					if (!input.isConnected() 
							// is this input actually required?
							&& input.isRequiredInput()) {
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
		notifyModelListeners();
		return super.remove(unitElement);
	}


	public void addModelListener(ModelListener listener) {
		if (! this.listeners.contains(listener)) {
			this.listeners.add(listener);
			notifyModelListener(listener);
		}
	}

	public void notifyModelListener(ModelListener listener) {
		listener.modelChanged(this);
	}

	public void notifyModelListeners() {
		for (final ModelListener listener : this.listeners) {
			notifyModelListener(listener);
		}
	}

	public void removeModelListener(ModelListener listener) {
		this.listeners.remove(listener);
	}
	
}
