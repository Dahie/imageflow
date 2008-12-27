package actions;

import graph.Node;
import graph.Selection;

import imageflow.models.unit.UnitElement;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JMenuItem;


public class SetDisplayAction extends AbstractUnitAction {

	
	public SetDisplayAction(Selection<Node> selection) {
		super(selection);
		String text;
		
		// if more selected units are invisible
		int numberOfVisibleUnits = 0;
		for (Iterator iterator = selectedUnits.iterator(); iterator.hasNext();) {
			UnitElement unit = (UnitElement) iterator.next();
			if(unit.isDisplayUnit()) {
				numberOfVisibleUnits++;
			}
		}
		
		// get if selected node is display or not.
		if(numberOfVisibleUnits > 0.5*selectedUnits.size()) {
			text = "Deactivate display";
		} else {
			text = "Activate display";
		}
		putValue(NAME, text);
	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)(e.getSource());

		for (Iterator iterator = selectedUnits.iterator(); iterator.hasNext();) {
			UnitElement unit = (UnitElement) iterator.next();
			if(unit.isDisplayUnit()) {
				// if it is a displayUnit, deactivate
				unit.setDisplayUnit(false);
			} else {
				// if it is a displayUnit, activate
				unit.setDisplayUnit(true);
			}
		}
		

	}


}
