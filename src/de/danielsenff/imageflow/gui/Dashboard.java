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


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.HorizontalAlignment;


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
	
	private BufferedImage dashWidgetImage;
	private BufferedImage dashPreviewImage;
	
	/**
	 * 
	 */
	public Dashboard() {
		this.dashs = new HashMap<String, JPanel>();
		this.setPreferredSize(new Dimension(800, 150));
		
		String defaultPath = "/de/danielsenff/imageflow/resources/";
		try {
			this.dashPreviewImage = readResource(defaultPath + "dash_preview.png");
			this.dashWidgetImage = readResource(defaultPath + "dash_widget.png");
		} catch (IOException e) {
			// TODO use dummy image
			e.printStackTrace();
		}
		
		setLayout(null);
	}
	
	/*
	 * TODO OPTIMIZE move into helper class
	 */
	private BufferedImage readResource(String path) throws IOException {
		return ImageIO.read(this.getClass().getResourceAsStream(path));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(SystemColor.textInactiveText);
		for (int x = 20; x < this.getWidth(); x+=GraphPanel.GRIDSIZE) {
			for (int y = 20; y < this.getHeight(); y+=GraphPanel.GRIDSIZE) {
				g2.drawRect(x, y, 1, 1);
			}
		}
		
		if(this.dashs.isEmpty()) {
			// TODO OPTIMIZE get this out of the paint method
			int boxWidth = 370;
			int x = WelcomeArea.margin;
			
			// paint info graphic
			// TODO extrude text to resource locals
			drawIntroBox(g2, "Add Dashboard", "Add the parameters of your processing node \nand gain easy access to their settings for \nquick manipulation.", 
					new Point(x, WelcomeArea.margin), new Dimension(boxWidth, 100), this.dashWidgetImage);
			drawIntroBox(g2, "Add Preview", "Add Previews of your processing Nodes to \nthe Dashboard to see live changes on your \nresulting data and images. ", 
					new Point(x+boxWidth+WelcomeArea.margin*2, WelcomeArea.margin), new Dimension(boxWidth, 100), this.dashPreviewImage);
		}
	}
	
	/**
	 * 
	 * @param g2d
	 * @param headline
	 * @param position
	 * @param dimension
	 * @param image
	 */
	private void drawIntroBox(final Graphics2D g2d, String headline, String description,
			Point position, Dimension dimension, BufferedImage image) {
		
		g2d.setColor(Color.white);
		g2d.fillRoundRect(position.x, position.y, dimension.width, dimension.height, 24, 24);
		g2d.setColor(Color.BLACK);
		g2d.drawRoundRect(position.x, position.y, dimension.width, dimension.height, 24, 24);
		
		g2d.drawImage(image, position.x+WelcomeArea.marginSmall, position.y+WelcomeArea.marginSmall, null);
		
		final Font font = g2d.getFont();
		final Font headlineFont = new Font(font.getFamily(), Font.BOLD, 24);
		
		
		
		TextBlock headlineText = TextUtilities.createTextBlock(headline, headlineFont, Color.GRAY);
		headlineText.setLineAlignment(HorizontalAlignment.LEFT);
		headlineText.draw(g2d, 
				position.x+WelcomeArea.marginSmall*2+image.getWidth(), 
				position.y+WelcomeArea.marginSmall, 
				TextBlockAnchor.TOP_LEFT);
		
		g2d.setFont(new Font(font.getFamily(), Font.PLAIN, 12));
		
		TextBlock descriptionText = TextUtilities.createTextBlock(description, g2d.getFont(), Color.BLACK);
		descriptionText.setLineAlignment(HorizontalAlignment.LEFT);
		descriptionText.draw(g2d, 
				position.x+WelcomeArea.marginSmall*2+image.getWidth(), 
				position.y+WelcomeArea.marginSmall*2+25,
				TextBlockAnchor.TOP_LEFT);
	}
	
	/**
	 * Removes the PreviewWidget of the given {@link UnitElement} from the Dashboard.
	 * @param unit
	 */
	public void removePreviewWidget(final UnitElement unit) {
		final String dashKey = unit.getNodeID()+"_preview";
		if(hasWidget(dashKey)) {
			final JPanel dash = dashs.get(dashKey);
			unit.setPreviewWidget(null);
			dashs.remove(dashKey);
			this.remove(dash);
		}
	}
	
	/**
	 * Removes the FormWidget of the given {@link UnitElement} from the Dashboard.
	 * @param unit
	 */
	public void removeWidget(final UnitElement unit) {
		if(hasWidget(unit.getNodeID())) {
			final JPanel dash = dashs.get(unit.getNodeID()+"");
			unit.setWidget(null);
			dashs.remove(unit.getNodeID()+"");
			this.remove(dash);
		}
	}

	/**
	 * Returns true if a PreviewWidget with the given unique dashKey was added to the Dashboard.
	 * @param dashKey
	 * @return
	 */
	public boolean hasPreviewWidget(final String dashKey) {
		return dashs.containsKey(dashKey+"_preview");
	}
	
	/**
	 * Returns true if a Widget with the given unique dashKey was added to the Dashboard.
	 * @param dashKey
	 * @return
	 */
	public boolean hasWidget(final String dashKey) {
		return dashs.containsKey(dashKey);
	}
	
	public boolean hasWidget(final Integer dashKey) {
		return dashs.containsKey(dashKey + "");
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
		if(!hasWidget(unit.getNodeID())) {
			final DashWidget dash = ParameterWidgetController.createWidgetFromUnit(unit);
			dash.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			dashs.put(unit.getNodeID()+"", dash);
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
		final String dashKey = unit.getNodeID()+"_preview";
		if(!hasWidget(dashKey)) {
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
			updatePreferredSize();
		}

		public void mouseMoved(final MouseEvent arg0) {}
		
	}
	
	protected Rectangle rect;
	
	/*
	 * TODO OPTIMIZE this method is taken from GPanel and could possibly be done nicer
	 */
	public void updatePreferredSize() {
		rect = new Rectangle();
		for (JComponent component : this.dashs.values()) {
			rect = rect.union(component.getBounds());
		}
		this.setPreferredSize(rect.getSize());
		this.getParent().setPreferredSize(rect.getSize());
		revalidate();
	}
	
}
