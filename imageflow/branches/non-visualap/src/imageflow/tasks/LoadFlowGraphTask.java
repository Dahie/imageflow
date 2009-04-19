package imageflow.tasks;

import imageflow.backend.GraphController;
import imageflow.models.unit.UnitList;

import java.io.File;
import java.io.IOException;


public class LoadFlowGraphTask extends LoadFileTask<GraphController, Void> {


	
	/**
     * Construct a LoadFlowGraphTask.
     *
     * @param file the file to load from.
     */
	public LoadFlowGraphTask(File file) {
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
        GraphController graphController = new GraphController();
        UnitList unitList = graphController.getUnitElements();
        unitList.clear();
        unitList.read(file);
    	
        if (!isCancelled()) {
            return graphController;
        } else {
            return null;
        }
    }
    
}
