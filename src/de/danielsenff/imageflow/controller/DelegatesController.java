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

	private DelegatesController() {
		delegates = new HashMap<TreeNode, Delegate>();

		DefaultMutableTreeNode top =
			new DefaultMutableTreeNode("Node delegates");
		delegatesModel = new DefaultTreeModel(top);
		JMenu insertMenu = new JMenu("Insert unit");

		String unitsLocation = "";
		try {
			// try to load xml units from surrounding jar
			unitsLocation = getClassResourceBase();
			setResourcesBase(DelegatesController.class.getClassLoader().getResource(unitsLocation));

			if (resourcesBase != null && resourcesBase.openConnection().getContentLength() > 0) {
				readDelegatesFromURL(top, insertMenu, resourcesBase);
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
		} else if (protocol.equals("jar")) {
			readDelegatesFromJar(node, menu, url);
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
				readDelegatesFromFolder(subNode, subMenu, file.toURI().toURL());
				((DefaultMutableTreeNode) node).add(subNode);
				menu.add(subMenu);
			} else if (file.isFile()
					&& isXML(file)
					&& !file.getName().startsWith(".")) {
				URL fileURL = file.toURI().toURL();
				addUnitDelegate(node, menu, fileURL);
			}
		}
	}

	/**
	 * Reads contents of a jar file and manages the trversal of the file tree.
	 */
	private void readDelegatesFromJar(MutableTreeNode node, JMenu menu, URL url)
	throws MalformedURLException {
		Dictionary<String, UnitDelegateInfo> unitGroups = new Hashtable<String, UnitDelegateInfo>();
		// strip out the jar file
		String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
		try {
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			// get _all_ entries in jar
			Enumeration<JarEntry> entries = jar.entries();

			// get all relevant files
			Set<String> relevantXmlFiles = new HashSet<String>();
			while (entries.hasMoreElements()) {
				String absoluteName = entries.nextElement().getName();
				if (absoluteName.startsWith(unitFolder)) { // filter specified path
					String fileName = absoluteName.substring(unitFolder.length());

					if (fileName.startsWith(".")) // ignore hidden files and folders
						continue;

					if (fileName.endsWith("/")) // ignore pure folder entries
						continue;

					relevantXmlFiles.add(fileName);
				}
			}

			// populate the menu
			String[] paths =
				relevantXmlFiles.toArray(new String[relevantXmlFiles.size()]);

			Arrays.sort(paths, new Comparator<String>() {
				public int compare(String s1, String s2) {
					int slash1 = s1.lastIndexOf('/');
					int slash2 = s2.lastIndexOf('/');
					return s1.substring(slash1 + 1).compareTo(s2.substring(slash2 + 1));
				}
				@Override
				public boolean equals(Object o) {
					return false;
				}
			});

			for (String p : paths) {
				reflectJarUnitsInMenu(unitGroups, node, menu, p, "", url);
			}
		}
		catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Goes through a given resource and creates menu and tree items when neccecary.
	 */
	private void reflectJarUnitsInMenu(Dictionary<String, UnitDelegateInfo> entries, MutableTreeNode node,
			JMenu menu, String resource, String basePath, URL url) {
		int slash = resource.indexOf("/");
		if (slash >= 0) { // is it a directory?
			String name = resource.substring(0, slash);

			resource = resource.substring(slash + 1);
			basePath = basePath + name + "/";

			// make no new items for files in relative root ("/", if applicable)
			if (basePath.length() > 1) {
				UnitDelegateInfo ud = entries.get(basePath);
				if (ud == null) {
					ud = addUnitDelegateGroup(name, node, menu);
					entries.put(basePath, ud);
				}
				reflectJarUnitsInMenu(entries, ud.treeNode, ud.subMenu, resource, basePath, url);
			} else if (resource.length() > 0) {
				reflectJarUnitsInMenu(entries, node, menu, resource, basePath, url);
			}
		} else {
			if (resource.length() == 0) // stop if there is no file to add
				return;

			try {
				String xmlPath = "/" + unitFolder + basePath + resource;
				URL fileURL = new URL(url, xmlPath);

				if (basePath.length() > 1) {
					UnitDelegateInfo ud = entries.get(basePath);
					if (ud == null) {
						ud = addUnitDelegateGroup(basePath, node, menu);
						entries.put(basePath, ud);
					}
					addUnitDelegate(ud.treeNode, ud.subMenu, fileURL);
				} else {
					addUnitDelegate(node, menu, fileURL);
				}
			}
			catch (MalformedURLException e){
				e.printStackTrace();
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
	 * added to the given parents and returned within a new UnitDelegateInfo object.
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

	/**
	 * @return
	 */
	public static String getUnitFolder() {
		return unitFolder;
	}

	/**
	 * @return
	 */
	public static String getUnitIconFolder() {
		return unitIconFolder;
	}


	/**
	 * Gets the resource base path of the initially loaded units.
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
		resourcesBase = new URL(path, "/");
	}

	/**
	 * Find a UnitDelegate by name.
	 * @param unitName 
	 * @return
	 */
	public UnitDelegate getDelegate(final String unitName) {
		UnitDelegate unitDelegate = null; 
		for (final Delegate delegate : getUnitDelegates().values()) {
			if(delegate instanceof UnitDelegate) {
				unitDelegate = (UnitDelegate) delegate;
				if (unitDelegate.getName().equals(unitName))
					return unitDelegate;		
			}
		}
		return null;

	}

}
