package de.danielsenff.imageflow.gui;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

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
			dash.addMouseListener(new DragListener(dash));
			dash.addMouseMotionListener(new DragListener(dash));
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
	
	class DragListener implements MouseListener, MouseMotionListener {

		JPanel dash;
		Point offset;
		Point delta;
		
		public DragListener(JPanel dash) {
			this.dash = dash;
			this.delta = new Point(0, 0);
		}

		public void mouseClicked(MouseEvent e) {
			
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e) {
			
		}

		public void mouseReleased(MouseEvent e) {}

		public void mouseDragged(MouseEvent e) {
			//e.translatePoint(offset.x, offset.y);
			int offsetX = delta.x - e.getPoint().x;
			int offsetY = delta.y - e.getPoint().y;
			
			System.out.println(e.getPoint());
			e.translatePoint(offsetX, offsetY);
			dash.setLocation(e.getPoint());
			delta = e.getPoint();
		}

		public void mouseMoved(MouseEvent arg0) {}
		
	}
}
