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
import java.awt.Point;
import java.util.HashMap;

public class Pin {
	protected Node parent; // node that contains this Pin
	protected int i, nump;
	protected String type;
	transient protected int mark; // used only for analysis of graph

	/**
	 *  type can be "input" or "output"
	 * @param type 
	 * @param i Pin ID
	 * @param nump Number of Pins on this node
	 * @param parent Parent Node
	 */
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
		} 
		
		return n.outPins[Integer.parseInt(pin.substring(6))]; // Note: 6 is the length of string "output"
	}

	public Node getParent () {
		return parent;
	}

	/**
	 * Pin ID
	 * @return
	 */
	public int getIndex () {
		return i;
	}

	public Point getLocation () {
		if (type.equals("input")) {
			Point point = new Point(parent.origin.x, 
					parent.origin.y + (parent.getDimension().height*i+parent.getDimension().height/2)/nump);
			return point;
		} else {
			// type.equals("output")
			Point point = new Point(parent.origin.x+parent.getDimension().width, 
					parent.origin.y + (parent.getDimension().height*i+parent.getDimension().height/2)/nump);
			return point;
		}
				
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
