package imageflow.backend;

import helper.Tools;
import imageflow.ImageFlow;
import imageflow.models.unit.UnitDelegate;
import imageflow.models.unit.UnitDescription;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitFactory;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import visualap.Delegate;

public class DelegatesController {

	private static String unitFolder = "xml_units";
	
	private static DelegatesController controller;
	//	private ArrayList<Delegate> unitDelegates;
	HashMap<TreeNode, Delegate> delegates;
	public DefaultTreeModel delegatesModel;

	public DelegatesController(String unitFolderPath) {
		this.unitFolder = unitFolderPath;
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

		String unitsFolder = System.getProperty("user.dir")+File.separator+unitFolder;
		File folder = new File(unitsFolder);
		if(folder.exists()) 
			readDelegatesFromFolder(top, insertMenu, folder);
		else 
			JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
					"The folder "+unitFolder+" is missing. No units have been found.",
					"No unit defintions found", 
					JOptionPane.WARNING_MESSAGE);
			System.out.println("No units-folder found");
	}

	private void readDelegatesFromFolder(MutableTreeNode node, JMenu menu, File folder) {
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if(file.isDirectory() && !file.isHidden()) {
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(file.getName());
				JMenu subMenu = new JMenu(file.getName());
				readDelegatesFromFolder(subNode, subMenu, file);
				((DefaultMutableTreeNode) node).add(subNode);
				menu.add(subMenu);
			} else if (file.isFile() && isXML(file)) {
				final UnitDescription unitDescription = new UnitDescription(Tools.getXMLRoot(file));
				final UnitDelegate unitDelegate = 
					new UnitDelegate(unitDescription.getUnitName(), unitDescription.getHelpString()) {

					@Override
					public UnitElement createUnit(final Point origin) {
						return UnitFactory.createProcessingUnit(unitDescription, origin);
					}

				};
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
}
