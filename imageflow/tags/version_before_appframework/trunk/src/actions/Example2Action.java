package actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import backend.GraphController;

public class Example2Action extends AbstractAction {

	private GraphController graphController;

	public Example2Action(final GraphController controller) {
		this.graphController = controller;
		putValue(NAME, "Example Workflow 2");
	}
	
	public void actionPerformed(ActionEvent arg0) {
		graphController.setupExample2();
	}
	
}
