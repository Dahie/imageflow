package de.danielsenff.imageflow.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import de.danielsenff.imageflow.gui.Dashboard;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.parameter.DoubleParameter;
import de.danielsenff.imageflow.models.parameter.IntegerParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.StringParameter;
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
		dash.setBackground(unit.getColor());
		Collection<Parameter> parameters = unit.getParameters();
		
		for (final Parameter parameter : parameters) {
			
			dash.add(new JLabel(parameter.getDisplayName()));
			JComponent widget = null;
			
			if(parameter instanceof DoubleParameter) {
				widget = new JTextField(((DoubleParameter) parameter).getValue()+"");
				widget.addKeyListener(new KeyListener() {
					
					public void keyTyped(KeyEvent e) {}
					public void keyReleased(KeyEvent e) {}
					public void keyPressed(KeyEvent e) {
						if(e.getKeyCode() == KeyEvent.VK_ENTER) {
							String string = ((JTextField)e.getSource()).getText();
							// TODO strip non number characters
							((DoubleParameter) parameter).setValue(Double.valueOf(string));
						}
					}
				});
			} else if (parameter instanceof IntegerParameter) {
				widget = new JTextField(((IntegerParameter) parameter).getValue()+"");
				widget.addKeyListener(new KeyListener() {
					
					public void keyTyped(KeyEvent e) {}
					public void keyReleased(KeyEvent e) {}
					public void keyPressed(KeyEvent e) {
						if(e.getKeyCode() == KeyEvent.VK_ENTER) {
							String string = ((JTextField)e.getSource()).getText();
							// TODO strip non number characters
							((IntegerParameter) parameter).setValue(Integer.valueOf(string));
						}
					}
				});
			} else if (parameter instanceof BooleanParameter) {
				widget = new JCheckBox(((BooleanParameter) parameter).getValue()+"");
			} else if (parameter instanceof ChoiceParameter) {
				widget = new JTextField(((StringParameter) parameter).getValue());
				//((ChoiceParameter) parameter).setValue((String) (gd.getNextChoice()));
				// set the ChoiceNumber to be able to save it
			} else if (parameter instanceof StringParameter) {
				widget = new JTextField(((StringParameter) parameter).getValue());
				widget.addKeyListener(new KeyListener() {
					
					public void keyTyped(KeyEvent e) {}
					public void keyReleased(KeyEvent e) {}
					public void keyPressed(KeyEvent e) {
						if(e.getKeyCode() == KeyEvent.VK_ENTER) {
							String string = ((JTextField)e.getSource()).getText();
							// TODO strip non number characters
							((StringParameter) parameter).setValue(string);
						}
					}
				});
			} 
			dash.add(widget);
		}
		
		return dash;
	}
	
	
}
