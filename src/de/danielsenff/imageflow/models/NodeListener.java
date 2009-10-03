package de.danielsenff.imageflow.models;

import visualap.GPanel;
import visualap.Node;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.gui.GraphPanel;

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
