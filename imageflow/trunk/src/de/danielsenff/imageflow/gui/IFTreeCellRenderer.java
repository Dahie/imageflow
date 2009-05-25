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
import javax.swing.tree.DefaultTreeCellRenderer;

import de.danielsenff.imageflow.models.unit.UnitDelegate;

public class IFTreeCellRenderer extends DefaultTreeCellRenderer {

	private Icon icon;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean isLeaf, int row,
			boolean hasFocus) {

		if (value instanceof UnitDelegate && isLeaf) {
			
			final UnitDelegate unitDelegate = (UnitDelegate)value;
			icon = new ImageIcon(drawIcon(unitDelegate.getColor(), 16, 16));
			this.setLeafIcon(icon);
		}
		
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, isLeaf,
				row, hasFocus);
	}
	
	private BufferedImage drawIcon(final Color color, final int width, final int height) {
		final BufferedImage icon = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D g2 = icon.createGraphics();

		final int x=0, y=0;
		final int arc = 5;

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
