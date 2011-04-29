/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* class Node

This class is used to create nodes in a graph.
A node can contain an <object>, e.g. a bean.
This class provides support for several features: blabla....


javalc6
*/
package visualap;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.danielsenff.imageflow.models.Selectable;

public abstract class Node implements Selectable, Cloneable {
	

	/**
	 * number of units instantiated, incremented with each new object
	 */
	static int ids;

	/**
	 * the id of this node
	 */
	protected int nodeID;
	
	protected Rectangle dragging=null;
	/**
	 * Origin/Position of this Node on the workspace.
	 */
	protected Point origin = new Point(0, 0);
	private Dimension dimension = new Dimension(0, 0);
    protected boolean selected=false, changed=false;
    /**
     * Displayed label of the node.
     */
    protected String label;

    // constructor not to be used, XMLEncoder/XMLDecoder
	public Node() {
		ids++;
		this.nodeID = ids;
	}


	// basic constructor
	public Node(Point origin) {
		this();
		this.origin = origin;
	}

	public String getLabel() {
		return(label);
	}

	public void setLabel(String label) {
		this.label = label;
	}
	

	/**
	 * Returns the ID of this Unit.
	 * @return
	 */
	public int getNodeID() {
		return this.nodeID;
	}
	


	/**
	 * Position on the workspace.
	 * @return
	 */
	public Point getOrigin() {
		return(origin);
	}

	public void setOrigin(Point aOrigin) {
		origin.setLocation(aOrigin);
	}


	public Object contains(int x, int y) {
		if ((x >= origin.x)&&(x < origin.x + getDimension().width)
				&&(y >= origin.y)&&(y < origin.y + getDimension().height)) 
			return this;
		else return null;
	}

	public boolean contained(Rectangle r) {
		return(r.contains(new Rectangle(origin, getDimension())));
	}

	public abstract Node clone() throws CloneNotSupportedException;

	
	public abstract Rectangle paint(Graphics g, ImageObserver io);

	public void setSelected(boolean sel) {
		selected = sel;
	}

	public void setChanged(boolean status) {
		changed = status;
	}

	public boolean isChanged() {
		return changed;
	}

	public void translate(int x, int y) {
		origin.translate(x,y);
		changed = true;
	}

	public void drag(int dx, int dy) {
		if(dragging != null)
			dragging.setLocation(origin.x+dx, origin.y+dy);
	}

	public void drag(boolean status) {
		if (status) dragging = new Rectangle(origin, getDimension());
		else dragging = null;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}


	public Dimension getDimension() {
		return dimension;
	}

}
