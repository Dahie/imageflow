/*
Version 1.0, 30-12-2007, First release
Version 1.1, 03-02-2008, prepared for MDI support

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* 
class DialogPref implements a dialog for the preferences

javalc6
*/
package visualap;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;



class DialogPref extends JDialog
                   implements PropertyChangeListener {

    private JTextField textField = new JTextField(10);

    private JOptionPane optionPane;

    private String btnString1 = "Apply";
    private String btnString2 = "Cancel";

	private VisualAp parent;


    /** Creates the reusable dialog. */
    public DialogPref(VisualAp parent) {
        super(parent, true);
		this.parent = parent;
        setTitle("Preferences");

//        textField.setText(Integer.toString((Integer) parent.activePanel.globalVars.get("blocksize")));
        //Create an array of the text and components to be displayed.
        String msgString1 = "Blocksize:";
        Object[] array = {msgString1, textField};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {

                    optionPane.setValue(new Integer(
                                        JOptionPane.CLOSED_OPTION));
            }
        });


		setResizable(false);
        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /** This method show the dialog */
    public void showDialog() {
        textField.setText(Integer.toString((Integer) parent.activePanel.globalVars.get("blocksize")));
        setVisible(true);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
				int bs;
				try {
					bs = Integer.parseInt(textField.getText());
				} catch (NumberFormatException ex)	{
					bs = -1; // note: negative value to trigger the error message
				}
				if (bs > 0) {
					parent.activePanel.globalVars.put("blocksize",bs);
					setVisible(false);
				} else JOptionPane.showMessageDialog(this, "Please enter an integer greater than 0","Error",JOptionPane.ERROR_MESSAGE);
            } else { //user closed dialog or clicked cancel
                setVisible(false);
            }
        }
    }

}
