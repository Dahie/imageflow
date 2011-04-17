/**
 * Copyright (C) 2008-2011 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.danielsenff.imageflow.gui;


import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
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
	public int dragStartX;
	public int dragStartY;
	
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
			final DashWidget dash = ParameterWidgetController.createWidgetFromUnit(unit);
			dash.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			dashs.put(unit.getLabel(), dash);
			unit.setWidget(dash);
			
			dash.setBounds(30, 30, dash.getPreferredSize().width, dash.getPreferredSize().height);
			dash.addMouseListener(new DragListener(dash));
			dash.addMouseMotionListener(new DragListener(dash));
			this.add(dash);
			this.repaint();
		}
	}

	public void addPreviewWidget(UnitElement unit) {
		String dashKey = unit.getLabel()+"_preview";
		if(!dashs.containsKey(dashKey)) {
			JPanel dash = ParameterWidgetController.createPreviewWidgetFromUnit(unit);
			dash.setBounds(30, 30, dash.getPreferredSize().width, dash.getPreferredSize().height);
			dashs.put(dashKey, dash);
			dash.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			dash.addMouseListener(new DragListener(dash));
			dash.addMouseMotionListener(new DragListener(dash));
			unit.setPreviewWidget(dash);
			this.add(dash);
			this.repaint();
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
			dragStartX = (int)(e.getXOnScreen());
			dragStartY = (int)(e.getYOnScreen());
		}

		public void mouseReleased(MouseEvent e) {}

		public void mouseDragged(MouseEvent e) {
			//e.translatePoint(offset.x, offset.y);
			int offsetX = delta.x - e.getPoint().x;
			int offsetY = delta.y - e.getPoint().y;
			int dx = (int)((e.getXOnScreen()) - dragStartX);
			int dy = (int)((e.getYOnScreen()) - dragStartY);
			
			e.translatePoint(dx, dy);
			Point dashLocation = dash.getLocation();
			dash.setLocation(new Point(dashLocation.x+dx, dashLocation.y+dy));
			
			dragStartX = (int)(e.getXOnScreen());
			dragStartY = (int)(e.getYOnScreen());
		}

		public void mouseMoved(MouseEvent arg0) {}
		
	}
}
