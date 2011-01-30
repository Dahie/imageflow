package de.danielsenff.imageflow.gui;

import java.util.HashMap;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.danielsenff.imageflow.controller.ParameterWidgetController;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class Dashboard extends JPanel {

	HashMap<String, JPanel> dashs;
	
	public Dashboard() {
		this.dashs = new HashMap<String, JPanel>();
		setLayout(//new FlowLayout(//FlowLayout.LEFT, 1, 1));
              //new WrapLayout()
              new MigLayout("left, wrap 3, debug, flowx, ",
                      "[110,fill]",
                      "[fill]")
		);
	}
	
	public void addWidget(UnitElement unit) {
		if(!dashs.containsKey(unit.getLabel())) {
			JPanel dash = ParameterWidgetController.createWidgetFromUnit(unit);
			dashs.put(unit.getLabel(), dash);
			this.add(dash, "flowy");
		}
	}
	
}
