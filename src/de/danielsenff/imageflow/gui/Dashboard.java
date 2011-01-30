package de.danielsenff.imageflow.gui;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
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
		this.setPreferredSize(new Dimension(500, 200));
		setLayout(null);

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
			final JPanel dash = ParameterWidgetController.createWidgetFromUnit(unit);
			dashs.put(unit.getLabel(), dash);
			unit.setWidget(dash);
			dash.setBounds(30, 30, 250, 150);
			dash.setBorder(BorderFactory.createTitledBorder(unit.getLabel()));
			dash.addMouseMotionListener(new MouseMotionListener() {
				
				public void mouseMoved(MouseEvent e) {
				}
				
				public void mouseDragged(MouseEvent e) {
					int offsetX = e.getPoint().x - dash.getLocation().x;  
					int offsetY = e.getPoint().y - dash.getLocation().y;
					
					//e.translatePoint(offsetX, offsetY);
					dash.setLocation(new Point(e.getPoint()));
				}
			});
			this.add(dash);
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
