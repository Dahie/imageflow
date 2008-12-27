package imageflow.tasks;

import imageflow.ImageFlow;
import imageflow.ImageFlowView;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import backend.GraphController;

/**
 * @author danielsenff
 *
 */
public class GenerateMacroTask extends Task {
	
	GraphController graphController;
	
	public GenerateMacroTask(final Application app, GraphController graphController) {
		super(app);
		this.graphController = graphController;
	}

	@Override 
	protected Void doInBackground() throws InterruptedException {
		
		String macro = graphController.generateMacro();
//		((ImageFlowView)ImageFlow.getApplication().getMainView()).
		graphController.runImageJMacro(macro);
		return null;
	}
	
	/*@Override protected void done() {
		setMessage(isCancelled() ? "Canceled." : "Done.");
	}*/
}
