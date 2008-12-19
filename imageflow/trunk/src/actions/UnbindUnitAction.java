package actions;

import graph.Node;
import graph.Selection;

import java.awt.event.ActionEvent;

import models.unit.UnitElement;
import visualap.GPanel;
import backend.GraphController;

public class UnbindUnitAction extends AbstractUnitAction {

	private GPanel activePanel;
	GraphController controller;

	public UnbindUnitAction(final Selection<Node> selection, GraphController controller) {
		super(selection);
		putValue(NAME, "Unbind");
		this.controller = controller;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if (this.selectedUnits.size() > 0) {
			
			/*HashSet<Edge> garbage = new HashSet<Edge>();
			ConnectionList connections = (ConnectionList) controller.getConnections();
			for (Node t : this.selectedUnits) {
				for (Edge c : connections)
					if ((c.from.getParent() == t)||(t == c.to.getParent()))
						garbage.add(c);
			}
			for (Edge c : garbage)
				connections.remove(c);*/
			
			for (Node unit : this.selectedUnits) {
				controller.getUnitElements().unbindUnit((UnitElement)unit);	
			}
			
			this.selectedUnits.clear();
//			activePanel.repaint();
		}
	}
	

}
