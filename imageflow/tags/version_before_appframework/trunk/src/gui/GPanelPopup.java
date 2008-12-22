/**
 * 
 */
package gui;

import graph.Node;
import graph.Selection;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import visualap.Delegate;
import visualap.GPanel;
import visualap.GPanelListener;
import actions.CopyUnitAction;
import actions.CutUnitAction;
import actions.PasteUnitAction;
import actions.RemoveUnitAction;
import actions.SetDisplayAction;
import actions.ShowUnitParametersAction;
import actions.UnbindUnitAction;
import backend.GraphController;

/**
 * @author danielsenff
 *
 */
public class GPanelPopup implements GPanelListener {

	protected GPanel activePanel;
	protected Point savedPoint = new Point(0,0);
	protected ArrayList<Node> copyL;
	protected ArrayList<Delegate> availableUnits;
	private GraphController graphController;



	/**
	 * @param availableUnits
	 * @param copyL
	 */
	public GPanelPopup(final ArrayList<Delegate> availableUnits, 
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
			if (selectedUnits.size() == 0) { 
				popup.add(new InsertUnitMenu("Insert unit", activePanel, availableUnits, savedPoint));
			} else {
				
				if (selectedUnits.size() == 1) {
					popup.add(new JMenuItem(new SetDisplayAction(selectedUnits)));
					popup.add(new ShowUnitParametersAction(selectedUnits));
					popup.addSeparator();
				}
				popup.add(new CutUnitAction(graphController, activePanel));
				popup.add(new CopyUnitAction(selectedUnits, copyL));
				popup.add(new UnbindUnitAction(selectedUnits, graphController));
				popup.add(new RemoveUnitAction(selectedUnits, graphController));
			}
			if (copyL.size() != 0) popup.add(new PasteUnitAction(copyL, activePanel));
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}



	
	/**
	 * @param activePanel the activePanel to set
	 */
	public void setActivePanel(GPanel activePanel) {
		this.activePanel = activePanel;
	}
}
