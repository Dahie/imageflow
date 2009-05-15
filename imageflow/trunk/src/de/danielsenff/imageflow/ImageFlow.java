/**
 * 
 */
package de.danielsenff.imageflow;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.PlugIn;

import java.io.File;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;


/**
 * Main-method
 * @author danielsenff
 *
 */
public class ImageFlow extends SingleFrameApplication implements PlugIn {

	protected ImageJ imageJ;

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
    
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

	@Override
	protected void startup() {
		
		if (IJ.isMacOSX()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true"); 
			System.setProperty("apple.awt.brushMetalRounded", "true");
		}
//		getImageJInstance();
		ImageFlowView imageFlowView = new ImageFlowView(this);
		
		show(imageFlowView);
		getMainFrame().setSize(800, 600);
	}

	
	/*private class MainFrameListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
        	exit(e);
        	System.exit(0);
        }
    }*/

	public void run(String args) {
		String oldPath = System.getProperty("user.dir");
		System.setProperty("user.dir", oldPath + File.separator + "plugins" + File.separator + "Imageflow");
		
		launch(ImageFlow.class, null);
	}

	public ImageJ getImageJInstance() {
		if(IJ.getInstance() == null)
			this.imageJ = new ImageJ();
		
		return this.imageJ;
	}



	public void handleQuit() throws IllegalStateException {
    	System.exit(0);
	}
	
}
