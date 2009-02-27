package imageflow.models.unit;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import imageflow.ImageFlow;
import imageflow.models.MacroElement;
import imageflow.models.Output;
import imageflow.models.parameter.StringParameter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Specialized {@link UnitElement} for loading image files.
 * @author danielsenff
 *
 */
public class SourceUnitElement extends UnitElement {

	/**
	 * @param origin
	 * @param unitName
	 * @param macroElement
	 */
	public SourceUnitElement(final Point origin, final String unitName,
			final MacroElement macroElement) {
		super(origin, unitName, macroElement);
	}
	
	/**
	 * @param origin
	 * @param unitName
	 * @param macroString
	 */
	public SourceUnitElement(final Point origin, final String unitName,
			final String macroString) {
		super(origin, unitName, macroString);
	}

	@Override
	public void showProperties() {
		
		// display filedialog
	    showFileChooser();
		
		
		super.showProperties();
		
		
		updateImageType();
		
		notifyModelListeners();
	}

	/**
	 * The current imagetype is determined by the currently selected file.
	 * The unit-icon and labels will be updated as well.
	 * If no file is selected or the file doesn't exist, a message is displayed.
	 */
	public void updateImageType() {
		int imageType = -1;
		if(getFile().exists()) {
			imageType = getImageType();
			this.unitComponentIcon.setIcon(getImagePlus().getImage().getScaledInstance(48, 48, BufferedImage.SCALE_FAST));
			
		} else {
			this.setIcon(null);
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"The file" +getFile()+ " you selected does not exist."+
					'\n'+"An image type can not be determined, which can invalidate the current graph.",
					"File doesn't exist", 
					JOptionPane.WARNING_MESSAGE);
			System.out.println("file doesn't exist");
		}
		
		this.setLabel(getFile().getName());
		// change bitdepth for all outputs
		setOutputImateType(imageType);
	}

	/**
	 * Opens a filechooser to select a new file.
	 */
	public void showFileChooser() {
		final JFileChooser fc = new JFileChooser();
	    String filepath = (String)getParameter(0).getValue();
	    fc.setSelectedFile(new File(filepath));
	    
	    final int option = fc.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	filepath = fc.getSelectedFile().getAbsolutePath();
	    	// backslashes need to be escaped
	    	filepath = filepath.replace("\\", "\\\\"); // \ to \\
	    	((StringParameter)getParameter(0)).setValue(filepath);
	    }
	}

	public void setOutputImateType(final int imageType) {
		for (final Output output : outputs) {
			output.setOutputBitDepth(imageType);
		}
	}


	/**
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
	
	public int getImageType() {
		final String path = getFilePath();
		if(new File(path).exists()) {
			final ImagePlus imp = IJ.openImage(path);
			imp.close();
			final int type = imp.getType();
			
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
		return -1; 
	}
	

	public ImagePlus getImagePlus() {
		final String path = getFilePath();
		if(new File(path).exists()) {
			final ImagePlus imp = IJ.openImage(path);
			return imp;
		}
		return null; 
	}
	
	public String getFilePath() {
		return ((StringParameter)parameters.get(0)).getValue();
	}
	
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
