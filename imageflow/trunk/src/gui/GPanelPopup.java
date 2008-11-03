/**
 * 
 */
package gui;

import graph.Edge;
import graph.Node;
import graph.NodeBean;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import visualap.Delegate;
import visualap.ErrorPrinter;
import visualap.GPanel;
import visualap.GPanelListener;
import actions.CopyUnitAction;
import actions.CutUnitAction;
import actions.PasteUnitAction;
import actions.RemoveUnitAction;
import actions.SetDisplayAction;
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
			if (activePanel.getSelection().size() == 0) { 
				popup.add(new InsertUnitMenu("Insert unit", activePanel, availableUnits, savedPoint));
			} else {
//				UnitElement unit = (UnitElement)activePanel.getSelection().get(0);
				
				if (activePanel.getSelection().size() == 1) {
					//					popup.add(editItem("Properties"));
					
					popup.add(new JMenuItem(new SetDisplayAction(activePanel.getSelection())));
					popup.addSeparator();
				}
				popup.add(new CutUnitAction(graphController, activePanel));
				popup.add(new CopyUnitAction(activePanel.getSelection(), copyL));
				popup.add(unbindItem("Unbind"));
				popup.add(new RemoveUnitAction(activePanel.getSelection(), graphController));
			}
			if (copyL.size() != 0) popup.add(new PasteUnitAction(copyL, activePanel));
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}


	public JMenuItem unbindItem(String text) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (activePanel.getSelection().size() > 0) {
					// il problema java.util.ConcurrentModificationException è stato risolto introducendo la lista garbage
					HashSet<Edge> garbage = new HashSet<Edge>();
					for (Node t : activePanel.getSelection()) {
						for (Edge c : activePanel.getEdgeL())
							if ((c.from.getParent() == t)||(t == c.to.getParent()))
								garbage.add(c);
					}
					for (Edge c : garbage)
						activePanel.getEdgeL().remove(c);
					activePanel.getSelection().clear();
					activePanel.repaint();
				}
			}});
		return menuItem;
	}

	
	/**
	 * @param activePanel the activePanel to set
	 */
	public void setActivePanel(GPanel activePanel) {
		this.activePanel = activePanel;
	}
}
