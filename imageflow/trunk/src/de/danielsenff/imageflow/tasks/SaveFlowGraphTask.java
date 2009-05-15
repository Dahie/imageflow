package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.io.IOException;

import de.danielsenff.imageflow.controller.GraphController;

public class SaveFlowGraphTask extends SaveFileTask<GraphController, Void> {

	/**
     * Construct a LoadTextFileTask.
     *
     * @param file the file to load from.
     */
	public SaveFlowGraphTask( File file) {
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
//        GraphController graphController = new GraphController();
    	// get current graphcontroller
    	GraphController graphController = view.getGraphController();
        graphController.write(file);
    	
        if (!isCancelled()) {
            return graphController;
        } else {
            return null;
        }
    }
	
}
