/**
 * 
 */
package de.danielsenff.imageflow.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;
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
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;

/**
 * Contextmenu on the GPanel
 * @author danielsenff
 *
 */
public class GPanelPopup implements GPanelListener {

	protected GPanel activePanel;
	protected GraphController graphController;



	/**
	 * @param availableUnits
	 * @param graphController 
	 * @param copyL
	 */
	public GPanelPopup(final GraphController graphController) {
		this.graphController = graphController;
	}

	/**
	 * @return the activePanel
	 */
	public GPanel getActivePanel() {
		return this.activePanel;
	}


	/**
	 * @param activePanel the activePanel to set
	 */
	public void setActivePanel(GPanel activePanel) {
		this.activePanel = activePanel;
	}

	public void showFloatingMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			Point savedPoint = e.getPoint();
			//Create the popup menu.
			JPopupMenu popup = new JPopupMenu();
			Selection<Node> selections = activePanel.getSelection();
			if (selections.isEmpty()) { 
				popup.add(new InsertUnitMenu("Insert unit", activePanel, savedPoint));
				if (!graphController.getCopyNodesList().isEmpty()) 
					popup.add(getAction("paste"));
			} else {
				showSingleUnitActions(popup, selections);
				popup.add(getAction("cut"));
				popup.add(getAction("copy"));
				popup.add(getAction("paste"));
				popup.add(getAction("unbind"));
				popup.add(getAction("delete"));
				if (!hasGroup(selections)) {
					popup.add(getAction("group"));
				}
			}

			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	private boolean hasGroup(Collection<Node> selectedUnits) {
		for (Node node : selectedUnits) {
			if (node instanceof GroupUnitElement) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param popup
	 * @param selectedUnits
	 */
	private void showSingleUnitActions(JPopupMenu popup,
			Selection<Node> selectedUnits) {
		if (selectedUnits.size() == 1 
				&& selectedUnits.get(0) instanceof UnitElement) {
			
			UnitElement unit = (UnitElement) selectedUnits.get(0);
			
			if(unit instanceof GroupUnitElement) {
				popup.add(getAction("showGroupContents"));
				popup.add(getAction("degroup"));
				popup.addSeparator();
			}
				
			
			JCheckBoxMenuItem chkBoxDisplayUnit = new JCheckBoxMenuItem(getAction("setDisplayUnit")); 
			boolean isDisplayUnit = unit.isDisplay();
			chkBoxDisplayUnit.setSelected(isDisplayUnit);
			popup.add(chkBoxDisplayUnit);
			
			JCheckBoxMenuItem chkBoxCollapseIcon = new JCheckBoxMenuItem(getAction("setUnitComponentSize"));
			boolean isCollapsedIcon = unit.getCompontentSize() == Size.SMALL ? true : false;
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

}
