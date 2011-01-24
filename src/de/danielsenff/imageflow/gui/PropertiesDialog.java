/**
 * 
 */
package de.danielsenff.imageflow.gui;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import de.danielsenff.imageflow.models.parameter.DoubleParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;

/**
 * @author dahie
 *
 */
public class PropertiesDialog extends JDialog implements ComponentForm {

	private FormPanel formPanel;




	public PropertiesDialog(final String title, final JFrame parent) {
		super(parent);
		setTitle(title);
		
		formPanel = new FormPanel();
		setContentPane(formPanel);
	}
	
	
	
	
	public static void main(final String[] args) {
		final PropertiesDialog dia = new PropertiesDialog("Properties", null);
		
		final Property doublePro = new DoubleProperty(new DoubleParameter("Double",	 2.1, "a double is a great number"));
		final DoubleParameter param = new DoubleParameter("Double2",	 2.2, "a double is a great number");
		final Property doublePro2 = new DoubleProperty(param);
		final Property doublePro3 = new DoubleProperty(new DoubleParameter("Double3",	 2.3, "a double is a great number"));
		
		final ArrayList<Property> group = new ArrayList<Property>();
		group.add(doublePro);
		group.add(doublePro2);
		
		dia.addMessage("This is the new properties dialog.");
		//dia.add(doublePro2);
		dia.add(param);
		dia.addFormset("title", group);

		
		dia.pack();
		dia.setVisible(true);
	}

	public void add(Parameter param) {
		formPanel.add(param);
	}

	public void addFormset(String title, ArrayList<Property> group) {
		formPanel.addFormset(title, group);
	}


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

	public void add(Property property) {
		formPanel.add(property);
	}

}
