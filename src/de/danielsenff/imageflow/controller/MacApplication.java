package de.danielsenff.imageflow.controller;

import org.jdesktop.application.FrameView;

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
                	FrameView imageFlowView;
                	if (!imageflow.hasImageFlowView()) {
                		imageFlowView = new ImageFlowView(imageflow);	
                	} else {
                		imageFlowView = imageflow.getMainView();
                	}
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