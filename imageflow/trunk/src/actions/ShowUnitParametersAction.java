/**
 * 
 */
package actions;

import graph.Node;
import graph.Selection;
import ij.gui.GenericDialog;
import imageflow.models.parameter.AbstractParameter;
import imageflow.models.parameter.BooleanParameter;
import imageflow.models.parameter.ChoiceParameter;
import imageflow.models.parameter.DoubleParameter;
import imageflow.models.parameter.IntegerParameter;
import imageflow.models.parameter.StringParameter;
import imageflow.models.unit.UnitElement;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Action;



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
