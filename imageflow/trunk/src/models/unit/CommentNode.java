package models.unit;

import graph.NodeText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

public class CommentNode extends NodeText {

	
	public CommentNode(Point point, String string) {
		super(point, string);
	}

	@Override
	public Rectangle paint(Graphics g, ImageObserver io) {
		Color saveColor = g.getColor();
		Font saveFont = g.getFont();

		Graphics2D g2 = (Graphics2D)g;
			g2.setFont(new Font("Arial", Font.PLAIN, 12));
			FontMetrics fm = g.getFontMetrics();
			g2.setColor(new Color(200,200,200));
			if (text == null) {
				System.out.println("Node.paint: Null Pointer Exception");
				text = "*Null Pointer Exception*"; //zz now the exception is hidden, but what the problem?
			}
			getDimension().setSize(fm.stringWidth(text) + 10, fm.getHeight() + 4);
			int padding = 2;
			g2.fillRoundRect(origin.x-padding, origin.y-padding, 
					getDimension().width+padding, getDimension().height+padding, 4, 4);

		    g2.setStroke(new BasicStroke(1f));
//		    g2.setColor(new Color(0,0,0,44));
		    g2.setColor(selected ? Color.BLACK : new Color(0,0,0,44));
		    g2.drawRoundRect(origin.x-padding, origin.y-padding, 
					getDimension().width+padding, getDimension().height+padding, 4, 4);
		    g2.setColor(Color.BLACK);
			g2.drawString(text, origin.x + 5, (origin.y + padding) + fm.getAscent());

		if (dragging != null) {
			g2.setColor(Color.black);
			g2.drawRect(dragging.x, dragging.y, dragging.width-1, dragging.height-1);
		}
		g2.setFont(saveFont);
		g2.setColor(saveColor);
        return new Rectangle(origin, getDimension());
	}
	
}
