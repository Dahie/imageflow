package actions;

import imageflow.backend.GraphController;

import javax.swing.AbstractAction;


public abstract class AbstractGraphAction extends AbstractAction {

	protected GraphController controller;
	
	public AbstractGraphAction(final GraphController controller) {
		this.controller = controller;
	}
	
}
