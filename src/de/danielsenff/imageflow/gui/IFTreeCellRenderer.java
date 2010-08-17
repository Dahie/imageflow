/**
 * Copyright (C) 2008-2010 Daniel Senff
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.danielsenff.imageflow.models.unit.UnitDelegate;

/**
 * CellRenderer in the Delegates-JTree
 * @author Daniel Senff
 *
 */
public class IFTreeCellRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Icon icon;
	
	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value,
			final boolean sel, final boolean expanded, final boolean isLeaf, final int row,
			final boolean hasFocus) {

		// register ToolTips
		ToolTipManager.sharedInstance().registerComponent(tree);
		// make them complete visible even on heavyweight components
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

		if (value instanceof UnitDelegate && isLeaf) {
			
			final UnitDelegate unitDelegate = (UnitDelegate)value;
			icon = new ImageIcon(drawIcon(unitDelegate.getColor(), 16, 16));
			this.setLeafIcon(icon);
			this.setToolTipText(unitDelegate.getToolTipText());
		}
		else {
			this.setToolTipText(null);
		}
		
		
		return super.getTreeCellRendererComponent(tree, 
				value, sel, expanded, isLeaf, row, hasFocus);
	}
	
	private BufferedImage drawIcon(final Color color, final int width, final int height) {
		final BufferedImage icon = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D g2 = icon.createGraphics();

		final int x=0, y=0;

		Color cTop = new Color(84, 121, 203, 255);
		Color cBottom = new Color(136, 169, 242, 255);

		final int delta = 20;

		final int r = color.getRed();
		final int g = color.getGreen();
		final int b = color.getBlue();

		cTop = new Color(
				(r-delta) > 255 ? 255 : r-delta,
				(g-delta) > 255 ? 255 : g-delta,
				(b-delta) > 255 ? 255 : b-delta);
		cBottom = new Color(
				(r+delta) > 255 ? 255 : r+delta,
				(g+delta) > 255 ? 255 : g+delta,
				(b+delta) > 255 ? 255 : b+delta);

		final GradientPaint gradient1 = new GradientPaint(x,y,cTop,x+10,y+10,cBottom);
		g2.setPaint(gradient1);
//		g2.fillRoundRect(x+2, y+2, width-4, height-4, arc, arc);
		g2.fillRect(x+2, y+2, width-4, height-4);

		g2.setStroke(new BasicStroke(1f));
		g2.setColor(new Color(0,0,0,44));
//		g2.drawRoundRect(x+2, y+2, width-4, height-4, arc, arc);
		g2.drawRect(x+2, y+2, width-4, height-4);

		return icon;
	}
	
}
