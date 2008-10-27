package test.ijplug;
/**
 * 
 * This class provides a custom editor to select a specific filter name
 * 
 * @author      javalc6
 * @version     1.0
 */
public class FilterNameEditor extends java.beans.PropertyEditorSupport {

    public String[] getTags() {
		return IJPlug.opsName;
    }

    public String getJavaInitializationString() {
		return (String)getValue();
    }

}

