package imageflow.gui;

import imageflow.backend.DelegatesController;
import imageflow.backend.Model;
import imageflow.backend.ModelListener;
import imageflow.models.unit.CommentNode;
import imageflow.models.unit.UnitDelegate;
import imageflow.models.unit.UnitElement;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import visualap.Delegate;
import visualap.ErrorPrinter;
import visualap.GPanel;

public class InsertUnitMenu extends JMenu {

	private GPanel activePanel;
	private final Collection<Delegate> availableUnits;
	private static Point savedPoint = new Point(75, 75);

	public InsertUnitMenu(final GPanel gpanel, final Collection<Delegate> availableUnits) {
		this("Insert", gpanel, availableUnits, savedPoint);
	}

	public InsertUnitMenu(final String name, 
			final GPanel gpanel, 
			final Collection<Delegate> availableUnits, 
			final Point savedPoint) {
		this.setName(name);
		this.setText(name);
		this.activePanel = gpanel;
		this.availableUnits = availableUnits;
		ActionListener newAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JMenuItem source = (JMenuItem)(e.getSource());
				String action = source.getText();
				if (action.equals("Comment")) {	
					CommentNode n = new CommentNode(savedPoint, "text"); 
					savedPoint.translate(4, 4);
					System.out.println(n);
					activePanel.getNodeL().add(n, "text$0");
					activePanel.getSelection().clear();
					activePanel.getSelection().add(n);
					activePanel.repaint();
					n.addModelListener(new ModelListener() {
						public void modelChanged(Model model) {
							activePanel.repaint();
						}
					});
					return;
				}

				// add selected node
				for (Delegate delegate : availableUnits) {
					if(delegate instanceof UnitDelegate) {
						UnitDelegate unitDelegate = (UnitDelegate) delegate;
						if (unitDelegate.getName().equals(action)) {
							try {
								UnitElement n = unitDelegate.createUnit(savedPoint);
								n.setContext(activePanel.getGlobalVars());
								activePanel.getNodeL().add(n, activePanel.shortName(action));
								activePanel.getSelection().clear();
								activePanel.getSelection().add(n);
								activePanel.repaint();
								n.addModelListener(new ModelListener() {
									public void modelChanged(Model model) {
										activePanel.repaint();
									}
								});
							} catch (Exception ex) {
								ErrorPrinter.printInfo("instantiation of a new bean failed"+ ex);
							}
							return;
						}	
					}
				}
			}}; 
			JMenuItem mi = new JMenuItem("Comment");
			mi.setToolTipText("Insert Notes or Comments to the graph.");
			add(mi).addActionListener(newAction);		

			TreeModel tree = DelegatesController.getInstance().delegatesModel;
			
			MutableTreeNode root =  (MutableTreeNode) tree.getRoot();
			createMenu(this, newAction, root);
			
			
			//list over all available units
			/*for (Delegate delegate : availableUnits) {
				if(delegate instanceof UnitDelegate) {
					UnitDelegate unitDelegate = (UnitDelegate) delegate;

					mi = new JMenuItem(unitDelegate.getName());
					mi.setToolTipText(unitDelegate.getToolTipText());
					add(mi).addActionListener(newAction);
				}
			}*/
	}

	private void createMenu(JMenu menu, ActionListener newAction, MutableTreeNode root) {
		JMenuItem mi;
		for (int i = 0; i < root.getChildCount(); i++) {
			MutableTreeNode node = (MutableTreeNode) root.getChildAt(i);
			if(node instanceof UnitDelegate) {
				UnitDelegate unitDelegate = (UnitDelegate)node;
				mi = new JMenuItem(unitDelegate.getName());
				mi.setToolTipText(unitDelegate.getToolTipText());
				menu.add(mi).addActionListener(newAction);
			} else {
				JMenu subMenu = new JMenu(node.toString());
				createMenu(subMenu, newAction, node);
				menu.add(subMenu);
			}
		}
	}

}
