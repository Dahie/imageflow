// ONLY FOR DEMO PURPOSE
package test.source;
import java.io.Serializable;
import java.io.File;

	 
// Bean has to implement Serializable

public class Source implements Serializable, Cloneable {
	public static final long serialVersionUID = -6595165591587747659L;
    private String aFile = "readme.txt";

	public static String getToolTipText() {
		return "generate a string using the current date";
	}

// It is possible to add properties, by using getter and setter --- an example follows to handle the <color> property 
    //property getter method
    public String getFile(){
        return aFile;
    }
 
    //property setter method
    public void setFile(String newFile){
        aFile = newFile;
    }
 

	public Object clone() {
		Source cloning = new Source();
		cloning.setFile(aFile); // il file andrebbe clonato???
		return cloning;
	}

// method accessible from the outside world!
    public String generate() {
		return new java.util.Date().toString();
    }

}
