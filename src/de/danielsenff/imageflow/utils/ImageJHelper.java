package de.danielsenff.imageflow.utils;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;

public class ImageJHelper {


	/**
	 * Returns the ImageType of the file specified in the FilePath.
	 * @param imp 
	 * @return
	 */
	public static int getImageType(ImagePlus imp) {
		return getImageType(imp, true);
	}
	
	/**
	 * Returns the ImageType of the file specified in the FilePath.
	 * @param imp 
	 * @param close Will close the ImagePlus afterwards, maybe move this outside
	 * @return
	 */
	public static int getImageType(ImagePlus imp, boolean close) {
		int imageType =0;
		if(imp != null) {
			final int type = imp.getType();
			boolean isStack = imp.getStackSize() > 1;

			if (close) {
				imp.close();
				imp = null;
			}

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
	
	
}
