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

import visualap.GPanel;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.gui.GraphPanel;
import de.danielsenff.imageflow.models.unit.Node;

/**
 * NodeModelListener is an implementation of {@link ModelListener}
 * It serves to update the {@link GraphPanel} in case the {@link Node} changes. 
 * @author senff
 *
 */
public class NodeListener implements ModelListener {

	private GPanel graphPanel;
	private ImageFlowView ifView;
	
	/**
	 * @param graphPanel
	 * @param ifView
	 */
	public NodeListener(final GPanel graphPanel, final ImageFlowView ifView) {
		this.graphPanel = graphPanel;
		this.ifView = ifView;
	}

	public void modelChanged(Model model) {
		graphPanel.invalidate();
		graphPanel.repaint();
		ifView.setModified(true);
	}

}
