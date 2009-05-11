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

/**
 * Background specialisation of the {@link UnitElement}.
 * @author danielsenff
 *
 */
public class BackgroundUnitElement extends UnitElement {

	/**
	 * @param unitName
	 * @param unitsImageJSyntax
	 */
	public BackgroundUnitElement(final String unitName, final String unitsImageJSyntax) {
		super(unitName, unitsImageJSyntax);
	}

	public BackgroundUnitElement(final Point origin, final String unitName,
			final String unitsImageJSyntax) {
		super(origin, unitName, unitsImageJSyntax);
	}

	public BackgroundUnitElement(final Point origin, final String unitName,
			final MacroElement macroElement) {
		super(origin, unitName, macroElement);
	}

	
	@Override
	public void showProperties() {
		super.showProperties();
		
		final int imageType = getImageType();
		
		// change bitdepth for all outputs
		setOutputImageType(imageType);
		
		notifyModelListeners();
	}
	
	/**
	 * @param choice
	 */
	public void setOutputImageType(final String choice) {
		final int imageType = fromChoice(choice);
		setOutputImageType(imageType);
	}
	
	/**
	 * @param imageType
	 */
	public void setOutputImageType(final int imageType) {
		for (final Output output : outputs) {
			output.setOutputBitDepth(imageType);
		}
	}

	private int fromChoice(final String choice) {

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
