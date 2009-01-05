/**
 * 
 */
package actions;

import imageflow.backend.GraphCheck;
import imageflow.backend.GraphController;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import visualap.Check;
import visualap.CheckException;

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
		
		GraphCheck chksys = new GraphCheck(controller.getUnitElements(), controller.getConnections()); 
		try	{
			chksys.checkSystem();
			JOptionPane.showMessageDialog(null, "System check passed", null, JOptionPane.INFORMATION_MESSAGE, null);
		} catch (CheckException ex) {
//			JOptionPane.showMessageDialog(VisualAp.this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//			if (showErrorDialog(ex.getMessage()))
//				hWindow.setPage(VisualAp.class.getResource("helpfile5a.html"));
			if (chksys.getErrorList() != null)	{
//				activePanel.selection = chksys.getErrorList();
//				activePanel.repaint();
			}
			ex.printStackTrace();
		}
		
		String status;
		if(controller.checkNetwork()) {
			status = "OK";
		} else {
			status = "not OK";
		}
		//TODO
		JOptionPane.showMessageDialog(null, "The graph is " + status + "\n"
				+ chksys.getErrorList());
	}
	
}
