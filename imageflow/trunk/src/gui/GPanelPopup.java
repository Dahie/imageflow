/**
 * 
 */
package gui;

import graph.Edge;
import graph.Node;
import graph.NodeBean;
import graph.NodeText;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import actions.SetDisplayAction;

import models.unit.UnitDelegate;
import models.unit.UnitElement;
import visualap.Delegate;
import visualap.ErrorPrinter;
import visualap.GPanel;
import visualap.GPanelListener;

/**
 * @author danielsenff
 *
 */
public class GPanelPopup implements GPanelListener {

	protected GPanel activePanel;

	protected Point savedPoint = new Point(0,0);
	protected ArrayList<Node> copyL = new ArrayList<Node>();

	protected ArrayList<Delegate> availableUnits;



	/**
	 * @return the activePanel
	 */
	public GPanel getActivePanel() {
		return this.activePanel;
	}

	
	public GPanelPopup(ArrayList<Delegate> availableUnits) {
		this.availableUnits = availableUnits;
	}

	public void showFloatingMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			savedPoint = e.getPoint();
			//Create the popup menu.
			JPopupMenu popup = new JPopupMenu();
			if (activePanel.getSelection().size() == 0) { 
				popup.add(insertItem("Insert unit"));
			} else {
				if (activePanel.getSelection().size() == 1) {
					//					popup.add(editItem("Properties"));
					UnitElement unit = (UnitElement)activePanel.getSelection().get(0);
					popup.add(new JMenuItem(new SetDisplayAction(unit)));
					popup.addSeparator();
				}
				popup.add(cutItem("Cut"));
				popup.add(unbindItem("Unbind"));
				popup.add(copyItem("Copy"));
			}
			if (copyL.size() != 0) popup.add(pasteItem("Paste"));
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public JMenuItem cutItem(String text) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (activePanel.getSelection().size() > 0) {
					// il problema java.util.ConcurrentModificationException è stato risolto introducendo la lista garbage
					HashSet<Edge> garbage = new HashSet<Edge>();
					for (Node t : activePanel.getSelection()) {
						for (Edge c : activePanel.getEdgeL())
							if ((c.from.getParent() == t)||(t == c.to.getParent()))
								garbage.add(c);
						activePanel.getNodeL().remove(t);
					}
					for (Edge c : garbage)
						activePanel.getEdgeL().remove(c);
					activePanel.getSelection().clear();
					activePanel.repaint();
				}
			}});
		return menuItem;
	}

	public JMenuItem unbindItem(String text) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (activePanel.getSelection().size() > 0) {
					// il problema java.util.ConcurrentModificationException è stato risolto introducendo la lista garbage
					HashSet<Edge> garbage = new HashSet<Edge>();
					for (Node t : activePanel.getSelection()) {
						for (Edge c : activePanel.getEdgeL())
							if ((c.from.getParent() == t)||(t == c.to.getParent()))
								garbage.add(c);
					}
					for (Edge c : garbage)
						activePanel.getEdgeL().remove(c);
					activePanel.getSelection().clear();
					activePanel.repaint();
				}
			}});
		return menuItem;
	}

	public JMenuItem copyItem(String text) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (activePanel.getSelection().size() > 0) {
					copyL.clear();
					for (Node t : activePanel.getSelection())
						try {
							Node clone = t.clone();	clone.setLabel(t.getLabel());
							copyL.add(clone);
							if (clone instanceof NodeBean)
								((NodeBean)clone).setContext(activePanel.getGlobalVars());
						} catch(CloneNotSupportedException ex) {
							ErrorPrinter.printInfo("CloneNotSupportedException");
						}
				}
			}});
		return menuItem;
	}

	public JMenuItem pasteItem(String text) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (copyL.size() > 0) {
					activePanel.getSelection().clear();activePanel.getSelection().addAll(copyL);
					copyL.clear();
					for (Node t : activePanel.getSelection()) {
						try {
							Node clone = t.clone();	clone.setLabel(t.getLabel());
							activePanel.getNodeL().add(t, t.getLabel());
							copyL.add(clone);
							if (clone instanceof NodeBean)
								((NodeBean)clone).setContext(activePanel.getGlobalVars());
						} catch(CloneNotSupportedException ex) {
							ErrorPrinter.printInfo("CloneNotSupportedException");
						}
					}
					//					copyL.clear(); copyL.addAll(activePanel.selection);
					activePanel.repaint();
				}
			}});
		return menuItem;
	}

	public JMenu insertItem(String text) {
		JMenu newMenu = new JMenu(text);
		ActionListener newAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JMenuItem source = (JMenuItem)(e.getSource());
				String action = source.getText();
				if (action.equals("Add Text")) {	
					// corrected fault: NodeText instead of NodeBean
					Node n = new NodeText(new Point(savedPoint), "text"); savedPoint.translate(4, 4);
					activePanel.getNodeL().add(n, "text$0");
					activePanel.getSelection().clear();
					activePanel.getSelection().add(n);
					activePanel.repaint();
					return;
				}

				// add selected node
				for (int i = 0; i < availableUnits.size(); i++) {
					UnitDelegate delegate = (UnitDelegate)availableUnits.get(i); 
					if (delegate.getName().equals(action)) {
						try {
							//							Object myBean = delegate.get(i).clazz.newInstance();
							UnitElement n = delegate.createUnit(savedPoint);
							n.setContext(activePanel.getGlobalVars());
							activePanel.getNodeL().add(n, activePanel.shortName(action));
							activePanel.getSelection().clear();
							activePanel.getSelection().add(n);
							activePanel.repaint();
						} catch (Exception ex) {
							ErrorPrinter.printInfo("instantiation of a new bean failed"+ ex);
						}
						return;
					}
				}
			}}; 
		JMenuItem mi = new JMenuItem("Add Text");
		mi.setToolTipText("Insert text");
		newMenu.add(mi).addActionListener(newAction);		

		//list over all available units

		for (int i = 0; i < availableUnits.size(); i++) {
			UnitDelegate delegate = (UnitDelegate) availableUnits.get(i);

			mi = new JMenuItem(delegate.getName());
			mi.setToolTipText(delegate.getToolTipText());
			newMenu.add(mi).addActionListener(newAction);		
		}
		return newMenu;
	}




	/**
	 * @param activePanel the activePanel to set
	 */
	public void setActivePanel(GPanel activePanel) {
		this.activePanel = activePanel;
	}
}
