package imageflow.backend;

import helper.Tools;
import imageflow.models.unit.UnitDelegate;
import imageflow.models.unit.UnitDescription;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitFactory;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import visualap.Delegate;

public class DelegatesController {

	private static DelegatesController controller;
	//	private ArrayList<Delegate> unitDelegates;
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
		//		unitDelegates = new ArrayList<Delegate>();
		delegates = new HashMap<TreeNode, Delegate>();

		DefaultMutableTreeNode top =
			new DefaultMutableTreeNode("Node delegates");
		delegatesModel = new DefaultTreeModel(top);

		File folder = new File("xml_units");
		
		readDelegatesFromFolder(top, folder); 
	}

	private void readDelegatesFromFolder(MutableTreeNode node, File folder) {
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if(file.isDirectory()) {
				readDelegatesFromFolder(new DefaultMutableTreeNode(file.getName()), file);
			} else if (file.isFile() && isXML(file)) {
//				System.out.println("File " + file.getName());

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

				((DefaultMutableTreeNode) node).add(treeNode);
			}
		}
	}


	//		unitDelegates.add(new UnitDelegate("Source", "This is an image source, which loads an imagefile into the workflow.") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				return UnitFactory.createSourceUnit(origin);
	//			}
	//		});


	//		unitDelegates.add(new UnitDelegate("Background", "Creates a new canvas, an empty background.") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				int width = Integer.parseInt(JOptionPane.showInputDialog("Width of the background:"));
	//				int height = Integer.parseInt(JOptionPane.showInputDialog("Height of the background:"));
	//				return UnitFactory.createBackgroundUnit(new Dimension(width,height), origin);
	//			}
	//		});
	//		unitDelegates.add(new UnitDelegate("Add Noise", "This filter adds noise to the image.") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				return UnitFactory.createAddNoiseUnit(origin);
	//			}
	//		});
	//		unitDelegates.add(new UnitDelegate("Gaussian Blur", "The image is blurred using Gaussian Blur.") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				return UnitFactory.createGaussianBlurUnit(origin);
	//			}
	//		});
	//		unitDelegates.add(new UnitDelegate("Find Edges", "Finds edges in the image and filters them.") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				return UnitFactory.createFindEdgesUnit(origin);
	//			}
	//		});
	//		unitDelegates.add(new UnitDelegate("Invert", "Invert the image.") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				return UnitFactory.createInvertUnit(origin);
	//			}
	//		});	
	//		unitDelegates.add(new UnitDelegate("Math", "Subtracts to images from another") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				return UnitFactory.createImageCalculatorUnit(origin);
	//			}
	//		});
	//		
	//		unitDelegates.add(new UnitDelegate("Measure", "Prints several measurements of the image.") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				return UnitFactory.buildUnitElement(name, "run(\"Measure\");", origin);
	//			}
	//		});
	//		unitDelegates.add(new UnitDelegate("Histogram", "Shows the histogramm of the input image.") {
	//			@Override
	//			public UnitElement createUnit(final Point origin) {
	//				return UnitFactory.createHistogramUnit(origin);
	//			}
	//		});

	//}

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
