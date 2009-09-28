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
 * @author danielsenff
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