package test.ijplug;
import java.beans.*;
import java.lang.reflect.Method;

/**
 * 
 * Support class that provides the needed BeanInfo for Imagefilter
 * 
 * @author      javalc6
 * @version     1.0
 */

public class IJPlugBeanInfo extends SimpleBeanInfo {

// Here put reference to ICON files (remove if not needed)

    public java.awt.Image getIcon(int iconKind) {
		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			java.awt.Image img = loadImage("Imagefilter16.png");
			return img;
		}
		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			java.awt.Image img = loadImage("Imagefilter32.png");
			return img;
		}
	return null;
    }

// Here put reference to custom property editors (remove if not needed)

    public PropertyDescriptor[] getPropertyDescriptors() {
	try {
	    PropertyDescriptor pd1 = new PropertyDescriptor("Filter",	IJPlug.class);
	    pd1.setPropertyEditorClass(FilterNameEditor.class);
	    PropertyDescriptor result[] = { pd1};
	    return result;
	} catch (IntrospectionException ex) {
	    System.err.println("MuxBeanInfo: unexpected exception: " + ex);
	    return null;
	}
    }


// Here put reference to supported methods (remove if not needed)

	public MethodDescriptor[] getMethodDescriptors() {
	// First find the "method" object.
		Class args[] = { java.awt.image.BufferedImage.class};
		Method m;
		try {
			m = IJPlug.class.getMethod("output", args);
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
