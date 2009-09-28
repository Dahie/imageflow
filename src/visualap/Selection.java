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
This class supports a list of selectable items

javalc6
*/
package visualap;
import java.util.*;

interface Selectable {
	public void setSelected(boolean sel);
}

public class Selection<E extends Selectable> extends ArrayList<E> {
	public boolean add(E element) {
		element.setSelected(true);
		return super.add(element);
	}

	public boolean remove(Selectable o) {
		boolean result = super.remove(o);
		if (result) o.setSelected(false);
		return result;
	}

	public void clear() {
		for (E element : this)
			element.setSelected(false);
		super.clear();
	}	
}