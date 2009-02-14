package imageflow.tasks;

import imageflow.ImageFlow;
import imageflow.ImageFlowView;
import imageflow.backend.GraphController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;


/**
 * @author danielsenff
 *
 */
public class ExportMacroTask extends Task {
	
	GraphController graphController;
	private File file;
	private static final Logger logger = Logger.getLogger(ImageFlowView.class.getName());
	
	public ExportMacroTask(final Application app, GraphController graphController) {
		super(app);
		this.graphController = graphController;
	}

	@Override 
	protected Void doInBackground() throws InterruptedException {
		
		String macro = graphController.generateMacro();
//		((ImageFlowView)ImageFlow.getApplication().getMainView()).
		System.out.println(macro);
//		graphController.runImageJMacro(macro, false);
		
		try {
//			FileOutputStream fos = new FileOutputStream(file);
			FileWriter fw = new FileWriter(file);
			fw.write(macro);
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public File getFile() {
		return file;
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
