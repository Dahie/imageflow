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
// Support for PropertyEditor with custom editors.

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

class PropertyDialog extends Dialog implements ActionListener {

    private Button doneButton;
    private Component body;
    private final static int vPad = 5;
    private final static int hPad = 4;

    PropertyDialog(Frame frame, PropertyEditor pe, int x, int y) {
	super(frame, pe.getClass().getName(), true);
//zz	new WindowCloser(this);
	setLayout(null);

	body = pe.getCustomEditor();
	add(body);

	doneButton = new Button("Done");
	doneButton.addActionListener(this);
	add(doneButton);

	setLocation(x, y);
	setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
        // Button down.
	dispose();
    }

    public void doLayout() {
        Insets ins = getInsets();
	Dimension bodySize = body.getPreferredSize();
	Dimension buttonSize = doneButton.getPreferredSize();

	int width = ins.left + 2*hPad + ins.right + bodySize.width;
	int height = ins.top + 3*vPad + ins.bottom + bodySize.height +
							buttonSize.height;

        body.setBounds(ins.left+hPad, ins.top+vPad,
				bodySize.width, bodySize.height);

	doneButton.setBounds((width-buttonSize.width)/2,
				ins.top+(2*hPad) + bodySize.height,
				buttonSize.width, buttonSize.height);

	setSize(width, height);

    }

}

