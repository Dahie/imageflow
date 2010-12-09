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

import java.util.Collection;
import java.util.HashSet;

import visualap.Node;
import visualap.Selection;


/**
 * SelectionList is a data container storing Selectable elements of the workflow.
 * @author danielsenff
 *
 */
public class SelectionList extends Selection<Node> {

	private final HashSet<SelectionListListener> listeners;
	
	/**
	 * 
	 */
	public SelectionList() {
		this.listeners = new HashSet<SelectionListListener>();
	}
	
	/*
	 * Override methods to add listener
	 */
	
	@Override
	public boolean add(final Node element) {
		final boolean add = super.add(element);
		this.notifySelectionListListeners();
		return add;
	}
	
	@Override
	public void add(final int index, final Node element) {
		super.add(index, element);
		this.notifySelectionListListeners();
	}
	
	@Override
	public boolean addAll(final Collection<? extends Node> c) {
		final boolean addAll = super.addAll(c);
		this.notifySelectionListListeners();
		return addAll;
	}
	
	@Override
	public boolean remove(final Object o) {
		final boolean remove = super.remove(o);
		this.notifySelectionListListeners();
		return remove;
	}
	
	@Override
	public Node remove(final int index) {
		final Node remove = super.remove(index);
		this.notifySelectionListListeners();
		return remove;
	}
	
	@Override
	public void clear() {
		super.clear();
		this.notifySelectionListListeners();
	}
	
	/*
	 * Listener registration methods
	 */
	
	/**
	 * Add the given a {@link SelectionListListener} to this SelectionList.
	 * @param listener 
	 */
	public void addSelectionListListener(final SelectionListListener listener) {
		if (! this.listeners.contains(listener)) {
			this.listeners.add(listener);
			notifySelectionListListener(listener);
		}
	}

	/**
	 * Notify the given {@link SelectionListListener} that selections have changed.
	 * @param listener
	 */
	public void notifySelectionListListener(final SelectionListListener listener) {
		listener.selectionChanged(this);
	}

	/**
	 * Notify the all {@link SelectionListListener}s that selections have changed.
	 */
	public void notifySelectionListListeners() {
		for (final SelectionListListener listener : this.listeners) {
			notifySelectionListListener(listener);
		}
	}

	/**
	 * Remove the given a {@link SelectionListListener} to this SelectionList.
	 * @param listener
	 * @return 
	 */
	public boolean removeSelectionListener(final ChangeListener listener) {
		return this.listeners.remove(listener);
	}

	/**
	 * This SelectionList contains Selectable items.
	 * @return
	 */
	public boolean hasSelections() {
		return !this.isEmpty();
	}
	
	/**
	 * SelectionListener reports changes in the {@link SelectionList}
	 * @author Daniel Senff
	 *
	 */
	public interface SelectionListListener {
		/**
		 * Called when selections in the associated SelectionList change.  
		 * @param list
		 */
		public void selectionChanged(SelectionList list);
	}
}
