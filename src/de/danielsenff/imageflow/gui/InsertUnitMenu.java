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


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import visualap.GPanel;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.NodeListener;
import de.danielsenff.imageflow.models.delegates.UnitDelegate;
import de.danielsenff.imageflow.models.unit.CommentNode;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;

/**
 * ContextMenu of the {@link GraphPanel}.
 * @author Daniel Senff
 *
 */
public class InsertUnitMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GPanel activePanel;

	/**
	 * 
	 * @param gpanel
	 */
	public InsertUnitMenu(final GPanel gpanel) {
		this("Insert", gpanel, new Point(75, 75));
	}

	/**
	 * 
	 * @param name
	 * @param gpanel
	 * @param availableUnits
	 * @param savedPoint
	 */
	public InsertUnitMenu(final String name, 
			final GPanel gpanel, 
			final Point savedPoint) {
		this.setName(name);
		this.setText(name);
		this.activePanel = gpanel;
		final ActionListener newAction = new ActionListener() {
			UnitList units = activePanel.getNodeL();
			public void actionPerformed(final ActionEvent e) {
				final JMenuItem source = (JMenuItem)(e.getSource());
				final String action = source.getText();
				final ImageFlowView ifView = ((ImageFlowView)ImageFlow.getApplication().getMainView());
				if (action.equals("Comment")) {	
					final CommentNode node = new CommentNode(new Point(savedPoint.x, savedPoint.y), "Newly added comment"); 
					node.addModelListener(new NodeListener(activePanel, ifView));
					savedPoint.translate(4, 4);
					units.add(node);
					activePanel.getSelection().clear();
					activePanel.getSelection().add(node);
					activePanel.repaint();
					return;
				}

				UnitDelegate unitDelegate = DelegatesController.getInstance().getDelegate(action);
				if(unitDelegate != null) {
					try {
						final UnitElement node = unitDelegate.createUnit(savedPoint);
						node.addModelListener(new NodeListener(activePanel, ifView));
						units.add(node);
						activePanel.getSelection().clear();
						activePanel.getSelection().add(node);
						activePanel.repaint();
					} catch (final Exception ex) {
					}
				}

				return;
			}}; 
			final JMenuItem mi = new JMenuItem("Comment");
			mi.setToolTipText("Insert Notes or Comments to the graph.");
			add(mi).addActionListener(newAction);		

			final TreeModel tree = DelegatesController.getInstance().delegatesTreeModel;

			final MutableTreeNode root =  (MutableTreeNode) tree.getRoot();
			createMenu(this, newAction, root);
	}

	private void createMenu(final JMenu menu, final ActionListener newAction, final MutableTreeNode root) {
		JMenuItem mi;
		for (int i = 0; i < root.getChildCount(); i++) {
			final MutableTreeNode node = (MutableTreeNode) root.getChildAt(i);
			if(node instanceof UnitDelegate) {
				final UnitDelegate unitDelegate = (UnitDelegate)node;
				mi = new JMenuItem(unitDelegate.getName());
				mi.setToolTipText(unitDelegate.getToolTipText());
				menu.add(mi).addActionListener(newAction);
			} else {
				final JMenu subMenu = new JMenu(node.toString());
				createMenu(subMenu, newAction, node);
				menu.add(subMenu);
			}
		}
	}

}
