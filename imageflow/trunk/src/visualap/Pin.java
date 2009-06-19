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
package visualap;
import java.awt.Point;

import de.danielsenff.imageflow.models.Connectable;
import de.danielsenff.imageflow.models.Output;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.unit.UnitElement;

public abstract class Pin implements Connectable {
	protected Node parent; // node that contains this Pin
	protected int index;
	transient protected int mark; // used only for analysis of graph

	/**
	 * Type of data expected from the connected {@link Output}.
	 */
	protected DataType dataType;
	protected String type = "Image";
	
	/**
	 *  type can be "input" or "output"
	 * @param type 
	 * @param index Pin ID
	 * @param nump Number of Pins on this node
	 * @param parent Parent Node
	 */
	public Pin (DataType type, int index, Node parent) {
//		this.type = type;
		this.dataType = type;
		this.index = index;
		
		this.parent = parent;
	}


	public Node getParent () {
		return parent;
	}

	/**
	 * Pin ID
	 * @return
	 */
	public int getIndex () {
		return index;
	}

	public String getName () {
		return parent.getLabel()+"."+type+index;
	}

	public DataType getDataType() {
		return dataType;
	}


	
	
	/*
	 * Markable :)
	 */
	
	public int getMark () {
		return mark;
	}

	public void setMark (int mark) {
		this.mark = mark;
	}
	
	/**
	 * Returns true, if this Output has been marked.
	 * The Mark is not 0.
	 * @return
	 */
	public boolean isMarked() {
		return (this.mark == 0) ? false : true;
	}

	/**
	 * Returns true, if this Output has not been marked.
	 * The Mark is not 0.
	 * @return
	 */
	public boolean isUnmarked() {
		return (this.mark == 0) ? true : false;
	}


	public abstract Point getLocation();

	/**
	 * Convenience for calling DataType.isCompatible(DataType);
	 * @param pin
	 * @return
	 */
	public boolean isCompatible(Pin pin) {
		return getDataType().isCompatible(pin.getDataType());
	}
	
}
