package actions;

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
	protected UnitElement unit;
	
	/**
	 * @param unit
	 */
	public AbstractUnitAction(final UnitElement unit) {
		this.unit = unit;
	}
	
}
