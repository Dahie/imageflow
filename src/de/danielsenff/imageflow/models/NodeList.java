/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* 
This class supports a list of changeable items, the list itself is changeable...

javalc6
*/
package de.danielsenff.imageflow.models;
import java.util.HashMap;
import java.util.Vector;

import visualap.Node;

interface Changeable {
	public void setChanged(boolean status);
	public boolean isChanged();
}

interface Labelable {
	public void setLabel(String label);
	public String getLabel();
}


public class NodeList<E extends Node> extends Vector<E> {
    private transient HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();

	public boolean add(E aNode, int id) {
		//aNode.setLabel(id, aNode);
		return super.add(aNode);
	}

	public void setChanged(boolean status) {
		for (E aNode : this)
			aNode.setChanged(status);
	}


	public void clear() {
		super.clear();
		nodeMap.clear();
	}	

	public boolean remove(E aNode) {
		nodeMap.remove(aNode.getLabel());
		return super.remove(aNode);
	}

	/**
	 * Returns a map of all uniue node IDs and their corresponding {@link Node}.
	 * @return
	 */
	public HashMap<Integer, Node> getNodeMap() {
		return this.nodeMap;
	}

	/**
	 * Returns the unit by the given id.
	 * @param id
	 * @return
	 */
	public Node getNodeByID(int id) {
		return getNodeMap().get(id);
	}
	
	/**
	 * Number of nodes in this List.
	 * @return
	 */
	public int getSize() {
		return super.size();
	}
	
}