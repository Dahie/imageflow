package de.danielsenff.imageflow.tasks;


import java.net.URL;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;


/**
 * Task to load a workflow xml.
 * @author danielsenff
 *
 */

public class LoadFlowGraphTask extends LoadURLTask<GraphController, Void> {

	
	/**
	 * Construct a LoadFlowGraphTask.
	 *
	 * @param url the url to load from.
	 */
	public LoadFlowGraphTask(final URL url) {
		super(url);
	}

    /**
     * If this task is cancelled before the entire file has been
     * read, null is returned.
     *
     * @return 
     */
    @Override
    protected GraphController doInBackground() throws IOException {
        final GraphController graphController = new GraphController();
        JFrame mainFrame = ImageFlow.getApplication().getMainFrame();
        ImageFlowView.getProgressBar().setIndeterminate(true);
    	ImageFlowView.getProgressBar().setVisible(true);
        
        
        graphController.getUnitElements().clear();
        try {
		graphController.read(url);
        } catch(OutOfMemoryError ex) {
        	ex.printStackTrace();
			JOptionPane.showMessageDialog(mainFrame, 
    				"The images are too large."	+'\n'+"I'm out of memory.",
    				"Of of memory", 
    				JOptionPane.ERROR_MESSAGE);
        }
        
    	
        if (!isCancelled()) {
            return graphController;
        } else {
            return null;
        }
    }
    
    @Override
    protected void succeeded(GraphController graphController) {
    	this.view.setGraphController(graphController);
    	super.succeeded(graphController);
    }
    
}
