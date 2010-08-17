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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;


/**
 * Task for saving a macro as txt file.
 * @author danielsenff
 *
 */
public class ExportMacroTask extends SaveFileTask {

	GraphController graphController;
	private static final Logger logger = Logger.getLogger(ImageFlowView.class.getName());

	/**
	 * @param app
	 * @param file
	 * @param graphController
	 */
	public ExportMacroTask(final File file, 
			final GraphController graphController) {
		super(file);
		this.graphController = graphController;
	}

	@Override 
	protected Void doInBackground() throws InterruptedException {

		// generates clean Macro without callback function (for progressBar)
		String macro = graphController.generateMacro(false);
		if(macro != null) {
			try {
				saveTextFile(macro);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	 /** 
	 * Writes the {@code text} to the specified {@code file}.  The 
	 * implementation is conservative: the {@code text} is initially
	 * written to ${file}.tmp, then the original file is renamed
	 * ${file}.bak, and finally the temporary file is renamed to ${file}.
	 * The Task's {@code progress} property is updated as the text is
	 * written.  
	 * <p>
	 * If this Task is cancelled before writing the temporary file
	 * has been completed, ${file.tmp} is deleted.
	 * <p>
	 * The conservative algorithm for saving to a file was lifted from
	 * the FileSaver class described by Ian Darwin here: 
	 * <a href="http://javacook.darwinsys.com/new_recipes/10saveuserdata.jsp">
	 * http://javacook.darwinsys.com/new_recipes/10saveuserdata.jsp
	 * </a>.
	 * 
	 * @return null
	 * @throws IOException 
         */
	private void saveTextFile(String text) throws IOException {
		String absPath = file.getAbsolutePath();
		File tmpFile = new File(absPath + ".tmp");
		tmpFile.createNewFile();
		tmpFile.deleteOnExit();
		File backupFile = new File(absPath + ".bak");
		BufferedWriter out = null;
		int fileLength = text.length();
		int blockSize = Math.max(1024, 1 + ((fileLength-1) / 100));
		try {
			out = new BufferedWriter(new FileWriter(tmpFile));
			int offset = 0;
			while(!isCancelled() && (offset < fileLength)) {
				int length = Math.min(blockSize, fileLength - offset);
				out.write(text, offset, length);
				offset += blockSize;
				setProgress(Math.min(offset, fileLength), 0, fileLength);
			}
		}
		finally {
			if (out != null) {
				out.close();
			}
		}
		if (!isCancelled()) {
			backupFile.delete();
			if (file.exists()) {
				renameFile(file, backupFile);
			}
			renameFile(tmpFile, file);
		}
		else {
			tmpFile.delete();
		}
	}
}
