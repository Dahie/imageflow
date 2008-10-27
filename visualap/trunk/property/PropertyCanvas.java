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

// Support for drawing a property value in a Canvas.
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

class PropertyCanvas extends Canvas implements MouseListener {

    PropertyCanvas(Frame frame, PropertyEditor pe) {
	this.frame = frame;
	editor = pe;
	addMouseListener(this);
    }

    public void paint(Graphics g) {
	Rectangle box = new Rectangle(2, 2, getSize().width - 4, getSize().height - 4);
	editor.paintValue(g, box);
    }

    private static boolean ignoreClick = false;

    public void mouseClicked(MouseEvent evt) {
	if (! ignoreClick) {
	    try {
		ignoreClick = true;
		int x = frame.getLocation().x - 30;
		int y = frame.getLocation().y + 50;
		new PropertyDialog(frame, editor, x, y);
	    } finally {
		ignoreClick = false;
	    }
	}
    }

    public void mousePressed(MouseEvent evt) {
    }

    public void mouseReleased(MouseEvent evt) {
    }

    public void mouseEntered(MouseEvent evt) {
    }

    public void mouseExited(MouseEvent evt) {
    }

    private Frame frame;
    private PropertyEditor editor;
}
