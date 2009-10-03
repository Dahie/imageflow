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
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.StringParameter;
import de.danielsenff.imageflow.utils.UrlCheck;

/**
 * Specialized {@link UnitElement} for loading image files.
 * This supports the file formats ImageJ does natively.
 * @author danielsenff
 *
 */
public class SourceUnitElement extends UnitElement implements ImageSourceUnit {

	private boolean exists = false;
	private static final int FILE_PARAMETER_INDEX = 0;
	
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
	
	/**
	 * @param origin
	 * @param unitName
	 * @param macroElement
	 * @param filepath 
	 */
	public SourceUnitElement(final Point origin, 
			final String unitName,
			final MacroElement macroElement, 
			final String filepath) 
	{
		super(origin, unitName, macroElement);
		setFilePath(filepath);
	}
	
	/**
	 * @param origin
	 * @param unitName
	 * @param macroString
	 * @param filepath 
	 */
	public SourceUnitElement(final Point origin, 
			final String unitName,
			final String macroString, 
			final String filepath) 
	{
		super(origin, unitName, macroString);
		setFilePath(filepath);
	}

	@Override
	public UnitElement clone() {
		
		return super.clone();
	}
	
	
	@Override
	public void showProperties() {
		
		// display file dialog
		if(!existsFile())
			showOpenFileChooser();
		
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
		setExistsFile(getFilePath());
		
		if(existsFile()) {
			imageType = getImageType();
			this.unitComponentIcon.setIcon(
					getImagePlus().getImage().getScaledInstance(48, 48, BufferedImage.SCALE_FAST));
		} else {
			this.setIcon(null);
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"The file " +getFile()+ " you selected does not exist."+
					'\n'+"An image type can not be determined, which can invalidate the current graph.",
					"File doesn't exist", 
					JOptionPane.WARNING_MESSAGE);
		}
		
		// change bit depth for all outputs
		setOutputImageType(imageType);
	}

	/**
	 * Display the default file chooser.
	 */
	protected void showOpenFileChooser() {
		  showOpenJFileChooser();
		  // showIJOpenDialog();
	}
	
	
	/**
	 * Opens a {@link JFileChooser} to select a new file.
	 */
	protected void showOpenJFileChooser() {
		final JFileChooser fc = new JFileChooser();
	    String filepath = getFilePath();
	    
	    fc.setSelectedFile(new File(filepath));
	    
	    final int option = fc.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	filepath = fc.getSelectedFile().getAbsolutePath();
	    	// backslashes need to be escaped
	    	filepath = filepath.replace("\\", "\\\\"); // \ to \\
	    	setFilePath(filepath);
	    }
	}
	
	/**
	 * Opens an ImageJ {@link OpenDialog} for selecting an image file.
	 */
	private void showIJOpenDialog() {
		OpenDialog openDialog;
		if(hasFilePath()) 
			openDialog = new OpenDialog("Select image", getFilePath());
		else
			openDialog = new OpenDialog("Select image", "");
		
		String filepath = openDialog.getDirectory() + openDialog.getFileName();
		if(openDialog.getFileName() != null) {
	    	// backslashes need to be escaped
	    	filepath = filepath.replace("\\", "\\\\"); // \ to \\
			setFilePath(filepath);
		}
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
			imp.close();
			bitDepth = imp.getBitDepth();
		}
		return bitDepth;
	}
	
	/**
	 * Returns the ImageType of the file specified in the FilePath.
	 * @return
	 */
	public int getImageType() {
		ImagePlus imp = getImagePlus();
		int imageType =0;
		if(imp != null) {
			final int type = imp.getType();
			boolean isStack = imp.getStackSize() > 1 ? true : false;

			imp.close();
			imp = null;

			switch (type) {
			case ImagePlus.GRAY8:
				imageType = PlugInFilter.DOES_8G;
				break;
			case ImagePlus.COLOR_256:
				imageType = PlugInFilter.DOES_8C;
				break;
			case ImagePlus.GRAY16:
				imageType = PlugInFilter.DOES_16;
				break;
			case ImagePlus.GRAY32:
				imageType = PlugInFilter.DOES_32;
				break;
			case ImagePlus.COLOR_RGB:
				imageType = PlugInFilter.DOES_RGB;
				break;
			}

			imageType += isStack ? PlugInFilter.DOES_STACKS : 0;

		}
		return imageType; 
	}
	
	
	/**
	 * {@link ImagePlus} based on the path saved in the first parameter of this UnitElement.
	 * @return
	 */
	public ImagePlus getImagePlus() {
		if(existsFile()) {
			return IJ.openImage(getFilePath());
		}
		return null; 
	}
	
	
	
	
	/*
	 * Handling File
	 */
	
	/**
	 * Returns true if the first parameter has a path.
	 * This doesn't check if the path is valid.
	 * @return
	 */
	public boolean hasFilePath() {
		return (getFilePath().length() > 0);
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
		final String path = getFilePath();
		return new File(path);
	}
	
	/**
	 * Set the file connected with the file path.
	 * @param filepath
	 */
	public void setFilePath(String filepath) {
		((StringParameter)getParameter(FILE_PARAMETER_INDEX)).setValue(filepath);
		String filename = filepath.substring(filepath.lastIndexOf(File.separator)+1);
		setLabel(filename);
		setExistsFile(filepath);
	}

	/**
	 * Returns true if the File exists.
	 * @return
	 */
	public boolean existsFile() {
		return this.exists;
	}
	
	private void setExistsFile(String path) {
		if (path.indexOf("://")>0) {
			// is url
			this.exists = UrlCheck.existsFile(path);
		} else {
			// is file
			this.exists = this.getFile().exists();
		}
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
		if(!existsFile() && !selected) {
			g.setColor(new Color(255,0,0,80));
		    g.fillRoundRect(origin.x, origin.y, getDimension().width, getDimension().height, 
		    		unitComponentIcon.arc, unitComponentIcon.arc);
		}
		final Rectangle paint = super.paint(g, io);
		return paint;
	}
	
}
