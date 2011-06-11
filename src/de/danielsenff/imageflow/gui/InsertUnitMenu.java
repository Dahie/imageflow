/**
 * Copyright (C) 2008-2011 Daniel Senff
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


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import visualap.GPanel;
import visualap.Node;
import visualap.Selection;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.controller.GraphControllerManager;
import de.danielsenff.imageflow.models.NodeListener;
import de.danielsenff.imageflow.models.delegates.UnitDelegate;
import de.danielsenff.imageflow.models.unit.CommentNode;
import de.danielsenff.imageflow.models.unit.UnitList;

/**
 * ContextMenu of the {@link GraphPanel}.
 * @author Daniel Senff
 *
 */
public class InsertUnitMenu extends JMenu implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GPanel graphPanel;
	private UnitList units;
	private Point location;

	/**
	 * 
	 * @param gpanel
	 */
	public InsertUnitMenu(final GPanel gpanel) {
		this("Insert", gpanel, new Point(75, 75));
	}

	/**
	 * 
	 * @param gpanel
	 * @param graphController
	 * @param location
	 */
	public InsertUnitMenu(final GPanel gpanel, Point location) {
		this("Insert", gpanel, location);
	}
	
	/**
	 * 
	 * @param name
	 * @param gpanel
	 * @param availableUnits
	 * @param location
	 */
	private InsertUnitMenu(final String name, 
			final GPanel gpanel, 
			final Point location) {
		this.setName(name);
		this.setText(name);
		this.graphPanel = gpanel;
		this.location = location;
		this.units = graphPanel.getNodeL();

		final JMenuItem mi = new JMenuItem("Comment");
		mi.setToolTipText("Insert Notes or Comments to the graph.");
		add(mi).addActionListener(this);		

		final TreeModel tree = DelegatesController.getInstance().delegatesTreeModel;
		createMenu(this, (MutableTreeNode) tree.getRoot());
	}

	private void createMenu(final JMenu menu, final MutableTreeNode root) {
		JMenuItem mi;
		for (int i = 0; i < root.getChildCount(); i++) {
			final MutableTreeNode node = (MutableTreeNode) root.getChildAt(i);
			if(node instanceof UnitDelegate) {
				final UnitDelegate unitDelegate = (UnitDelegate)node;
				mi = new JMenuItem(unitDelegate.getName());
				mi.setToolTipText(unitDelegate.getToolTipText());
				menu.add(mi).addActionListener(this);
			} else {
				final JMenu subMenu = new JMenu(node.toString());
				createMenu(subMenu, node);
				menu.add(subMenu);
			}
		}
	}
	
	public GraphController getGraphController() {
		return GraphControllerManager.getInstance().getController();
	}

	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem)(event.getSource());
		final String action = source.getText();
		final ImageFlowView ifView = ((ImageFlowView)ImageFlow.getApplication().getMainView());
		Selection<Node> selections = graphPanel.getSelection();

		/* TODO don't hardcode this ... */
		if (action.equals("Comment")) {	
			final CommentNode node = new CommentNode(new Point(location.x, location.y), "Newly added comment"); 
			node.addModelListener(new NodeListener(graphPanel, ifView));
			location.translate(4, 4);
			units.add(node);
			selections.clear();
			selections.add(node);
			graphPanel.repaint();
			return;
		}

		UnitDelegate unitDelegate = DelegatesController.getInstance().getDelegate(action);
		if(unitDelegate != null) {
			try {
				Node node = getGraphController().addNode(unitDelegate, location);
				selections.clear();
				selections.add(node);
				graphPanel.repaint();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}

		return;
	}

}
