package de.danielsenff.imageflow.tasks;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.controller.GraphController;

/**
 * Generate and display the macro based on the workflow.
 * @author danielsenff
 *
 */
public class GenerateMacroTask extends Task<Object, String> {

	protected GraphController graphController;
	protected boolean showlog;
	
	/**
	 * @param app
	 * @param graphController
	 */
	public GenerateMacroTask(final Application app, 
			final GraphController graphController) {
		super(app);
		this.graphController = graphController;
		this.showlog = true;
	}
	
	@Override 
	protected String doInBackground() throws InterruptedException {

		final String macro = graphController.generateMacro();
		
		if(this.showlog) {
//			System.out.println(macro);
			JDialog preview = new JDialog(ImageFlow.getApplication().getMainFrame(), "generated Macro");
			JTextArea ta = new JTextArea(macro);
			ta.setPreferredSize(new Dimension(350,150));
			preview.setLayout(new BorderLayout());

			JScrollPane scrollPane = new JScrollPane(ta);
			preview.add(scrollPane, BorderLayout.CENTER);
			preview.pack();
			preview.setVisible(true);			
		}

		return macro;
	}
	
}
