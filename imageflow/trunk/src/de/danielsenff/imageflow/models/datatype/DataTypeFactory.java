package de.danielsenff.imageflow.models.datatype;

import ij.plugin.filter.PlugInFilter;
import visualap.Pin;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.ProxyOutput;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * The DataTypeFactory is used to create instances of DataTypes.
 * A DataType only describes what kind of data can be used on a {@link Pin}
 * It does not actually store the value passed along in the workflow.
 * @author danielsenff
 *
 */
public class DataTypeFactory {


	/**
	 * Create a {@link DataType} based on its name.
	 * @param dataType
	 * @return
	 */
	public static DataType createDataType(final String dataType) {
		if(dataType.toLowerCase().equals("integer")) {
			return createInteger();
		}else if(dataType.toLowerCase().equals("image")) {
			return createImage(31); // 31 ie takes all
		}else if(dataType.toLowerCase().equals("double")) {
			return createDouble();
		}else if(dataType.toLowerCase().equals("number")) {
			return createNumber();
		}
		System.err.println("unrecognized DataType: "+ dataType);
		return null;
	}

	/**
	 * Creates an Integer-Datatype.
	 * @return
	 */
	public static Integer createInteger() {
		return new Integer();
	}

	/**
	 * Creates an Double-Datatype.
	 * @return
	 */
	public static Double createDouble() {
		return new Double();
	}
	
	public static Number createNumber() {
		return new Number();
	}

	/**
	 * Creates an Image-Datatype. This includes definitions for ImageJ ImageTypes.
	 * @param imageBitDepth 
	 * @return
	 */
	public static Image createImage(final int imageBitDepth) {
		return new Image(imageBitDepth);
	}



	/**
	 * Number-DataType
	 * @author danielsenff
	 *
	 */
	public static class Number implements DataType {
		public boolean isCompatible(DataType compareType) {
			boolean compatible = compareType instanceof Number;
			return compatible;
		}
		
		@Override
		public Number clone() {
			return new Number();
		}
	}


	/**
	 * Integer-DataType.
	 * @author danielsenff
	 *
	 */
	public static class Integer extends Number {
		public boolean isCompatible(DataType compareType) {
			boolean compatible = compareType instanceof Integer;
			return compatible;
		}
		@Override
		public Integer clone() {
			return new Integer();
		}
	}

	/**
	 * Double-DataType
	 * @author danielsenff
	 *
	 */
	public static class Double extends Number {
		public boolean isCompatible(DataType compareType) {
			boolean compatible = compareType instanceof Integer || compareType instanceof Double;
			return compatible;
		}

		@Override
		public Double clone() {
			return new Double();
		}
	}

	/**
	 * Image-DataType. 
	 * This contains a definition for the ImageJ ImageTypes supported. 
	 * @author danielsenff
	 *
	 */
	public static class Image implements DataType {

		/**
		 * defines the allowed ImageTypes used by ImageJ.
		 */
		protected int imageBitDepth;
		/**
		 * Unit to which this DataType belongs
		 */
		protected UnitElement parent;
		protected Pin parentPin;

		/**
		 * @param imageBitDepth
		 */
		public Image(final int imageBitDepth) {
			this.imageBitDepth = imageBitDepth;
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
			/*System.out.println(this);
			System.out.println(parent);
			System.out.println(imageBitDepth);*/
			if(this.imageBitDepth != -1 
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
						if(input.isConnected() && input.getDataType() instanceof Image) {
							return ((Image)input.getFromOutput().getDataType()).getImageBitDepth();
						}
					}

					return -1;
				}

			} else if(parentPin instanceof Output) {

				//TODO this could be nicer, how to handle multiple inputs?
				if(parent.hasInputsConnected()) {
					for (Input input : parent.getInputs()) {
						if(input.isConnected() && input.getDataType() instanceof Image)
							return ((Image)input.getFromOutput().getDataType()).getImageBitDepth();
					}

					return -1;
				} 
			} 
			/*
			 * it seems I don't need this, usually I always check inputs if their outputs are legal
			 * else if(parentPin instanceof Input) {

				Input parentInput = (Input) parentPin;
				if(parentInput.isConnected()) {
					Connection conn = parentInput.getConnection();
					return ((Image)conn.getOutput().getDataType()).getImageBitDepth();
				}
			}*/

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

		/*	public int getImageBitDepth() {

			if(this.imageBitDepth != -1 && this.imageBitDepth != PlugInFilter.DOES_ALL) {
				return this.imageBitDepth; 
			} else if(parentPin instanceof ProxyInput) {


			} else if(parent != null) {

				//TODO this could be nicer, how to handle multiple inputs?
				if(parent.hasInputsConnected()) {
					for (Input input : parent.getInputs()) {
						if(input.isConnected() && input.getDataType() instanceof Image)
							return ((Image)input.getFromOutput().getDataType()).getImageBitDepth();
					}

					return -1;
				} else if(parent instanceof ImageSourceUnit) {
					return ((ImageSourceUnit)parent).getImageType();
				} 
			}
			// this means our output doesn't know his own capabilities
			// and because it has no inputs, it can't get them anywhere
			// this sucks
			return this.imageBitDepth;
		}*/

		/**
		 * Returns true, if the imageBitDepth in question is supported
		 * by this Input.
		 * @param foreignImageBitDepth
		 * @return
		 */
		public boolean isImageBitDepthCompatible(final int foreignImageBitDepth) {
			int ownImageBitDepth = getImageBitDepth();

			if(ownImageBitDepth != -1 && foreignImageBitDepth != -1) {
				/*System.out.println(this);
				System.out.println(ownImageBitDepth + " vs " + foreignImageBitDepth);*/
				int remain = ownImageBitDepth&foreignImageBitDepth;

				// if 0 -> it doesn'T fit
				// if value, we got a match

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

		public void setParentPin(Pin pin) {
			this.parentPin = pin;
		}

		@Override
		public Image clone()  {

			Image image = new Image(this.imageBitDepth);
			return image;
		}

	}
}
