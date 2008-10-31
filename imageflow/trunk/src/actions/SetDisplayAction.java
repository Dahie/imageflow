package actions;

import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import models.unit.UnitElement;

public class SetDisplayAction extends AbstractUnitAction {

	
	public SetDisplayAction(UnitElement unit) {
		super(unit);
		String text;
		
		// get if selected node is display or not.
		if(unit.isDisplayUnit()) {
			text = "Deactivate display";
		} else {
			text = "Activate display";
		}
		putValue(NAME, text);
	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)(e.getSource());
		String action = source.getText();

		if(unit.isDisplayUnit()) {
			// if it is a displayUnit, deactivate
			unit.setDisplayUnit(false);
			System.out.println("setting false");
		} else {
			// if it is a displayUnit, activate
			unit.setDisplayUnit(true);
		}
	}


}
