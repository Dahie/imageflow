package imageflow.gui;

import imageflow.backend.DelegatesController;
import imageflow.models.unit.UnitDelegate;
import imageflow.models.unit.UnitList;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import visualap.Delegate;

public class DelegatesPanel extends JPanel {

	private HashMap<TreeNode, Delegate> delegates;

	public DelegatesPanel(final UnitList unitList) {
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
						//						StringSelection selection = new StringSelection("test");
						Transferable trans = new Transferable() {

							public Object getTransferData(DataFlavor arg0)
							throws UnsupportedFlavorException,
							IOException {
								return null;
							}

							public DataFlavor[] getTransferDataFlavors() {
								return null;
							}

							public boolean isDataFlavorSupported(DataFlavor arg0) {
								return false;
							}

						};
						e.startDrag(null, trans);
					} 
				};
				DragSource dragSource = new DragSource();
				DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(
						delegatesTree, DnDConstants.ACTION_MOVE, dragGestureListener);
			}

			public void mouseMoved(MouseEvent e) {}
		});

		delegatesTree.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTree tree = (JTree) e.getSource();

					int selRow = tree.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					if(selRow != -1 && selPath.getLastPathComponent() instanceof UnitDelegate) {
						//			        	 myDoubleClick(selRow, selPath);
						UnitDelegate ud = ((UnitDelegate)selPath.getLastPathComponent());
						unitList.add(ud.createUnit(UnitDelegate.POINT));
					}
				}
			}

			public void mouseEntered(MouseEvent arg0) {}

			public void mouseExited(MouseEvent arg0) {}

			public void mousePressed(MouseEvent arg0) {}

			public void mouseReleased(MouseEvent arg0) {}

		});
		delegatesTree.setCellRenderer(new IFTreeCellRenderer());

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


	class IFTreeCellRenderer extends JPanel implements TreeCellRenderer {
		JLabel filename = new JLabel();
		private JLabel fileicon;


		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean isLeaf, int row,
				boolean hasFocus) {

			init(value, isSelected, isLeaf);

			return this;//return component used to render
		}

		private void init(Object value, boolean isSelected, boolean isLeaf) {

			
			if(value instanceof UnitDelegate && isLeaf) {
				UnitDelegate unitDelegate = (UnitDelegate)value;
				String theLabel = unitDelegate.getName();
				

//				Icon systemIcon = FileSystemView.getFileSystemView().getSystemIcon(file);
//				fileicon = new JLabel(systemIcon);
				
				setLayout(new BorderLayout());
				filename.setText(theLabel);
				filename.setAlignmentX(Component.LEFT_ALIGNMENT);
				if(isSelected){	//set the red ball
					setBackground(SystemColor.textHighlight);
				}else{	//set the blue ball for not selected
					setBackground(SystemColor.text);
				}
//				this.add(fileicon, BorderLayout.LINE_START);
				this.add(filename, BorderLayout.CENTER);
				
			} else if (value instanceof MutableTreeNode && !isLeaf) {
				String theLabel = ((MutableTreeNode)value).toString();
				setLayout(new BorderLayout());
				filename.setText(theLabel);
				filename.setAlignmentX(Component.LEFT_ALIGNMENT);
				if(isSelected){	//set the red ball
					setBackground(SystemColor.textHighlight);
				}else{	//set the blue ball for not selected
					setBackground(SystemColor.text);
				}
//				this.add(fileicon, BorderLayout.LINE_START);
				this.add(filename, BorderLayout.CENTER);
			}

			
		}
	}

}