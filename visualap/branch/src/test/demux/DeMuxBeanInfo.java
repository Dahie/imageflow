
/** 
 * The only thing we define in the SimpleBean BeanInfo is a GIF icon.
 */

package test.demux;
import java.beans.*;
import common.SampledAudio;
import java.lang.reflect.Method;

public class DeMuxBeanInfo extends SimpleBeanInfo {

// Here put reference to ICON files (remove if not needed)

    public java.awt.Image getIcon(int iconKind) {
		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			java.awt.Image img = loadImage("DeMux16.png");
			return img;
		}
		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			java.awt.Image img = loadImage("DeMux32.png");
			return img;
		}
	return null;
    }

// Here put reference to supported methods (remove if not needed)

	public MethodDescriptor[] getMethodDescriptors() {
	// First find the "method" object.
		Method m, m2;
		try {
			m = DeMux.class.getMethod("channel0", new Class []{ SampledAudio.class });
			m2 = DeMux.class.getMethod("channel1", new Class []{ SampledAudio.class });
		} catch (Exception ex) {
			// "should never happen"
			throw new Error("Missing method: " + ex);
		}

	// Now create the MethodDescriptor array:
		return new MethodDescriptor[]{
			new MethodDescriptor(m), new MethodDescriptor(m2)};
    }

}
