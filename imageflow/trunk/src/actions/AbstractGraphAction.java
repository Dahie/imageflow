package actions;

import javax.swing.AbstractAction;

import backend.GraphController;

public abstract class AbstractGraphAction extends AbstractAction {

	protected GraphController controller;
	
	public AbstractGraphAction(final GraphController controller) {
		this.controller = controller;
	}
	
}
