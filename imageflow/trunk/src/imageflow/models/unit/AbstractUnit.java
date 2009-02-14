package imageflow.models.unit;

import graph.NodeAbstract;
import imageflow.backend.Model;
import imageflow.backend.ModelListener;

import java.awt.Point;
import java.util.ArrayList;

public abstract class AbstractUnit extends NodeAbstract implements Model {


	/**
	 * number of units instantiated, incremented with each new object
	 */
	static int ids;

	/**
	 * the id of this unit
	 */
	protected int unitID;
	
	private ArrayList<ModelListener> listeners;


	public AbstractUnit(Point origin, Object object) {
		super(origin, object);

		ids++;
		this.unitID = ids;
		
		this.listeners = new ArrayList<ModelListener>();
	}


	/**
	 * Returns the ID of this Unit.
	 * @return
	 */
	public int getUnitID() {
		return this.unitID;
	}


	/*
	 * (non-Javadoc)
	 * @see backend.Model#addModelListener(backend.ModelListener)
	 */
	public void addModelListener(ModelListener listener) {
		if (! this.listeners.contains(listener)) {
			this.listeners.add(listener);
			notifyModelListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see backend.Model#notifyModelListener(backend.ModelListener)
	 */
	public void notifyModelListener(ModelListener listener) {
		listener.modelChanged(this);
	}

	/*
	 * (non-Javadoc)
	 * @see backend.Model#notifyModelListeners()
	 */
	public void notifyModelListeners() {
		for (final ModelListener listener : this.listeners) {
			notifyModelListener(listener);
		}
		setChanged(true);
	}

	/*
	 * (non-Javadoc)
	 * @see backend.Model#removeModelListener(backend.ModelListener)
	 */
	public void removeModelListener(ModelListener listener) {
		this.listeners.remove(listener);
	}
	
	@Override
	public void setOrigin(Point aOrigin) {
		super.setOrigin(aOrigin);
		notifyModelListeners();
	}
	
	@Override
	public void setLabel(String label) {
		super.setLabel(label);
		notifyModelListeners();
	}
}
