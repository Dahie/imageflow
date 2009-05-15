package de.danielsenff.imageflow.models.unit;

import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.plugin.filter.PlugInFilter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.Output;
import de.danielsenff.imageflow.models.parameter.StringParameter;

/**
 * Specialized {@link UnitElement} for loading image files.
 * This supports the file formats ImageJ does natively.
 * @author danielsenff
 *
 */
public class SourceUnitElement extends UnitElement {

	/**
	 * @param origin
	 * @param unitName
	 * @param macroElement
	 */
	public SourceUnitElement(final Point origin, 
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
	public SourceUnitElement(final Point origin, 
			final String unitName,
			final String macroString) 
	{
		super(origin, unitName, macroString);
	}

	@Override
	public void showProperties() {
		
		// display filedialog
//	    showOpenFileChooser();
		showIJOpenDialog();
		
		super.showProperties();
		
		updateImageType();
		
		notifyModelListeners();
	}

	/**
	 * The current ImageType is determined by the currently selected file.
	 * The unit-icon and labels will be updated as well.
	 * If no file is selected or the file doesn't exist, a message is displayed.
	 */
	public void updateImageType() {
		int imageType = -1;
		if(getFile().exists()) {
			imageType = getImageType();
			this.unitComponentIcon.setIcon(
					getImagePlus().getImage().getScaledInstance(48, 48, BufferedImage.SCALE_FAST));
			
		} else {
			this.setIcon(null);
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"The file" +getFile()+ " you selected does not exist."+
					'\n'+"An image type can not be determined, which can invalidate the current graph.",
					"File doesn't exist", 
					JOptionPane.WARNING_MESSAGE);
		}
		
		// change bitdepth for all outputs
		setOutputImageType(imageType);
	}

	/**
	 * Opens a {@link JFileChooser} to select a new file.
	 */
	public void showOpenFileChooser() {
		final JFileChooser fc = new JFileChooser();
	    String filepath = (String)getParameter(0).getValue();
	    fc.setSelectedFile(new File(filepath));
	    
	    final int option = fc.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	filepath = fc.getSelectedFile().getAbsolutePath();
	    	// backslashes need to be escaped
	    	filepath = filepath.replace("\\", "\\\\"); // \ to \\
	    	((StringParameter)getParameter(0)).setValue(filepath);
	    	String filename = filepath.substring(filepath.lastIndexOf(File.separator)+1);
	    	setLabel(filename);
	    }
	}
	
	/**
	 * Opens an ImageJ {@link OpenDialog} for selecting an image file.
	 */
	public void showIJOpenDialog() {
		OpenDialog openDialog;
		if(hasFilePath()) 
			openDialog = new OpenDialog("Select image", getFilePath());
		else
			openDialog = new OpenDialog("Select image", "");
		
		String filepath = openDialog.getDirectory() + openDialog.getFileName();
		if(openDialog.getFileName() != null) {
	    	// backslashes need to be escaped
	    	filepath = filepath.replace("\\", "\\\\"); // \ to \\
			((StringParameter)getParameter(0)).setValue(filepath);
	    	String filename = filepath.substring(filepath.lastIndexOf(File.separator)+1);
	    	setLabel(filename);
		}
		
	}

	/**
	 * The ImageType on the output depends on the current image.
	 * This function updates all {@link Output}s to the specified imageType.
	 * @param imageType
	 */
	public void setOutputImageType(final int imageType) {
		for (final Output output : outputs) {
			output.setOutputBitDepth(imageType);
		}
	}


	/**
	 * Bitdepth of the file behind the specified FilePath
	 * @return
	 */
	public int getBitDepth() {
		final String path = getFilePath();
		if(new File(path).exists()) {
			final ImagePlus imp = IJ.openImage(path);
			imp.close();
			final int bitDepth = imp.getBitDepth();
			return bitDepth;
		}
		return -1;
	}
	
	/**
	 * Returns the ImageType of the file specified in the FilePath.
	 * @return
	 */
	public int getImageType() {
		final String path = getFilePath();
		if(new File(path).exists()) {
			final ImagePlus imp = IJ.openImage(path);
			if(imp != null) {
				final int type = imp.getType();
				imp.close();
				switch (type) {
				case ImagePlus.GRAY8:
					return PlugInFilter.DOES_8G;
				case ImagePlus.COLOR_256:
					return PlugInFilter.DOES_8C;
				case ImagePlus.GRAY16:
					return PlugInFilter.DOES_16;
				case ImagePlus.GRAY32:
					return PlugInFilter.DOES_32;
				case ImagePlus.COLOR_RGB:
					return PlugInFilter.DOES_RGB;
				}
			}
			
			
		}
		return -1; 
	}
	

	/**
	 * {@link ImagePlus} based on the path saved in the first parameter of this UnitElement.
	 * @return
	 */
	public ImagePlus getImagePlus() {
		final String path = getFilePath();
		if(new File(path).exists()) {
			final ImagePlus imp = IJ.openImage(path);
			return imp;
		}
		return null; 
	}
	
	/**
	 * Returns true if the first parameter has a path.
	 * This doesn't check if the path is valid.
	 * @return
	 */
	public boolean hasFilePath() {
		final StringParameter stringParameter = (StringParameter)parameters.get(0);
		return (stringParameter.getValue().length() > 0);
	}
	
	/**
	 * Returns the path of the file from the first parameter.
	 * @return
	 */
	public String getFilePath() {
		return ((StringParameter)parameters.get(0)).getValue();
	}
	
	/**
	 * The path of the current file.
	 * This is taken from the first parameter of the {@link UnitElement}.
	 * @return
	 */
	public File getFile() {
		final String path = ((StringParameter)parameters.get(0)).getValue();
		return new File(path);
	}

	@Override
	public Rectangle paint(final Graphics g, final ImageObserver io) {
		
		if(!getFile().exists() && !selected) {
			g.setColor(new Color(255,0,0,80));
		    g.fillRoundRect(origin.x, origin.y, getDimension().width, getDimension().height, 
		    		unitComponentIcon.arc, unitComponentIcon.arc);
		}
		
		final Rectangle paint = super.paint(g, io);
		return paint;
	}
	
}
