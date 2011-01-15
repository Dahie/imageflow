package de.danielsenff.imageflow.models.parameter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ParameterWidgetFactory {


	public static JComponent createForm(final Parameter parameter) {
		try {
			if (parameter instanceof ChoiceParameter) {
				return createComboBox((ChoiceParameter)parameter);
			} else if (parameter instanceof IntegerParameter) {
				if (parameter.getOptions() != null 
						&& parameter.getOptions().get("as") != null 
						&& parameter.getOptions().get("as") instanceof String
						&& ((String)parameter.getOptions().get("as")).equalsIgnoreCase("slider")) {
					return createSlider((IntegerParameter)parameter);
				} else {
					return createTextField(parameter);
				}

			} else if (parameter instanceof DoubleParameter
					|| parameter instanceof StringParameter) {
				return createTextField(parameter);
			} else if (parameter instanceof BooleanParameter) {
				return createCheckBox((BooleanParameter)parameter);
			}

		} catch (Exception e) {
			System.err.println("caugh error, skip form");
		}
		return null;
	}

	private static JComponent createSlider(final IntegerParameter parameter) throws IllegalArgumentException {
		JPanel panel = new JPanel();
		
		final JTextField component = new JTextField(parameter.getValue().toString());
		component.setEnabled(!parameter.isReadOnly());
		component.setColumns(5);
		
		
		int min = (Integer) parameter.getOptions().get("min");
		int max = (Integer) parameter.getOptions().get("max");
		int value = (Integer) parameter.getValue();

		Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(min, new JLabel(Integer.toString(min)));
		labels.put(max, new JLabel(Integer.toString(max)));
		
		final JSlider slider = new JSlider(min, max, value);
		slider.setLabelTable(labels);
		slider.setPaintTicks(true);
		//slider.setPaintLabels(true);
		slider.setEnabled(!parameter.isReadOnly());
		slider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent event) {
				int newValue = ((JSlider) event.getSource()).getValue();
				component.setText(Integer.toString(newValue));
				parameter.setValue(newValue);
				slider.setValue(newValue);
			}
		});
		component.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String string = ((JTextField)e.getSource()).getText();
					// TODO strip non number characters
					parameter.setValue(Integer.valueOf(string));
					slider.setValue(Integer.valueOf(string));
				}
			}
		});
		
		panel.add(new JLabel(Integer.toString(min)));
		panel.add(slider);
		panel.add(new JLabel(Integer.toString(max)));
		panel.add(component);
		
		return panel;
	}

	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createTextField(final Parameter parameter) {
		JComponent component = new JTextField(parameter.getValue().toString());
		component.setEnabled(!parameter.isReadOnly());
		component.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String string = ((JTextField)e.getSource()).getText();
					// TODO strip non number characters
					if(parameter instanceof StringParameter) {
						((StringParameter)parameter).setValue(string);
					} else if(parameter instanceof DoubleParameter) {	
						((DoubleParameter)parameter).setValue(Double.valueOf(string));
					} else if(parameter instanceof IntegerParameter) {	
						((IntegerParameter)parameter).setValue(Integer.valueOf(string));
					}
				}
			}
		});
		return component;
	}
	/**
	 * Create a {@link JCheckBox} for {@link BooleanParameter}
	 * @param parameter
	 * @return
	 */
	public static JComponent createCheckBox(final BooleanParameter parameter) {
		JCheckBox chkBox = new JCheckBox(parameter.getDisplayName());
		chkBox.setSelected(parameter.getValue());
		chkBox.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent event) {
				boolean newValue = ((JCheckBox)event.getSource()).isSelected();
				parameter.setValue(newValue);
			}
		});
		return chkBox;
	}

	/**
	 * Create a {@link JComboBox} for the given {@link ChoiceParameter}.
	 * @param parameter
	 * @return
	 */
	public static JComboBox createComboBox(final ChoiceParameter parameter) {
		JComboBox combo = new JComboBox();
		final String[] choices = parameter.getChoicesArray();
		combo = new JComboBox(choices);
		combo.setSelectedItem(parameter.getValue());
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final int newindex = ((JComboBox)e.getSource()).getSelectedIndex();
				parameter.setValue(newindex);
			}
		});
		return combo;
	}
}
