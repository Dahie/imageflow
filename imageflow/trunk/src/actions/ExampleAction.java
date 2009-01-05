package actions;

import imageflow.backend.GraphController;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class ExampleAction extends AbstractAction {

	private GraphController graphController;

	public ExampleAction(final GraphController controller) {
		this.graphController = controller;
		putValue(NAME, "Example Workflow");
	}
	
	public void actionPerformed(ActionEvent arg0) {
		graphController.setupExample1();
	}
	
	
}
