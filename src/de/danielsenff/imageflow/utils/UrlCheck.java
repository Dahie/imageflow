package de.danielsenff.imageflow.utils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author danielsenff
 *
 */
public class UrlCheck {

public static boolean exists(final String URLName){
	try {
		HttpURLConnection.setFollowRedirects(false);
		// note : you may also need
		// HttpURLConnection.setInstanceFollowRedirects(false);
    	final HttpURLConnection con =	(HttpURLConnection) new URL(URLName).openConnection();
    	con.setRequestMethod("HEAD");
    		return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (final Exception e) {
			e.printStackTrace();
       		return false;
		}
	}

public static boolean existsFile(final String URLName){
	try {
	    HttpURLConnection.setFollowRedirects(false);
	    // note : you may also need
	    // HttpURLConnection.setInstanceFollowRedirects(false);
	    final HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
	    con.setRequestMethod("HEAD");
	    
	    if ((con.getResponseCode() == HttpURLConnection.HTTP_OK) &&
	    		con.getContentType().equals("text/html") == false ){
	    	final String contentType = con.getContentType();
	    	System.out.println("Contenttype: " + contentType);
	    	return true;
	  	} else {
	  		return false;
	  	}
	} catch (final Exception e) {
	       e.printStackTrace();
	       return false;
	       }
	}
}