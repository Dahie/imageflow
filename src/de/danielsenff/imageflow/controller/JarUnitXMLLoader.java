package de.danielsenff.imageflow.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * XMLLoader to load Unit-Definitions from within the launchable jar. 
 * @author dahie
 *
 */
public class JarUnitXMLLoader implements UnitDelegateLoader {

	private DelegatesController delegatesController;
	
	public JarUnitXMLLoader(DelegatesController delegatesController) {
		this.delegatesController = delegatesController;
	}
	
	/**
	 * Reads contents of a jar file and manages the traversal of the file tree.
	 */
	public void readDelegates(MutableTreeNode node, JMenu menu, URL url)
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
				if (absoluteName.startsWith(delegatesController.getUnitFolder())) { // filter specified path
					String fileName = absoluteName.substring(delegatesController.getUnitFolder().length());

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
	 * Goes through a given resource and creates menu and tree items when necessary.
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
				String xmlPath = "/" + delegatesController.getUnitFolder() + basePath + resource;
				URL fileURL = new URL(url, xmlPath);

				if (basePath.length() > 1) {
					UnitDelegateInfo ud = entries.get(basePath);
					if (ud == null) {
						ud = addUnitDelegateGroup(basePath, node, menu);
						entries.put(basePath, ud);
					}
					delegatesController.addUnitDelegate(ud.treeNode, ud.subMenu, fileURL);
				} else {
					delegatesController.addUnitDelegate(node, menu, fileURL);
				}
			}
			catch (MalformedURLException e){
				e.printStackTrace();
			}
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
	
}
