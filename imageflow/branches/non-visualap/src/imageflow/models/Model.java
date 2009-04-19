package imageflow.models;


public interface Model {

	void addModelListener(final ModelListener temperatureModelListener);

	void removeModelListener(final ModelListener modelListener);

	void notifyModelListeners();

	void notifyModelListener(final ModelListener modelListener);
	
}
