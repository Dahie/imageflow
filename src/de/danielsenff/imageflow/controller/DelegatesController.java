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

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Node delegates");
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
		UnitXMLLoader loader = UnitXMLLoaderFactory.createUnitXMLLoaderByProtocol(this, url.getProtocol()); 
		loader.readDelegates(node, menu, url);
	}
	
	/**
	 * Adds a XML unit to the unit tree and the menu.
	 */
	void addUnitDelegate(MutableTreeNode node, JMenu menu, URL url) {
		final UnitDescription unitDescription = new UnitDescription(url, Tools.getXMLRoot(url));
		final UnitDelegate unitDelegate = new UnitDelegate(unitDescription);
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(unitDelegate.getName());
		delegates.put(treeNode, unitDelegate);

		((DefaultMutableTreeNode) node).add(unitDelegate);
		JMenuItem item = new JMenuItem(unitDelegate.getName());
		menu.add(item);
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
			resourcesBase = new URL(path, System.getProperty("user.dir") + File.separator + getUnitFolder());
		} else if (protocol.equals("jar")) {
			resourcesBase = new URL(path, "/");
		}
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
