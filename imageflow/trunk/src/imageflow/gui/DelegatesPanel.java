package imageflow.gui;

import java.awt.BorderLayout;
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

	public DelegatesPanel(HashMap<TreeNode, Delegate> delegates) {
	
		this.delegates = delegates;
		setLayout(new BorderLayout());
		
		DefaultMutableTreeNode top =
	        new DefaultMutableTreeNode("ImageJ Filters");
		DefaultTreeModel delegatesModel = new DefaultTreeModel(top);
		createNodes(top);
		
		
		
		JTree delegatesTree = new JTree(delegatesModel);
		delegatesTree.setRootVisible(false);
		delegatesTree.setToggleClickCount(1);
		
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
