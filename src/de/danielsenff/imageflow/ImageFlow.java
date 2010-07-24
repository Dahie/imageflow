    /**
 * 
 */
package de.danielsenff.imageflow;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.PlugIn;

import java.awt.Window;
import java.io.File;
import java.util.Vector;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;


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
		addWindow(imageFlowView.getFrame());
	}

	@Override
	protected void shutdown() {
		// TODO clean imagej shutdown
		//imageJ.
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
	
	public void addWindow(final Window window) {
		this.windows.add(window);
	}
	
}
