package de.danielsenff.imageflow.controller;


import java.io.File;
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

	public static String unitFolder = "xml_units";
	public static String unitIconFolder = "xml_icons";
	
	private static DelegatesController controller;
	HashMap<TreeNode, Delegate> delegates;
	public DefaultTreeModel delegatesModel;

	public DelegatesController(String unitFolderPath) {
		DelegatesController.setUnitFolder(unitFolderPath);
	}
	
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
		//		unitDelegates = new ArrayList<Delegate>();
		delegates = new HashMap<TreeNode, Delegate>();

		DefaultMutableTreeNode top =
			new DefaultMutableTreeNode("Node delegates");
		delegatesModel = new DefaultTreeModel(top);
		JMenu insertMenu = new JMenu("Insert unit");

		String unitsFolder = System.getProperty("user.dir")+File.separator+getUnitFolder();
		/*String unitsFolder = ""; 
		try {
			ProtectionDomain protectionDomain = DelegatesController.class.getProtectionDomain();
			CodeSource codeSource = protectionDomain.getCodeSource();
			URL location = codeSource.getLocation();
			URI uri = location.toURI();
			File jarFile = new File(uri);
			if(jarFile.exists()) {
				unitsFolder = jarFile.getParentFile().getAbsolutePath()+File.separator+unitFolder;	
			} else
				unitsFolder = System.getProperty("user.dir")+File.separator+unitFolder;	
			
		} catch (URISyntaxException e) {
//			e.printStackTrace();
		}*/ 
		
		
		File folder = new File(unitsFolder);
		if(folder.exists()) 
			readDelegatesFromFolder(top, insertMenu, folder);
		else 
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"The folder "+getUnitFolder()+" is missing. No units have been found.",
					"No unit defintions found", 
					JOptionPane.WARNING_MESSAGE);
	}

	private void readDelegatesFromFolder(MutableTreeNode node, JMenu menu, File folder) {
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if(file.isDirectory() 
					&& !file.isHidden() 
					&& !file.getName().startsWith(".")) {
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(file.getName());
				JMenu subMenu = new JMenu(file.getName());
				readDelegatesFromFolder(subNode, subMenu, file);
				((DefaultMutableTreeNode) node).add(subNode);
				menu.add(subMenu);
			} else if (file.isFile() 
					&& isXML(file)  
					&& !file.getName().startsWith(".")) {

				final UnitDescription unitDescription = new UnitDescription(file, Tools.getXMLRoot(file));
				final UnitDelegate unitDelegate = new UnitDelegate(unitDescription);
				//				unitDelegates.add(unitDelegate);
				DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(unitDelegate.getName());
				delegates.put(treeNode, unitDelegate);

				((DefaultMutableTreeNode) node).add(unitDelegate);
				JMenuItem item = new JMenuItem(unitDelegate.getName());
				menu.add(item);

			}
		}
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
