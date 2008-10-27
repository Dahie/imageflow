/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.

This source code is based on package sun.beanbox
*/


package property;

// Support for PropertyEditors that use tags.

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

class PropertySelector extends Choice implements ItemListener {

    PropertySelector(PropertyEditor pe) {
	editor = pe;
	String tags[] = editor.getTags();
	for (int i = 0; i < tags.length; i++) {
	    addItem(tags[i]);
	}
	select(0);
	// This is a noop if the getAsText is not a tag.
	select(editor.getAsText());
	addItemListener(this);
    }

    public void itemStateChanged(ItemEvent evt) {
	String s = getSelectedItem();
	editor.setAsText(s);
    }

    public void repaint() {
	select(editor.getAsText());
    }

    PropertyEditor editor;    
}
