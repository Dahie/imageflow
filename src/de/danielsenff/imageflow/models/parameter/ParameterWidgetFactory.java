/**
 * Copyright (C) 2008-2011 Daniel Senff
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

package de.danielsenff.imageflow.models.parameter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import java.util.Locale;

import javax.swing.BoundedRangeModel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Creates form elements based on {@link Parameter}
 * @author dahie
 *
 */
public class ParameterWidgetFactory {

	private static final class TextfieldParamChangeListener implements
	ParamChangeListener {
		private final JTextField component;

		private TextfieldParamChangeListener(JTextField component) {
			this.component = component;
		}

		public void parameterChanged(Parameter source) {
			if (source.getValue() instanceof String) {
				component.setText((String) source.getValue());
			} else if (source.getValue() instanceof Integer) {
				component.setText(Integer.toString((Integer) source.getValue()));
			} else if (source.getValue() instanceof Double) {
				Double value = (Double) source.getValue();
				DecimalFormat decimal = new DecimalFormat("0.00");
				decimal.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
				component.setText(decimal.format(value));
			}
		}
	}

	private static class CheckBoxParamChangeListener implements
	ParamChangeListener {
		private JCheckBox component;

		private CheckBoxParamChangeListener(JCheckBox component) {
			this.component = component;
		}

		public void parameterChanged(Parameter source) {
			if (source.getValue() instanceof Boolean) {
				Boolean selected = (Boolean) source.getValue();
				component.setSelected(selected);
			}
		}
	}

	private static class ComboBoxParamChangeListener implements
	ParamChangeListener {
		private JComboBox component;

		public ComboBoxParamChangeListener(JComboBox component) {
			this.component = component;
		}

		public void parameterChanged(Parameter source) {
			if (source instanceof ChoiceParameter) {
				this.component.setSelectedIndex(((ChoiceParameter) source).getChoiceIndex());
			}
		}
	}

	private static final class SliderParamChangeListener implements
	ParamChangeListener {
		private final JSlider component;

		private SliderParamChangeListener(JSlider component) {
			this.component = component;
		}

		public void parameterChanged(Parameter source) {
			if (source.getValue() instanceof Integer) {
				component.setValue(Integer.valueOf((Integer) source.getValue()));
			} else if (source.getValue() instanceof Double) {
				Integer value = (Integer) source.getValue()*100;
				DecimalFormat decimal = new DecimalFormat("0.00");
				decimal.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
				component.setValue(value);
			}

		}
	}

	enum FileChooser { OPEN, SAVE}

	public static JComponent createForm(final Parameter parameter) {
		try {
			if (parameter instanceof ChoiceParameter) {
				// choice can be displayed as dropdown or radio buttons
				if(optionsContainString(parameter, "as", "radio")) {
					return createRadios((ChoiceParameter)parameter);
				} else 
					return createComboBox((ChoiceParameter)parameter);
			} else if (parameter instanceof IntegerParameter) {
				// integer can be displayed as textfield or slider 
				// TODO add as spinner
				if (optionsContainString(parameter, "as", "slider")) {
					return createSlider((IntegerParameter)parameter);
				} else {
					return createTextField(parameter);
				}

			} else if (parameter instanceof DoubleParameter) {
				// integer can be displayed as textfield or slider 
				// TODO add as spinner
				if (optionsContainString(parameter, "as", "slider")) {
					return createSlider((DoubleParameter)parameter);
				} else {
					return createTextField(parameter);
				}
			} else if (parameter instanceof StringParameter) {
				// string can be displayed as textfield
				// special treatment for paths
				// TODO textarea
				if (optionsContainString(parameter, "as", "openfilechooser")) {
					return createFileChooser((StringParameter)parameter, FileChooser.OPEN);
				} else if (optionsContainString(parameter, "as", "savefilechooser")) {
					return createFileChooser((StringParameter)parameter, FileChooser.SAVE);
				} else {
					return createTextField(parameter);
				}
			} else if (parameter instanceof BooleanParameter) {
				if (optionsContainString(parameter, "as", "openfilechooser")) {
					// TODO return createToggleButton((BooleanParameter)parameter);
				} else {
					return createCheckBox((BooleanParameter)parameter);
				}
			}

		} catch (final Exception e) {
			System.err.println("caught error, skip form");
		}
		return null;
	}

