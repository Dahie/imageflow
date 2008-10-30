/**
 * 
 */
package application;

import java.io.File;

import javax.swing.JFileChooser;

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
		
		JFileChooser imageFileChooser = new JFileChooser();
		
		imageFileChooser.setMultiSelectionEnabled(false);
		imageFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		final int res = imageFileChooser.showOpenDialog(null);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			final File files = imageFileChooser.getSelectedFile();
			System.out.println(files);
			GraphController controller = new GraphController(files.getAbsolutePath());
		
			new Applicationframe(controller);
		}
	}
	

}
