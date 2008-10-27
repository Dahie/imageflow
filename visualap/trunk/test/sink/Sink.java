// ONLY FOR DEMO PURPOSE

package test.sink;
import java.io.Serializable;
import java.beans.PropertyVetoException;

	 
// Bean has to implement Serializable

public class Sink implements Serializable, Cloneable {
	public static final long serialVersionUID = -5394354278610294801L;
    private int value = 5;

// It is possible to add properties, by using getter and setter --- an example follows to handle the <color> property 
    //property getter method
    public int getValue(){
        return value;
    }
 
    //property setter method
    public void setValue(int newValue) throws PropertyVetoException {
		if (newValue > 9)
			throw new PropertyVetoException("value must be lower than 10!", null);
        value = newValue;
    }
 

	public Object clone() {
		Sink cloning = new Sink();
		try {		
			cloning.setValue(value);
		}
		catch (PropertyVetoException ignore) {	}
		return cloning;
	}

// method accessible from the outside world!
    public void translateString(String str) {
		System.out.println(str);
	}

	public static String getToolTipText() {
		return "print the input string";
	}

}
