/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* class HelpWindow

This class is the help file browser.

javalc6
*/
package visualap;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.io.*;

public class HelpWindow extends JFrame{
	JEditorPane editorPane;
	public HelpWindow(int x, int y) {
		super("Help Window");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});

		editorPane = new JEditorPane(); 
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if (e instanceof HTMLFrameHyperlinkEvent) {
				((HTMLDocument)editorPane.getDocument()).processHTMLFrameHyperlinkEvent(
					(HTMLFrameHyperlinkEvent)e);
				} else {
					try {
						editorPane.setPage(e.getURL());
					} catch (IOException ex) {
						ErrorPrinter.printInfo("Attempted to read a bad URL: " + e.getURL());
					}
				}
			}
			}
		});		
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(editorScrollPane);
		setLocation(x, y);
		setSize(450,500);
	}

    protected void setPage(java.net.URL helpURL) {
        if (helpURL != null) {
            try {
				setVisible(true);
				setTitle(helpURL.toString());
                editorPane.setPage(helpURL);
            } catch (IOException ex) {
                ErrorPrinter.printInfo("Attempted to read a bad URL: " + helpURL);
            }
        } else {
            ErrorPrinter.printInfo("Help URL is null");
        }

        return;
    }


}