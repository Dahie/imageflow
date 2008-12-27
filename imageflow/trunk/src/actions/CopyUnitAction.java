package actions;

import graph.Node;
import graph.Selection;

import imageflow.models.unit.UnitElement;

import java.awt.event.ActionEvent;
import java.util.ArrayList;


public class CopyUnitAction extends AbstractUnitAction {


	private ArrayList<Node> copyUnitsList;

	/**
	 * @param selection
	 * @param copyUnitsList
	 */
	public CopyUnitAction(final Selection<Node> selection, 
			final ArrayList<Node> copyUnitsList) {
		super(selection);
		putValue(NAME, "Copy");
		this.copyUnitsList = copyUnitsList;
	}

	public void actionPerformed(ActionEvent e) {
		if (selectedUnits.size() > 0) {
			copyUnitsList.clear();
			for (Node t : selectedUnits) {
				Node clone = ((UnitElement)t).clone();	
				clone.setLabel(t.getLabel());
				copyUnitsList.add(clone);
			}
		}
	}

}
