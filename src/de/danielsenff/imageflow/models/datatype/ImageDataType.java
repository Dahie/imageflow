package de.danielsenff.imageflow.models.datatype;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;

import java.awt.Dimension;

import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.Pin;
import de.danielsenff.imageflow.models.connection.ProxyOutput;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * Image-DataType. 
 * This contains a definition for the ImageJ ImageTypes supported. 
 * @author danielsenff
 *
 */
public class ImageDataType implements DataType {

	/**
	 * defines the allowed ImageTypes used by ImageJ.
	 */
	protected int imageBitDepth;
	/**
	 * Unit to which this DataType belongs
	 */
	protected UnitElement parent;
	/**
	 * 
	 */
	protected Pin parentPin;
	
	private Dimension dimension;

	
	/**
	 * @param imageBitDepth
	 * @param width
	 * @param height
	 */
	public ImageDataType(final int imageBitDepth, final int width, final int height) {
		this.imageBitDepth = imageBitDepth;
		this.setDimension(new Dimension(width, height));
	}
	
	/**
	 * @param imageBitDepth
	 */
	public ImageDataType(final int imageBitDepth) {
		this.imageBitDepth = imageBitDepth;
		this.setDimension(new Dimension());
	}

	/**
	 * @param bitDepth
	 */
	public void setImageBitDepth(final int bitDepth) {
		this.imageBitDepth = bitDepth;
	}

	/**
	 * Gets the Images bitdepth.
	 * @return
	 */
	public int getImageBitDepth() {
		if(this.imageBitDepth > 0 
				&& this.imageBitDepth != PlugInFilter.DOES_ALL) {
			return this.imageBitDepth; 
		} else if(parentPin instanceof ProxyOutput) {

			// instead of looking at our own parent to get a valid image type from an input
			// we look at the parent of the embedded pin, thereby bubbling through
			// the internal list of the group
			UnitElement parent = ((ProxyOutput)parentPin).getEmbeddedOutput().getParent();

			//TODO this could be nicer, how to handle multiple inputs?
			if(parent.hasInputsConnected()) {
				for (Input input : parent.getInputs()) {
					if(input.isConnected() && input.getDataType() instanceof ImageDataType) {
						int inheritedBitDepth = ((ImageDataType)input.getFromOutput().getDataType()).getImageBitDepth();
						// if -2 then convert from stack to image
						inheritedBitDepth -= this.imageBitDepth == -2 ? PlugInFilter.DOES_STACKS : 0;
						return inheritedBitDepth;
					}
				}

				return -1;
			}

		} else if(parentPin instanceof Output) {

			//TODO this could be nicer, how to handle multiple inputs?
			if(parent.hasInputsConnected()) {
				for (Input input : parent.getInputs()) {
					if(input.isConnected() && input.getDataType() instanceof ImageDataType) {
						int inheritedBitDepth = ((ImageDataType)input.getFromOutput().getDataType()).getImageBitDepth();
						inheritedBitDepth -= this.imageBitDepth == -2 ? PlugInFilter.DOES_STACKS : 0;
						return inheritedBitDepth;
					}
				}

				return -1;
			} 
		} 
		

		// this means our output doesn't know his own capabilities
		// and because it has no inputs, it can't get them anywhere
		// this sucks
		return this.imageBitDepth;
	}
	
	public String getSimpleName() { 
		return "Image";
	}
		
	public String getName() { 
		return "Image ("+getVerboseImageFormat()+")";
	}

	private String getVerboseImageFormat() {
		
		switch(getImageBitDepth()) {
		case PlugInFilter.DOES_8G:
			return "8-bit grayscale";
		case PlugInFilter.DOES_16:
			return "16-bit grayscale";
		case PlugInFilter.DOES_32:
			return "32-bit floating-point grayscale";
		case PlugInFilter.DOES_8C:
			return "8-bit indexed color";
		case PlugInFilter.DOES_RGB:
			return "32-bit RGB color";
		case PlugInFilter.DOES_ALL:
			return "ambigous type";
		}
		return "unknown";
	}

	public boolean isCompatible(DataType compareType) {
		if(compareType instanceof ImageDataType)
			return isImageBitDepthCompatible(((ImageDataType) compareType).getImageBitDepth());

		return false;
	}

	/**
	 * Returns true, if the imageBitDepth in question is supported
	 * by this Input.
	 * @param foreignImageBitDepth
	 * @return
	 */
	public boolean isImageBitDepthCompatible(final int foreignImageBitDepth) {
		int ownImageBitDepth = getImageBitDepth();

		if(ownImageBitDepth != -1 && foreignImageBitDepth != -1) {
			int remain = ownImageBitDepth&foreignImageBitDepth;

			/*
			 *  if 0 -> it doesn't fit
			 *  if value, we got a match
			 */

			return remain != 0;	
		}
		return false;
	}

	/**
	 * @param parent
	 */
	public void setParentUnitElement(final UnitElement parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return super.toString() + " ParentPin: "+parentPin.getDisplayName();
	}
	

	/**
	 * @param pin
	 */
	public void setParentPin(Pin pin) {
		this.parentPin = pin;
	}

	@Override
	public ImageDataType clone()  {

		ImageDataType image = new ImageDataType(this.imageBitDepth);
		return image;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public int getWidth() { 
		return this.dimension.width;
	}
	
	public int getHeight() { 
		return this.dimension.height;
	}
	
}