	/**
	 * Returns true if the options hash contains the given key with the given value.
	 * @param parameter
	 * @param key String key that needs to be included in the options hash
	 * @param value  value the key has to have
	 * @return
	 */
	public static boolean optionsContainString(final Parameter parameter, 
			final String key, 
			final String value) {
		return parameter.getOptions() != null 
		&& parameter.getOptions().get(key) != null 
		&& parameter.getOptions().get(key) instanceof String
		&& ((String)parameter.getOptions().get(key)).equalsIgnoreCase(value);
	}

	public static JComponent createSlider(final IntegerParameter parameter) throws IllegalArgumentException {
		final JPanel panel = new JPanel();
		panel.setOpaque(false);

		final JTextField component = new JTextField(parameter.getValue().toString());
		component.setEnabled(!parameter.isReadOnly());
		component.setColumns(5);
		component.setOpaque(false);
		parameter.addParamChangeListener(new TextfieldParamChangeListener(component));

		final int min = (Integer) parameter.getOptions().get("min");
		final int max = (Integer) parameter.getOptions().get("max");
		final int value = (Integer) parameter.getValue();

		final Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(min, new JLabel(Integer.toString(min)));
		labels.put(max, new JLabel(Integer.toString(max)));	


		final JSlider slider = new JSlider(min, max, value);
		slider.setLabelTable(labels);
		slider.setOpaque(false);
		slider.setPaintTicks(true);
		slider.setEnabled(!parameter.isReadOnly());
		slider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(final ChangeEvent event) {
				Object source = event.getSource();
				if (source instanceof BoundedRangeModel) {
					BoundedRangeModel aModel = (BoundedRangeModel) source;
					if (!aModel.getValueIsAdjusting()) {
						int newValue = aModel.getValue();
						component.setText(Integer.toString(newValue));
						parameter.setValue(newValue);
					}
				} else if (source instanceof JSlider) {
					JSlider theJSlider = (JSlider) source;
					if (!theJSlider.getValueIsAdjusting()) {
						int newValue = theJSlider.getValue();
						component.setText(Integer.toString(newValue));
						parameter.setValue(newValue);
					}
				} else {
					System.out.println("Something changed: " + source);
				}


			}
		});
		parameter.addParamChangeListener(new SliderParamChangeListener(slider));
		component.addKeyListener(new KeyListener() {

			public void keyTyped(final KeyEvent e) {}
			public void keyReleased(final KeyEvent e) {}
			public void keyPressed(final KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					final String string = ((JTextField)e.getSource()).getText();
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
	 * @param parameter
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static JComponent createSlider(final DoubleParameter parameter) 
	throws IllegalArgumentException {
		final JPanel panel = new JPanel();
		panel.setOpaque(false);

		final JTextField component = new JTextField(parameter.getValue().toString());
		component.setEnabled(!parameter.isReadOnly());
		component.setColumns(5);
		parameter.addParamChangeListener(new TextfieldParamChangeListener(component));

		final double min = (Double) parameter.getOptions().get("min");
		final double max = (Double) parameter.getOptions().get("max");
		final double value = (Double) parameter.getValue();

		final Hashtable<Double, JComponent> labels = new Hashtable<Double, JComponent>();
		labels.put(min, new JLabel(Double.toString(min)));
		labels.put(max, new JLabel(Double.toString(max)));	


		final JSlider slider = new JSlider((int)(min*100), (int)(max*100), (int)(value*100));
		slider.setLabelTable(labels);
		slider.setPaintTicks(true);
		slider.setEnabled(!parameter.isReadOnly());
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(final ChangeEvent event) {
				double newValue = ((JSlider) event.getSource()).getValue()*0.01;
				DecimalFormat decimal = new DecimalFormat("0.00");
				decimal.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
				component.setText(decimal.format(newValue));
				parameter.setValue(newValue);
			}
		});
		parameter.addParamChangeListener(new SliderParamChangeListener(slider));
		component.addKeyListener(new KeyListener() {

			public void keyTyped(final KeyEvent e) {}
			public void keyReleased(final KeyEvent e) {}
			public void keyPressed(final KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					double oldValue = parameter.getValue();
					try {
						final String string = ((JTextField)e.getSource()).getText();
						// TODO improve strip non number characters
						double newValue = Double.valueOf(string);
						slider.setValue((int)(newValue*100));
						parameter.setValue(newValue);
					} catch (NumberFormatException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		panel.add(new JLabel(Double.toString(min)));
		panel.add(slider);
		panel.add(new JLabel(Double.toString(max)));
		panel.add(component);

		return panel;
	}


	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createRadios(final ChoiceParameter parameter) {
		final JPanel panel = new JPanel();
		panel.setOpaque(false);

		JRadioButton radioButton;
		ButtonGroup group = new ButtonGroup();
		for (final String choice : parameter.getChoices()) {
			radioButton = new JRadioButton(choice);
			radioButton.setActionCommand(choice);
			radioButton.setOpaque(false);
			radioButton.addActionListener(new RadioActionListener(parameter));
			if (parameter.getValue().equals(choice)) {
				radioButton.setSelected(true);
			}
			group.add(radioButton);
			panel.add(radioButton);
		}



		return panel;
	}

	static class RadioActionListener implements ActionListener {

		final private ChoiceParameter parameter;
		public RadioActionListener(ChoiceParameter parameter) {
			this.parameter = parameter;
		}

		public void actionPerformed(ActionEvent event) {
			parameter.setValue(event.getActionCommand());
		}
	}

	/**
	 * Creates a Textfield form element based on the {@link Parameter}
	 * @param parameter
	 * @return
	 */
	public static JComponent createTextField(final Parameter parameter) {
		final JTextField component = new JTextField(parameter.getValue().toString());
		component.setEnabled(!parameter.isReadOnly());
		parameter.addParamChangeListener(new TextfieldParamChangeListener(component));
		component.addKeyListener(new KeyListener() {

			public void keyTyped(final KeyEvent e) {}
			public void keyReleased(final KeyEvent e) {}
			public void keyPressed(final KeyEvent e) {
				try {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						final String string = ((JTextField)e.getSource()).getText();
						// TODO strip non number characters
						if(parameter instanceof StringParameter) {
							((StringParameter)parameter).setValue(string);
						} else if(parameter instanceof DoubleParameter) {	
							((DoubleParameter)parameter).setValue(Double.valueOf(string));
						} else if(parameter instanceof IntegerParameter) {	
							((IntegerParameter)parameter).setValue(Integer.valueOf(string));
						}
						component.transferFocus();
					}
				} catch (NumberFormatException numEx) {
					String message = "The value you entered not supported.";
					System.err.println(message);
					//showErrorDialog("XML parsing error", message);
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
	public static JCheckBox createCheckBox(final BooleanParameter parameter) {
		final JCheckBox chkBox = new JCheckBox();
		//chkBox.setText(parameter.getDisplayName());
		chkBox.setSelected(parameter.getValue());
		chkBox.setOpaque(false);
		parameter.addParamChangeListener(new CheckBoxParamChangeListener(chkBox));
		chkBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent event) {
				final boolean newValue = ((JCheckBox)event.getSource()).isSelected();
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
		parameter.addParamChangeListener(new ComboBoxParamChangeListener(combo));
		combo.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					final int newindex = ((JComboBox)e.getSource()).getSelectedIndex();
					parameter.setValue(newindex);
				}
			}
		});
		return combo;
	}

	/**
	 * @param parameter
	 * @param chooser
	 * @return
	 */
	public static JComponent createFileChooser(final StringParameter parameter, final FileChooser chooser) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());
		JTextField textfield = new JTextField(parameter.getValue());
		textfield.setEnabled(false);

		JButton fileDialogButton = new JButton("Choose");
		fileDialogButton.addActionListener(new FileChooserActionListener(parameter, textfield, chooser));

		panel.add(textfield, BorderLayout.CENTER);
		panel.add(fileDialogButton, BorderLayout.LINE_END);

		return panel;
	}

	private static class FileChooserActionListener implements ActionListener {

		private StringParameter parameter;
		private JTextField textfield;
		FileChooser chooser;

		public FileChooserActionListener(StringParameter parameter, JTextField textfield, FileChooser chooser) {
			this.parameter = parameter;
			this.textfield = textfield;
			this.chooser = chooser;
		}

		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			String filepath = parameter.getValue(); 
			fc.setSelectedFile(new File(filepath));
			final int option;
			if(chooser == FileChooser.OPEN) {
				option = fc.showOpenDialog(null);
			} else {
				option = fc.showSaveDialog(null);
			}
			if (option == JFileChooser.APPROVE_OPTION) {
				filepath = fc.getSelectedFile().getAbsolutePath();
				// backslashes need to be escaped
				//filepath = filepath.replace("\\", "\\\\"); // \ to \\
				parameter.setValue(filepath);
				textfield.setText(filepath);
			}
		}
	}

}
