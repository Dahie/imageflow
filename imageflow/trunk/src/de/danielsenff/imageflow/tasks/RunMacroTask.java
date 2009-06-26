package de.danielsenff.imageflow.tasks;

import ij.IJ;
import ij.ImageJ;

import org.jdesktop.application.Application;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.controller.GraphController;


/**
 * Task to Run the current workflow.
 * @author danielsenff
 *
 */
public class RunMacroTask extends GenerateMacroTask {
	
	/**
	 * @param app
	 * @param graphController
	 * @param doShowLog
	 */
	public RunMacroTask(final Application app, 
			final GraphController graphController, 
			final boolean doShowLog) {
		super(app, graphController);
		this.showlog = doShowLog;
	}

	@Override 
	protected String doInBackground() throws InterruptedException {
		
		String macro = super.doInBackground(); 
		
		ImageJ imagej = ((ImageFlow)ImageFlow.getInstance()).getImageJInstance();
		if(this.showlog)
			IJ.log(macro);

		IJ.runMacro(macro, "");
		
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
		
		return macro;
	}
}
