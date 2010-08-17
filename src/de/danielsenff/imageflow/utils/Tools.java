package de.danielsenff.imageflow.utils;

import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class Tools {

	
	public static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
	
	public static Element getXMLRoot(URL url) {
		try {
//			System.out.println("Reading xml-description");
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(url);

			Element root = doc.getRootElement();
			
			return root;
		}
		catch (Exception e) {
			System.err.println("Invalid XML document!");
			e.printStackTrace();
		}
		return null;
	}	

}
