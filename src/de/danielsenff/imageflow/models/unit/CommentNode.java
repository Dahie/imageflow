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
package de.danielsenff.imageflow.models.unit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import de.danielsenff.imageflow.models.ChangeListener;

import visualap.Node;


/**
 * Note-Node, sorry the pun, this is a Unit element, just for writing a note.
 * @author danielsenff
 *
 */
public class CommentNode extends AbstractUnit {

	/**
	 * Text of the Comment
	 */
	protected String text;
	
	/**
	 * @param point
	 * @param string
	 */
	public CommentNode(final Point point, final String string) {
		super(point, string);
		setLabel(string);
		setText(string);
	}
	

	/**
	 * Get the Text displayed in the CommentNode.
	 * @return
	 */
	public String getText() {
		return(text);
	}

	/**
	 * Set the Text displayed in the CommentNode.
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
		setLabel(text);
	}

	
	@Override
	public Node clone() throws CloneNotSupportedException {
		return new CommentNode(new Point(origin.x+15, origin.y+15), new String(text));
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
