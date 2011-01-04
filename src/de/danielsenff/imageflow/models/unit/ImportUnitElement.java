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
package de.danielsenff.imageflow.models.unit;

import ij.ImagePlus;
import ij.WindowManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Vector;

import de.danielsenff.imageflow.gui.PropertiesDialog;
import de.danielsenff.imageflow.imagej.GenericDialog;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.utils.ImageJHelper;

/**
 * Specialized {@link UnitElement} for loading image files.
 * This supports the file formats ImageJ does natively.
 * @author danielsenff
 *
 */
public class ImportUnitElement extends UnitElement implements ImageSourceUnit {

	private static final int WINDOW_PARAMETER_INDEX = 0;
	private ImagePlus image;
	
	/**
	 * @param origin
	 * @param unitName
	 * @param macroElement
	 */
	public ImportUnitElement(final Point origin, 
			final String unitName,
			final MacroElement macroElement) 
	{
		super(origin, unitName, macroElement);
	}
	
	/**
	 * @param origin
	 * @param unitName
	 * @param macroString
	 */
	public ImportUnitElement(final Point origin, 
			final String unitName,
			final String macroString) 
	{
		super(origin, unitName, macroString);
	}
	
	/**
	 * @param origin
	 * @param unitName
	 * @param macroElement
	 * @param filepath 
	 */
	public ImportUnitElement(final Point origin, 
			final String unitName,
			final MacroElement macroElement, 
			final String filepath) 
	{
		super(origin, unitName, macroElement);
	}
	
	/**
	 * @param origin
	 * @param unitName
	 * @param macroString
	 * @param filepath 
	 */
	public ImportUnitElement(final Point origin, 
			final String unitName,
			final String macroString, 
			final String filepath) 
	{
		super(origin, unitName, macroString);
	}

	@Override
	public ImportUnitElement clone() {
		// clone the object
		String imageJSyntax;
		try {
			imageJSyntax = (String) cloneNonClonableObject(this.obj);
		} catch (CloneNotSupportedException e) {
			imageJSyntax = ((MacroElement)this.obj).getImageJSyntax();
		}

		ImportUnitElement clone = new ImportUnitElement(new Point(origin.x+15, origin.y+15), this.label, imageJSyntax);
		for (int j = 0; j < getInputsCount(); j++) {
			cloneInput(clone, j);
		}
		for (int i = 0; i < getOutputsCount(); i++) {
			cloneOutput(clone, i);
		}
		for (Parameter parameter : parameters) {
			cloneParameter(clone, parameter);
		}
		clone.setDisplay(isDisplay());
		clone.setColor(this.color);
		clone.setIcon(this.preview);
		clone.setHelpString(this.infoText);
		clone.setCompontentSize(this.getCompontentSize());
		return clone;
	}
	
	
	@Override protected void addParameterWidgets(final PropertiesDialog gd) {
		final ArrayList<Parameter> parameterList = getParameters();
		
		if (parameterList.isEmpty()) {
			gd.addMessage("This unit has no parameters and can not be adjusted.");
		} else {
			ChoiceParameter windowChoice = getWindowChoiceParameter(parameterList);
			if(WindowManager.getCurrentImage() != null) {
				
				Vector<String> imageWindows = getImageWindows();
//				if(imageWindows.contains(windowChoice.getValue())) {
//					// if same window is opened as before
//					gd.addMessage("Expected opened Image by the name "+windowChoice.getValue()+".");
//				} else {
					// the original window is not opened
					
					// reset Choices list
					windowChoice.getChoices().clear();
					
					windowChoice.getChoices().addAll(imageWindows);
					/*gd.addChoice(windowChoice.getDisplayName(), 
							windowChoice.getChoicesArray(), 
							windowChoice.getValue());*/
//				}
				
			} else {
				gd.addMessage("There are no images opened in ImageJ.");
			} 
			if (windowChoice.isChoicesEmpty())
				gd.addMessage("Expected opened Image by the name "+windowChoice.getValue()+".");
			
		}
	}

	private ChoiceParameter getWindowChoiceParameter(
			final ArrayList<Parameter> parameterList) {
		return (ChoiceParameter) parameterList.get(WINDOW_PARAMETER_INDEX);
	}
			
	
	@Override protected void updateParameters(final GenericDialog gd) {
		setLabel((String) (gd.getNextString()).trim());
		setDisplay(gd.getNextBoolean());
		
		String selectedWindow = (String) (gd.getNextChoice());
		getWindowChoiceParameter(getParameters()).setValue(selectedWindow);
		setImagePlus(WindowManager.getImage(selectedWindow));
	}
	
	private void setImagePlus(ImagePlus image) {
		this.image = image;
		updateImageType();
	}

	private Vector<String> getImageWindows() {
		Vector<String> imagelist = new Vector<String>();
		int imageID;
		for (int i = 1; i < WindowManager.getImageCount()+1; i++) {
    		imageID = WindowManager.getNthImageID(i);
			ImagePlus ip = WindowManager.getImage(imageID);
			if (ip != null) {
				imagelist.add(ip.getTitle());
			}
    	}
		return imagelist;
	}
	
	/**
	 * The current ImageType is determined by the currently selected {@link ImagePlus} window.
	 * The unit-icon and labels will be updated as well.
	 * If no file is selected or the file doesn't exist, a message is displayed.
	 */
	public void updateImageType() {
		int imageType = -1;
		
		if(getImagePlus() != null) {
			imageType = getImageType();
			int width = 48, height = 48;
			BufferedImage thumbnail = scaleThumbnail(width, height);
			
			this.unitComponentIcon.setIcon(thumbnail);
		} 
		
		// change bit depth for all outputs
		setOutputImageType(imageType);
	}

	/**
	 * The ImageType on the output depends on the current image.
	 * This function updates all {@link Output}s to the specified imageType.
	 * @param imageType
	 */
	public void setOutputImageType(final int imageType) {
		for (final Output output : outputs) {
			if(output.getDataType() instanceof DataTypeFactory.Image)
				((DataTypeFactory.Image)output.getDataType()).setImageBitDepth(imageType);
		}
	}


	/**
	 * Bit depth of the file behind the specified FilePath
	 * @return
	 */
	public int getBitDepth() {
		ImagePlus imp = getImagePlus();
		int bitDepth = 0;
		if(imp != null) {
			//imp.close();
			bitDepth = imp.getBitDepth();
		}
		return bitDepth;
	}
	
	
	
	/**
	 * {@link ImagePlus} from the selected {@link ImagePlus} window in ImageJ.
	 * @return
	 */
	public ImagePlus getImagePlus() {
		return this.image; 
	}
	

	public int getImageType() {
		return ImageJHelper.getImageType(getImagePlus(), false);
	}
	
	/*
	 * painting
	 */
	
	
	/*
	 * (non-Javadoc)
	 * @see de.danielsenff.imageflow.models.unit.UnitElement#paint(java.awt.Graphics, java.awt.image.ImageObserver)
	 */
	@Override
	public Rectangle paint(final Graphics g, final ImageObserver io) {
		if(getImagePlus() == null && !selected) {
			g.setColor(new Color(255,0,0,80));
		    g.fillRoundRect(origin.x, origin.y, getDimension().width, getDimension().height, 
		    		unitComponentIcon.arc, unitComponentIcon.arc);
		}
		final Rectangle paint = super.paint(g, io);
		return paint;
	}
	
	private BufferedImage scaleThumbnail(int width, int height) {
		BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) thumbnail.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(getImagePlus().getImage(), 0, 0, width, height, null);
		g2.dispose();
		return thumbnail;
	}

	
}
