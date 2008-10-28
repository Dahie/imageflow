/**
 * 
 */
package application;

import gui.Applicationframe;
import backend.Controller;
import Models.unit.UnitList;

/**
 * @author danielsenff
 *
 */
public class ImageFlow {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Controller controller = new Controller();
		UnitList units = controller.getUnitElements();
		System.out.println(units.size());
		new Applicationframe(units);
	}

}
