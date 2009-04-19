package imageflow.tasks;

import imageflow.ImageFlow;
import imageflow.ImageFlowView;
import imageflow.backend.GraphController;
import imageflow.models.unit.UnitList;

import java.io.File;
import java.io.IOException;


public class ImportGraphTask extends LoadFileTask<GraphController, Void> {

	
	/**
     * Construct a LoadTextFileTask.
     *
     * @param file the file to load from.
     */
	public ImportGraphTask(File file) {
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
        GraphController graphController = 
        	((ImageFlowView)ImageFlow.getApplication()
        			.getMainView()).getGraphController();
        UnitList unitList = graphController.getUnitElements();
        unitList.read(file);
        
        if (!isCancelled()) {
            return graphController;
        } else {
            return null;
        }
    }
}
