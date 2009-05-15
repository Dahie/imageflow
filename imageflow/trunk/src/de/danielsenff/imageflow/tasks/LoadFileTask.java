package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Task;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;



public abstract class LoadFileTask<T, V> extends Task<T, V> {
	
	protected boolean modified = false;
	private static final Logger logger = Logger.getLogger(ImageFlowView.class.getName());
    protected final File file;
    protected ImageFlowView view;
	
    /* Construct the LoadFileTask object.  The constructor
     * will run on the EDT, so we capture a reference to the 
     * File to be loaded here.  To keep things simple, the 
     * resources for this Task are specified to be in the same 
     * ResourceMap as the DocumentEditorView class's resources.
     * They're defined in resources/DocumentEditorView.properties.
     */
    public LoadFileTask(File file) {
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
    protected void succeeded(T fileContents) {
        view.setFile(getFile());
        view.setGraphController((GraphController)fileContents);
        
//        textArea.setText(fileContents);
        view.setModified(false);
    }

    public File getFile() {
		return file;
	}

	/* Called on the EDT if doInBackground fails because
     * an uncaught exception is thrown.  We show an error
     * dialog here.  The dialog is configured with resources
     * loaded from this Tasks's ResourceMap.
     */
    @Override 
    protected void failed(Throwable e) {
        logger.log(Level.WARNING, "couldn't load " + getFile(), e);
        String msg = getResourceMap().getString("loadFailedMessage", getFile());
        String title = getResourceMap().getString("loadFailedTitle");
        int type = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), msg, title, type);
    }

}
