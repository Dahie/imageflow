package imageflow.models.unit;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import imageflow.models.MacroElement;
import imageflow.models.Output;
import imageflow.models.parameter.ChoiceParameter;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

public class BackgroundUnitElement extends UnitElement {

	public BackgroundUnitElement(String unitName, String unitsImageJSyntax) {
		super(unitName, unitsImageJSyntax);
	}

	public BackgroundUnitElement(Point origin, String unitName,
			String unitsImageJSyntax) {
		super(origin, unitName, unitsImageJSyntax);
	}

	public BackgroundUnitElement(Point origin, String unitName,
			MacroElement macroElement) {
		super(origin, unitName, macroElement);
	}

	
	@Override
	public void showProperties() {
		super.showProperties();
		
		int imageType = getImageType();
		
		// change bitdepth for all outputs
		for (Output output : outputs) {
			output.setOutputBitDepth(imageType);
		}
		
		notifyModelListeners();
	}
	

	private int fromChoice(String choice) {

		if(choice.equalsIgnoreCase("32-bit")) {
			return PlugInFilter.DOES_32;
		} else if (choice.equalsIgnoreCase("16-bit")) {
			return PlugInFilter.DOES_16;
		} else if (choice.equalsIgnoreCase("8-bit")) {
			return PlugInFilter.DOES_8G;
		} else if (choice.equalsIgnoreCase("RGB")) {
			return PlugInFilter.DOES_RGB;
		}
		
		return -1;
	}

	public int getImageType() {
		return fromChoice((String)getParameter(2).getValue()); 
	}
	
}
