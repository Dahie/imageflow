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
package de.danielsenff.imageflow.models.connection;
import java.awt.Point;


import de.danielsenff.imageflow.models.Lockable;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.unit.Node;

/**
 * Pin is the connecting point on a UnitElement between Connections.
 * @author Daniel Senff
 *
 */
public abstract class Pin implements Connectable, Cloneable, Lockable {
	/**
	 * Node that contains this Pin
	 */
	protected Node parent;
	protected int index;
	/**
	 * Integer value used only for analysis of graph
	 */
	transient protected int mark; 

	/**
	 * the name to be displayed in the context help
	 */
	protected String displayName;
	

	/**
	 * the short name to be displayed on the unit's icon
	 */
	protected String shortDisplayName = "I";
	
	
	/**
	 * Type of data expected from the connected {@link Output}.
	 */
	protected DataType dataType;
	
	/**
	 *  type can be "input" or "output"
	 * @param type 
	 * @param index Pin ID
	 * @param nump Number of Pins on this node
	 * @param parent Parent Node
	 */
	public Pin (DataType type, int index, Node parent) {
		this.dataType = type;
		this.index = index;
		this.parent = parent;
	}


	/**
	 * Node to which this Pin is attached.
	 * @return
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Pin ID
	 * @return
	 */
	public int getIndex () {
		return index;
	}

	/**
	 * Name of this Pin.
	 * @return
	 */
	public String getName () {
		return parent.getLabel()+"." + dataType.getClass().getSimpleName() + index + "." + displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Get the abbreviated DisplayName
	 * @return
	 */
	public String getShortDisplayName() {
		return shortDisplayName;
	}
	
	
	/**
	 * {@link DataType} this Pin is permitting.
	 * @return
	 */
	public DataType getDataType() {
		return dataType;
	}


	
	
	/*
	 * Markable :)
	 */
	
	/**
	 * Get the value of the mark.
	 * @return
	 */
	public int getMark () {
		return mark;
	}

	/**
	 * Set the value of the mark.
	 * @param mark
	 */
	public void setMark (int mark) {
		this.mark = mark;
	}
	
	/**
	 * Returns true, if this Output has been marked.
	 * The Mark is not 0.
	 * @return
	 */
	public boolean isMarked() {
		return (this.mark != 0);
	}

	/**
	 * Returns true, if this Pin has not been marked.
	 * The Mark is not 0.
	 * @return
	 */
	public boolean isUnmarked() {
		return (this.mark == 0);
	}


	/**
	 * Coordinates of this pin on the Panel.
	 * @return
	 */
	public abstract Point getOrigin();

	/**
	 * Convenience for calling DataType.isCompatible(DataType);
	 * @param pin
	 * @return
	 */
	public boolean isCompatible(Pin pin) {
		return getDataType().isCompatible(pin.getDataType());
	}
	
}
