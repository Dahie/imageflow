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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * Read Unit-XML from hard disc.
 * @author Daniel Senff
 *
 */
public class FolderUnitXMLLoader implements UnitDelegateLoader {

	private final DelegatesController delegatesController;
	
	/**
	 * @param delegatesController
	 */
	public FolderUnitXMLLoader(final DelegatesController delegatesController) {
		this.delegatesController = delegatesController;
	}
	
	public void readDelegates(final MutableTreeNode node, final JMenu menu, final URL url)
	throws MalformedURLException {
		File folder;
		try {
			folder = new File(url.toURI());
		} catch (final URISyntaxException e) {
			folder = new File(url.getPath());
		}
		final File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			final File file = listOfFiles[i];
			if(file.isDirectory()
					&& !file.isHidden()
					&& !file.getName().startsWith(".")) {
				final DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(file.getName());
				final JMenu subMenu = new JMenu(file.getName());
				readDelegates(subNode, subMenu, file.toURI().toURL());
				((DefaultMutableTreeNode) node).add(subNode);
				menu.add(subMenu);
			} else if (file.isFile()
					&& isXML(file)
					&& !file.getName().startsWith(".")) {
				final URL fileURL = file.toURI().toURL();
				delegatesController.addUnitDelegate(node, menu, fileURL);
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
