/**
 * 
 */
package actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import backend.GraphController;

/**
 * @author danielsenff
 *
 */
public class RunMacroAction extends AbstractGraphAction {

	/**
	 * @param controller
	 */
	public RunMacroAction(GraphController controller) {
		super(controller);
		putValue(Action.NAME, "Run Macro");
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		controller.generateMacro();
		
//		runMacro();
	}

}
