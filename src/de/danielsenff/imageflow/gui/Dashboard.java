package de.danielsenff.imageflow.gui;

import java.util.HashMap;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.controller.ParameterWidgetController;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class Dashboard extends JPanel {

	private HashMap<String, JPanel> dashs;
	private GraphController graphController;
	
	public Dashboard(GraphController controller) {
		this.graphController = controller;
		this.dashs = new HashMap<String, JPanel>();
		setLayout(//new FlowLayout(//FlowLayout.LEFT, 1, 1));
              //new WrapLayout()
              new MigLayout("left, wrap 3, debug, flowx, ",
                      "[110,fill]",
                      "[fill]")
		);
	}
	
	public void removeWidget(UnitElement unit) {
		if(dashs.containsKey(unit.getLabel())) {
			JPanel dash = dashs.get(unit.getLabel());
			unit.setWidget(null);
			dashs.remove(unit.getLabel());
			this.remove(dash);
		}
	}
	
	public void removePreviewWidget(UnitElement unit) {
		String dashKey = unit.getLabel()+"_preview";
		if(dashs.containsKey(dashKey)) {
			JPanel dash = dashs.get(dashKey);
			unit.setPreviewWidget(null);
			dashs.remove(dashKey);
			this.remove(dash);
		}
	}
	
	public void addWidget(UnitElement unit) {
		if(!dashs.containsKey(unit.getLabel())) {
			JPanel dash = ParameterWidgetController.createWidgetFromUnit(unit);
			dashs.put(unit.getLabel(), dash);
			unit.setWidget(dash);
			this.add(dash, "flowy");
		}
	}

	public void addPreviewWidget(UnitElement unit) {
		String dashKey = unit.getLabel()+"_preview";
		if(!dashs.containsKey(dashKey)) {
			JPanel dash = ParameterWidgetController.createPreviewWidgetFromUnit(unit);
			dashs.put(dashKey, dash);
			unit.setPreviewWidget(dash);
			this.add(dash, "flowy");
		}
	}

	public void clear() {
		this.removeAll();
		this.dashs.clear();
	}
	
}
