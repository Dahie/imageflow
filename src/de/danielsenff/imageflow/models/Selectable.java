package de.danielsenff.imageflow.models;

/**
 * @author dahie
 *
 */
public interface Selectable {

	/**
	 * Set the selection state of this object.
	 * @param sel
	 */
	public void setSelected(boolean sel);
	/**
	 * Return the selection state of this object.
	 * @return
	 */
	public boolean isSelected();
	
	public void addSelectionListener(final SelectionListener listener);

	public void notifySelectionListener(final SelectionListener listener);

	public void notifySelectionListeners();

	public void removeSelectionListener(final SelectionListener listener);

}
