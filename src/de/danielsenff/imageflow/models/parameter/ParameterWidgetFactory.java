package de.danielsenff.imageflow.models.parameter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Hashtable;

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

	enum FileChooser { OPEN, SAVE}

	public static JComponent createForm(final Parameter parameter) {
		try {
			if (parameter instanceof ChoiceParameter) {
				if(optionsContainString(parameter, "as", "radio")) {
					return createRadios((ChoiceParameter)parameter);
				} else 
					return createComboBox((ChoiceParameter)parameter);
			} else if (parameter instanceof IntegerParameter) {
				if (optionsContainString(parameter, "as", "slider")) {
					return createSlider((IntegerParameter)parameter);
				} else {
					return createTextField(parameter);
				}

			} else if (parameter instanceof DoubleParameter) {
				return createTextField(parameter);
			} else if (parameter instanceof StringParameter) {
				if (optionsContainString(parameter, "as", "openfilechooser")) {
					return createFileChooser((StringParameter)parameter, FileChooser.OPEN);
				} else if (optionsContainString(parameter, "as", "savefilechooser")) {
					return createFileChooser((StringParameter)parameter, FileChooser.SAVE);
				} else {
					return createTextField(parameter);
				}
			} else if (parameter instanceof BooleanParameter) {
				return createCheckBox((BooleanParameter)parameter);
			}

		} catch (final Exception e) {
			System.err.println("caught error, skip form");
		}
		return null;
	}

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
		
		final JTextField component = new JTextField(parameter.getValue().toString());
		component.setEnabled(!parameter.isReadOnly());
		component.setColumns(5);
		
		final int min = (Integer) parameter.getOptions().get("min");
		final int max = (Integer) parameter.getOptions().get("max");
		final int value = (Integer) parameter.getValue();

		final Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(min, new JLabel(Integer.toString(min)));
		labels.put(max, new JLabel(Integer.toString(max)));
		
		final JSlider slider = new JSlider(min, max, value);
		slider.setLabelTable(labels);
		slider.setPaintTicks(true);
		//slider.setPaintLabels(true);
		slider.setEnabled(!parameter.isReadOnly());
		slider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(final ChangeEvent event) {
				final int newValue = ((JSlider) event.getSource()).getValue();
				component.setText(Integer.toString(newValue));
				parameter.setValue(newValue);
				slider.setValue(newValue);
			}
		});
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
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createRadios(final ChoiceParameter parameter) {
		final JPanel panel = new JPanel();
		
		JRadioButton radioButton;
		ButtonGroup group = new ButtonGroup();
		for (final String choice : parameter.getChoices()) {
			radioButton = new JRadioButton(choice);
			radioButton.setActionCommand(choice);
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
		final JComponent component = new JTextField(parameter.getValue().toString());
		component.setEnabled(!parameter.isReadOnly());
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
		final JCheckBox chkBox = new JCheckBox(parameter.getDisplayName());
		chkBox.setSelected(parameter.getValue());
		chkBox.addChangeListener(new ChangeListener() {

			public void stateChanged(final ChangeEvent event) {
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
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final int newindex = ((JComboBox)e.getSource()).getSelectedIndex();
				parameter.setValue(newindex);
			}
		});
		return combo;
	}
	
	public static JComponent createFileChooser(StringParameter parameter, FileChooser chooser) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JTextField textfield = new JTextField(parameter.getValue());
		textfield.setEnabled(false);
		
		JButton fileDialogButton = new JButton("Choose");
			fileDialogButton.addActionListener(new FileChooserActionListener(parameter, textfield, chooser));
		
		panel.add(textfield, BorderLayout.CENTER);
		panel.add(fileDialogButton, BorderLayout.LINE_END);
		
		return panel;
	}
	
	static class FileChooserActionListener implements ActionListener {

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
