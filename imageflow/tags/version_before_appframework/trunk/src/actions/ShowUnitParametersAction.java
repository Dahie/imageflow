/**
 * 
 */
package actions;

import graph.Node;
import graph.Selection;
import ij.gui.GenericDialog;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Action;

import models.BooleanParameter;
import models.ChoiceParameter;
import models.DoubleParameter;
import models.IntegerParameter;
import models.AbstractParameter;
import models.StringParameter;
import models.unit.UnitElement;


/**
 * @author danielsenff
 *
 */
public class ShowUnitParametersAction extends AbstractUnitAction {
	
	/**
	 * @param controller
	 */
	public ShowUnitParametersAction(Selection<Node> selectedUnits) {
		super(selectedUnits);
		putValue(Action.NAME, "Properties");
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		
		for (int i = 0; i < selectedUnits.size(); i++) {
			UnitElement unit = (UnitElement)selectedUnits.get(i);
			unit.showProperties();
		}
	}

}
