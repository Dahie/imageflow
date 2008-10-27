
/** 
 * The only thing we define in the SimpleBean BeanInfo is a GIF icon.
 */


package test.inspect;
import java.beans.*;

import java.lang.reflect.Method;

public class InspectBeanInfo extends SimpleBeanInfo {

// Here put reference to ICON files (remove if not needed)

    public java.awt.Image getIcon(int iconKind) {
		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			java.awt.Image img = loadImage("inspect16.png");
			return img;
		}
		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			java.awt.Image img = loadImage("inspect32.png");
			return img;
		}
	return null;
    }

// Here put reference to supported methods (remove if not needed)

	public MethodDescriptor[] getMethodDescriptors() {
	// First find the "method" object.
		Class args[] = { Object.class };
		Method m;
		try {
			m = Inspect.class.getMethod("print", args);
		} catch (Exception ex) {
			// "should never happen"
			throw new Error("Missing method: " + ex);
		}

	// Now create the MethodDescriptor array:
		MethodDescriptor result[] = new MethodDescriptor[1];
		result[0] = new MethodDescriptor(m); 
		return result;
    }

}
