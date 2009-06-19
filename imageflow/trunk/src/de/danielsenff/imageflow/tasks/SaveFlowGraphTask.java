package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.io.IOException;

import de.danielsenff.imageflow.controller.GraphController;

/**
 * Task to save a workflow in a XML-file.
 * @author danielsenff
 *
 */
public class SaveFlowGraphTask extends SaveFileTask<GraphController, Void> {

	/**
     * Construct a LoadTextFileTask.
     *
     * @param file the file to load from.
     */
	public SaveFlowGraphTask( final File file) {
		super(file);
	}

    /**
     * If this task is cancelled before the entire file has been
     * read, null is returned.
     *
     * @return 
     */
    @Override
    protected GraphController doInBackground() throws IOException {
    	// get current graphcontroller
    	final GraphController graphController = view.getGraphController();
        graphController.write(file);
    	
        if (!isCancelled()) {
            return graphController;
        } else {
            return null;
        }
    }
	
}
