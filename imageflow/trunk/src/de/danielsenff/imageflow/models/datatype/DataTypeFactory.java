package de.danielsenff.imageflow.models.datatype;

import ij.plugin.filter.PlugInFilter;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class DataTypeFactory {

	
	public static DataType createDataType(String dataType) {
		if(dataType.equals("Integer")) {
			return createInteger();
		}else if(dataType.equals("Image")) {
			return createImage(31); // 31 ie takes all
		}
		return null;
	}
	
	public static Integer createInteger() {
		return new Integer();
	}
	
	public static Double createDouble() {
		return new Double();
	}
	
	public static Image createImage(int imageBitDepth) {
		return new Image(imageBitDepth);
	}
	
	public static class Integer implements DataType {
		public boolean isCompatible(DataType compareType) {
			boolean compatible = compareType instanceof Integer;
			return compatible;
		}
	}
	
	public static class Double implements DataType {
		public boolean isCompatible(DataType compareType) {
			boolean compatible = compareType instanceof Integer || compareType instanceof Double;
			return compatible;
		}
	}
	
	public static class Image implements DataType {
		
		protected int imageBitDepth;
		protected UnitElement parent;
		
		public Image(int imageBitDepth) {
			this.imageBitDepth = imageBitDepth;
		}
		
		public void setImageBitDepth(int bitDepth) {
			this.imageBitDepth = bitDepth;
		}
		
		/**
		 * Gets the Images bitdepth.
		 * @return
		 */
		public int getImageBitDepth() {
			if(this.imageBitDepth != -1 && this.imageBitDepth != PlugInFilter.DOES_ALL) {
				return this.imageBitDepth; 
			} else if(parent != null) {
				
				//TODO this could be nicer, how to handle multiple inputs?
				if(parent.hasInputsConnected()) {
					for (Input input : parent.getInputs()) {
						if(input.isConnected() && input.getDataType() instanceof Image)
							return ((Image)input.getFromOutput().getDataType()).getImageBitDepth();
					}
					
					return -1;
				} 
			}
			// this means our output doesn't know his own capabilities
			// and because it has no inputs, it can't get them anywhere
			// this sucks
			return this.imageBitDepth;
		}
		
		public boolean isCompatible(DataType compareType) {
			if(compareType instanceof Image)
				return isImageBitDepthCompatible(((Image) compareType).getImageBitDepth());
				
			return false;
		}
		
		/**
		 * Returns true, if the imageBitDepth in question is supported
		 * by this Input.
		 * @param imageBitDepth
		 * @return
		 */
		public boolean isImageBitDepthCompatible(final int imageBitDepth) {
			if(getImageBitDepth() != -1 && imageBitDepth != -1) {
				return (getImageBitDepth()&imageBitDepth) != 0;	
			}
			return false;
		}
		
		public void setParentUnitElement(final UnitElement parent) {
			this.parent = parent;
		}

	
	}
}
