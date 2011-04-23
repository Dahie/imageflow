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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import javax.swing.JPanel;

import de.danielsenff.imageflow.controller.ParameterWidgetController;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * The Dashboard is a special JPanel that displays Widgets. 
 * Widgets are JComponents that are free-floating.
 * @author dahie
 *
 */
public class Dashboard extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3021893590568590763L;
	private final HashMap<String, JPanel> dashs;
	private int dragStartX;
	private int dragStartY;
	
	/**
	 * 
	 */
	public Dashboard() {
		this.dashs = new HashMap<String, JPanel>();
		this.setPreferredSize(new Dimension(500, 200));
		setLayout(null);
	}
	
	/**
	 * Removes the FormWidget of the given {@link UnitElement} from the Dashboard.
	 * @param unit
	 */
	public void removeWidget(final UnitElement unit) {
		if(dashs.containsKey(unit.getLabel())) {
			final JPanel dash = dashs.get(unit.getLabel());
			unit.setWidget(null);
			dashs.remove(unit.getLabel());
			this.remove(dash);
		}
	}
	
	/**
	 * Removes the PreviewWidget of the given {@link UnitElement} from the Dashboard.
	 * @param unit
	 */
	public void removePreviewWidget(final UnitElement unit) {
		final String dashKey = unit.getLabel()+"_preview";
		if(dashs.containsKey(dashKey)) {
			final JPanel dash = dashs.get(dashKey);
			unit.setPreviewWidget(null);
			dashs.remove(dashKey);
			this.remove(dash);
		}

	}
	
	/**
	 * Generate and add a new FormWidget based on the given {@link UnitElement}.
	 * The Widget is added to an automatic position.
	 * @param unit
	 */
	public void addWidget(final UnitElement unit) {
		addWidget(unit, new Point(30, 30));
	}
	
	/**
	 * Generate and add a new FormWidget based on the given {@link UnitElement} added to the given position.
	 * @param unit
	 * @param position
	 */
	public void addWidget(final UnitElement unit, final Point position) {
		if(!dashs.containsKey(unit.getLabel())) {
			final DashWidget dash = ParameterWidgetController.createWidgetFromUnit(unit);
			dash.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			dashs.put(unit.getLabel(), dash);
			unit.setWidget(dash);
			
			dash.setBounds(position.x, position.y, dash.getPreferredSize().width, dash.getPreferredSize().height);
			dash.addMouseListener(new DragListener(dash));
			dash.addMouseMotionListener(new DragListener(dash));
			this.add(dash);
			this.repaint();
		}
	}

	/**
	 * Generate and add a new PreviewWidget based on the given {@link UnitElement}.
	 * The Widget is added to an automatic position.
	 * @param unit
	 */
	public void addPreviewWidget(final UnitElement unit) {
		addPreviewWidget(unit, new Point(30, 30));
	}
	
	/**
	 * Generate and add a new PreviewWidget based on the given {@link UnitElement} added to the given position.
	 * @param unit
	 * @param position
	 */
	public void addPreviewWidget(final UnitElement unit, final Point position) {
		final String dashKey = unit.getLabel()+"_preview";
		if(!dashs.containsKey(dashKey)) {
			final JPanel dash = ParameterWidgetController.createPreviewWidgetFromUnit(unit);
			dash.setBounds(position.x, position.y, dash.getPreferredSize().width, dash.getPreferredSize().height);
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
		
		public DragListener(final JPanel dash) {
			this.dash = dash;
			this.delta = new Point(0, 0);
		}

		public void mouseClicked(final MouseEvent e) {}

		public void mouseEntered(final MouseEvent e) {}

		public void mouseExited(final MouseEvent e) {}

		public void mousePressed(final MouseEvent e) {
			dragStartX = (int)(e.getXOnScreen());
			dragStartY = (int)(e.getYOnScreen());
		}

		public void mouseReleased(final MouseEvent e) {}

		public void mouseDragged(final MouseEvent e) {
			final int dx = (int)((e.getXOnScreen()) - dragStartX);
			final int dy = (int)((e.getYOnScreen()) - dragStartY);
			
			e.translatePoint(dx, dy);
			final Point dashLocation = dash.getLocation();
			dash.setLocation(new Point(dashLocation.x+dx, dashLocation.y+dy));
			
			dragStartX = (int)(e.getXOnScreen());
			dragStartY = (int)(e.getYOnScreen());
		}

		public void mouseMoved(final MouseEvent arg0) {}
		
	}
}
