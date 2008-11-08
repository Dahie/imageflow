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
public class RunMacroAction extends AbstractAction {

	private GraphController controller;
	

	/**
	 * 
	 */
	public RunMacroAction() {
		putValue(Action.NAME, "Run Macro");
	}
	
	
	/**
	 * @param controller
	 */
	public RunMacroAction(GraphController controller) {
		this();
		this.controller = controller;
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		controller.generateMacro();
		
	}

}
