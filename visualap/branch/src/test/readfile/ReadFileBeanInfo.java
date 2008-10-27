package test.readfile;
import java.beans.*;
import java.lang.reflect.Method;

/**
 * 
 * Support class that provides the needed BeanInfo for ReadFile
 * 
 * @author      javalc6
 * @version     1.0
 */

public class ReadFileBeanInfo extends SimpleBeanInfo {

/**
* Returns the icon representing the component
* 
* @param iconKind The kind of icon requested
* @return      icon
*/

    public java.awt.Image getIcon(int iconKind) {
		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			java.awt.Image img = loadImage("ReadFile16.gif");
			return img;
		}
		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			java.awt.Image img = loadImage("ReadFile32.gif");
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
	    PropertyDescriptor pd = new PropertyDescriptor("File",	ReadFile.class);
	    pd.setPropertyEditorClass(FileEditorX.class);
	    PropertyDescriptor result[] = { pd };
	    return result;
	} catch (IntrospectionException ex) {
	    System.err.println("ReadFileBeanInfo: unexpected exception: " + ex);
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
		Class args[] = new Class[0];
		Method m;
		try {
			m = ReadFile.class.getMethod("generate", args);
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
