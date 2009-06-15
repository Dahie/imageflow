/**
 * 
 */
package de.danielsenff.imageflow.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import visualap.GPanel;
import visualap.GPanelListener;
import visualap.Node;
import visualap.Selection;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.models.Delegate;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;

/**
 * @author danielsenff
 *
 */
public class GPanelPopup implements GPanelListener {

	protected GPanel activePanel;
	protected Point savedPoint = new Point(0,0);
	protected ArrayList<Node> copyL;
	protected Collection<Delegate> availableUnits;



	/**
	 * @param availableUnits
	 * @param graphController 
	 * @param copyL
	 */
	public GPanelPopup(final Collection<Delegate> availableUnits, 
			final GraphController graphController) {
		this.availableUnits = availableUnits;
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
				if (!copyL.isEmpty()) 
					popup.add(getAction("paste"));
			} else {
				
				showSingleUnitActions(popup, selectedUnits);
				popup.add(getAction("cut"));
				popup.add(getAction("copy"));
				popup.add(getAction("paste"));
				popup.add(getAction("unbind"));
				popup.add(getAction("delete"));
			}
			
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * @param popup
	 * @param selectedUnits
	 */
	private void showSingleUnitActions(JPopupMenu popup,
			Selection<Node> selectedUnits) {
		if (selectedUnits.size() == 1 
				&& selectedUnits.get(0) instanceof UnitElement) {
			
			JCheckBoxMenuItem chkBoxDisplayUnit = new JCheckBoxMenuItem(getAction("setDisplayUnit")); 
			boolean isDisplayUnit = ((UnitElement)selectedUnits.get(0)).isDisplayUnit();
			chkBoxDisplayUnit.setSelected(isDisplayUnit);
			popup.add(chkBoxDisplayUnit);
			
			JCheckBoxMenuItem chkBoxCollapseIcon = new JCheckBoxMenuItem(getAction("setUnitComponentSize"));
			boolean isCollapsedIcon = ((UnitElement)selectedUnits.get(0)).getCompontentSize() == Size.SMALL ? true : false;
			chkBoxCollapseIcon.setSelected(isCollapsedIcon);
			popup.add(chkBoxCollapseIcon);
//					popup.add(getAction("preview"));
			
			popup.add(getAction("showUnitParameters"));
			popup.addSeparator();
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
