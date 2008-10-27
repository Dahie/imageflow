/*
Version 1.0, 03-02-2008, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* 

class WebFetch retrieves files from the internet

javalc6
*/
package visualap;

import java.io.*;
import java.net.*;

public class WebFetch {

	public String fetchURL(String urlName, String element) throws IOException {
		URL url = new URL(urlName);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		String os = System.getProperty("os.name")+";"+System.getProperty("os.arch")+";"+System.getProperty("os.version");
		connection.setRequestProperty("User-Agent",VisualAp.getAppName()+" "+VisualAp.version+"("+os+")");
		int responseCode = connection.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new IOException("HTTP response code: " + String.valueOf(responseCode));
		}
		BufferedReader inStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = inStream.readLine())!= null) {
			line = line.toLowerCase();
			int i = line.indexOf(element);
			if (i != -1) {
				return (line.substring(i+element.length()).split("<")[0].trim());
			}
		}
		inStream.close();
		return null;
	}

	public void fetchFile(String urlName, File outFile) throws IOException {
		URL url = new URL(urlName);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		String os = System.getProperty("os.name")+";"+System.getProperty("os.arch")+";"+System.getProperty("os.version");
		connection.setRequestProperty("User-Agent",VisualAp.getAppName()+" "+VisualAp.version+"("+os+")");
		int responseCode = connection.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new IOException("HTTP response code: " + String.valueOf(responseCode));
		}
		InputStream inStream = new DataInputStream(connection.getInputStream());
		OutputStream outStream = new FileOutputStream(outFile);

		byte [] buffer = new byte[1024];
		int len;
		while ((len = inStream.read(buffer))!= -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		outStream.close();
	}
}
