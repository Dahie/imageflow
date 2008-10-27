// ONLY FOR DEMO PURPOSE

package test.transform;
import java.awt.*;
import java.io.Serializable;

	 
// Bean has to implement Serializable

public class Transform implements Serializable, Cloneable {
	public static final long serialVersionUID = 6153774490973587719L;
    private Color color = Color.green;

	public static String getToolTipText() {
		return "change a string to uppercase";
	}


// Constructor sets inherited properties!
    public Transform(){
    }

// It is possible to add properties, by using getter and setter --- an example follows to handle the <color> property 
    //property getter method
    public Color getColor(){
        return color;
    }
 
    //property setter method
    public void setColor(Color newColor){
        color = newColor;
    }


	public Object clone() {
		Transform cloning = new Transform();
		cloning.setColor(color);
		return cloning;
	}

// method accessible from the outside world!
    public String translateString(String str) {
		return str.toUpperCase();
    }

}
