package de.danielsenff.imageflow.controller;

import ij.IJ;

import java.awt.Window;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;

public class MacApplication extends Application {
	
	ImageFlow imageflow;
	
    public MacApplication() {
        addApplicationListener( new ApplicationAdapter(){
                public void handleReOpenApplication(ApplicationEvent event) {
                	 ImageFlowView imageFlowView = new ImageFlowView(imageflow);
                	 imageflow.show(imageFlowView.getFrame());
                }
                
                public void handleQuit( ApplicationEvent event ) {
					imageflow.exit();
                }
                public void handleAbout(ApplicationEvent event){

                }
            });
    }

	public MacApplication(ImageFlow imageFlow) {
		this();
		this.imageflow = imageFlow;
	}
}