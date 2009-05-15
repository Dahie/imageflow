/**
 * 
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Color;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import visualap.Delegate;

/**
 * This class is a Meta-Class for the {@link UnitElement}.
 * Basically what this does is describe a UnitElement 
 * and give the interface to instantiate a new UnitElement-Object. 
 * the GPanel requires it, but nothing else
 * @author danielsenff
 *
 */
public class UnitDelegate extends Delegate implements MutableTreeNode {

	/**
	 * default startpoint of nodes
	 */
	public static Point POINT = new Point(40, 40);
	private UnitDescription unitDescription;
	
	/**
	 * @param unitName 
	 * @param tooltipText 
	 */
	private UnitDelegate(final String unitName, final String tooltipText) {
		this.name = unitName;
		this.toolTipText = tooltipText;
		this.treenodes = new Vector<MutableTreeNode>();
	}
	
	public UnitDelegate(UnitDescription unitDescription) {
		this(unitDescription.getUnitName(), unitDescription.getHelpString());
		this.unitDescription = unitDescription;
	}

	/**
	 * Instantiates an object of this {@link UnitElement}
	 * @param origin 
	 * @return 
	 */
	public UnitElement createUnit(final Point origin) {
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
	
	public UnitDescription getUnitDescription() {
		return unitDescription;
	}
	
	public Color getColor() {
		return unitDescription.color;
	}
	
	@Override
	public String toString() {
		return super.name;
	}

	
	private Vector<MutableTreeNode> treenodes;
	private MutableTreeNode parent;
	
	public void insert(MutableTreeNode arg0, int arg1) {
		this.treenodes.add(arg1, arg0);
	}

	public void remove(int arg0) {
		this.treenodes.remove(arg0);
	}

	public void remove(MutableTreeNode arg0) {
		this.remove(arg0);
	}

	public void removeFromParent() {
		this.parent.remove(this);
	}

	public void setParent(MutableTreeNode arg0) {
		this.parent = arg0;
	}

	public void setUserObject(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	public Enumeration children() {
		return null;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public TreeNode getChildAt(int arg0) {
		return null;
	}

	public int getChildCount() {
		return 0;
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
	
}
