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

//	private static ResourceBundle bundle = ResourceBundle.getBundle("Texte");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphController controller = new GraphController();
		
		new Applicationframe(controller);
	}

}
