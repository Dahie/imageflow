package de.danielsenff.imageflow.gui;

import javax.swing.JComponent;

import de.danielsenff.imageflow.models.parameter.Parameter;

public interface Property {

	JComponent getComponent();
	String getLabel();
	Parameter getParameter();
	
	void addListener();
	
	
}
