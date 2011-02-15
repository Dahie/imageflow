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
	/**
	 * ProgressObserver is a workaround.
	 * We get the status of the progress from ImageJ from a static class.
	 * We need to convert this to a synchronized object to 
	 * be able to get the status in realtime.
	 */
	private static ProgressObserver progressObserver;
	
	/**
	 * @param app
	 * @param graphController
	 * @param showCode 
	 * @param closeAll 
	 * @param doShowLog
	 */
	public RunMacroTask(final Application app, 
			final GraphController graphController, 
			final boolean showCode, 
			final boolean closeAll) {
		super(app, graphController);
		this.showCode = showCode;
		this.closeAll = closeAll;
		progressObserver = new ProgressObserver(new MacroCallbackListener());
	}
	
	/**
	 * @param app
	 * @param graphController
	 * @param showCode
	 */
	public RunMacroTask(final Application app, 
			final GraphController graphController, 
			final boolean showCode) {
		this(app, graphController, showCode, false);
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
			
			ImageJ imagej = ((ImageFlow)ImageFlow.getInstance()).getImageJInstance();
			Macro_Runner mr = new Macro_Runner();
			String resultString = mr.runMacro(macro, "");
			
			/*
			 * TODO FIXME
			 * This Task has one major problem.
			 * We create/use an instance of ImageJ. The tasks expects ImageJ to terminate at some point.
			 * Therefore as ImageJ is kept alive and the task is never closed.
			 * In MacOS X this causes ImageFlow to not shutdown on close.
			 * In Linux it can cause problems with ImageJ not wanting to close.
			 */
			
			/* beginning for new functions, but not today, daniel */
			/*int[] imageIDs = WindowManager.getIDList();
			for (int i = 0; i < WindowManager.getImageCount(); i++) {
				ImagePlus image = WindowManager.getImage(imageIDs[i]);
				String imagetitle = image.getTitle(); 
				System.out.println(imagetitle);
				
				if(imagetitle.contains("-"))
					imagetitle.substring(0, imagetitle.indexOf('-'));
					
				String[] titleStrings = imagetitle.split("_");
				int unitID = Integer.valueOf(titleStrings[1]);
				int outputID = Integer.valueOf(titleStrings[3]);;			
				
				if(nodes.getUnit(unitID) instanceof UnitElement) {
					UnitElement unit = (UnitElement) nodes.getUnit(unitID);
					unit.setIcon(image.getImage().getScaledInstance(48, 48, Image.SCALE_FAST));
				}
				
				System.out.println("unit "+unitID+ " and output "+ outputID);
			}*/
			
			for (ImageJResult result : openedImages) {
				
				Output parentOutput = result.parentOutput;
				if (parentOutput.getDataType() instanceof ImageDataType) {
					IJ.selectWindow(parentOutput.getDisplayName());
					ImagePlus ip = IJ.getImage();
					if (ip != null) {
						UnitElement unitElement = (UnitElement) result.node;
						unitElement.setIconScaled(ip.getImage());
						unitElement.getOutput(parentOutput.getIndex()-1).setOutputObject(ip);
					}
				}
			}
			
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
		
		progressObserver.setValue(progressValue);
	}
	
	public static void setOutputData(String outputId, Object data) {
		// write the data as DataObject for the output of the given ID
		if (data != null && data instanceof ImagePlus) {
			
		}
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

		public void fireOutputData(String outputId, Object data) {
			graphController.setOutputData(outputId, data);
		}
	}
	
	static class OutputDataObserver {
		Object data;
		MacroCallbackListener progListener;
		private String outputIDvalue;
		private Object dataValue;
		
		public OutputDataObserver(MacroCallbackListener listener) {
			this.progListener = listener;
		}
		
		public void setValue(String outputId, Object data) {
			outputIDvalue = outputId; 
			dataValue = data;
			progListener.fireOutputData(outputIDvalue, dataValue);
		} 
	}
	
	static class ProgressObserver {
		public static float value = 0;
		
		MacroCallbackListener progListener;
		
		public ProgressObserver(MacroCallbackListener listener) {
			this.progListener = listener;
		}

		public void setValue(float progressValue) {
			value = progressValue;
			progListener.fireProgressChanged(value);
		} 
	}
}


