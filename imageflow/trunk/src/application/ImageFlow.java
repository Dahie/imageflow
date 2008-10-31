/**
 * 
 */
package application;

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
		
		GraphController controller = new GraphController();
		new Applicationframe(controller);
		
	}
	

}
