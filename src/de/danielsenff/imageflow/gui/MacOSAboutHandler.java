/**
 * Copyright (C) 2008-2010 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.danielsenff.imageflow.gui;

import javax.swing.JFrame;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

import de.danielsenff.imageflow.ImageFlowAboutBox;

/**
 * Mac OSX Window Handler
 * @author dahie
 *
 */
public class MacOSAboutHandler extends Application {

	private JFrame frame;
	
    /**
     * 
     */
    @SuppressWarnings("deprecation")
	public MacOSAboutHandler() {
        addApplicationListener(new AboutBoxHandler());
    }

    /**
     * @param frame
     */
    @SuppressWarnings("deprecation")
	public MacOSAboutHandler(final JFrame frame) {
    	this.frame = frame;
        addApplicationListener(new AboutBoxHandler());
    }
    
    class AboutBoxHandler extends ApplicationAdapter {
        @Override
		public void handleAbout(ApplicationEvent event) {
            ImageFlowAboutBox about = new ImageFlowAboutBox(frame);
            about.setVisible(true);
        }
        
       @Override
	    public void handleQuit(ApplicationEvent arg0) {
    	   System.exit(0);
	    }
    }
}