package actions;

import imageflow.backend.GraphController;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class Example1Action extends AbstractAction {

	private GraphController graphController;

	public Example1Action(final GraphController controller) {
		this.graphController = controller;
		putValue(NAME, "Example Workflow 1");
	}
	
	public void actionPerformed(ActionEvent arg0) {
		graphController.setupExample1();
	}
	
	
}
