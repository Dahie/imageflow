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
package de.danielsenff.imageflow.io;


/**
 * Factory for conveniently getting the right UnitXMLLoader.
 * @author Daniel Senff
 *
 */
public class UnitXMLLoaderFactory {

	/**
	 * Creates a new FolderUnitXMLLoader.
	 * @param controller
	 * @return
	 */
	public static UnitDelegateLoader createFolderUnitXMLLoader() {
		return new FolderUnitXMLLoader();
	}
	
	/**
	 * Creates a new JarUnitXMLLoader.
	 * @return
	 */
	public static UnitDelegateLoader createJarUnitXMLLoader() {
		return new JarUnitXMLLoader();
	}

	/**
	 * Creates the suitable {@link UnitDelegateLoader} for the given protocol.
	 * @param controller
	 * @param protocol
	 * @return
	 */
	public static UnitDelegateLoader createUnitXMLLoaderByProtocol(final String protocol) {
		if (protocol.equals("file")) {
			return UnitXMLLoaderFactory.createFolderUnitXMLLoader();
		} else if (protocol.equals("jar")) {
			return UnitXMLLoaderFactory.createJarUnitXMLLoader();
		} else {
			throw new RuntimeException("Currenty only jar and file are valid resource protocols!");
		}
	}
	
}
