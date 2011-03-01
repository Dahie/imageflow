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
package de.danielsenff.imageflow.models.datatype;

import java.awt.Dimension;

import ij.plugin.filter.PlugInFilter;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.Pin;
import de.danielsenff.imageflow.models.connection.ProxyOutput;
import de.danielsenff.imageflow.models.unit.UnitElement;

/**
 * The DataTypeFactory is used to create instances of DataTypes.
 * A DataType only describes what kind of data can be used on a {@link Pin}
 * It does not actually store the value passed along in the workflow.
 * @author Daniel Senff
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
	
	/**
	 * @return
	 */
	public static Number createNumber() {
		return new Number();
	}

	/**
	 * Creates an {@link ImageDataType}. This includes definitions for ImageJ ImageTypes.
	 * @param imageBitDepth 
	 * @return
	 */
	public static ImageDataType createImage(final int imageBitDepth) {
		return new ImageDataType(imageBitDepth);
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
		
		public String getName() { return this.getClass().getSimpleName();}
	}


	/**
	 * Integer-DataType.
	 * @author danielsenff
	 *
	 */
	public static class Integer extends Number {
		@Override
		public boolean isCompatible(DataType compareType) {
			boolean compatible = compareType instanceof Integer;
			return compatible;
		}
		@Override
		public Integer clone() {
			return new Integer();
		}
		
		@Override
		public String getName() { return this.getClass().getSimpleName();}
	}


	
	/**
	 * Double-DataType
	 * @author danielsenff
	 *
	 */
	public static class Double extends Number {
		@Override
		public boolean isCompatible(DataType compareType) {
			boolean compatible = compareType instanceof Integer || compareType instanceof Double;
			return compatible;
		}

		@Override
		public Double clone() {
			return new Double();
		}
		@Override
		public String getName() { return this.getClass().getSimpleName();}
	}
	
}
