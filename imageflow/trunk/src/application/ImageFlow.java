/**
 * 
 */
package application;

import gui.Applicationframe;
import backend.GraphController;

/**
 * @author danielsenff
 *
 */
public class ImageFlow {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphController controller = new GraphController();
		
		new Applicationframe(controller);
	}

}
