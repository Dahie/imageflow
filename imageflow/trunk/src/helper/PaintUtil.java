/**
 * 
 */
package helper;


/**
 * @author danielsenff
 *
 */
public class PaintUtil {

	public static int alignY(int numberOutputs, int i, int height, int pinSize) {
		int value = (i*height/numberOutputs) + (height/(2*numberOutputs));
		return value - (pinSize / 2);
	}
	
}
