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

import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.StringParameter;
import de.danielsenff.imageflow.utils.ImageJHelper;
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
			setIconScaled(getImagePlus().getImage());
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
	    	//filepath = filepath.replace("\\", "\\\\"); // \ to \\
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
		return ImageJHelper.getImageType(getImagePlus());
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
	
	
	@Override
	public SourceUnitElement clone() {
		// clone the object
		String imageJSyntax;
		try {
			imageJSyntax = (String) cloneNonClonableObject(this.obj);
		} catch (CloneNotSupportedException e) {
			imageJSyntax = ((MacroElement)this.obj).getImageJSyntax();
		}

		SourceUnitElement clone = new SourceUnitElement(new Point(origin.x+15, origin.y+15), 
				this.label, imageJSyntax);
		for (int j = 0; j < getInputsCount(); j++) {
			cloneInput(clone, j);
		}
		for (int i = 0; i < getOutputsCount(); i++) {
			cloneOutput(clone, i);
		}
		for (Parameter parameter : parameters) {
			cloneParameter(clone, parameter);
		}
		
		// set filepath
		clone.setFilePath(this.getFilePath());
		
		
		clone.setDisplay(isDisplay());
		clone.setColor(this.color);
		clone.setIcon(this.preview);
		clone.setHelpString(this.infoText);
		clone.setCompontentSize(this.getCompontentSize());
		return clone;
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
		return ((StringParameter)getParameter(FILE_PARAMETER_INDEX)).getValue();
	}
	
	/**
	 * The path of the current file.
	 * This is taken from the first parameter of the {@link UnitElement}.
	 * @return
	 */
	public File getFile() {
		return new File(getFilePath());
	}
	
	/**
	 * Set the file connected with the file path.
	 * @param filepath
	 */
	public void setFilePath(String filepath) {
		((StringParameter)getParameter(FILE_PARAMETER_INDEX)).setValue(filepath);
		setLabel(filepath.substring(filepath.lastIndexOf(File.separator)+1));
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
		if (path.indexOf("://")>0)  // is url
			this.exists = UrlCheck.existsFile(path);
		else // is file
			this.exists = this.getFile().exists();
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
