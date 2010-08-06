package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.io.IOException;

import de.danielsenff.imageflow.ImageFlowView;
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
    	ImageFlowView.getProgressBar().setIndeterminate(true);
    	ImageFlowView.getProgressBar().setVisible(true);
    	
    	// get current graphcontroller
    	final GraphController graphController = view.getGraphController();
    	
    	// defensive file saving, so we create temp-file and backup
    	String absPath = file.getAbsolutePath();
		File tmpFile = new File(absPath + ".tmp");
		tmpFile.createNewFile();
		tmpFile.deleteOnExit();
		File backupFile = new File(absPath + ".bak");
    	
		graphController.write(tmpFile);
		
		if (!isCancelled()) {
			if (file.exists()) {
				renameFile(file, backupFile);
			}
			renameFile(tmpFile, file);
			backupFile.delete();
			return graphController;
		} else {
			tmpFile.delete();
			return null;
		}
    }
	
}
