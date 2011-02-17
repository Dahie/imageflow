/**
 * Copyright (C) 2008-2010 Daniel Senff
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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import visualap.Node;

import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.ParameterWidgetFactory;

public class FormPanel extends JPanel implements ComponentForm, MouseListener {

	GridBagConstraints c;
	int rows;
	
	public FormPanel() {
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.insets = new Insets(4,4,4,4);  //top padding
		c.anchor = GridBagConstraints.LINE_START;
		c.ipady = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		rows = 0;
		
		
	}
	
	/**
	 * @param component
	 */
	@Override
	public Component add(final Component component) {
		c.gridwidth = 2;
		c.gridheight = 1;
		
		c.gridx = 0;
		c.gridy = rows+1;
		add(component, c);
		rows++;
		return component;
	}
	
	/**
	 * Add a message line.
	 * @param message
	 */
	public void addMessage(final String message) {
		add(new JLabel(message));
	}
	
	/**
	 * Add a {@link JSeparator} to the dialog.
	 */
	public void addSeparator() {
		add(new JSeparator());
	}
	
	/**
	 * Adds a new Form-Element using the label and the component.
	 * @param label
	 * @param component
	 */
	public void addForm(final String label, final Component component) {
		if (component != null) {
			c.gridwidth = 1;
			c.gridheight = 1;
			
			c.gridx = 0;
			c.gridy = rows+1;
			add(new JLabel(label), c);
			
			c.gridx = 1;
			c.gridy = rows+1;
			c.fill = GridBagConstraints.HORIZONTAL;
			
			add(component, c);
			rows++;
		}
	}
	
	/**
	 * add Widget to Dialog
	 * @param property
	 */
	public void add(final Property property) {
		addForm(property.getLabel(), property.getComponent());
	}
	
	/**
	 * 
	 */
	public void add(final Parameter parameter) {
		addForm(parameter.getDisplayName(), ParameterWidgetFactory.createForm(parameter));
	}
	
	public void addFormset(final String title, final ArrayList<Property> group) {
		final JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.setLayout(new GridBagLayout());
		int r = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		for (final Property property : group) {
			c.gridwidth = 1;
			c.gridheight = 1;
			c.gridx = 0;
			c.gridy = r;
			panel.add(new JLabel(property.getLabel()), c);
			
			c.gridx = 1;
			panel.add(property.getComponent(), c);
			r++;
		}
		
		c.gridwidth = 2;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = rows+1;
		add(panel, c);
		rows++;
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent event) {
		if(event.isPopupTrigger()) {
			JPopupMenu popup = new JPopupMenu();
			/*for (iterable_type iterable_element : iterable) {
				
			}*/
			popup.add("Form elements");
			
			popup.show(event.getComponent(), event.getX(), event.getY());
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
