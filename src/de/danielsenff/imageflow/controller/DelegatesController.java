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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.delegates.Delegate;
import de.danielsenff.imageflow.models.delegates.UnitDelegate;
import de.danielsenff.imageflow.models.delegates.UnitDescription;
import de.danielsenff.imageflow.models.delegates.UnitMutableTreeNode;
import de.danielsenff.imageflow.utils.Tools;


/**
 * Controller for reading and managing {@link UnitDelegate}s.
 * @author senff
 *
 */
public class DelegatesController {

	/**
	 * folder, which contains the unit xml descriptions
	 * TODO not hardcoded ...
	 */
	public static String unitFolder = "xml_units/";
	/**
	 * folder containing the icons which might be referenced in the xml descriptions
	 * TODO not hardcoded ...
	 */
	public static String unitIconFolder = "xml_icons";

	private static DelegatesController controller;
//	HashMap<TreeNode, Delegate> delegates;
	public DefaultTreeModel delegatesModel;

	/**
	 * Get a list of all Units that can be added to the workflow.
	 * @return
	 */
//	public HashMap<TreeNode, Delegate> getDelegates() {
//		return  delegates;
//	}

	/**
	 * The URL from where the initial units have been loaded.
	 */
	public URL resourcesBase;

	/**
	 * Get a TreeModel with the delegates and their tree structure
	 * @return
	 */
	public DefaultTreeModel getDelegatesModel() {
		return delegatesModel;
	}

	protected DelegatesController() {
//		delegates = new HashMap<TreeNode, Delegate>();

		UnitMutableTreeNode top = new UnitMutableTreeNode("Insert unit");
		delegatesModel = new DefaultTreeModel(top);

		fillDelegatesModelFromJar(top);
	}
	
	
	/**
	 * @param top
	 * @param insertMenu
	 */
	protected void fillDelegatesModelFromJar(final UnitMutableTreeNode top) {
		String unitsLocation = "";
		try {
			// try to load xml units from surrounding jar by default
			unitsLocation = getClassResourceBase();
			setResourcesBase(DelegatesController.class.getClassLoader().getResource(unitsLocation));
			if (resourcesBase != null && resourcesBase.openConnection().getContentLength() > 0) {
				readDelegatesFromURL(top, resourcesBase);
			} else throw new IOException("The resource has no content!");
			
			// see if there is a unit xml folder and load those as well
			//File unitFolderFile = new File(getAbsolutePathToFolder(""));
//			File unitFolderFile = new File(getAbsolutePathToUnitFolder());
//			System.out.println(unitFolderFile);
//			System.out.println(unitFolderFile.exists());
//			if(unitFolderFile.exists()) {
//				readDelegatesFromFolder(top, unitFolderFile.toURI().toURL());
//			}
			
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(),
					"The URL " + unitsLocation + " is malformed. No units have been found.",
					"No unit defintions found",
					JOptionPane.WARNING_MESSAGE);

		} catch (IOException e) {
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"The resource " + unitsLocation + " is missing. No units have been found.",
					"No unit defintions found", 
					JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Gets the base path of the resources contained in this jar.
	 */
	private static String getClassResourceBase() {
		return DelegatesController.class.getName().replace(".", "/") + ".class";
	}

	private void readDelegatesFromURL(UnitMutableTreeNode node, URL url)
		throws RuntimeException, MalformedURLException {
		UnitDelegateLoader loader = UnitXMLLoaderFactory.createUnitXMLLoaderByProtocol(url.getProtocol()); 
		loader.readDelegates(node, url);
	}
	
	private void readDelegatesFromFolder(UnitMutableTreeNode node, URL url) 
		throws RuntimeException, MalformedURLException {
		UnitDelegateLoader loader = UnitXMLLoaderFactory.createFolderUnitXMLLoader(); 
		loader.readDelegates(node, url);
	}
	
    public static boolean containsMenuItem(final JMenu menu, final JMenuItem item) {
    	JMenuItem existingItem;
    	for (int i = 0; i < menu.getItemCount(); i++) {
			existingItem = menu.getItem(i);
    		if (existingItem.getText().equalsIgnoreCase(item.getText()))
    			return true;
		}
    	return false;
    }
    
    public static JMenuItem findMenuItem(final JMenu menu, final JMenuItem item) {
    	JMenuItem existingItem;
    	for (int i = 0; i < menu.getItemCount(); i++) {
			existingItem = menu.getItem(i);
    		if (existingItem.getText().equalsIgnoreCase(item.getText()))
    			return existingItem;
		}
    	return null;
    }
	
	/**
	 * {@link DelegatesController} is a Singleton, 
	 * this returns the available Instance.
	 * @return
	 */
	public static DelegatesController getInstance() {
		if(controller == null) {
			controller = new DelegatesController();
		}
		return controller;
	}

	/**
	 * Name of the folder that can contain customized Unit descriptions.
	 * @return
	 */
	public static String getUnitFolderName() {
		return unitFolder;
	}

	/**
	 * @return
	 */
	public static String getUnitIconFolderName() {
		return unitIconFolder;
	}


	/**
	 * Gets the resource base url of the initially loaded units.
	 * This could be a folder or a jar file.
	 * @return 
	 */
	public URL getResourcesBase() {
		return resourcesBase;
	}

	/**
	 * Sets the resource base path by taking a URL and
	 * removing its relative parts.
	 */
	private void setResourcesBase(URL path) throws MalformedURLException {
		String protocol = path.getProtocol();
		if (protocol.equals("file")) {
			resourcesBase = new URL(path, getAbsolutePathToUnitFolder());
		} else if (protocol.equals("jar")) {
			resourcesBase = new URL(path, "/");
		}
	}

	/**
	 * Returns the absolute path to the executed jar and a folder of a given name.
	 * @param folderName
	 * @return
	 */
	public static String getAbsolutePathToFolder(String folderName) {
		return System.getProperty("user.dir") + File.separator + folderName;
	}
	
	public static String getAbsolutePathToUnitFolder() {
		return getAbsolutePathToFolder(getUnitFolderName());
	}

	/**
	 * Find a UnitDelegate by name.
	 * @param unitName 
	 * @return
	 */
	public UnitDelegate getDelegate(final String unitName) {
		UnitDelegate unitDelegate = null; 
		
		/*
		for (final Delegate delegate : getDelegates().values()) {
			if(delegate instanceof UnitDelegate) {
				unitDelegate = (UnitDelegate) delegate;
				if (unitDelegate.getName().equals(unitName))
					return unitDelegate;		
			}
		}*/
		return null;

	}

}
