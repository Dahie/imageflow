package de.danielsenff.imageflow.controller;

public class GraphControllerManager {

	private static GraphControllerManager manager;
	
	private GraphController controller;
	
	/**
	 * @return the controller
	 */
	public GraphController getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	public final void setController(GraphController controller) {
		this.controller = controller;
	}

	private GraphControllerManager() {}
	
	public static GraphControllerManager getInstance() {
		if(manager == null) {
			manager = new GraphControllerManager();
		} 
		return manager;
	}
	
	
	
}
