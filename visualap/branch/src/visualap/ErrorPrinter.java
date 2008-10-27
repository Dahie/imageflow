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
The ErrorPrinter is a static class providing useful support for error printout/logging

javalc6

*/

package visualap;
import java.io.*;
import java.util.*;
import java.awt.Component;
import javax.swing.JOptionPane;


public class ErrorPrinter {

// printInfo() prints a message
	public static void printInfo(String message) {
        Throwable t = new Throwable();
        StackTraceElement elements[] = t.getStackTrace();
        System.err.println(elements[1].getClassName()+"." + elements[1].getMethodName()+": "+message);
	}

// printInfo() prints a message
	public static void printInfo(Exception ex) {
        StackTraceElement elements[] = ex.getStackTrace();
        System.err.println(elements[1].getClassName()+"." + elements[1].getMethodName()+": "+"Exception: "+ex.getMessage());
	}

// exit() prints a message and then terminates execution (FATAL ERRORS)
	public static void exit(String message) {
		printInfo(message);
		System.exit(0);
	}


// dump() appends the error information on file error.log
	public static void dump(Exception ex, String [] versionInformation) {
		try {
			PrintWriter outStream = new PrintWriter(new FileWriter("error.log", true));
			outStream.println("Time: "+new Date().toString());
			ex.printStackTrace(outStream);
			for (int i=0; i < versionInformation.length; i++)	{
				outStream.println(versionInformation[i]);
			}

			Properties props = System.getProperties();
			Iterator keys = props.keySet().iterator();
			String key;
			TreeMap properties = new TreeMap();
			while (keys.hasNext()) {
				key = (String)keys.next();
				if (key.startsWith("java."))
					outStream.println(key+"="+props.getProperty(key));
			}
			outStream.println("========================================");
			outStream.close();
		} catch (IOException e) {	}
    }

// showDialog() show a message to the user
	public static void showDialog(Component parentComponent, Exception ex) {
		JOptionPane.showMessageDialog(parentComponent, "Exception\n\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
    }
// showDialog() show a message to the user
	public static void showDialog(Component parentComponent, String message) {
		JOptionPane.showMessageDialog(parentComponent, message,"Error",JOptionPane.ERROR_MESSAGE);
    }

}

