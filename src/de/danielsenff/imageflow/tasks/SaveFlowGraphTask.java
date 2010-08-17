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


import java.io.File;
import java.io.IOException;

import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;

/**
 * Task to save a workflow in a XML-file.
 * @author Daniel Senff
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
