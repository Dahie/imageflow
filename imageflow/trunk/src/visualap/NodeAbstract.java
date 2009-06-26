/**
 * 
 */
package visualap;

import java.awt.Point;


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

	public Object getObject() {
		return(obj);
	}
	

}
