package de.danielsenff.imageflow.models.unit;

import de.danielsenff.imageflow.models.connection.Output;

/**
 * SourceUnits are units that are a source of data.  
 */
public interface ImageSourceUnit {

	/**
	 * Returns the bit depth of the Image in this unit.
	 * @return
	 */
	public int getBitDepth();
	
	/**
	 * Returns the ImageJ ImageType of the Image in this unit.
	 * @return
	 */
	public int getImageType();
	
	/**
	 * The ImageType on the output depends on the current image.
	 * This function updates all {@link Output}s to the specified imageType.  
	 * @param imageType
	 */
	public void setOutputImageType(final int imageType);
	
}
