/**
 * Copyright (C) 2008-2010 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.danielsenff.imageflow.tasks;


import java.io.IOException;
import java.net.URL;

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
    				"Out of memory", 
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
