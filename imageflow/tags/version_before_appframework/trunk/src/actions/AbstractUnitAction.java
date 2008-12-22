package actions;

import graph.Node;
import graph.Selection;

import javax.swing.AbstractAction;

import models.unit.UnitElement;

/**
 * Superclass for Actions which directly work on a {@link UnitElement}.
 * @author danielsenff
 *
 */
public abstract class AbstractUnitAction extends AbstractAction {

	/**
	 * The {@link UnitElement} in question.
	 */
	protected Selection<Node> selectedUnits;
	
	private AbstractUnitAction() {}
	
	/**
	 * @param unit
	 */
	public AbstractUnitAction(final Selection<Node> selection) {
		this.selectedUnits = selection;
	}
	
}
