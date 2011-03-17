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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;


/**
 * JComponent for drawing {@link BufferedImage}s.
 * @author danielsenff
 *
 */
public class BICanvas extends JPanel implements Scrollable, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private Image biRendered;
	private Image biSource;
	private float zoomFactor = 1.0f;
	
	public BICanvas() {
		this.addMouseMotionListener(this);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	/**
	 * Displays a {@link BufferedImage} in the channel-mode specified.
	 * @param controller 
	 * @param image 
	 * @param biRendered BufferedImage to display
	 * @param channel Channel of the BufferedImage to display 
	 */
	public BICanvas(final BufferedImage image) {
		this();
		this.biRendered = image;
		this.biSource = image;
		
		this.setPreferredSize(new Dimension(biRendered.getWidth(null), biRendered.getHeight(null)));
	}
	
	/**
	 * Returns the {@link BufferedImage} currently displayed on the canvas.
	 * @return
	 */
	public Image getCanvas() {
		return this.biRendered;
	}
	
	/**
	 * @return
	 */
	public Image getSource() {
		return this.biSource;
	}
	
	/**
	 * Overwrite the current source-BufferedImage.
	 * This will also update the displayed canvas and the window-size.
	 * @param bi
	 */
	public void setSourceBI(final Image bi) {
		this.biRendered = bi;
		this.biSource = bi;
		int width = (int) (zoomFactor*bi.getWidth(null));
		int height = (int) (zoomFactor*bi.getHeight(null));
		this.setPreferredSize(new Dimension(width, height));
		this.getParent().setPreferredSize(new Dimension(bi.getWidth(null), bi.getHeight(null)));
		invalidate();
	}
		
	/**
	 * Sets the factor the original-image dimensions are multiplied with
	 * @param zoom
	 */
	public void setZoomFactor(final float zoom) {
		float oldValue = this.zoomFactor;
		this.zoomFactor = zoom;
		int newW = (int) (biRendered.getWidth(null) * zoom);
		int newH = (int) (biRendered.getHeight(null) * zoom);
		this.setPreferredSize(new Dimension(newW, newH));
		this.revalidate();
		
		firePropertyChange("zoomFactor", oldValue, zoomFactor);
	}
	
	/**
	 * Returns the factor the original-image dimensions are multiplied with
	 * @return
	 */
	public float getZoomFactor() {
		return this.zoomFactor;
	}

	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (biRendered != null)
			drawImage(g);
	}

	private void drawImage(Graphics g) {
		int width =  biRendered.getWidth(null), height =  biRendered.getHeight(null);
		int newW = (int) (zoomFactor * width); 
		int newH = (int) (zoomFactor * height);
		int offsetX = (int) ((0.5*g.getClipBounds().getWidth()) - (0.5*newW)); // offset im viewport
		int offsetY = (int) ((0.5*g.getClipBounds().getHeight())- (0.5*newH)); // offset im viewport
		int moveX = 0; //offset on bi
		int moveY = 0; //offset on bi
		offsetX=0;
		offsetY=0;
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		//g.drawImage(this.displayBi, 0, 0, this);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);   
        g.drawImage(biRendered, offsetX, offsetY, newW+offsetX, newH+offsetY, moveX, moveY, biRendered.getWidth(null), biRendered.getHeight(null), null);
	}


	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}


	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
	 */
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 50;
	}


	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}


	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * The dimensions of the stored {@link BufferedImage} multiplied by the zoom-factor.
	 * @return dimension Dimension of the stored {@link BufferedImage}
	 */
	public Dimension getViewDimension() {
		return new Dimension((int) (zoomFactor * biRendered.getWidth(null)), (int) (zoomFactor * biRendered.getWidth(null)));
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
	 */
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 15; // pixel
	}


	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		int x = (int) (e.getPoint().x/zoomFactor);
		int y = (int) (e.getPoint().y/zoomFactor);
		
		if (biSource instanceof BufferedImage) {
			BufferedImage image = (BufferedImage)biSource;
			Raster data = image.getData();
			ColorModel colorModel = image.getColorModel();
			String tooltip = "Coordinate (" + x + ", "+ y + "), ";
			int index = y*image.getWidth() + x;
			if(colorModel instanceof IndexColorModel) {
				tooltip	+= "Index Colors ("
					+ colorModel.getAlpha(index) + ", "
					+ colorModel.getRed(index) + ", "
					+ colorModel.getGreen(index) + ", "
					+ colorModel.getBlue(index) + ")";
			} else if(colorModel.getNumComponents() > 3) {
				tooltip	+= "ARGB ("
					+ data.getSample((int)x, (int)y, 3) + ", "
					+ data.getSample((int)x, (int)y, 0) + ", "
					+ data.getSample((int)x, (int)y, 1) + ", "
					+ data.getSample((int)x, (int)y, 2) + ")";
			} else {
				tooltip	+= "RGB ("
					+ data.getSample((int)x, (int)y, 0) + ", "
					+ data.getSample((int)x, (int)y, 1) + ", "
					+ data.getSample((int)x, (int)y, 2) + ")";	
			}
			this.setToolTipText(tooltip);
		}
	}
}
