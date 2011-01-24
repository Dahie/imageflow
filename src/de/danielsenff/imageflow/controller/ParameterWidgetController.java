package de.danielsenff.imageflow.controller;

import java.util.Collection;

import javax.swing.JToolBar;

import de.danielsenff.imageflow.gui.FormPanel;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class ParameterWidgetController {

	
	/**
	 * Create a {@link JToolBar} with all required widgets based on 
	 * the Parameters of the given unit.
	 * @param unit
	 * @return
	 */
	public static JToolBar createToolbarFromUnit(UnitElement unit) {
		
		JToolBar dash = new JToolBar(unit.getLabel());
		
		FormPanel formPanel = new FormPanel();
		formPanel.setBackground(unit.getColor());
		Collection<Parameter> parameters = unit.getParameters();
		
		for (final Parameter parameter : parameters) {
			formPanel.add(parameter);
		}
		
		dash.add(formPanel);
		return dash;
	}
	
	
}
