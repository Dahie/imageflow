/*
Version 1.0, 30-12-2007, First release
Version 1.1, 03-02-2008, added <version> field in components, Cloneable interface now is optional for components

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* class NodeBean

This class is used to create nodes in a graph.
A node can contain an <object>, e.g. a bean.
This class provides support for several features: blabla....

todo:
- setObject() e BeanDelegate sono in relazione, trovare il modo di unificare

javalc6
*/
package graph;
import java.io.*;
import java.beans.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*; 
import java.lang.reflect.*;
import java.util.HashMap;

public class NodeBean extends Node {
	private Object obj=null;
	private transient Image icon=null;
	private long serialUID;
	private String version="0.0"; // default value is 0.0

// inPins and outPins declared public to allow graph analysis
	public transient Pin [] inPins = new Pin[0];
	public transient Pin [] outPins = new Pin[0];

// constructor not to be used, XMLEncoder/XMLDecoder
	public NodeBean() {
		super();
	}


// basic constructor
	public NodeBean(Point origin) {
		super(origin);
	}

// constructor including object
// Note: the class of the object shall implement Cloneable interfaces.
	public NodeBean(Point origin, Object obj) {
		super(origin);
		setObject(obj);
		setSerialUID(getObjSerialUID());
		setVersion(getObjVersion());
	}

	public long getObjSerialUID() {
		if (obj != null)
			try {
				Field f = obj.getClass().getField("serialVersionUID");	
				return f.getLong(obj.getClass());
			}
			catch (NoSuchFieldException ex) {
	//				throw new BeanException("Missing serialVersionUID");
			}
			catch (IllegalAccessException ex) {
	//				throw new BeanException("IllegalAccessException");
			}
		return 0L;
	}

	public String getObjVersion() {
		if (obj != null)
			try {
				Field f = obj.getClass().getField("version");	
				return (String) f.get(obj.getClass());
			}
			catch (NoSuchFieldException ex) {
				return "0.0";
			}
			catch (IllegalAccessException ex) {
	//				throw new BeanException("IllegalAccessException");
			}
		return "0.0";
	}


	public Object getObject() {
		return(obj);
	}

	public void setObject(Object obj) {
		this.obj = obj;
		if (obj != null)
			try {
// code here must be in sync with BeanDelegate.java constructor
					Class clazz = obj.getClass();
					BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
					MethodDescriptor[] methods = bi.getMethodDescriptors();
					Class<?>[] input = methods[0].getMethod().getParameterTypes();					
					inPins = new Pin[input.length];
					for (int i = 0; i < inPins.length; i++) {
						inPins[i] = new Pin("input", i, inPins.length, this);
					}
					int n_outputs = 0;
					for (int i=0; i <methods.length; i++) {
						if (methods[i].getMethod().getReturnType() != Void.TYPE)
							n_outputs++;
					}
					outPins = new Pin[n_outputs];
					for (int i = 0; i < outPins.length; i++) {
						outPins[i] = new Pin("output", i, outPins.length, this);
					}					
					icon = bi.getIcon(BeanInfo.ICON_COLOR_32x32);
				} catch(java.beans.IntrospectionException ex) {	ex.printStackTrace(); }
	}

	public long getSerialUID() {
		return serialUID;
	}

	public void setSerialUID(long aSerialUID) {
		serialUID = aSerialUID;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Object contains(int x, int y) {
		if ((x >= origin.x - 3)&&(x < origin.x + 2))	{
			for (int i = 0; i < inPins.length; i++) {
				int lower_y = origin.y-3 +(dimension.height*i+dimension.height/2)/inPins.length;
				if ((y >= lower_y)&&(y <= lower_y + 6)) return inPins[i];
			}
		}
		if ((x >= origin.x + dimension.width - 2)&&(x < origin.x + dimension.width + 3))	{
			for (int i = 0; i < outPins.length; i++) {
				int lower_y = origin.y-3 +(dimension.height*i+dimension.height/2)/outPins.length;
				if ((y >= lower_y)&&(y <= lower_y + 6)) return outPins[i];
			}
		}
		return super.contains(x,y);
	}

	public Node clone() throws CloneNotSupportedException {
// clone object translated by 4 pixels
// due to the fact that clone() method is protected, the following workaround is used:
			Object clobj;
			if (obj instanceof Cloneable) {
				try {
				   Class c = obj.getClass();
				   Method m = c.getMethod("clone", (Class[]) null);
				   m.setAccessible(true);
				   clobj = m.invoke(obj, (Object[]) null);
				} catch(Exception ex) 
				{	throw new CloneNotSupportedException(ex.getMessage()); }
			} else {
// Version 1.1: Cloneable is optional
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);
					oos.writeObject(obj);
					oos.close();
					clobj = (new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))).readObject();
				} catch (Exception ex) {
					throw new CloneNotSupportedException(ex.getMessage());
				}
			}
			return new NodeBean(new Point(origin.x+4, origin.y+4), clobj);
	}
	
	public Rectangle paint(Graphics g, ImageObserver io) {
		Color saveColor = g.getColor();
		Font saveFont = g.getFont();

		if (icon == null) {
// obj != null, icon == null
			g.setFont(new Font("Arial", Font.PLAIN, 14));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(selected ? Color.red : new Color(250, 220, 100));
			dimension.setSize(fm.stringWidth(label) + 10, fm.getHeight() + 4);
			g.fillRect(origin.x, origin.y, dimension.width, dimension.height);
			g.setColor(Color.black);
			g.drawRect(origin.x, origin.y, dimension.width-1, dimension.height-1);
			g.drawString(label, origin.x + 5, (origin.y + 2) + fm.getAscent());
		} else {
// obj != null
			if (selected) {
				g.setColor(Color.red);
				g.drawRect(origin.x-2, origin.y-2, 35, 35);
			}
/*				if (icon == null) {
					icon = Introspector.getBeanInfo(obj.getClass()).getIcon(BeanInfo.ICON_COLOR_32x32);
					if (icon == null) 
						icon = javax.imageio.ImageIO.read(Node.class.getResource("unknown32.gif"));
				}
*/
			g.drawImage(icon, origin.x, origin.y, 32, 32, io);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(label, origin.x, (origin.y + 32) + g.getFontMetrics().getAscent());
			dimension.setSize(32, 32);
		}
		for (int i = 0; i < inPins.length; i++) {
				g.setColor(Color.blue);
				g.drawRect(origin.x-2, origin.y-2 +(dimension.height*i+dimension.height/2)/inPins.length, 4, 4);
		}
		for (int i = 0; i < outPins.length; i++) {
				g.setColor(Color.blue);
				g.drawRect(origin.x+dimension.width-2, origin.y-2 +(dimension.height*i+dimension.height/2)/outPins.length, 4, 4);
		}

		if (dragging != null) {
			g.setColor(Color.black);
			g.drawRect(dragging.x, dragging.y, dragging.width-1, dragging.height-1);
		}
		g.setFont(saveFont);
		g.setColor(saveColor);
        return new Rectangle(origin, dimension);
    }


	public void clear() {
		try {
		   Class c = obj.getClass();
		   Method m = c.getMethod("dispose", (Class[]) null);
		   m.setAccessible(true);
		   m.invoke(obj, (Object[]) null);
		} catch(Exception ex) { } // do nothing!
		obj = null;
	}

	public void setContext(HashMap<String, Object> globalvars) {
		try {
		   Class c = obj.getClass();
		   Method m = c.getMethod("setContext", new Class[]{HashMap.class});
		   m.invoke(obj, new Object[]{globalvars});
		} catch(Exception ex) { } // do nothing!
	}


}
