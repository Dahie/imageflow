package de.danielsenff.imageflow.gui;


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import visualap.GPanel;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.Delegate;
import de.danielsenff.imageflow.models.NodeListener;
import de.danielsenff.imageflow.models.unit.CommentNode;
import de.danielsenff.imageflow.models.unit.ForGroupUnitElement;
import de.danielsenff.imageflow.models.unit.UnitDelegate;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * ContextMenü of the {@link GraphPanel}.
 * @author senff
 *
 */
public class InsertUnitMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GPanel activePanel;
	private static Point savedPoint = new Point(75, 75);

	/**
	 * 
	 * @param gpanel
	 * @param availableUnits
	 */
	public InsertUnitMenu(final GPanel gpanel, final Collection<Delegate> availableUnits) {
		this("Insert", gpanel, savedPoint);
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
			public void actionPerformed(final ActionEvent e) {
				final JMenuItem source = (JMenuItem)(e.getSource());
				final String action = source.getText();
				final ImageFlowView ifView = ((ImageFlowView)ImageFlow.getApplication().getMainView());
				if (action.equals("Comment")) {	
					final CommentNode node = new CommentNode(savedPoint, "text"); 
					node.addModelListener(new NodeListener(activePanel, ifView));
					savedPoint.translate(4, 4);
					activePanel.getNodeL().add(node);
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
						activePanel.getNodeL().add(node);
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

			final TreeModel tree = DelegatesController.getInstance().delegatesModel;

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
