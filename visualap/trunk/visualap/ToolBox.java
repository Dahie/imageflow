/*
Version 1.0, 30-12-2007, First release
Version 1.1, 03-02-2008, prepared for MDI support

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/*
The ToolBox is a panel that shows icons and ID strings
for the available JavaBeans in the current VisualAp.

javalc6

*/

package visualap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.*;

interface callback {
	void doInsert(Class bean, String beanName);
	boolean isNull();
}

class ToolBox extends JPanel implements MouseListener {

	ArrayList<BeanDelegate> beans;
	callback cb;

    private int topPad = 0;
    private int sidePad = 0;
    private final static int rowHeight = 20;

    private static Cursor crosshairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    private static Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	ToolBox(ArrayList<BeanDelegate> beans, callback cb) {
		this.beans = beans;
		this.cb = cb;
//		setLayout(null);
		setBackground(new Color(224, 224, 224));
		setFont(new Font("Dialog", Font.PLAIN, 10));
		addMouseListener(this);
		doLayout();
	}



    public Dimension getPreferredSize() {	
		if ((beans != null) && beans.size() != 0) {
			return new Dimension(145, rowHeight*(beans.size()+1) + getInsets().bottom + 32);
		}  else {
			return new Dimension(145, 0); // empty toolbox
		}
    }

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		Color saveColor = g.getColor();
		Font saveFont = g.getFont();
		g.setFont(new Font("Arial", Font.PLAIN, 12));
//		topPad = frame.getInsets().top;
		sidePad = getInsets().left;

		for (int i = 0; i < beans.size(); i++) {
			String name = beans.get(i).name;
			int ix = name.lastIndexOf('.');
			if (ix >= 0) {
				name = name.substring(ix+1);
			}
			g.drawString(name, sidePad + 21, topPad + (rowHeight*i) + rowHeight-4);
			Image img = beans.get(i).icon;	
			if (img != null)
				g.drawImage(img, sidePad+2, topPad + (rowHeight*i) + 2, 16, 16, this);
		}
		g.setFont(saveFont);
//		g.setColor(saveColor);
    }	


    //----------------------------------------------------------------------

    // Mouse listener methods for this panel.

    public void mouseClicked(MouseEvent evt) {
    }

    public void mousePressed(MouseEvent evt) {
		int row = (evt.getY() - topPad)/rowHeight;
		if (row < beans.size()) {
			synchronized (this) {
//					Object myBean = beans.get(row).clazz.newInstance();
				setCursor(crosshairCursor);
				// do the insertion.
				cb.doInsert(beans.get(row).clazz, beans.get(row).name);
			}
		}
    }

    public void mouseReleased(MouseEvent evt) {
    }

    public void mouseEntered(MouseEvent evt) {
// set cursor to default if the bean has been inserted in gparent (GPanel)
// 
		if (cb.isNull())
			setCursor(defaultCursor); 
    }

    public void mouseExited(MouseEvent evt) {
    }

    public void mouseDragged(MouseEvent evt) {
    }

    public void mouseMoved(MouseEvent evt) {
    }

}
