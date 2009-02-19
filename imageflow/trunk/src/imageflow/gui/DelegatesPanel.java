package imageflow.gui;

import imageflow.backend.DelegatesController;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import visualap.Delegate;

public class DelegatesPanel extends JPanel {

	private HashMap<TreeNode, Delegate> delegates;

	public DelegatesPanel() {
		DelegatesController delegatesController = DelegatesController.getInstance();
		this.delegates = delegatesController.getUnitDelegates();
		setLayout(new BorderLayout());

		DefaultMutableTreeNode top =
			new DefaultMutableTreeNode("ImageJ Filters");
		DefaultTreeModel delegatesModel = delegatesController.getDelegatesModel();
		//		createNodes(top);



		final JTree delegatesTree = new JTree(delegatesModel);
		delegatesTree.setRootVisible(false);
		delegatesTree.setToggleClickCount(1);
		delegatesTree.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e) {
				DragGestureListener dragGestureListener = new DragGestureListener() {
					public void dragGestureRecognized(DragGestureEvent e) {
						// Der Text des Label soll
						// JVM-intern Ÿbertragen werden
						StringSelection selection = new StringSelection("test");
						Transferable trans = new Transferable() {

							public Object getTransferData(DataFlavor arg0)
									throws UnsupportedFlavorException,
									IOException {
								// TODO Auto-generated method stub
								return null;
							}

							public DataFlavor[] getTransferDataFlavors() {
								// TODO Auto-generated method stub
								return null;
							}

							public boolean isDataFlavorSupported(DataFlavor arg0) {
								// TODO Auto-generated method stub
								return false;
							}
							
						};
						e.startDrag(null, selection);
					} 
				};
				DragSource dragSource = new DragSource();
				DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(
						delegatesTree, DnDConstants.ACTION_MOVE, dragGestureListener);
			}

			public void mouseMoved(MouseEvent e) {}

		});

		JScrollPane scrollPane = new JScrollPane(delegatesTree);
		add(scrollPane, BorderLayout.CENTER);

	}

	private void createNodes(DefaultMutableTreeNode top) {

		Set<TreeNode> keys = delegates.keySet();

		DefaultMutableTreeNode nodeAnalysis = new DefaultMutableTreeNode("Analysis");
		top.add(nodeAnalysis);

		for (TreeNode treeNode : keys) {
			nodeAnalysis.add((MutableTreeNode) treeNode);
		}


		/*top.add(new DefaultMutableTreeNode("Filter"));
		top.add(new DefaultMutableTreeNode("Adjust"));
		top.add(new DefaultMutableTreeNode("Lookup Tables"));*/
	}


}
