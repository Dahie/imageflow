package de.danielsenff.imageflow.tasks;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import de.danielsenff.imageflow.controller.GraphController;


/**
 * Task to Run the current workflow.
 * @author danielsenff
 *
 */
public class RunMacroTask extends Task<Object, Void> {
	
	GraphController graphController;
	private boolean showlog;
	
	/**
	 * @param app
	 * @param graphController
	 * @param doShowLog
	 */
	public RunMacroTask(final Application app, 
			final GraphController graphController, 
			final boolean doShowLog) {
		super(app);
		this.graphController = graphController;
		this.showlog = doShowLog;
	}

	@Override 
	protected Void doInBackground() throws InterruptedException {
		
		String macro = graphController.generateMacro();
		//TODO use exceptions 
		if(macro != null) {
			System.out.println(macro);
			graphController.runImageJMacro(macro, this.showlog);	
		}
		
		
		return null;
	}
	
	/*@Override protected void done() {
		setMessage(isCancelled() ? "Canceled." : "Done.");
	}*/
}
