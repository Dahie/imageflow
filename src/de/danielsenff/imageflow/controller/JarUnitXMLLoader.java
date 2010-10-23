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
package de.danielsenff.imageflow.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.tree.MutableTreeNode;

import de.danielsenff.imageflow.models.delegates.UnitMutableTreeNode;

/**
 * XMLLoader to load Unit-Definitions from within the launchable jar. 
 * @author Daniel Senff
 *
 */
public class JarUnitXMLLoader extends BasicUnitXMLLoader {

	public JarUnitXMLLoader() {
		super();
	}
	
	/**
	 * Reads contents of a jar file and manages the traversal of the file tree.
	 * @param node 
	 * @param url 
	 * @throws MalformedURLException 
	 */
	public void readDelegates(UnitMutableTreeNode node, URL url) throws MalformedURLException {
		
		// strip out the jar file
		String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
		try {
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			// get _all_ entries in jar
			Enumeration<JarEntry> entries = jar.entries();

			// get all relevant files
			Set<String> relevantXmlFiles = new HashSet<String>();
			retrieveRelevantXMLPaths(entries, relevantXmlFiles);

			// populate the menu
			String[] paths = populateMenu(relevantXmlFiles);

			for (String unitPath : paths) {
				System.out.println(unitPath);
				reflectUnitsInMenu(unitGroups, node, unitPath, "", url);
			}
		}
		catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void retrieveRelevantXMLPaths(Enumeration entries,
			Set<String> relevantXmlFiles) {
		while (entries.hasMoreElements()) {
			JarEntry entry = (JarEntry) entries.nextElement();
			String absoluteName = entry.getName();
			if (absoluteName.startsWith(DelegatesController.getUnitFolderName())) { // filter specified path
				String fileName = absoluteName.substring(DelegatesController.getUnitFolderName().length());

				if (fileName.startsWith(".")) // ignore hidden files and folders
					continue;

				if (fileName.endsWith("/")) // ignore pure folder entries
					continue;

				relevantXmlFiles.add(fileName);
			}
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
				String xmlPath = "/" + DelegatesController.getUnitFolderName() + basePath + resource;
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

	
}
