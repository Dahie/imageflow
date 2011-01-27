package de.danielsenff.imageflow.gui;

import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JPanel;

import de.danielsenff.imageflow.controller.ParameterWidgetController;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class Dashboard extends JPanel {

	HashMap<String, JPanel> dashs;
	
	public Dashboard() {
		this.dashs = new HashMap<String, JPanel>();
		//this.setFloatable(false);
		setLayout(//new FlowLayout(//FlowLayout.LEFT, 1, 1));
              //new WrapLayout());
		new GraphPaperLayout());
	}
	
	public void addWidget(UnitElement unit) {
		if(!dashs.containsKey(unit.getLabel())) {
			JPanel dash = ParameterWidgetController.createWidgetFromUnit(unit);
			dashs.put(unit.getLabel(), dash);
			this.add(dash, new Rectangle());
		}
	}
	
}
