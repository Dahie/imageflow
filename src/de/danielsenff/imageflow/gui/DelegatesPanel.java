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
package de.danielsenff.imageflow.gui;


import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.unit.UnitList;


/**
 * Panel which displays the units that can be inserted into the workflow.
 * @author Daniel Senff
 *
 */
public class DelegatesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JTree delegatesTree;

	/**
	 * @param unitList
	 */
	public DelegatesPanel(final UnitList unitList) {
		final DelegatesController delegatesController = DelegatesController.getInstance();
		final DefaultTreeModel delegatesModel = delegatesController.getDelegatesModel();

		delegatesTree = new JTree(delegatesModel);
		delegatesTree.setRootVisible(false);
		delegatesTree.setToggleClickCount(1);
		delegatesTree.setCellRenderer(new IFTreeCellRenderer());
		// makes delegatesPanel as big as the sidePane when resized
		this.setLayout(new BorderLayout());
		final JScrollPane scrollPane = new JScrollPane(delegatesTree);
		add(scrollPane, BorderLayout.CENTER);

	}

	/**
	 * @return the delegatesTree
	 */
	public JTree getDelegatesTree() {
		return delegatesTree;
	}

}