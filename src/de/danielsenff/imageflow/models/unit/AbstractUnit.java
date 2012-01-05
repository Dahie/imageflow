/**
 * Copyright (C) 2008-2010 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import de.danielsenff.imageflow.models.Model;
import de.danielsenff.imageflow.models.ModelListener;

/**
 * Commonly shared methods of Units.
 * @author dahie
 *
 */
public abstract class AbstractUnit extends NodeAbstract implements Model {


	
	private ArrayList<ModelListener> listeners;

	/**
	 * @param origin
	 * @param object
	 */
	public AbstractUnit(final Point origin, final Object object) {
		super(origin, object);

		this.listeners = new ArrayList<ModelListener>();
	}


	protected Object cloneNonClonableObject(Object obj) throws CloneNotSupportedException {
		Object clobj;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.close();
			clobj = (new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))).readObject();
		} catch (Exception ex) {
			throw new CloneNotSupportedException(ex.getMessage());
		}
		return clobj;
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
	
	public boolean isSelected() {
		return this.selected;
	}
}
