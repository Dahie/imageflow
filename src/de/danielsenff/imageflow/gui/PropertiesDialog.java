/**
 * Copyright (C) 2008-2011 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.danielsenff.imageflow.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

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
		
		formPanel = new FormPanel(true);
		setContentPane(formPanel);
		setResizable(false);
		setLocationRelativeTo(null);
		addKeyListener(this);
	}
	
	public void add(Parameter param) {
		formPanel.add(param);
	}

	public void addMessage(String string) {
		formPanel.addMessage(string);
	}
	
	public void addMessage(String string, Color color) {
		formPanel.addMessage(string, color);
	}
	
	/**
	 * Add any kind of {@link JComponent}. This will span 2 columns.
	 */
	public void addComponent(JComponent component) {
		formPanel.add(component);
	}

	public void showDialog() {
		this.pack();
		this.setVisible(true);
	}
	
	public void showDialog(Point location) {
		this.setLocation(location);
		showDialog();
	}

	public void addSeparator() {
		formPanel.addSeparator();
	}

	public void addForm(String label, Component component) {
		formPanel.addForm(label, component);
	}

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
