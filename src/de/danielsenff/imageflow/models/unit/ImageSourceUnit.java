package de.danielsenff.imageflow.models.unit;

/**
 * SourceUnits are units that are a source of data.  
 */
public interface ImageSourceUnit {

	public int getBitDepth();
	
	public int getImageType();
	
	public void setOutputImageType(final int imageType);
	
}
