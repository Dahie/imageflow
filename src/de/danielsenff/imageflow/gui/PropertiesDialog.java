/**
 * 
 */
package de.danielsenff.imageflow.gui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import de.danielsenff.imageflow.models.parameter.Parameter;

/**
 * @author dahie
 *
 */
public class PropertiesDialog extends JDialog implements ComponentForm, KeyListener {

	private FormPanel formPanel;

	public PropertiesDialog(final String title, final JFrame parent) {
		super(parent);
		setTitle(title);
		
		formPanel = new FormPanel();
		setContentPane(formPanel);
		setResizable(false);
		setLocationByPlatform(true);
		addKeyListener(this);
	}
	
	public void add(Parameter param) {
		formPanel.add(param);
	}

	/*public void addFormset(String title, ArrayList<Property> group) {
		formPanel.addFormset(title, group);
	}*/


	public void addMessage(String string) {
		formPanel.addMessage(string);
	}

	public void showDialog() {
		this.pack();
		this.setVisible(true);
	}

	public void addSeparator() {
		formPanel.addSeparator();
	}

	public void addForm(String label, Component component) {
		formPanel.addForm(label, component);
	}

	/*public void add(Property property) {
		formPanel.add(property);
	}*/

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode(); 
		
		if(keyCode == KeyEvent.VK_ESCAPE) {
			dispose(); 
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

}
