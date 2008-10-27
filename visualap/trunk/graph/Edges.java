/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* Edge.java

javalc6
*/
package graph;
import java.util.ArrayList;
import java.util.HashMap;

public class Edges extends ArrayList<Edge> {
	public boolean add(Pin from, Pin to) {
		return super.add(new Edge(from, to));
	}

	public boolean add(String string, HashMap<String, Object> labels) {
		return super.add(new Edge(string, labels));
	}

	public boolean contains(Pin from, Pin to) {
		for (Edge aEdge : this) {
			if (aEdge.from.equals(from)&&aEdge.to.equals(to))
				return true;
			if (aEdge.from.equals(to)&&aEdge.to.equals(from))
				return true;
		}
		return false;
	}
}