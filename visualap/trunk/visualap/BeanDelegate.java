/*
Version 1.0, 30-12-2007, First release
Version 1.1, 03-02-2008, added <version> field in components

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* class BeanDelegate

This class is used as delegate for JavaBeans, providing adapted method and information

javalc6
*/
package visualap;
import java.net.URL;
import java.net.URLClassLoader;
import java.awt.Image;
import java.lang.Class;
import java.lang.Void;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.beans.*;

public class BeanDelegate {
	protected String name;
	protected Class clazz;
	protected Image icon;
	protected URL helpfile;
	protected URLClassLoader classLoader;
	protected String toolTipText;
	protected long serialUID;
	protected String version;
	protected Class<?>[] input; // type of input
	protected Class<?>[] output = new Class<?>[0]; // type of output

	public BeanDelegate(String _name, URLClassLoader cl) throws BeanException {
		name = _name;
		classLoader = cl;
		try {
			clazz = cl.loadClass(name);
		} catch (ClassNotFoundException ex) {
			throw new BeanException("ClassNotFoundException");
		}
		try {
// code here must be in sync with Vertex.java
			BeanInfo bi = Introspector.getBeanInfo(clazz);
			icon = bi.getIcon(BeanInfo.ICON_COLOR_16x16);
			helpfile = clazz.getResource(name+".html");
			if (helpfile == null)
				helpfile = clazz.getResource(shortName(name)+".html");
			try {
				Method m = clazz.getMethod("getToolTipText",new Class[0]);		
				toolTipText = (String)m.invoke(null, new Object[0]);
			}
			catch (NoSuchMethodException ex) {} // don't care
			try {
				Field f = clazz.getField("serialVersionUID");		
				serialUID = f.getLong(clazz);
			}
			catch (NoSuchFieldException ex) {
				throw new BeanException("Missing serialVersionUID");
			}
			try {
				Field f = clazz.getField("version");		
				version = (String) f.get(clazz);
			}
			catch (NoSuchFieldException ex) {
				version = "0.0";
			}
			MethodDescriptor[] methods = bi.getMethodDescriptors();
//			if (methods.length == 0) throw new BeanException("Bean does not expose any method");
			if (methods.length == 0) return;

			input = methods[0].getMethod().getParameterTypes();

			if ((methods.length == 1)&&(methods[0].getMethod().getReturnType() == Void.TYPE)) return;
			output = new Class<?>[methods.length];
			for (int i=0; i <methods.length; i++) {
				output[i] = methods[i].getMethod().getReturnType();
				if (output[i] == Void.TYPE) throw new BeanException("Void method not allowed here");
			}
			for (int i=1; i <methods.length; i++) {
				Class<?>[] input_i = methods[i].getMethod().getParameterTypes();
				if (input_i.length != input.length)
					throw new BeanException("Incoherent type of input parameters found in method "+methods[i].getMethod().getName());
				for (int j=0; j <input.length; j++) {
					if (input_i[j] != input[j])
						throw new BeanException("Incoherent type of input parameters across methods");
				}
			}
		} catch (Throwable th) {
			throw new BeanException(th.toString());
		}

	}

	protected String shortName(String fullName) {
		int ix = fullName.lastIndexOf('.');
		if (ix >= 0) {
			return fullName.substring(ix+1);
		} else	return fullName;
	}
	
}
