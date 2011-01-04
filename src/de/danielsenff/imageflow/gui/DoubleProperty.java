package de.danielsenff.imageflow.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.danielsenff.imageflow.controller.ParameterWidgetController;
import de.danielsenff.imageflow.models.parameter.DoubleParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.ParameterWidgetFactory;

public class DoubleProperty implements Property {

	private String label;
	final DoubleParameter parameter;
	JComponent component;
	
	public DoubleProperty(DoubleParameter param) {
		this.label = param.getDisplayName();
		this.parameter = param;
		
		
		// choose component based on parameter-widget-type
		this.component = ParameterWidgetFactory.createTextField(param);
	}
	
	public void addListener() {

	}

	public JComponent getComponent() {
		JPanel widget = new JPanel();
		widget.add(new JLabel(label));
		widget.add(component);
		return component;
	}

	public String getLabel() {
		return label;
	}

	public Parameter getParameter() {
		return parameter;
	}

}
