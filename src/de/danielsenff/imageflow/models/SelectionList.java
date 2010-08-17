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
package de.danielsenff.imageflow.models;

import visualap.Selection;

import java.util.ArrayList;
import java.util.Collection;

import visualap.Node;

/**
 * @author danielsenff
 *
 */
public class SelectionList extends Selection<Node> 
	implements Selectable 
{

	private final ArrayList<SelectionListener> listeners;
	
	/**
	 * 
	 */
	public SelectionList() {
		this.listeners = new ArrayList<SelectionListener>();
	}
	
	/*
	 * Override methods to add listener
	 */
	
	@Override
	public boolean add(Node element) {
		boolean add = super.add(element);
		this.notifySelectionListeners();
		return add;
	}
	
	@Override
	public void add(int index, Node element) {
		super.add(index, element);
		this.notifySelectionListeners();
	}
	
	@Override
	public boolean addAll(Collection<? extends Node> c) {
		boolean addAll = super.addAll(c);
		this.notifySelectionListeners();
		return addAll;
	}
	
	@Override
	public boolean remove(Object o) {
		boolean remove = super.remove(o);
		this.notifySelectionListeners();
		return remove;
	}
	
	@Override
	public Node remove(int index) {
		Node remove = super.remove(index);
		this.notifySelectionListeners();
		return remove;
	}
	
	@Override
	public void clear() {
		super.clear();
		this.notifySelectionListeners();
	}
	
	/*
	 * Listener registration methods
	 */
	
	
	public void addSelectionListener(final SelectionListener listener) {
		if (! this.listeners.contains(listener)) {
			this.listeners.add(listener);
			notifySelectionListener(listener);
		}
	}

	public void notifySelectionListener(final SelectionListener listener) {
		listener.selectionChanged(this);
	}

	public void notifySelectionListeners() {
		for (final SelectionListener listener : this.listeners) {
			notifySelectionListener(listener);
		}
	}

	public void removeSelectionListener(final SelectionListener listener) {
		this.listeners.remove(listener);
	}

	public boolean isSelected() {
		return !this.isEmpty();
	}

	public void setSelected(boolean sel) {
		// FIXME empty, actually senseless
	}

}
