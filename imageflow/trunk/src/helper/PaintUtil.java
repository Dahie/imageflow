/**
 * 
 */
package helper;


/**
 * @author danielsenff
 *
 */
public class PaintUtil {

	public static int alignY(int maxOutputs, int inputN, int height, int pinSize) {
		int value = (inputN*height/maxOutputs) + (height/(2*maxOutputs));
		return value - (pinSize / 2);
	}
	
}
