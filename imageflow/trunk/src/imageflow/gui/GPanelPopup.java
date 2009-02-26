/**
 * 
 */
package imageflow.gui;

import graph.Node;
import graph.Selection;
import imageflow.ImageFlow;
import imageflow.ImageFlowView;
import imageflow.backend.GraphController;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPopupMenu;

import visualap.Delegate;
import visualap.GPanel;
import visualap.GPanelListener;

/**
 * @author danielsenff
 *
 */
public class GPanelPopup implements GPanelListener {

	protected GPanel activePanel;
	protected Point savedPoint = new Point(0,0);
	protected ArrayList<Node> copyL;
	protected Collection<Delegate> availableUnits;
	private GraphController graphController;



	/**
	 * @param availableUnits
	 * @param copyL
	 */
	public GPanelPopup(final Collection<Delegate> availableUnits, 
			final GraphController graphController) {
		this.availableUnits = availableUnits;
		this.graphController = graphController;
		this.copyL = graphController.getCopyNodesList();
	}

	/**
	 * @return the activePanel
	 */
	public GPanel getActivePanel() {
		return this.activePanel;
	}

	

	public void showFloatingMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			savedPoint = e.getPoint();
			//Create the popup menu.
			JPopupMenu popup = new JPopupMenu();
			Selection<Node> selectedUnits = activePanel.getSelection();
			if (selectedUnits.isEmpty()) { 
				popup.add(new InsertUnitMenu("Insert unit", activePanel, availableUnits, savedPoint));
			} else {
				
				if (selectedUnits.size() == 1) {
					popup.add(getAction("setDisplayUnit"));
					popup.add(getAction("showUnitParameters"));
//					popup.add(getAction("preview"));
					popup.addSeparator();
				}
				popup.add(getAction("cut"));
				popup.add(getAction("copy"));
				popup.add(getAction("paste"));
				popup.add(getAction("unbind"));
				popup.add(getAction("delete"));
			}
			if (!copyL.isEmpty()) 
				popup.add(getAction("paste"));
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	

	
	private Action getAction(String actionName) {
		ActionMap actionMap = ImageFlow.getApplication().getContext().getActionMap(
				ImageFlowView.class, ImageFlow.getApplication().getMainView());
		return actionMap.get(actionName);
	}

	/**
	 * @param activePanel the activePanel to set
	 */
	public void setActivePanel(GPanel activePanel) {
		this.activePanel = activePanel;
	}
}
