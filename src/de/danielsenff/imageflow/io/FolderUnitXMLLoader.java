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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Set;

import javax.swing.tree.MutableTreeNode;

import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.delegates.UnitMutableTreeNode;

/**
 * Read Unit-XML from hard disc.
 * @author Daniel Senff
 *
 */
public class FolderUnitXMLLoader extends BasicUnitXMLLoader {
	
	public FolderUnitXMLLoader() {
		super();
	}

	public void readDelegates(final UnitMutableTreeNode node, final URL url)
	throws MalformedURLException {
		File folder;
		try {
			folder = new File(url.toURI());
		} catch (final URISyntaxException e) {
			folder = new File(url.getPath());
		}

		try {
			// get all relevant files
			retrieveRelevantXMLPaths(makeEnumeration(folder.listFiles()), relevantXmlFiles);
		
			String[] paths = sortPaths(relevantXmlFiles);
	
			for (String unitPath : paths) {
				reflectUnitsInMenu(getEntries(), node, unitPath, "", url);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void retrieveRelevantXMLPaths(Enumeration files, Set<String> relevantXmlFiles) throws IOException {
		while (files.hasMoreElements()) {
			File file = (File) files.nextElement();

			if(file.isDirectory()
					&& !file.isHidden()
					&& !file.getName().startsWith(".")) {

				retrieveRelevantXMLPaths(makeEnumeration(file.listFiles()), relevantXmlFiles);

			} else if (file.isFile()
					&& isXML(file)
					&& !file.getName().startsWith(".")) {
				String resourceBase = DelegatesController.getAbsolutePathToUnitFolder();
				String fileName = file.getAbsolutePath().substring(resourceBase.length());
				relevantXmlFiles.add(fileName);
			}
		}

	}

	/**
	 * Simple conversion from Object-Array to Enumeration.
	 * @param obj
	 * @return
	 */
	static private Enumeration makeEnumeration(final Object obj) {
		Class<? extends Object> type = obj.getClass();
		if (!type.isArray()) {
			throw new IllegalArgumentException(obj.getClass().toString());
		} else {
			return (new Enumeration() {
				int size = Array.getLength(obj);
				int cursor;

				public boolean hasMoreElements() {
					return (cursor < size);
				}

				public Object nextElement() {
					return Array.get(obj, cursor++);
				}
			});
		}
	}

	/**
	 * Goes through a given resource and creates menu and tree items when necessary.
	 */
	private void reflectUnitsInMenu(Dictionary<String, UnitDelegateInfo> entries, MutableTreeNode node,
			String resource, String basePath, URL url) {
		int slash = resource.indexOf("/");
		if (slash >= 0) { // is it a directory?
			String name = resource.substring(0, slash);

			resource = resource.substring(slash + 1);
			basePath = basePath + name + "/";

			// make no new items for files in relative root ("/", if applicable)
			if (basePath.length() > 1) {
				UnitDelegateInfo ud = entries.get(basePath);
				if (ud == null) {
					ud = addUnitDelegateGroup(name, node);
					entries.put(basePath, ud);
				}
				reflectUnitsInMenu(entries, ud.treeNode, resource, basePath, url);
			} else if (resource.length() > 0) {
				reflectUnitsInMenu(entries, node, resource, basePath, url);
			}
		} else {
			if (resource.length() == 0) // stop if there is no file to add
				return;

			try {
				String xmlPath = basePath + resource;
				// FIXME  when I remove this printout, the SaveAs unit is not found in Linux... #sigh
				System.out.println(xmlPath);
				URL fileURL = new URL(url, xmlPath);

				if (basePath.length() > 1) {
					UnitDelegateInfo ud = entries.get(basePath);
					if (ud == null) {
						ud = addUnitDelegateGroup(basePath, node);
						entries.put(basePath, ud);
					}
					addUnitDelegate(ud.treeNode, fileURL);
				} else {
					addUnitDelegate(node, fileURL);
				}
			}
			catch (MalformedURLException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns true if the checked file file has the ,xml-extension.
	 * @param file
	 * @return
	 */
	private boolean isXML(final File file) {
		return file.getName().toLowerCase().contains(".xml");
	}

}
