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
package de.danielsenff.imageflow.tasks;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.Macro_Runner;

import org.jdesktop.application.Application;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.imagej.MacroGenerator.ImageJResult;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.ImageDataType;
import de.danielsenff.imageflow.models.unit.UnitElement;


/**
 * Task to Run the current workflow.
 * @author Daniel Senff
 *
 */
public class RunMacroTask extends GenerateMacroTask {
	
	private boolean closeAll;
	private boolean showImageJ;
	/**
	 * ProgressObserver is a workaround.
	 * We get the status of the progress from ImageJ from a static class.
	 * We need to convert this to a synchronized object to 
	 * be able to get the status in realtime.
	 */
	private static CallbackObserver callbackObserver;
	
	/**
	 * @param app
	 * @param graphController
	 * @param showCode 
	 * @param closeAll 
	 * @param doShowLog
	 */
	public RunMacroTask(final Application app, 
			final GraphController graphController, 
			final boolean showImageJ,
			final boolean showCode, 
			final boolean closeAll) {
		super(app, graphController);
		this.showCode = showCode;
		this.showImageJ = showImageJ;
		this.closeAll = closeAll;
		MacroCallbackListener listener = new MacroCallbackListener();
		callbackObserver = new CallbackObserver(listener);
	}
	
	/**
	 * @param app
	 * @param graphController
	 * @param showCode
	 */
	public RunMacroTask(final Application app, 
			final GraphController graphController, 
			final boolean showCode) {
		this(app, graphController, true, showCode, false);
	}

	@Override 
	protected String doInBackground() throws InterruptedException {
		setMessage("Translating workflow... ");
		// create macro
		String macro = super.doInBackground(); 
		
		setMessage("Executing Macro...");

		// if the graph checks turn out false, the resulting macro will be just a null-pointer
		if(macro != null) {
		
			if(closeAll) {
				String closeAllCommand = "while (nImages>0) { \n selectImage(nImages);\n close(); } ";
				macro = closeAllCommand + macro;
			}
			
			ImageFlow imageFlow = (ImageFlow)ImageFlow.getInstance();
			ImageJ imagej = imageFlow.getImageJInstance();
			Macro_Runner mr = new Macro_Runner();
			String resultString = mr.runMacro(macro, "");
			
			for (ImageJResult result : openedImages) {
				
				Output parentOutput = result.parentOutput;
				if (parentOutput.getDataType() instanceof ImageDataType) {
					IJ.selectWindow(parentOutput.getDisplayName());
					ImagePlus ip = IJ.getImage();
					if (ip != null) {
						UnitElement unitElement = (UnitElement) result.node;
						unitElement.setIconScaled(ip.getImage());
					}
				}
			}
			imageFlow.setImageJVisible(this.showImageJ);
			
			return resultString;
		}
		
		return macro;
	}
	
	/**
	 * Sets the current value of the ProgressBar. The parameter must be of type String
	 * to meet the demands of the macro call() function
	 * @param progress must be between 0.0 and 1.0
	 */
	public static void setProgress(String progress) {
		float progressValue = Float.valueOf(progress).floatValue();
		
		if (progressValue < 0) {progressValue = 0.0f;}
		else if(progressValue > 1) {progressValue = 1.0f;}
		
		callbackObserver.setProgressValue(progressValue);
	}
	
	public static void setOutputData(int nodeID, int outputID, double data) {
		// write the data as DataObject for the output of the given ID
		callbackObserver.setOutputData(nodeID, outputID, data);
	}
	
	public static void setOutputData(int nodeID, int outputID, int data) {
		// write the data as DataObject for the output of the given ID
		callbackObserver.setOutputData(nodeID, outputID, data);
	}
	
	public static void setOutputData(int nodeID, int outputID, String data) {
		// write the data as DataObject for the output of the given ID
		callbackObserver.setOutputData(nodeID, outputID, data);
	}
	
	/**
	 * Does not need to pass the object as an argument. Takes the current
	 * selected Image from ImageJ.
	 * @param nodeID
	 * @param outputId
	 */
	public static void setOutputImage(int nodeID, int outputID) {
		// get ImagePlus by imageTitle from ImageJ instance
		ImagePlus ip = IJ.getImage();
		callbackObserver.setOutputData(nodeID, outputID, ip);
	}
	
	@Override protected void succeeded(final Object superclass) {
	    setMessage("Done");
	}
	@Override protected void cancelled() {
	    setMessage("Canceled");
	}
	
	private class MacroCallbackListener {
		public void fireProgressChanged(float value) {
			setProgress(value);
		}

		public void fireOutputData(int nodeID, int outputID,  Object data) {
			graphController.setOutputData(nodeID, outputID, data);
		}
	}
	
	
	static class CallbackObserver {
		MacroCallbackListener progListener;
		
		public CallbackObserver(MacroCallbackListener listener) {
			this.progListener = listener;
		}

		public void setProgressValue(float progressValue) {
			progListener.fireProgressChanged(progressValue);
		} 
		
		public void setOutputData(int nodeID, int outputID, Object data) {
			progListener.fireOutputData(nodeID, outputID, data);
		}
	}
}


