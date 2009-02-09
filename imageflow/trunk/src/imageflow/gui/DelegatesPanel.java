package imageflow.gui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import visualap.Delegate;

public class DelegatesPanel extends JPanel {

	private HashMap<TreeNode, Delegate> delegates;

	public DelegatesPanel(HashMap<TreeNode, Delegate> delegates) {
	
		this.delegates = delegates;
		setLayout(new BorderLayout());
		
		DefaultMutableTreeNode top =
	        new DefaultMutableTreeNode("The Java Series");
		DefaultTreeModel delegatesModel = new DefaultTreeModel(top);
		createNodes(top);
		
		
		
		JTree delegatesTree = new JTree(delegatesModel);
		
		JScrollPane scrollPane = new JScrollPane(delegatesTree);
		add(scrollPane, BorderLayout.CENTER);
		
	}

	private void createNodes(DefaultMutableTreeNode top) {
		
		Set<TreeNode> keys = delegates.keySet();
		for (TreeNode treeNode : keys) {
			top.add(new DefaultMutableTreeNode("Analysis"));
		}
		
		
		/*top.add(new DefaultMutableTreeNode("Filter"));
		top.add(new DefaultMutableTreeNode("Adjust"));
		top.add(new DefaultMutableTreeNode("Lookup Tables"));*/
	}
	
	
}
