/**
 * Copyright (C) 2008-2011 Daniel Senff
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
