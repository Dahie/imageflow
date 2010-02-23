package de.danielsenff.imageflow.controller;

import java.net.URL;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.MalformedURLException;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.lang.RuntimeException;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Arrays;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.Delegate;
import de.danielsenff.imageflow.models.unit.UnitDelegate;
import de.danielsenff.imageflow.models.unit.UnitDescription;
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
	public static String unitFolder = "xml_units";
	/**
	 * folder containing the icons which might be referenced in the xml descriptions
	 * TODO not hardcoded ...
	 */
	public static String unitIconFolder = "xml_icons";
	
	private static DelegatesController controller;
	HashMap<TreeNode, Delegate> delegates;
	public DefaultTreeModel delegatesModel;

	/**
	 * Get a list of all Units that can be added to the workflow.
	 * @return
	 */
	public HashMap<TreeNode, Delegate> getUnitDelegates() {
		return  delegates;
	}

	/**
	 * Get a TreeModel with the delegates and their tree structure
	 * @return
	 */
	public DefaultTreeModel getDelegatesModel() {
		return delegatesModel;
	}

	private DelegatesController() {
		delegates = new HashMap<TreeNode, Delegate>();

		DefaultMutableTreeNode top =
			new DefaultMutableTreeNode("Node delegates");
		delegatesModel = new DefaultTreeModel(top);
		JMenu insertMenu = new JMenu("Insert unit");

		String unitsLocation = "";
		try {
			// the location ot the unit xml files
			unitsLocation = System.getProperty("user.dir")+File.separator+getUnitFolder();
			URL resource = (new File(unitsLocation)).toURL();

			if (resource != null && resource.openConnection().getContentLength() > 0) {
				readDelegatesFromURL(top, insertMenu, resource);
			} else {
				throw new IOException("The resource has no content!");
			}
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

	private DelegatesController(String unitFolderPath) {
		DelegatesController.setUnitFolder(unitFolderPath);

	}

	/**
	 * Gets the base path of the resources contained in this jar.
	 */
	private String getClassResourceBase() {
		return DelegatesController.class.getName().replace(".", "/") + ".class";
	}

	private void readDelegatesFromURL(MutableTreeNode node, JMenu menu, URL url)
			throws RuntimeException, MalformedURLException {
		String protocol = url.getProtocol();
		if (protocol.equals("file")) {
			readDelegatesFromFolder(node, menu, url);
		} else {
			throw new RuntimeException("Currenty only jar and file are valid resource protocols!");
		}
	}

	private void readDelegatesFromFolder(MutableTreeNode node, JMenu menu, URL url)
			throws MalformedURLException {
		File folder;
		try {
			folder = new File(url.toURI());
		} catch (URISyntaxException e) {
			folder = new File(url.getPath());
		}
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if(file.isDirectory()
					&& !file.isHidden()
					&& !file.getName().startsWith(".")) {
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(file.getName());
				JMenu subMenu = new JMenu(file.getName());
				readDelegatesFromFolder(subNode, subMenu, file.toURL());
				((DefaultMutableTreeNode) node).add(subNode);
				menu.add(subMenu);
			} else if (file.isFile()
					&& isXML(file)
					&& !file.getName().startsWith(".")) {
				URL fileURL = file.toURL();
				addUnitDelegate(node, menu, fileURL);
			}
		}
	}

	/**
	 * An inner class for convenience. It represents some information about unit delegates.
	 */
	private class UnitDelegateInfo {
		public String name;
		public JMenu subMenu;
		public DefaultMutableTreeNode treeNode;

		public UnitDelegateInfo(String name, JMenu submenu, MutableTreeNode node) {
			this.name = name;
			this.subMenu = submenu;
			this.treeNode = (DefaultMutableTreeNode) node;
		}
		public UnitDelegateInfo(String name, JMenu submenu, DefaultMutableTreeNode node) {
			this.name = name;
			this.subMenu = submenu;
			this.treeNode = node;
		}
	}
	

	/**
	 * Creates a menu item and a tree node for a given file name. These objects are
	 * added to the given parents and returned within a nev UnitDelegateInfo object.
	 */
	private UnitDelegateInfo addUnitDelegateGroup(String fileName, MutableTreeNode node, JMenu menu) {
		// cut away leading and trailing slashs
		String displayName = fileName;
		if (displayName.startsWith("/"))
			displayName = displayName.substring(1);
		if (displayName.endsWith("/"))
			displayName = displayName.substring(0, displayName.length() - 1);

		DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(displayName);
		((DefaultMutableTreeNode) node).add(subNode);

		JMenu subMenu = new JMenu(displayName);
		menu.add(subMenu);

		UnitDelegateInfo udi = new UnitDelegateInfo(displayName, subMenu, subNode);
		return udi;
	}

	/**
	 * Adds a XML unit to the unit tree and the menu.
	 */
	private void addUnitDelegate(MutableTreeNode node, JMenu menu, URL url) {
		final UnitDescription unitDescription = new UnitDescription(url, Tools.getXMLRoot(url));
		final UnitDelegate unitDelegate = new UnitDelegate(unitDescription);
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(unitDelegate.getName());
		delegates.put(treeNode, unitDelegate);

		((DefaultMutableTreeNode) node).add(unitDelegate);
		JMenuItem item = new JMenuItem(unitDelegate.getName());
		menu.add(item);
	}

	private boolean isXML(File file) {
		return file.getName().toLowerCase().contains("xml");
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

	public static void setUnitFolder(String unitFolder) {
		DelegatesController.unitFolder = unitFolder;
	}

	public static String getUnitFolder() {
		return unitFolder;
	}
	
	public static String getUnitIconFolder() {
		return unitIconFolder;
	}
	
	/**
	 * 
	 * @return
	 */
	public UnitDelegate getDelegate(String unitName) {
		UnitDelegate unitDelegate = null; 
		for (final Delegate delegate : getUnitDelegates().values()) {
			if(delegate instanceof UnitDelegate) {
				unitDelegate = (UnitDelegate) delegate;
			}
			
			if (unitDelegate != null && unitDelegate.getName().equals(unitName)) {
				return unitDelegate;		
			}
		}
		return null;
		
	}
	
}
