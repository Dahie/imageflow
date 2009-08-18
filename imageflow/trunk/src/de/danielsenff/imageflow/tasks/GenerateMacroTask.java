package de.danielsenff.imageflow.tasks;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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

	protected String generateMacro() {
		
		ImageFlowView.getProgressBar().setIndeterminate(true);
		ImageFlowView.getProgressBar().setVisible(true);
    	// generates Macro with callback function (for progressBar)
    	final String macro = graphController.generateMacro(true);
		
		if(this.showCode) {
//			System.out.println(macro);
			// generates cleaner Macro without callback function (for progressBar)
			final String extendedMacro = graphController.generateMacro(false);
			JDialog codePreview = new JDialog(ImageFlow.getApplication().getMainFrame(), "generated Macro");
			codePreview.setPreferredSize(new Dimension(350,150));
			JTextArea ta = new JTextArea(extendedMacro);
			codePreview.setLayout(new BorderLayout());

			JScrollPane scrollPane = new JScrollPane(ta);
			codePreview.add(scrollPane, BorderLayout.CENTER);
			codePreview.pack();
			codePreview.setVisible(true);			
		}
		ImageFlowView.getProgressBar().setIndeterminate(false);
		return macro;
	}
    
	@Override
	protected void succeeded(final Object superclass) {
    	ImageFlowView.getProgressBar().setVisible(false);
    	ImageFlowView.getProgressBar().setValue(0);
    }
	
}
