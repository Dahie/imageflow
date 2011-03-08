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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.ParameterWidgetFactory;

public class FormPanel extends JPanel implements ComponentForm, MouseListener {

	private GridBagConstraints c;
	private int rows;
	private LinkedHashMap<Parameter, ArrayList<Component>> parameters;
	private boolean showAllParameters = false;
	
	public FormPanel(boolean showAllParameters) {
		this();
		this.showAllParameters = showAllParameters;
	}
	
	public FormPanel() {
		
		setLayout(new GridBagLayout());
		addMouseListener(this);
		this.parameters = new LinkedHashMap<Parameter, ArrayList<Component>>();
		
		c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2);  //top padding
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
		addForm(new JLabel(label), component);
	}
	
	/**
	 * Adds a new Form-Element using the label and the component.
	 * @param label
	 * @param component
	 */
	public void addForm(final JLabel label, final Component component) {
		if (component != null) {
			c.gridwidth = 1;
			c.gridheight = 1;
			
			c.gridx = 0;
			c.gridy = rows+1;
			add(label, c);
			
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
	/*public void add(final Property property) {
		properties.add(property);
		if (property.getParameter().isHidden()) {
			addForm(property.getLabel(), property.getComponent());
		}
	}*/
	
	/**
	 * 
	 */
	public void add(final Parameter parameter) {
		ArrayList<Component> parameterComponents = new ArrayList<Component>();
		parameters.put(parameter, parameterComponents);
		
		String parameterName = parameter.getDisplayName();
		JComponent formComponent = ParameterWidgetFactory.createForm(parameter);
		JLabel labelComponent = new JLabel(parameterName);
		
		if(parameter.isHidden() && !this.showAllParameters ) {
			formComponent.setVisible(false);
			labelComponent.setVisible(false);
		}
		
		parameterComponents.add(formComponent);
		parameterComponents.add(labelComponent);
		
		addForm(labelComponent, formComponent);
	}
	
	/*public void addFormset(final String title, final ArrayList<Property> group) {
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
	}*/

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
		if(event.isPopupTrigger() && !this.showAllParameters) {
			JPopupMenu popup = new JPopupMenu();
			for (Parameter parameter : parameters.keySet()) {
				JCheckBoxMenuItem item = new JCheckBoxMenuItem(parameter.getDisplayName(), !parameter.isHidden());
				popup.add(item);
				item.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JCheckBoxMenuItem jMenuItem = (JCheckBoxMenuItem)e.getSource();
						String itemName = jMenuItem.getText();
						for (Parameter parameter : parameters.keySet()) {
							if (parameter.getDisplayName() == itemName) {
								ArrayList<Component> components = parameters.get(parameter);
								for (Component component : components) {
									component.setVisible(!component.isVisible());
								}
								jMenuItem.setSelected(!jMenuItem.isSelected());
								parameter.setHidden(jMenuItem.isSelected());
							}
						}
					}
				});
			}
			
			popup.show(event.getComponent(), event.getX(), event.getY());
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
