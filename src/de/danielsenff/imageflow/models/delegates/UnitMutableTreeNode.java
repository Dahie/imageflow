package de.danielsenff.imageflow.models.delegates;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class UnitMutableTreeNode extends DefaultMutableTreeNode {

	private JMenu menuItem;

	public UnitMutableTreeNode(String title) {
		super(title);
		this.menuItem = new JMenu(title);
	}
	
	public JMenuItem getMenu() {
		return this.menuItem;
	}

	@Override
	public void add(MutableTreeNode newChild) {
		super.add(newChild);
		//this.menuItem.add(((MutableTreeNode) newChild).getMenu());
	}
	
	/*public void add(UnitDelegate unitDelegate) {
		JMenuItem item = new JMenuItem(unitDelegate.getName());
		super.add(newChild);


		final UnitDescription unitDescription = new UnitDescription(url, Tools.getXMLRoot(url));
		final UnitDelegate unitDelegate = new UnitDelegate(unitDescription);
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(unitDelegate.getName());

//		delegates.put(treeNode, unitDelegate);
		node.add(unitDelegate);
	}*/
}

