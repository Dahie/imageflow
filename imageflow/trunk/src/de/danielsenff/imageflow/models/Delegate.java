/**
 * 
 */
package de.danielsenff.imageflow.models;

import java.awt.Image;
import java.net.URL;

/**
 * @author danielsenff
 *
 */
public abstract class Delegate {

	protected String name;

	protected Image icon;
	protected URL helpfile;
	protected String version;
	protected String toolTipText;
	
	protected String shortName(String fullName) {
		int ix = fullName.lastIndexOf('.');
		if (ix >= 0) {
			return fullName.substring(ix+1);
		} else	return fullName;
	}

	
	
}
