/**
 * 
 */
package actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import backend.GraphController;

/**
 * @author danielsenff
 *
 */
public class CheckGraphAction extends AbstractGraphAction {


	
	/**
	 * 
	 */
	public CheckGraphAction(GraphController controller) {
		super(controller);
		putValue(Action.NAME, "Check graph");
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		String status;
		if(controller.checkNetwork()) {
			status = "OK";
		} else {
			status = "not OK";
		}
		JOptionPane.showMessageDialog(null, "The graph is " + status);
	}
	
}
