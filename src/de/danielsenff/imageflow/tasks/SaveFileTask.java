package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.io.IOException;
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
    /**
     * File to which the workflow is saved and that is set in the view.
     */
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
        ImageFlowView.getProgressBar().setIndeterminate(true);
    	ImageFlowView.getProgressBar().setVisible(true);
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
    	ImageFlowView.getProgressBar().setVisible(false);
        view.setModified(false);
    }

    /**
	 * Return the File that the {@link #getText text} will be
	 * written to.
	 *
	 * @return the value of the read-only file property.
	 */
	public final File getFile() {
	    return file;
	}
	
	

	/**
	 * @param oldFile
	 * @param newFile
	 * @throws IOException
	 */
	protected void renameFile(final File oldFile, final File newFile) throws IOException {
	    if (!oldFile.renameTo(newFile)) {
		String fmt = "file rename failed: %s => %s";
		throw new IOException(String.format(fmt, oldFile, newFile));
	    }
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
    	ImageFlowView.getProgressBar().setVisible(false);
        JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), msg, title, type);
    }

}
