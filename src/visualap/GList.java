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
package visualap;
import java.util.*;
import java.io.*;

interface Changeable {
	public void setChanged(boolean status);
	public boolean isChanged();
}

interface Labelable {
	public void setLabel(String label);
	public String getLabel();
}


public class GList<E extends Changeable&Labelable> extends Vector<E> {
	private transient boolean changed=false;
    private transient HashMap<String, Object> labels = new HashMap<String, Object>();

	public boolean add(E aNode, String label) {
		changed=true;
		aNode.setLabel(getUniqueName(label, aNode));
		return super.add(aNode);
	}

	public void setChanged(boolean status) {
		for (E aNode : this)
			aNode.setChanged(status);
		changed = status;
	}

	public boolean isChanged() {
		if (!changed)	{
			for (E aNode : this)
				if (aNode.isChanged()) return true;
		}
		return changed;
	}

	public void clear() {
//		for (E aNode : this)
//			aNode.clear();
		super.clear();
		labels.clear();
		changed=true;
	}	

	public boolean remove(E aNode) {
//		aNode.clear();
		boolean result = super.remove(aNode);
		labels.remove(aNode.getLabel());
		if (result) changed = true;
		return result;
	}

	public void updateLabels() {
		labels.clear();
		for (E aNode : this)
			labels.put(aNode.getLabel(), aNode);
	}

	public HashMap<String, Object> getLabels() {
		return labels;
	}

	/**
	 * zz build a unique name: da migliorare!
	 * @param s
	 * @param value
	 * @return
	 */
	String getUniqueName(String s, Object value) {
		if (! labels.containsKey(s)) {
			labels.put(s, value);
			return s;
		}
		char last = s.charAt(s.length()-1);
		int count = 0;
		if ((last >= '0')&&(last <= '9')) {
			s = s.substring(0, s.length()-1);
			count = last - '0' + 1;
		}
		while (labels.containsKey(s+count)) {
			count += 1;
		}
		labels.put(s+count, value);
		return s+count;
	}

}