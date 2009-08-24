    /**
 * 
 */
package de.danielsenff.imageflow;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.PlugIn;

import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.JFrame;

import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

import de.danielsenff.imageflow.controller.GraphController;


/**
 * Main-method
 * @author danielsenff
 *
 */
public class ImageFlow extends Application implements PlugIn {

	protected ImageJ imageJ;
	
	Vector<Window> windows;
	

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
    	windows = new Vector<Window>();
    	
    	
    	// open workflow by argument
    	String workflowPath = args != null ? args[0] : "none";
    	System.out.println(workflowPath);
    	GraphController graphController = new GraphController();
    	try {
			graphController.read(new File(workflowPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
    	
    }
    
    private ImageFlowView imageFlowView;
    
	@Override
	protected void startup() {
		
		if (IJ.isMacOSX()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true"); 
			System.setProperty("apple.awt.brushMetalRounded", "true");
		}
		imageFlowView = new ImageFlowView(this);
//		if()
//		imageFlowView.setG
		imageFlowView.getFrame().setSize(800, 600);
		
		show(imageFlowView);
		addWindow(imageFlowView.getFrame());
	}

	
	public FrameView getMainView() {
		return this.imageFlowView;
	}
	
	public JFrame getMainFrame() {
		return this.imageFlowView.getFrame();
	}
	
	/*private class MainFrameListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
        	exit(e);
        	System.exit(0);
        }
    }*/

	/*
	 * Start-method for starting the app from within ImageJ as plugin.
	 */
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
	
	public void addWindow(final Window window) {
		this.windows.add(window);
	}
	
}
