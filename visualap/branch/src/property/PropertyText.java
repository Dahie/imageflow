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

// Support for a PropertyEditor that uses text.


import java.awt.*;
import java.awt.event.*;
import java.beans.*;

class PropertyText extends TextField implements KeyListener, FocusListener {

    PropertyText(PropertyEditor pe) {
	super(pe.getAsText());
	editor = pe;
	addKeyListener(this);
	addFocusListener(this);
    }

    public void repaint() {
	setText(editor.getAsText());
    }

    protected void updateEditor() {
	try {
	    editor.setAsText(getText());
	} catch (IllegalArgumentException ex) {
	    // Quietly ignore.
	}
    }
    
    //----------------------------------------------------------------------
    // Focus listener methods.

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
    	updateEditor();
    }
    
    //----------------------------------------------------------------------
    // Keyboard listener methods.

    public void keyReleased(KeyEvent e) {
 	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	    updateEditor();
	}
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    //----------------------------------------------------------------------
    private PropertyEditor editor;
}
