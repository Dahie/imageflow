/**
 * 
 */
package actions;

import graph.Node;
import graph.Selection;
import ij.gui.GenericDialog;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;

import models.DoubleParameter;
import models.Parameter;
import models.unit.UnitElement;


/**
 * @author danielsenff
 *
 */
public class RunParaAction extends AbstractUnitAction {

	
	/**
	 * @param controller
	 */
	public RunParaAction(Selection<Node> selectedUnits) {
		super(selectedUnits);
		putValue(Action.NAME, "Edit parameters");
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		
		for (int i = 0; i < selectedUnits.size(); i++) {
			UnitElement unit = (UnitElement)selectedUnits.get(i);

			// Dialog fuer Auswahl der Bilderzeugung
			GenericDialog gd = new GenericDialog("Parameter");
			ArrayList<Parameter> parameterList = unit.getParameters();
			
			for (Parameter parameter : parameterList) {
				
				if(parameter instanceof DoubleParameter) {
					gd.addNumericField(parameter.getDisplayName(), (Double) parameter.getValue(), 2);
				}
				
			}
			
//			String str = unit.getName();
//				gd.addMessage(str);
//			gd.addCheckbox("Do it", false);
//			gd.addNumericField("Para 1 (0 - 5):", 1, 2);

			// generiere Eingabefenster
			gd.showDialog();

			if( gd.wasCanceled())
				return;
			
			
			for (Parameter parameter : parameterList) {
				if(parameter instanceof DoubleParameter) {
					double value = (double) (gd.getNextNumber());
					((DoubleParameter) parameter).setValue(value);
				}
			}
			
	        /*int hue = (int) (gd.getNextNumber());
	        double saettigung = (gd.getNextNumber());
	        double bright = (double) (gd.getNextNumber());
	        double contrast = (gd.getNextNumber());
	        boolean tocolor = gd.getNextBoolean();*/
		
		}
		
	}

}
