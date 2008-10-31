package actions;

import java.awt.event.ActionEvent;

import models.unit.UnitElement;

public class RemoveUnitAction extends AbstractUnitAction {

	public RemoveUnitAction(UnitElement unit) {
		super(unit);
		putValue(NAME, "Remove unit");
	}

	
	
	public void actionPerformed(ActionEvent arg0) {
		
	}

}
