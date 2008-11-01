package actions;

import graph.Edge;
import graph.Node;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;

import visualap.GPanel;

public class CutUnitAction extends AbstractUnitAction {

	private ArrayList<Node> copyUnitsList;
	private GPanel activePanel;
	
	public CutUnitAction(final ArrayList<Node> copyUnitsList, GPanel gpanel) {
		super(gpanel.getSelection());
		putValue(NAME, "Cut");
		this.activePanel = gpanel;
		this.copyUnitsList = copyUnitsList;
	}

	public void actionPerformed(ActionEvent e) {
		if (selectedUnits.size() > 0) {
			// il problema java.util.ConcurrentModificationException è stato risolto introducendo la lista garbage
			HashSet<Edge> garbage = new HashSet<Edge>();
			for (Node t : selectedUnits) {
				for (Edge c : activePanel.getEdgeL())
					if ((c.from.getParent() == t)||(t == c.to.getParent()))
						garbage.add(c);
				copyUnitsList.add(t);
				activePanel.getNodeL().remove(t);
			}
			for (Edge c : garbage)
				activePanel.getEdgeL().remove(c);
			selectedUnits.clear();
			activePanel.repaint();
		}
	}

}
