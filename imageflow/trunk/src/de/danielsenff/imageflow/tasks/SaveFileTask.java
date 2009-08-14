package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Task;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;



/**
 * Abstract Task for saving files.
 * @author danielsenff
 *
 * @param <T>
 * @param <V>
 */
public abstract class SaveFileTask<T, V> extends Task<T, V> {
	
	protected boolean modified = false;
    protected final File file;
    protected ImageFlowView view;
    private static final Logger logger = Logger.getLogger(ImageFlowView.class.getName());
	
    /** Construct the LoadFileTask object.  The constructor
     * will run on the EDT, so we capture a reference to the 
     * File to be loaded here.  To keep things simple, the 
     * resources for this Task are specified to be in the same 
     * ResourceMap as the DocumentEditorView class's resources.
     * They're defined in resources/ImageFlowView.properties.
     * @param file 
     */
    public SaveFileTask(final File file) {
    	super(ImageFlow.getApplication());
		this.file = file;
		this.view = (ImageFlowView) ImageFlow.getApplication().getMainView();
    }

    
    
    
    /**
     * Called on the EDT if doInBackground completes without 
     * error and this Task isn't cancelled.  We update the
     * GUI as well as the file and modified properties here.
     * @param fileContents
     */
    @Override
	protected void succeeded(final T fileContents) {
        view.setFile(getFile());
        ImageFlowView.getProgressBar().setIndeterminate(false);
//    	ImageFlowView.getProgressBar().setVisible(false);
        view.setModified(false);
    }

    /**
     * File handled, by this Task.
     * @return
     */
    public File getFile() {
		return file;
	}

	/* Called on the EDT if doInBackground fails because
     * an uncaught exception is thrown.  We show an error
     * dialog here.  The dialog is configured with resources
     * loaded from this Tasks's ResourceMap.
     */
    @Override 
    protected void failed(final Throwable e) {
        logger.log(Level.WARNING, "couldn't save " + getFile(), e);
        final String msg = getResourceMap().getString("loadFailedMessage", getFile());
        final String title = getResourceMap().getString("loadFailedTitle");
        final int type = JOptionPane.ERROR_MESSAGE;
        ImageFlowView.getProgressBar().setIndeterminate(false);
//    	ImageFlowView.getProgressBar().setVisible(false);
        JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), msg, title, type);
    }

}
