package gui;
import graph.GList;
import graph.Node;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import visualap.Delegate;
import visualap.GPanel;
import visualap.GPanelListener;
import Models.unit.NodeUnit;
import Models.unit.UnitDelegate;
import Models.unit.UnitElement;
import Models.unit.UnitList;

/**
 * 
 */

/**
 * @author danielsenff
 *
 */
public class Applicationframe extends JFrame implements GPanelListener {

	public static String TITLE = "ImageFlow";
	
	private UnitList units;
	
	/**
	 * 
	 */
	public Applicationframe() {
		init();
	}
	
	/**
	 * 
	 */
	public Applicationframe(UnitList units) {
		this.units = units;
		init();
		
	}

	/**
	 * 
	 */
	private void init() {
		this.setTitle(TITLE);
		this.setName(TITLE);
		this.setSize(400, 200);
		
		addComponents();
		
		addMenu();
		
		this.setVisible(true);
//		this.pack();
	}

	/**
	 * Adds all components of
	 */
	private void addMenu() {
		JMenu fileMenu;
	}

	/**
	 * Adds all components to the Jframe
	 */
	private void addComponents() {
		
		ArrayList<Delegate> units = new ArrayList<Delegate>();
		UnitDelegate unitDelegate = new UnitDelegate();
		units.add(unitDelegate);
		
		GList<Node> nodeL = new GList<Node>();
		for (UnitElement unit : this.units) {
			if(unit != null)
				nodeL.add(new NodeUnit(new Point(25,25), unit));
		}
		
		
		this.setLayout(new BorderLayout());
		
		
		
		//working area aka graphpanel
		
		GPanel graphPanel = new GPanel(units , this);
		graphPanel.setSize(400, 200);
		graphPanel.setNodeL(nodeL);
		graphPanel.setPreferredSize(new Dimension(400, 300));
		ScrollPane graphScrollpane = new ScrollPane();
		graphScrollpane.add(graphPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				true, 
				graphScrollpane, 
				new InsertUnitPanel() );
//		splitpane.setDividerLocation(0.8);
		this.add(graphScrollpane, BorderLayout.CENTER);
		
		// area for selecting unit to insert them in the graphpanel

		JPanel unitSelectionPanel = new JPanel();
		unitSelectionPanel.setName("Insert filter");
		
		JPanel logPanel = new JPanel();
		logPanel.setName("Log");
		
		JPanel macroPanel = new JPanel();
		macroPanel.setName("Macro");
		
		
		JTabbedPane functionTabPane = new JTabbedPane();
		functionTabPane.add(unitSelectionPanel);
		functionTabPane.add(logPanel);
		functionTabPane.add(macroPanel);
	}

	
	public void showFloatingMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
		/*	savedPoint = e.getPoint();
			//Create the popup menu.
			JPopupMenu popup = new JPopupMenu();
			if (activePanel.selection.size() == 0) popup.add(newItem("New"));
			else {
				if (activePanel.selection.size() == 1) {
					popup.add(editItem("Properties"));
					popup.addSeparator();
				}
				popup.add(cutItem("Cut"));
				popup.add(unbindItem("Unbind"));
				popup.add(copyItem("Copy"));
			}
			if (copyL.size() != 0) popup.add(pasteItem("Paste"));
			popup.show(e.getComponent(), e.getX(), e.getY());*/
		}
	}
	
}
