package actions;

import graph.Node;
import graph.Selection;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.KeyStroke;

import models.unit.UnitElement;
import backend.GraphController;

public class RemoveUnitAction extends AbstractUnitAction {

	GraphController controller;
	
	public RemoveUnitAction(final Selection<Node> selection, final GraphController controller) {
		super(selection);
		this.controller = controller;
		putValue(NAME, "Remove");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("BACKSPACE"));
		
	}

	
	
	public void actionPerformed(ActionEvent arg0) {
		for (Iterator iterator = selectedUnits.iterator(); iterator.hasNext();) {
			UnitElement unit = (UnitElement) iterator.next();
			controller.removeUnit(unit);
		}
		
	}

}
