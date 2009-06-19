package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;


/**
 * Task for saving a macro as txt file.
 * @author danielsenff
 *
 */
public class ExportMacroTask extends SaveFileTask {

	GraphController graphController;
	private static final Logger logger = Logger.getLogger(ImageFlowView.class.getName());

	/**
	 * @param app
	 * @param file
	 * @param graphController
	 */
	public ExportMacroTask(final File file, 
			final GraphController graphController) {
		super(file);
		this.graphController = graphController;
	}

	@Override 
	protected Void doInBackground() throws InterruptedException {

		String macro = graphController.generateMacro();
		if(macro != null) {
			System.out.println(macro);
			//		graphController.runImageJMacro(macro, false);

			try {
				FileWriter fw = new FileWriter(file);
				fw.write(macro);
				fw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/*@Override protected void done() {
		setMessage(isCancelled() ? "Canceled." : "Done.");
	}*/


	/* Called on the EDT if doInBackground fails because
	 * an uncaught exception is thrown.  We show an error
	 * dialog here.  The dialog is configured with resources
	 * loaded from this Tasks's ResourceMap.
	 */
	@Override 
	protected void failed(Throwable e) {
		logger.log(Level.WARNING, "couldn't save " + getFile(), e);
		String msg = getResourceMap().getString("loadFailedMessage", getFile());
		String title = getResourceMap().getString("loadFailedTitle");
		int type = JOptionPane.ERROR_MESSAGE;
		JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), msg, title, type);
	}
}
