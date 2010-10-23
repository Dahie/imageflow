package de.danielsenff.imageflow.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.danielsenff.imageflow.models.delegates.UnitDelegate;
import de.danielsenff.imageflow.models.delegates.UnitDescription;
import de.danielsenff.imageflow.models.delegates.UnitMutableTreeNode;
import de.danielsenff.imageflow.utils.Tools;

public abstract class BasicUnitXMLLoader implements UnitDelegateLoader {

	protected Set<String> relevantXmlFiles;
	//protected Dictionary<String, UnitDelegateInfo> unitEntries;

	public BasicUnitXMLLoader() {
		//this.unitEntries = new Hashtable<String, UnitDelegateInfo>();
		this.relevantXmlFiles = new HashSet<String>();
	}
	
	protected String[] sortPaths(Set<String> relevantXmlFiles) {
		String[] paths = relevantXmlFiles.toArray(new String[relevantXmlFiles.size()]);

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
		return paths;
	}
	
	/**
	 * Adds a XML unit to the unit tree and the menu.
	 */
	static void addUnitDelegate(MutableTreeNode parentNode, URL url) {
		boolean withinJar = url.getProtocol().equals("jar");
		final UnitDescription unitDescription = new UnitDescription(url, Tools.getXMLRoot(url));
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
					UnitDelegate child = (UnitDelegate) parentNode.getChildAt(i);
					if (child.getName().equals(unitDelegate.getName())) {
						parentNode.remove(i);
						parentNode.insert(unitDelegate, i);
					}
				}
			}
			DelegatesController.getInstance().replaceDelegate(unitDelegate);
		}
	}

	public Dictionary<String, UnitDelegateInfo> getEntries() {
		return DelegatesController.getInstance().getUnitEntries();
	}
	
	protected abstract void retrieveRelevantXMLPaths(Enumeration entries,
			Set<String> relevantXmlFiles) throws IOException;
	
	/**
	 * An inner class for convenience. It represents some information about unit delegates.
	 */
	protected class UnitDelegateInfo {
		public String name;
		public DefaultMutableTreeNode treeNode;

		public UnitDelegateInfo(String name, MutableTreeNode node) {
			this.name = name;
			this.treeNode = (DefaultMutableTreeNode) node;
		}
		
		public UnitDelegateInfo(String name, DefaultMutableTreeNode node) {
			this.name = name;
			this.treeNode = node;
		}
	}
	

	/**
	 * Creates a menu item and a tree node for a given file name. These objects are
	 * added to the given parents and returned within a new UnitDelegateInfo object.
	 */
	protected UnitDelegateInfo addUnitDelegateGroup(String fileName, MutableTreeNode node) {
		// cut away leading and trailing slashs
		String displayName = fileName;
		if (displayName.startsWith("/"))
			displayName = displayName.substring(1);
		if (displayName.endsWith("/"))
			displayName = displayName.substring(0, displayName.length() - 1);

		UnitMutableTreeNode subNode = new UnitMutableTreeNode(displayName);
		((UnitMutableTreeNode) node).add(subNode);

		UnitDelegateInfo udi = new UnitDelegateInfo(displayName, subNode);
		return udi;
	}
	
}
