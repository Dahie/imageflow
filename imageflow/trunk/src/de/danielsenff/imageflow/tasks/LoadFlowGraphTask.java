package de.danielsenff.imageflow.tasks;


import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.danielsenff.imageflow.ImageFlow;
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
        try {
        	unitList.read(file);	
        } catch(OutOfMemoryError ex) {
        	ex.printStackTrace();
        	JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
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
    	System.out.println("replace graphcontroller");
    	super.succeeded(graphController);
    }
    
}
