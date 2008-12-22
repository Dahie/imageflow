package actions;

import java.awt.event.ActionEvent;

import models.unit.UnitList;

import backend.GraphController;

/**
 * Clears the graph, deleting all units and connections.
 * @author danielsenff
 *
 */
public class ClearGraphAction extends AbstractGraphAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param controller
	 */
	public ClearGraphAction(final GraphController controller) {
		super(controller);
		putValue(NAME, "Clear");
	}

	public void actionPerformed(final ActionEvent arg0) {
		final UnitList units = controller.getUnitElements();
		units.clear();
	}

}
