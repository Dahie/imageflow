package de.danielsenff.imageflow.models.datatype;

public interface DataType {

	/**
	 * Compares two Datatypes to see if they are compatible.
	 * @param compareType
	 * @return
	 */
	public boolean isCompatible(DataType compareType);
	
}
