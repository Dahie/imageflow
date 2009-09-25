package de.danielsenff.imageflow.gui;

import javax.swing.JFrame;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

import de.danielsenff.imageflow.ImageFlowAboutBox;

public class MacOSAboutHandler extends Application {

	private JFrame frame;
	
    public MacOSAboutHandler() {
        addApplicationListener(new AboutBoxHandler());
    }

    public MacOSAboutHandler(JFrame frame) {
    	this.frame = frame;
        addApplicationListener(new AboutBoxHandler());
    }
    
    class AboutBoxHandler extends ApplicationAdapter {
        public void handleAbout(ApplicationEvent event) {
            ImageFlowAboutBox about = new ImageFlowAboutBox(frame);
            about.setVisible(true);
        }
        
       @Override
	    public void handleQuit(ApplicationEvent arg0) {
//	    	super.handleQuit(arg0);
    	   
    	   System.exit(0);
	    }
    }
}