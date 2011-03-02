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
package de.danielsenff.imageflow.models.delegates;

import java.awt.Color;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;


/**
 * This class is a Meta-Class for the {@link UnitElement}.
 * Basically what this does is describe a UnitElement 
 * and give the interface to instantiate a new UnitElement-Object.
 * @author Daniel Senff
 *
 */
public class UnitDelegate extends Delegate implements MutableTreeNode {

	/**
	 * default starting point of nodes
	 */
	public static Point POINT = new Point(40, 40);
	private NodeDescription unitDescription;
	private boolean withinJar;
	
	private Vector<MutableTreeNode> treenodes;
	private MutableTreeNode parent;
	
	
	/**
	 * @param unitName 
	 * @param tooltipText 
	 */
	private UnitDelegate(final String unitName, final String tooltipText) {
		this.name = unitName;
		this.toolTipText = tooltipText;
		this.treenodes = new Vector<MutableTreeNode>();
	}
	
	/**
	 * @param unitDescription
	 * @param withinJar 
	 */
	public UnitDelegate(final NodeDescription unitDescription, boolean withinJar) {
		this(unitDescription.getUnitName(), unitDescription.getHelpString());
		this.unitDescription = unitDescription;
		this.withinJar = withinJar;
	}

	/**
	 * Instantiates an object of this {@link UnitElement}
	 * @param origin 
	 * @return 
	 */
	public UnitElement buildUnit(final Point origin) {
		return UnitFactory.createProcessingUnit(unitDescription, origin);
	}
	
	/**
	 * Set the name.
	 * @param name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the name
	 * @return
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set the tooltip.
	 * @param toolTipText
	 */
	public void setToolTipText(final String toolTipText) {
		this.toolTipText = toolTipText;
	}

	/**
	 * Get the ToolTip.
	 * @return
	 */
	public String getToolTipText() {
		return toolTipText;
	}
	
	/**
	 * Returns the {@link UnitDescription}.
	 * @return
	 */
	public NodeDescription getUnitDescription() {
		return unitDescription;
	}
	
	/**
	 * Get Color of unit.
	 * @return
	 */
	public Color getColor() {
		return unitDescription.getColor();
	}
	
	@Override
	public String toString() {
		return super.name;
	}

	public void insert(MutableTreeNode arg0, int arg1) {
		this.treenodes.add(arg1, arg0);
	}

	public void remove(int arg0) {
		this.treenodes.remove(arg0);
	}

	public void remove(MutableTreeNode arg0) {
		this.treenodes.remove(arg0);
	}

	public void removeFromParent() {
		this.parent.remove(this);
	}

	public void setParent(MutableTreeNode node) {
		this.parent = node;
	}

	public void setUserObject(Object arg0) {
		
	}

	public Enumeration children() {
		return null;
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int index) {
		return this.treenodes.get(index);
	}

	public int getChildCount() {
		return this.treenodes.size();
	}

	public int getIndex(TreeNode arg0) {
		for (int i = 0; i < treenodes.size(); i++) {
			if(arg0.equals(treenodes.get(i))) 
				return i;
		}
		return 0;
	}

	public TreeNode getParent() {
		return this.parent;
	}

	public boolean isLeaf() {
		return treenodes.isEmpty();
	}

	/**
	 * Returns the name of the xml-definition of this node.
	 * @return
	 */
	public String getXMLPath() {
		return this.unitDescription.getXMLName();
	}
	
}
