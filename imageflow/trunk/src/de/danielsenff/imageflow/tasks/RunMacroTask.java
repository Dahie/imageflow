package de.danielsenff.imageflow.tasks;

import ij.IJ;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;


/**
 * @author danielsenff
 *
 */
public class RunMacroTask extends Task {
	
	GraphController graphController;
	private boolean showlog;
	
	public RunMacroTask(final Application app, GraphController graphController, boolean doShowLog) {
		super(app);
		this.graphController = graphController;
		this.showlog = doShowLog;
	}

	@Override 
	protected Void doInBackground() throws InterruptedException {
		
		String macro = graphController.generateMacro();
//		((ImageFlowView)ImageFlow.getApplication().getMainView()).
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
