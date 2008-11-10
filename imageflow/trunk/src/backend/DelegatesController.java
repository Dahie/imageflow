package backend;

import helper.Tools;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import models.unit.UnitDelegate;
import models.unit.UnitDescription;
import models.unit.UnitElement;
import models.unit.UnitFactory;
import visualap.Delegate;
import application.ApplicationController;

public class DelegatesController extends ApplicationController {

	private static DelegatesController controller;
	private ArrayList<Delegate> unitDelegates;
	
	/**
	 * Get a list of all Units that can be added to the workflow.
	 * @return
	 */
	public ArrayList<Delegate> getUnitDelegates() {
		return unitDelegates;
	}

	private DelegatesController() {
		unitDelegates = new ArrayList<Delegate>();
		
		File folder = new File("xml");
	    File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	    	if (listOfFiles[i].isFile()) {
	    		System.out.println("File " + listOfFiles[i].getName());
	    		
	    		final UnitDescription unitDescription = new UnitDescription(Tools.getRoot(listOfFiles[i]));
	    		unitDelegates.add(new UnitDelegate(unitDescription.getUnitName(), unitDescription.getHelpString()) {
	    			@Override
	    			public UnitElement createUnit(final Point origin) {
	    				//return UnitFactory.createSourceUnit(origin);
	    				return UnitFactory.createProcessingUnit(unitDescription, origin);
	    			}
	    		});
	    	} 
	    }
		
		
//		unitDelegates.add(new UnitDelegate("Source", "This is an image source, which loads an imagefile into the workflow.") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				return UnitFactory.createSourceUnit(origin);
//			}
//		});
		
		
//		unitDelegates.add(new UnitDelegate("Background", "Creates a new canvas, an empty background.") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				int width = Integer.parseInt(JOptionPane.showInputDialog("Width of the background:"));
//				int height = Integer.parseInt(JOptionPane.showInputDialog("Height of the background:"));
//				return UnitFactory.createBackgroundUnit(new Dimension(width,height), origin);
//			}
//		});
//		unitDelegates.add(new UnitDelegate("Add Noise", "This filter adds noise to the image.") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				return UnitFactory.createAddNoiseUnit(origin);
//			}
//		});
//		unitDelegates.add(new UnitDelegate("Gaussian Blur", "The image is blurred using Gaussian Blur.") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				return UnitFactory.createGaussianBlurUnit(origin);
//			}
//		});
//		unitDelegates.add(new UnitDelegate("Find Edges", "Finds edges in the image and filters them.") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				return UnitFactory.createFindEdgesUnit(origin);
//			}
//		});
//		unitDelegates.add(new UnitDelegate("Invert", "Invert the image.") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				return UnitFactory.createInvertUnit(origin);
//			}
//		});	
//		unitDelegates.add(new UnitDelegate("Math", "Subtracts to images from another") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				return UnitFactory.createImageCalculatorUnit(origin);
//			}
//		});
//		
//		unitDelegates.add(new UnitDelegate("Measure", "Prints several measurements of the image.") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				return UnitFactory.buildUnitElement(name, "run(\"Measure\");", origin);
//			}
//		});
//		unitDelegates.add(new UnitDelegate("Histogram", "Shows the histogramm of the input image.") {
//			@Override
//			public UnitElement createUnit(final Point origin) {
//				return UnitFactory.createHistogramUnit(origin);
//			}
//		});

	}
	
	public static DelegatesController getInstance() {
		if(controller == null) {
			controller = new DelegatesController();
		}
		return controller;
	}
}
