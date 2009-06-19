package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.io.IOException;

import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.models.unit.UnitList;


/**
 * Task to load a workflow xml.
 * @author danielsenff
 *
 */
public class LoadFlowGraphTask extends LoadFileTask<GraphController, Void> {


	
	/**
     * Construct a LoadFlowGraphTask.
     *
     * @param file the file to load from.
     */
	public LoadFlowGraphTask(final File file) {
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
        final GraphController graphController = new GraphController();
        final UnitList unitList = graphController.getUnitElements();
        unitList.clear();
        unitList.read(file);
    	
        if (!isCancelled()) {
            return graphController;
        } else {
            return null;
        }
    }
    
}
