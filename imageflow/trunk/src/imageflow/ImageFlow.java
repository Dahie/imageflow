/**
 * 
 */
package imageflow;

import ij.IJ;
import imageflow.tasks.GenerateMacroTask;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

/**
 * Main-method
 * @author danielsenff
 *
 */
public class ImageFlow extends SingleFrameApplication {


	/**
	 * Main, start of the application
	 * @param args
	 */
	/*public static void main(String[] args) {
		
		if (IJ.isMacOSX()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true"); 
			System.setProperty("apple.awt.brushMetalRounded", "true");
		}
		
		new Applicationframe();
		
	}*/
	
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
		ImageFlowView imageFlowView = new ImageFlowView(this);
		
		show(imageFlowView);
		getMainFrame().setSize(800, 600);
	}

	
	private class MainFrameListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            exit(e);
        }
    }

}
