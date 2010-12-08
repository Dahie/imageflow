package de.danielsenff.imageflow.gui;

import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import de.danielsenff.imageflow.controller.ParameterWidgetController;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class Dashboard extends JPanel {

	HashMap<String, JToolBar> dashs;
	
	public Dashboard() {
		this.dashs = new HashMap<String, JToolBar>();
		//this.setFloatable(false);
		setLayout(//new FlowLayout(//FlowLayout.LEFT, 1, 1));
              new WrapLayout());
	}
	
	public void addToolbar(UnitElement unit) {
		if(!dashs.containsKey(unit.getLabel())) {
			JToolBar dash = ParameterWidgetController.createToolbarFromUnit(unit);
			dashs.put(unit.getLabel(), dash);
			this.add(dash);
		}
	}
	
}
