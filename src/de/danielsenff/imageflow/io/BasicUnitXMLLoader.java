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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.zip.DataFormatException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.jdom.JDOMException;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.delegates.UnitDelegate;
import de.danielsenff.imageflow.models.delegates.UnitDescription;
import de.danielsenff.imageflow.models.delegates.UnitMutableTreeNode;

/**
 * Shared methods for reading XML Unit Definitions from Jar and from Folder.
 * @author dahie
 *
 */
public abstract class BasicUnitXMLLoader implements UnitDelegateLoader {

	protected Set<String> relevantXmlFiles;
	protected Dictionary<String, UnitDelegateInfo> unitEntries;

	public BasicUnitXMLLoader() {
		this.unitEntries = new Hashtable<String, UnitDelegateInfo>();
		this.relevantXmlFiles = new HashSet<String>();
	}
	
	protected String[] sortPaths(final Set<String> relevantXmlFiles) {
		final String[] paths = relevantXmlFiles.toArray(new String[relevantXmlFiles.size()]);

		Arrays.sort(paths, new Comparator<String>() {
			public int compare(final String s1, final String s2) {
				final int slash1 = s1.lastIndexOf('/');
				final int slash2 = s2.lastIndexOf('/');
				return s1.substring(slash1 + 1).compareTo(s2.substring(slash2 + 1));
			}
			@Override
			public boolean equals(final Object o) {
				return false;
			}
		});
		return paths;
	}
	
	/**
	 * Adds a XML unit to the unit tree and the menu.
	 */
	static void addUnitDelegate(final MutableTreeNode parentNode, final URL url) {
		final boolean withinJar = url.getProtocol().equals("jar");
		final UnitDescription unitDescription = new UnitDescription(url);
		try {
			unitDescription.readXML();
			
			final UnitDelegate unitDelegate = new UnitDelegate(unitDescription, withinJar);
			
			if (DelegatesController.getInstance().getDelegate(unitDelegate.getName()) == null) {
				// if we don't know this unit yet, we add it to the controller
				((DefaultMutableTreeNode) parentNode).add(unitDelegate);
				unitDelegate.setParent(parentNode);
				DelegatesController.getInstance().addDelegate(unitDelegate);
			} else {
				// if a unit delegate by this name already exists, we replace it with a new 
				for (int i = 0; i < parentNode.getChildCount(); i++) {
					if (parentNode.getChildAt(i) instanceof UnitDelegate) {
						final UnitDelegate child = (UnitDelegate) parentNode.getChildAt(i);
						if (child.getName().equals(unitDelegate.getName())) {
							parentNode.remove(i);
							parentNode.insert(unitDelegate, i);
						}
					}
				}
				DelegatesController.getInstance().replaceDelegate(unitDelegate);
			}
			
			
		} catch (JDOMException e) {
			String message = "There has been a problem parsing a XML unit description." +'\n' 
				+ e.getMessage() + '\n'
				+ "The program start will continue without this unit.";
			showErrorDialog("XML parsing error", message);
		} catch (IOException e) {
			String message = "There has been a problem opening the xml definition for file " 
				+ url + "." + '\n'
				+ "The program start will continue without this unit.";
		showErrorDialog("XML dataformat error", message);
		} catch (DataFormatException e) {
			String message = "There has been a data format problem in a XML unit description." +'\n' 
				+ "The error ocures in " + url + "." + '\n'
				+ "The program start will continue without this unit.";
			showErrorDialog("XML dataformat error", message);
		}
	}

	private static void showErrorDialog(String title, String message) {
		JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE) {
			public int getMaxCharactersPerLineCount() {
				return 80;
			}
		};
		JDialog dialog = pane.createDialog(ImageFlow.getApplication().getMainFrame(),title);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}

	public Dictionary<String, UnitDelegateInfo> getEntries() {
		return this.unitEntries;
	}
	
	protected abstract void retrieveRelevantXMLPaths(Enumeration entries,
			Set<String> relevantXmlFiles) throws IOException;
	
	/**
	 * An inner class for convenience. It represents some information about unit delegates.
	 */
	protected class UnitDelegateInfo {
		public String name;
		public DefaultMutableTreeNode treeNode;

		public UnitDelegateInfo(final String name, final MutableTreeNode node) {
			this.name = name;
			this.treeNode = (DefaultMutableTreeNode) node;
		}
		
		public UnitDelegateInfo(final String name, final DefaultMutableTreeNode node) {
			this.name = name;
			this.treeNode = node;
		}
	}
	

	/**
	 * Creates a menu item and a tree node for a given file name. These objects are
	 * added to the given parents and returned within a new UnitDelegateInfo object.
	 */
	protected UnitDelegateInfo addUnitDelegateGroup(final String fileName, final MutableTreeNode node) {
		// cut away leading and trailing slashs
		String displayName = fileName;
		if (displayName.startsWith("/"))
			displayName = displayName.substring(1);
		if (displayName.endsWith("/"))
			displayName = displayName.substring(0, displayName.length() - 1);

		final UnitMutableTreeNode subNode = new UnitMutableTreeNode(displayName);
		((UnitMutableTreeNode) node).add(subNode);

		final UnitDelegateInfo udi = new UnitDelegateInfo(displayName, subNode);
		return udi;
	}
	
}
