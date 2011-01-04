/**
 * 
 */
package de.danielsenff.imageflow.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import de.danielsenff.imageflow.models.parameter.DoubleParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.ParameterWidgetFactory;

/**
 * @author dahie
 *
 */
public class PropertiesDialog extends JDialog {

	GridBagConstraints c;
	int rows;
	
	public PropertiesDialog(final String title, JFrame parent) {
		super(parent);
		setTitle(title);
		
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.insets = new Insets(4,4,4,4);  //top padding
		c.anchor = GridBagConstraints.LINE_START;
		c.ipady = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		rows = 0;
		
		addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {}
			
			public void keyReleased(KeyEvent e) {}
			
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.out.println("off");
				}
			}
		});
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
	public void addMessage(String message) {
		add(new JLabel(message));
	}
	
	public void addSeparator() {
		add(new JSeparator());
	}
	
	public void addForm(String label, final Component component) {
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
	
	/**
	 * add Widget to Dialog
	 * @param property
	 */
	public void add(Property property) {
		addForm(property.getLabel(), property.getComponent());
	}
	
	public void add(Parameter parameter) {
		addForm(parameter.getDisplayName(), ParameterWidgetFactory.createForm(parameter));
	}
	
	public void addFormset(String title, ArrayList<Property> group) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.setLayout(new GridBagLayout());
		int r = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		for (Property property : group) {
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
	
	
	public static void main(String[] args) {
		PropertiesDialog dia = new PropertiesDialog("Properties", null);
		
		Property doublePro = new DoubleProperty(new DoubleParameter("Double",	 2.1, "a double is a great number"));
		DoubleParameter param = new DoubleParameter("Double2",	 2.2, "a double is a great number");
		Property doublePro2 = new DoubleProperty(param);
		Property doublePro3 = new DoubleProperty(new DoubleParameter("Double3",	 2.3, "a double is a great number"));
		
		ArrayList<Property> group = new ArrayList<Property>();
		group.add(doublePro);
		group.add(doublePro2);
		
		dia.addMessage("This is the new properties dialog.");
		//dia.add(doublePro2);
		dia.add(param);
		dia.addFormset("title", group);

		
		dia.pack();
		dia.setVisible(true);
	}

	public void showDialog() {
		this.pack();
		this.setVisible(true);
	}
}
