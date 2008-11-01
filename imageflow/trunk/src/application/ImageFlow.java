/**
 * 
 */
package application;

import ij.IJ;
import gui.Applicationframe;
import backend.GraphController;

/**
 * Main-method
 * @author danielsenff
 *
 */
public class ImageFlow {


	/**
	 * Main, start of the application
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (IJ.isMacOSX()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true"); 
			System.setProperty("apple.awt.brushMetalRounded", "true");
		}
		
		GraphController controller = new GraphController();
		new Applicationframe(controller);
		
	}
	

}
