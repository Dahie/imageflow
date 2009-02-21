package imageflow.models.unit;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import imageflow.models.MacroElement;
import imageflow.models.Output;
import imageflow.models.parameter.StringParameter;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;

public class SourceUnitElement extends UnitElement {

	public SourceUnitElement(Point origin, String unitName,
			MacroElement macroElement) {
		super(origin, unitName, macroElement);
	}
	
	public SourceUnitElement(Point origin, String unitName,
			String macroString) {
		super(origin, unitName, macroString);
	}

	@Override
	public void showProperties() {
		
		// display filedialog
	    JFileChooser fc = new JFileChooser();
	    String filepath = (String)getParameter(0).getValue();
	    fc.setSelectedFile(new File(filepath));
	    
	    int option = fc.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	filepath = fc.getSelectedFile().getAbsolutePath();
	    	// backslashes need to be escaped
	    	filepath = filepath.replace("\\", "\\\\"); // \ to \\
	    	((StringParameter)getParameter(0)).setValue(filepath);
	    }
		
		
		super.showProperties();
		
		
		int imageType = -1;
		if(getFile().exists()) {
			imageType = getImageType();
			this.unitComponentIcon.setIcon(getImagePlus().getImage().getScaledInstance(48, 48, BufferedImage.SCALE_FAST));
			
		} else {
			System.out.println("file doesn't exist");
		}
		
		this.setLabel(getFile().getName());
		// change bitdepth for all outputs
		for (Output output : outputs) {
			output.setOutputBitDepth(imageType);
		}
		
		notifyModelListeners();
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

}
