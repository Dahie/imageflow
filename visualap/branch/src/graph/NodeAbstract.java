/**
 * 
 */
package graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.lang.reflect.*;
import java.util.HashMap;


/**
 * @author danielsenff
 *
 */
public abstract class NodeAbstract extends Node {
	protected Object obj=null;
	protected long serialUID;
	protected String version="0.0"; // default value is 0.0

	
	/**
	 *  constructor not to be used, XMLEncoder/XMLDecoder
	 */
	public NodeAbstract() {
		super();
	}

	/**
	 * basic constructor
	 */
	public NodeAbstract(Point origin) {
		super(origin);
	}

	/**
	 * constructor including object
	 * Note: the class of the object shall implement Cloneable interfaces.
	 * @param origin
	 * @param obj
	 */
	public NodeAbstract(Point origin, Object obj) {
		super(origin);
		setObject(obj);
		setSerialUID(getObjSerialUID());
		setVersion(getObjVersion());
	}

	/**
	 * 
	 */
	public void setObject(Object obj) {
		this.obj = obj;
	};

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

	public long getObjSerialUID() {
		if (obj != null)
			getClassField("serialVersionUID");
		return 0L;
	}

	public String getObjVersion() {
		if (obj != null)
			return getClassField("version");
		return "0.0";
	}

	private String getClassField(String parameter) {
		try {
			Field f = obj.getClass().getField(parameter);	
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
