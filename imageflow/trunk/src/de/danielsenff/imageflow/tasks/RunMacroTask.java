package de.danielsenff.imageflow.tasks;

import ij.ImageJ;
import ij.plugin.Macro_Runner;

import org.jdesktop.application.Application;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.controller.GraphController;


/**
 * Task to Run the current workflow.
 * @author danielsenff
 *
 */
public class RunMacroTask extends GenerateMacroTask {
	
	private boolean closeAll;
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
		this.progressObserver = new ProgressObserver(new ProgListener());
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
		
		if(closeAll) {
			String closeAllCommand = "while (nImages>0) { \n selectImage(nImages);\n close(); } ";
			macro = closeAllCommand + macro;
		}
		
		
		// if the graph checks turn out false, the resulting macro will be just a null-pointer
		if(macro != null) {
		
			ImageJ imagej = ((ImageFlow)ImageFlow.getInstance()).getImageJInstance();
			Macro_Runner mr = new Macro_Runner();
			return mr.runMacro(macro, "");
			
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
		}
		
		System.out.println("we are done");
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
	
	
	@Override protected void succeeded(final Object superclass) {
	    setMessage("Done");
	}
	@Override protected void cancelled() {
	    setMessage("Canceled");
	}
	
	private class ProgListener {
		public void fireProgressChanged(float value) {
			setProgress(value);
		}
	}
	
	static class ProgressObserver {
		public static float value = 0;
		
		ProgListener progListener;
		
		public ProgressObserver(ProgListener listener) {
			this.progListener = listener;
		}

		public void setValue(float progressValue) {
			value = progressValue;
			progListener.fireProgressChanged(value);
		} 
	}
}


