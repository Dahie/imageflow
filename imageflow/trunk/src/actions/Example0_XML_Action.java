package actions;

import imageflow.backend.GraphController;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class Example0_XML_Action extends AbstractAction {

	private GraphController graphController;

	public Example0_XML_Action(final GraphController controller) {
		this.graphController = controller;
		putValue(NAME, "Example Workflow 0 (from XML-File)");
	}
	
	public void actionPerformed(ActionEvent arg0) {
		graphController.setupExample0_XML();
	}
	
}
