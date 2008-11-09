package actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import backend.GraphController;

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
