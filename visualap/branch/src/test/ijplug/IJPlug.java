// This software contains parts of ImageOps.java developed by Sun, please read the disclaimer at the end of this file

package test.ijplug;
import java.io.Serializable;
import java.awt.*;
import java.awt.image.*;

/**
 * 
 * Imagefilter is a component used to perform image processing.
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     1.0
 */

public class IJPlug implements Serializable, Cloneable {
	public static final long serialVersionUID = -1109418379058460364L;
    protected static String opsName[] = { 
              "Invert", "3x3 Blur", "3x3 Sharpen", "3x3 Edge", "5x5 Edge"};
    private static BufferedImageOp biop[] = new BufferedImageOp[opsName.length];
    private String filter = "Invert";

    static {
        byte invert[] = new byte[256];
        byte ordered[] = new byte[256];
        for (int j = 0; j < 256 ; j++) {
            invert[j] = (byte) (256-j);
            ordered[j] = (byte) j;
        }
        biop[0] = new LookupOp(new ByteLookupTable(0,invert), null);
        int dim[][] = {{3,3}, {3,3}, {3,3}, {5,5}};
        float data[][] = { {0.1f, 0.1f, 0.1f,              // 3x3 blur
                            0.1f, 0.2f, 0.1f,
                            0.1f, 0.1f, 0.1f},
                           {-1.0f, -1.0f, -1.0f,           // 3x3 sharpen
                            -1.0f, 9.0f, -1.0f,
                            -1.0f, -1.0f, -1.0f},
                           { 0.f, -1.f,  0.f,                  // 3x3 edge
                            -1.f,  5.f, -1.f,
                             0.f, -1.f,  0.f},
                           {-1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  // 5x5 edge
                            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                            -1.0f, -1.0f, 24.0f, -1.0f, -1.0f,
                            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f}};
        for (int j = 0; j < data.length; j++) {
            biop[j+1] = new ConvolveOp(new Kernel(dim[j][0],dim[j][1],data[j]));
        }
    }


/**
* Performs cloning of the current object
* 
* @return      the cloned object
*/
	public Object clone() {
		IJPlug cloning = new IJPlug();
		cloning.setFilter(filter);
		return cloning;
	}

/**
* Filter getter
* 
* @return      FilterName
*/
    public String getFilter(){
        return filter;
    }
 
/**
* Filter setter
* 
* @param newFilter FilterName
*/
    public void setFilter(String newFilter){
        filter = newFilter;
    }

/**
* Performs the image processing using the selected filter
* 
* @param image the image to process
* @return      the processed image
*/
    public BufferedImage output(BufferedImage image) throws Exception {
        for (int j = 0; j < opsName.length ; j++) {
			if (filter.equals(opsName[j]))
				return biop[j].filter(image, null);
		}
		return null; // should never happen
    }

/**
* Returns a string that provide short information about the component
* 
* @return      the short information about the component
*/

	public static String getToolTipText() {
		return "filters an image according to user settings";
	}

}


/*
 * @(#)ImageOps.java	1.32 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */