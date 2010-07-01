package de.danielsenff.imageflow.models.datatype;

import visualap.Pin;

/**
 * A DataType only describes what kind of data can be used on a {@link Pin}
 * It does not actually store the value passed along in the workflow.
 * @author danielsenff
 *
 */
public interface DataType extends Cloneable {

	/**
	 * Compares two DataTypes to see if they are compatible.
	 * @param compareType
	 * @return
	 */
	public boolean isCompatible(DataType compareType);
	
	/**
	 * Clone this DataType.
	 * @return
	 */
	public DataType clone();
	
	/**
	 * Returns the simple Class name of this DataType. 
	 * @return
	 */
	public String getName();
}
