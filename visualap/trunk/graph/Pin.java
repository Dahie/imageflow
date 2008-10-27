/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* class Pin

This class is used for pins (part of Node)

javalc6
*/
package graph;
import java.util.HashMap;
import java.awt.Point;

public class Pin {
	protected Node parent; // node that contains this Pin
	protected int i, nump;
	protected String type;
	transient protected int mark; // used only for analysis of graph

// type can be "input" or "output"
	public Pin (String type, int i, int nump, Node parent) {
		this.type = type;
		this.i = i;
		this.nump = nump;
		this.parent = parent;
	}

	static Pin getPin(String str, HashMap<String, Object> labels) {
		String label = str.substring(0,str.lastIndexOf('.'));
		String pin = str.substring(str.lastIndexOf('.')+1);
		NodeBean n = (NodeBean) labels.get(label);
		if (pin.startsWith("input"))	{
			return n.inPins[Integer.parseInt(pin.substring(5))]; // Note: 5 is the length of string "input"
		} else {
			return n.outPins[Integer.parseInt(pin.substring(6))]; // Note: 6 is the length of string "output"
		}
	}

	public Node getParent () {
		return parent;
	}

	public int getIndex () {
		return i;
	}

	public Point getLocation () {
		if (type.equals("input"))
			return new Point(parent.origin.x, parent.origin.y +
				(parent.dimension.height*i+parent.dimension.height/2)/nump);	
		else // type.equals("output")
			return new Point(parent.origin.x+parent.dimension.width, 
				parent.origin.y +(parent.dimension.height*i+parent.dimension.height/2)/nump);	
	}

	public String getName () {
		return parent.getLabel()+"."+type+i;
	}

	public int getMark () {
		return mark;
	}

	public void setMark (int mark) {
		this.mark = mark;
	}

}
