package imageflow.tasks;

import imageflow.models.unit.UnitList;

import java.io.File;
import java.io.IOException;

import backend.GraphController;

public class LoadFlowGraphTask extends LoadFileTask<GraphController, Void> {


	
	/**
     * Construct a LoadTextFileTask.
     *
     * @param file the file to load from.
     */
	public LoadFlowGraphTask(File file) {
		super(file);
	}

    /**
     * Load the file into a String and return it.  The
     * {@code progress} property is updated as the file is loaded.
     * <p>
     * If this task is cancelled before the entire file has been
     * read, null is returned.
     *
     * @return 
     */
    @Override
    protected GraphController doInBackground() throws IOException {
        GraphController graphController = new GraphController();
        UnitList unitList = graphController.getUnitElements();
        unitList.clear();
        unitList.readUnitList(file);
    	
        if (!isCancelled()) {
            return graphController;
        } else {
            return null;
        }
    }
    
}
