package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Task;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;



/**
 * Abstract Task for loading files.
 * @author danielsenff
 *
 * @param <T>
 * @param <V>
 */
public abstract class LoadFileTask<T, V> extends Task<T, V> {
	
	protected boolean modified = false;
	private static final Logger logger = Logger.getLogger(ImageFlowView.class.getName());
    /**
     * File that is loaded.
     */
    protected final File file;
    protected ImageFlowView view;
	
    /* Construct the LoadFileTask object.  The constructor
     * will run on the EDT, so we capture a reference to the 
     * File to be loaded here.  To keep things simple, the 
     * resources for this Task are specified to be in the same 
     * ResourceMap as the DocumentEditorView class's resources.
     * They're defined in resources/DocumentEditorView.properties.
     */
    public LoadFileTask(final File file) {
    	super(ImageFlow.getApplication());
		this.file = file;
		this.view = (ImageFlowView) ImageFlow.getApplication().getMainView();
//        super(DocumentEditorView.this.getApplication(), file);
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
        view.setGraphController((GraphController)fileContents);
        ImageFlowView.getProgressBar().setIndeterminate(false);
    	ImageFlowView.getProgressBar().setVisible(false);
//        textArea.setText(fileContents);
        view.setModified(false);
    }

    /**
     * File this task is handling.
     * @return
     */
    public File getFile() {
		return file;
	}

    @Override
    protected void cancelled() {
    	ImageFlowView.getProgressBar().setIndeterminate(false);
    	ImageFlowView.getProgressBar().setVisible(false);
    	super.cancelled();
    }
    
	/* Called on the EDT if doInBackground fails because
     * an uncaught exception is thrown.  We show an error
     * dialog here.  The dialog is configured with resources
     * loaded from this Tasks's ResourceMap.
     */
    @Override 
    protected void failed(final Throwable e) {
        logger.log(Level.WARNING, "couldn't load " + getFile(), e);
        final String msg = getResourceMap().getString("loadFailedMessage", getFile());
        final String title = getResourceMap().getString("loadFailedTitle");
        final int type = JOptionPane.ERROR_MESSAGE;
        ImageFlowView.getProgressBar().setIndeterminate(false);
    	ImageFlowView.getProgressBar().setVisible(false);
        JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), msg, title, type);
    }

}
