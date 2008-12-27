package actions;

import graph.Node;

import imageflow.models.unit.UnitElement;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import visualap.ErrorPrinter;
import visualap.GPanel;

public class PasteUnitAction extends AbstractUnitAction {

	private GPanel activePanel;
	private ArrayList<Node> copyUnitsList;
	
	
	public PasteUnitAction(final ArrayList<Node> copyUnitsList, GPanel gpanel) {
		super(gpanel.getSelection());
		putValue(NAME, "Paste");
		this.activePanel = gpanel;
		this.copyUnitsList = copyUnitsList;
	}

	public void actionPerformed(ActionEvent e) {
		if (copyUnitsList.size() > 0) {
			selectedUnits.clear();
			selectedUnits.addAll(copyUnitsList);
			copyUnitsList.clear();
			for (Node t : selectedUnits) {
//			for (Node t : copyUnitsList) {
				try {
					UnitElement clone = (UnitElement)t.clone();	
					clone.setLabel(t.getLabel());
					activePanel.getNodeL().add(t, t.getLabel());
					copyUnitsList.add(clone);
				} catch(CloneNotSupportedException ex) {
					ErrorPrinter.printInfo("CloneNotSupportedException");
				}
			}
			//					copyL.clear(); 
			//copyL.addAll(activePanel.selection);
			activePanel.repaint();
		}
	}

}
