package actions;

import graph.Edge;
import graph.Node;
import graph.Selection;

import imageflow.models.unit.UnitElement;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;

import visualap.GPanel;
import backend.GraphController;

public class CutUnitAction extends AbstractUnitAction {

	private ArrayList<Node> copyUnitsList;
	private GPanel activePanel;
	private GraphController graphController;
	
	public CutUnitAction(final GraphController graphController, GPanel gpanel) {
		super(gpanel.getSelection());
		putValue(NAME, "Cut");
		this.activePanel = gpanel;
		this.graphController = graphController;
		this.copyUnitsList = graphController.getCopyNodesList();
	}


	public void actionPerformed(ActionEvent e) {
		if (selectedUnits.size() > 0) {
			// il problema java.util.ConcurrentModificationException è stato risolto introducendo la lista garbage
			HashSet<Edge> garbage = new HashSet<Edge>();
			copyUnitsList.clear();
			for (Node t : selectedUnits) {
				/*for (Edge c : activePanel.getEdgeL())
					if ((c.from.getParent() == t)||(t == c.to.getParent()))
						garbage.add(c);
				copyUnitsList.add(t);
				activePanel.getNodeL().remove(t);*/
				this.copyUnitsList.add(t);
				graphController.removeUnit((UnitElement)t);
			}
			for (Edge c : garbage) {
				graphController.getConnections().remove(c);
//				activePanel.getEdgeL().remove(c);
			}
			selectedUnits.clear();
//			activePanel.repaint();
		}
	}

}
