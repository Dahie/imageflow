/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* class Vertex

This class is used to perform graph analysis and running

javalc6
*/
package visualap;
import graph.*;
import java.lang.Class;
import java.lang.Void;
import java.lang.reflect.*;
import java.beans.*;
import java.util.HashMap;

class Backward {
	int index;
	Vertex obj;
}

public class Vertex {
	protected transient Object obj;
	protected transient NodeBean aNode;
	protected transient Backward [] backward = new Backward[0]; 
	protected transient Object [] iobuf_in = new Object[0]; 
	protected transient Object [] iobuf_out = new Object[0]; 
	protected transient Method [] methoda = new Method[0]; 
	protected transient Method start = null; 
	protected transient Method iterate = null; 
	protected transient Method stop = null; 
	protected transient boolean iterative;
	protected transient boolean isSource;
	protected transient boolean isSink;


	public Vertex(NodeBean aNode) {
		this.aNode = aNode;
		obj = aNode.getObject();
		try {
// code here must be in sync with BeanDelegate.java constructor
				Class clazz = obj.getClass();
				try {
					start = clazz.getMethod("start",new Class[0]);	
				}
				catch (NoSuchMethodException ex) {} // don't care
				try {
					iterate = clazz.getMethod("iterate",new Class[0]);	
					iterative = true;
				}
				catch (NoSuchMethodException ex) {} // don't care
				try {
					stop = clazz.getMethod("stop",new Class[0]);	
				}
				catch (NoSuchMethodException ex) {} // don't care

				BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
				MethodDescriptor[] methods = bi.getMethodDescriptors();
				backward = new Backward[aNode.inPins.length];
				iobuf_in = new Object[aNode.inPins.length];
				methoda = new Method[methods.length];
				int n_outputs = 0;
				for (int i=0; i <methods.length; i++) {
					methoda[i] = methods[i].getMethod();
					if (methods[i].getMethod().getReturnType() != Void.TYPE)
						n_outputs++;
				}
				iobuf_out = new Object[n_outputs];
				isSource = (iobuf_in.length == 0)&&(iobuf_out.length > 0);
				isSink = (iobuf_in.length > 0)&&(iobuf_out.length == 0);
			} catch(java.beans.IntrospectionException ex) {	ex.printStackTrace(); }

	}

// start() used only for running of graph
	public void start() throws InvocationTargetException {
		if (start!= null)
				try {
					start.invoke(obj,new Object[0]);
				} catch (IllegalAccessException ex) { } // ignore
	}
// iterate() used only for running of graph
	public boolean iterate() throws InvocationTargetException  {
		if (iterate!= null)
				try {
					return iterate.invoke(obj,new Object[0]).equals(true);
				} catch (IllegalAccessException ex) { } // ignore
		else if (isSource)
				return false;
		return true;
	}
// stop() used only for running of graph
	public void stop()  {
		if (stop!= null)
				try {
					stop.invoke(obj,new Object[0]);
				} catch (IllegalAccessException ex) { // ignore
				} catch (InvocationTargetException ex) { } // ignore
	}
}
