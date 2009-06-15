/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* class NodeText

This class is used to create nodes in a graph.
A node can contain an <object>, e.g. a bean.
This class provides support for several features: blabla....

todo:
- setObject() e BeanDelegate sono in relazione, trovare il modo di unificare

javalc6
*/
package visualap;
import java.io.*;
import java.beans.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*; 
import java.lang.reflect.*;
import java.util.HashMap;

public class NodeText extends Node {
	protected String text;

// constructor not to be used, XMLEncoder/XMLDecoder
	public NodeText() {
		super();
	}


// basic constructor
	public NodeText(Point origin) {
		super(origin);
	}

// constructor for text box
	public NodeText(Point origin, String text) {
		super(origin);
		setText(text);
	}

	public String getText() {
		return(text);
	}

	public void setText(String text) {
		this.text = text;
	}


	public Object contains(int x, int y) {
		return super.contains(x,y);
	}

	public Node clone() throws CloneNotSupportedException {
// clone object translated by 4 pixels
		return new NodeText(new Point(origin.x+4, origin.y+4), text);
	}
	
	public Rectangle paint(Graphics g, ImageObserver io) {
		Color saveColor = g.getColor();
		Font saveFont = g.getFont();

			g.setFont(new Font("Arial", Font.PLAIN, 12));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(selected ? Color.red : new Color(250, 220, 100));
			if (text == null) {
				System.out.println("Node.paint: Null Pointer Exception");
				text = "*Null Pointer Exception*"; //zz now the exception is hidden, but what the problem?
			}
			getDimension().setSize(fm.stringWidth(text) + 10, fm.getHeight() + 4);
			g.fillRect(origin.x, origin.y, getDimension().width, getDimension().height);
			g.setColor(Color.black);
			g.drawRect(origin.x, origin.y, getDimension().width-1, getDimension().height-1);
			g.drawString(text, origin.x + 5, (origin.y + 2) + fm.getAscent());

		if (dragging != null) {
			g.setColor(Color.black);
			g.drawRect(dragging.x, dragging.y, dragging.width-1, dragging.height-1);
		}
		g.setFont(saveFont);
		g.setColor(saveColor);
        return new Rectangle(origin, getDimension());
    }


	public void clear() {
	}


}
