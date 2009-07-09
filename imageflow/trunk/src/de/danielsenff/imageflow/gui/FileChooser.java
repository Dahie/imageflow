package de.danielsenff.imageflow.gui;
/*
 * Baba XP - Extreme Programming Projects Manager
 * Copyright (c) 2004 babaxp 
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
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

//import org.babaxp.view.frames.MsgErrorFrame;


/**
 * File Write and Read. 
 * 
 * @author <a href="mailto:vitor@babaxp.org">Vitor Fernando Pamplona</a>
 * 
 * @since 19/12/2003
 * @version $Id: FileChooser.java,v 1.3 2004/10/27 20:49:47 vfpamp Exp $
 */
public class FileChooser extends JFileChooser {

	/** 
	 * Loads a file
	 * 
	 * @param parent Comonent that request the file
	 * @param title of the frame
	 * @param ext extensions filter  
	 */
	public static File loadFile(Component parent, String title, HashMap ext) {
		FileChooser openDialog = new FileChooser();
		openDialog.setDialogTitle(title);
				
		boolean first = true; 
		Iterator i = ext.keySet().iterator();
		String strExt = null;
		String strDesc = null;
		while (i.hasNext()) {
			strExt = (String) i.next();
			strDesc = (String) ext.get(strExt);
			if (first) {
//				openDialog.setFileFilter(new SimpleFileFilter(strExt,strDesc));
				first = false;
			} else {
//				openDialog.addChoosableFileFilter(new SimpleFileFilter(strExt,strDesc));
			}
		}
				
		openDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int result = openDialog.showOpenDialog(parent);
		
		if (result == JFileChooser.CANCEL_OPTION)
			return null; 
					
		File file = openDialog.getSelectedFile();		
		System.out.println(file);
		return file;
	}

	public static File loadFile(Component parent, String title, String ext, String extDesc) {	
		HashMap extensions = new HashMap();
		extensions.put(ext, extDesc);
		
		return loadFile(parent, title, extensions);
	}

	/** 
	 * Saves a File
	 * 
	 * Returns null if no file was saved
	 * 
	 * @param parentPanel Parent Component
	 * @param title Title of the frame
	 * @param ext Extensions 
	 */
	/*public static File saveFile(Component parent, String title, HashMap ext) {
		FileChooser saveDialog = new FileChooser();

		boolean first = true; 
		Iterator i = ext.keySet().iterator();
		String strExt = null;
		String strDesc = null;
		while (i.hasNext()) {
			strExt = (String) i.next();
			strDesc = (String) ext.get(strExt);
			if (first) {
//				saveDialog.setFileFilter(new SimpleFileFilter(strExt,strDesc));
				first = false;
			} else {
//				saveDialog.addChoosableFileFilter(new SimpleFileFilter(strExt,strDesc));
			}
		}

		saveDialog.setDialogTitle(title);
		saveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int resultado = saveDialog.showSaveDialog(parent);
	
		if (resultado == JFileChooser.CANCEL_OPTION)
			return null; 
				
		File file = saveDialog.getSelectedFile();				
	
		if (file.exists()) {
			int res = saveDialog.;
			if (res == JOptionPane.NO_OPTION || res == JOptionPane.CANCEL_OPTION) 
				return null;								
		} else { 
			try {
				file.createNewFile();
			} catch (IOException e1) {
//				MsgErrorFrame.show(e1);
			}
		}

		return file;				
	}
	

	public static File saveFile(Component parent, String title, String ext, String extDesc) {	
		HashMap extensions = new HashMap();
		extensions.put(ext, extDesc);
		
		return saveFile(parent, title, extensions);
	}*/
	
	public void addChoosableFileFilter(String ext, String descExt) {
//		this.addChoosableFileFilter(new SimpleFileFilter(ext,descExt));
	} 
	
	

}
