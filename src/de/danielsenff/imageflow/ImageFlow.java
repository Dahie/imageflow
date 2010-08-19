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
package de.danielsenff.imageflow;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.PlugIn;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import de.danielsenff.imageflow.controller.MacApplication;


/**
 * Main-method
 * @author danielsenff
 *
 */
public class ImageFlow extends SingleFrameApplication implements PlugIn {

	/**
	 * Current ImageJ instance.
	 */
	protected ImageJ imageJ;
	private ImageFlowView imageFlowView;

	/**
	 * Main, start of the application
	 * @param args
	 */
	public static void main(String[] args) {
		
	
		
		launch(ImageFlow.class, args);
	}


	/**
	 * A convenient static getter for the application instance.
	 * @return the instance of DocumentEditorApp
	 */
	public static ImageFlow getApplication() {
		return Application.getInstance(ImageFlow.class);
	}

	@Override
	protected void initialize(String[] args) {
		super.initialize(args);

		if(System.getProperty("mrj.version") == null){
			/*addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent we){
					//not on a mac cleanup
					System.exit(0);
				}});*/
		} else {
			System.out.println("bli");
			MacApplication macApplication = new MacApplication(getApplication());
		}

		// open workflow by argument
		/*String workflowPath = args != null ? args[0] : "none";
    	System.out.println(workflowPath);
    	GraphController graphController = new GraphController();
    	try {
			graphController.read(new File(workflowPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};*/

	}


	@Override
	protected void startup() {
		imageFlowView = new ImageFlowView(this);

		show(imageFlowView);
	}

	@Override
	protected void shutdown() {
		// TODO clean imagej shutdown

		super.shutdown();
	}

	/*
	 * Start-method for starting the app from within ImageJ as plugin.
	 */
	public void run(String args) {
		String oldPath = System.getProperty("user.dir");
		System.setProperty("user.dir", oldPath + File.separator + "plugins" + File.separator + "Imageflow");

		launch(ImageFlow.class, null);
	}

	/**
	 * Return the currently running ImageJ instance.
	 * @return
	 */
	public ImageJ getImageJInstance() {
		if(IJ.getInstance() == null)
			this.imageJ = new ImageJ();
		return this.imageJ;
	}
	
	public static Window[] getWindows() {
		return Window.getWindows();
	}
	
	public static int getWindowsCount() {
		return Window.getWindows().length;
	}

}
