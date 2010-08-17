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

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;


/**
 * Task to import an workflow xml into the existing workflow.
 * @author Daniel Senff
 *
 */
public class ImportGraphTask extends LoadURLTask<GraphController, Void> {

	
	/**
	 * Construct a LoadTextFileTask.
	 *
	 * @param url the url to load from.
	 */
	public ImportGraphTask(final URL url) {
		super(url);
	}

	/**
	 * Construct a LoadTextFileTask.
	 *
	 * @param file the file to load from.
	 *
	public ImportGraphTask(final File file) {
		super(file.toURL());
	}
	*/

    /**
     * Load the url into a String and return it.  The
     * {@code progress} property is updated as the file is loaded.
     * <p>
     * If this task is cancelled before the entire file has been
     * read, null is returned.
     *
     * @return 
     */
    @Override
    protected GraphController doInBackground() throws IOException {
        final GraphController graphController = 
        	((ImageFlowView)ImageFlow.getApplication()
        			.getMainView()).getGraphController();
        graphController.read(url);
        
        if (!isCancelled()) {
            return graphController;
        } else {
            return null;
        }
    }
}
