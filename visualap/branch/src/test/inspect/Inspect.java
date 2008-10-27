// ONLY FOR DEMO PURPOSE

package test.inspect;
import common.SampledAudio;
import java.io.Serializable;
import java.lang.reflect.*;
import java.awt.image.BufferedImage;

	 
/**
 * 
 * Inspect is a component used to print type information.
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     1.0
 */


public class Inspect implements Serializable, Cloneable {
	public static final long serialVersionUID = -2198213713506132935L;

	public Object clone() {
		Inspect cloning = new Inspect();
		return cloning;
	}

// method accessible from the outside world!
    public void print(Object obj) {
		if (obj != null) {
			Class c = obj.getClass();
			if (c.isArray())
				System.out.print(c.getComponentType().getName()
					+"["+Array.getLength(obj)+"]");
			else if (c.isPrimitive()) {
				System.out.print(", value = "+obj);
			} else {
				System.out.print(c.getName());
				if (obj instanceof SampledAudio)
					System.out.print("["+((SampledAudio)obj).format.toString()+" "+((SampledAudio)obj).length+" bytes ]");
				if (obj instanceof BufferedImage) {
					BufferedImage image = (BufferedImage)obj;
					System.out.print("["+image.getWidth()+" x "+image.getHeight()+"]");
				}
			}
			System.out.println();
		}
		else System.out.println("null");
	}

	public static String getToolTipText() {
		return "print type information";
	}

}
