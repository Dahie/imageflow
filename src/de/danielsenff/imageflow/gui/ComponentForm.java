package de.danielsenff.imageflow.gui;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JSeparator;

import de.danielsenff.imageflow.models.parameter.Parameter;

public interface ComponentForm {

	public Component add(final Component component);
	
	/**
	 * Add a message line.
	 * @param message
	 */
	public void addMessage(final String message);
	
	/**
	 * Add a {@link JSeparator} to the dialog.
	 */
	public void addSeparator();
	
	/**
	 * Adds a new Form-Element using the label and the component.
	 * @param label
	 * @param component
	 */
	public void addForm(final String label, final Component component);
	
	/**
	 * add Widget to Dialog
	 * @param property
	 */
	public void add(final Property property);
	
	public void add(final Parameter parameter);
	
	public void addFormset(final String title, final ArrayList<Property> group);
		
	
	
}
