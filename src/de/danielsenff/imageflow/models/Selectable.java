package de.danielsenff.imageflow.models;

public interface Selectable {

	public void setSelected(boolean sel);
	public boolean isSelected();
	
	public void addSelectionListener(final SelectionListener listener);

	public void notifySelectionListener(final SelectionListener listener);

	public void notifySelectionListeners();

	public void removeSelectionListener(final SelectionListener listener);

}
