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
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import visualap.GPanel;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.controller.GraphControllerManager;
import de.danielsenff.imageflow.models.Selection;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.Node;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;

/**
 * Context menu on the GPanel
 * @author Daniel Senff
 *
 */
public class GPanelPopup {

	protected GPanel activePanel;


	/**
	 */
	public GPanelPopup() {
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
				popup.add(new InsertUnitMenu(activePanel, savedPoint));
				if (!getGraphController().getCopyNodesList().isEmpty()) 
					popup.add(getAction("paste"));
			} else {
				popup.add(getAction("showUnitParameters"));
				
				showSingleUnitActions(popup, selections);

				popup.addSeparator();
				
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
	
	public GraphController getGraphController() {
		return GraphControllerManager.getInstance().getController();
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
			chkBoxDisplayUnit.setSelected(unit.isDisplay());
			popup.add(chkBoxDisplayUnit);
			chkBoxDisplayUnit = new JCheckBoxMenuItem(getAction("setSilentDisplayUnit")); 
			chkBoxDisplayUnit.setSelected(unit.isDisplaySilent());
			//popup.add(chkBoxDisplayUnit);
			
			
			JCheckBoxMenuItem chkBoxCollapseIcon = new JCheckBoxMenuItem(getAction("setUnitComponentSize"));
			boolean isCollapsedIcon = unit.getCompontentSize() == Size.SMALL ? true : false;
			chkBoxCollapseIcon.setSelected(isCollapsedIcon);
			popup.add(chkBoxCollapseIcon);
			
			popup.addSeparator();
			
			if(unit.hasParameters()) {
				if(unit.hasWidget()) {
					popup.add(getAction("removeFromDashboard"));
				} else {
					popup.add(getAction("addToDashboard"));
				}
			}
			if(unit.hasOutputs()) {
				if(unit.hasPreviewWidget()) {
					popup.add(getAction("removeOutputFromDashboard"));
				} else {
					popup.add(getAction("addOutputToDashboard"));
				}
			}
		}
	}
	
	private Action getAction(String actionName) {
		ActionMap actionMap = ImageFlow.getApplication().getContext().getActionMap(
				ImageFlowView.class, ImageFlow.getApplication().getMainView());
		return actionMap.get(actionName);
	}

}
