
/** 
 * The only thing we define in the SimpleBean BeanInfo is a GIF icon.
 */
package test.tonegenerator;
import java.beans.*;

import java.lang.reflect.Method;

public class ToneGeneratorBeanInfo extends SimpleBeanInfo {

// Here put reference to ICON files (remove if not needed)

    public java.awt.Image getIcon(int iconKind) {
		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			java.awt.Image img = loadImage("Tone16.png");
			return img;
		}
		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			java.awt.Image img = loadImage("Tone32.png");
			return img;
		}
	return null;
    }

// Here put reference to custom property editors (remove if not needed)

    public PropertyDescriptor[] getPropertyDescriptors() {
	try {
	    PropertyDescriptor pd1 = new PropertyDescriptor("Type",	ToneGenerator.class);
	    pd1.setPropertyEditorClass(ToneGeneratorEditor.class);
	    PropertyDescriptor pd2 = new PropertyDescriptor("Duration_ms",	ToneGenerator.class);
	    PropertyDescriptor pd3 = new PropertyDescriptor("Frequency",	ToneGenerator.class);
	    PropertyDescriptor pd4 = new PropertyDescriptor("Volume",	ToneGenerator.class);
	    PropertyDescriptor result[] = { pd1, pd2, pd3, pd4 };
	    return result;
	} catch (IntrospectionException ex) {
	    System.err.println("MuxBeanInfo: unexpected exception: " + ex);
	    return null;
	}
    }

// Here put reference to supported methods (remove if not needed)

	public MethodDescriptor[] getMethodDescriptors() {
	// First find the "method" object.
		Class args[] = new Class[0];
		Method m;
		try {
			m = ToneGenerator.class.getMethod("generate", args);
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
