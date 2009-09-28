package de.danielsenff.imageflow.tasks;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;

/**
 * Generate and display the macro based on the workflow.
 * @author danielsenff
 *
 */
public class GenerateMacroTask extends Task<Object, String> {

	protected GraphController graphController;
	protected boolean showCode;
	
	/**
	 * @param app
	 * @param graphController
	 */
	public GenerateMacroTask(final Application app, 
			final GraphController graphController) {
		super(app);
		this.graphController = graphController;
		this.showCode = true;
	}
	
	@Override 
	protected String doInBackground() throws InterruptedException {

		final String macro = generateMacro();

		return macro;
	}

	/**
	 * Create macro from the {@link GraphController} stored in this Task.
	 * @return
	 */
	protected String generateMacro() {
		
		ImageFlowView.getProgressBar().setIndeterminate(true);
		ImageFlowView.getProgressBar().setVisible(true);
    	// generates Macro with callback function (for progressBar)
    	final String macro = graphController.generateMacro(true);
		
		if(this.showCode) {
//			System.out.println(macro);
			// generates cleaner Macro without callback function (for progressBar)
			final String extendedMacro = graphController.generateMacro(false);
			((ImageFlowView)((ImageFlow)ImageFlow.getApplication()).getMainView())
				.getCodePreviewBox().setMacroCode(extendedMacro);
						
		}
		ImageFlowView.getProgressBar().setIndeterminate(false);
		return macro;
	}
    
	/*@Override
	protected void succeeded(final Object superclass) {
    	ImageFlowView.getProgressBar().setVisible(false);
    	ImageFlowView.getProgressBar().setValue(0);
    }*/
	
}
