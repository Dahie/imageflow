package imageflow.models;

public interface Selectable {

	public void addSelectionListener(final SelectionListener listener);

	public void notifySelectionListener(final SelectionListener listener);

	public void notifySelectionListeners();

	public void removeSelectionListener(final SelectionListener listener);

	public boolean isSelected();
}
