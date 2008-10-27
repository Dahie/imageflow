package test.writefile;
import java.beans.*;
import java.lang.reflect.Method;
/**
 * 
 * Support class that provides the needed BeanInfo for WriteFile
 * 
 * @author      javalc6
 * @version     1.0
 */
public class WriteFileBeanInfo extends SimpleBeanInfo {

/**
* Returns the icon representing this component
* 
* @param iconKind The kind of icon requested
* @return      icon
*/

    public java.awt.Image getIcon(int iconKind) {
		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			java.awt.Image img = loadImage("WriteFile16.gif");
			return img;
		}
		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			java.awt.Image img = loadImage("WriteFile32.gif");
			return img;
		}
	return null;
    }

/**
* Returns an array of PropertyDescriptors describing the editable properties supported by this component
* 
* @return      array of PropertyDescriptors
*/

    public PropertyDescriptor[] getPropertyDescriptors() {
	try {
	    PropertyDescriptor pd = new PropertyDescriptor("File", WriteFile.class);
	    pd.setPropertyEditorClass(FileEditorW.class);
	    PropertyDescriptor result[] = { pd };
	    return result;
	} catch (IntrospectionException ex) {
	    System.err.println("WriteFileBeanInfo: unexpected exception: " + ex);
	    return null;
	}
    }

/**
* Returns an array of MethodDescriptors describing the externally visible methods supported by this component
* 
* @return      array of MethodDescriptors
*/

	public MethodDescriptor[] getMethodDescriptors() {
	// First find the "method" object.
		Class args[] = { Object.class };
		Method m;
		try {
			m = WriteFile.class.getMethod("write", args);
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
