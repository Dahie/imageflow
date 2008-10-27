/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/*
This class is a workaround to avoid the ClassNotFoundException when using XMLEncoder and XMLDecoder

This class has been designed by Antony Miguel (thankx!)
*/

package visualap;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;

public class ClassPathHacker {
	 
	private static final Class[] parameters = new Class[]{URL.class};
	 
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}
	 
	public static void addFile(File f) throws IOException {
		addURL(f.toURL());
	}	 
	 
	public static void addURL(URL u) throws IOException {			
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
	 
		try {
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}		
	}	
}
