package gui;
import graph.Edges;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import models.unit.UnitDelegate;
import models.unit.UnitList;
import visualap.Delegate;
import visualap.GPanel;
import visualap.GPanelListener;
import actions.RunMacroAction;
import backend.GraphController;

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
	private Edges edges;
	private GraphController controller;

	private JTextArea macroArea;
	
	/**
	 * 
	 */
	public Applicationframe() {
		init();
	}
	
	/**
	 * 
	 */
	public Applicationframe(UnitList units, Edges edges) {
		this.units = units;
		this.edges = edges;
		init();
	}
	
	public Applicationframe(GraphController controller) {
		this.controller = controller;
		this.units = controller.getUnitElements();
		this.edges = controller.getConnections();
		init();
	}

	/**
	 * 
	 */
	private void init() {
		this.setTitle(TITLE);
		this.setName(TITLE);
		this.setSize(500, 400);
		
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
		
		ArrayList<Delegate> unitsDelegates = new ArrayList<Delegate>();
		UnitDelegate unitDelegate = new UnitDelegate();
		unitsDelegates.add(unitDelegate);
		
		
		
		this.setLayout(new BorderLayout());
		
		//working area aka graphpanel
		
		GPanel graphPanel = new GraphPanel(unitsDelegates , this);
		graphPanel.setSize(400, 300);
		graphPanel.setNodeL(units);
		graphPanel.setEdgeL(edges);
		graphPanel.setPreferredSize(new Dimension(400, 300));
//		graphPanel.getSelection();
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
		
		JPanel selectUnitPanel = new JPanel();
		selectUnitPanel.setBackground(Color.WHITE);
		selectUnitPanel.setPreferredSize(new Dimension(400, 80));
		
		JScrollPane selectUnitScrollpane = new JScrollPane(selectUnitPanel); 
		unitSelectionPanel.add(selectUnitScrollpane);
		
		//properties of the selected node
		JPanel propertiesPanel = new JPanel();
		propertiesPanel.setName("Properties");
		
		
		// logging the history?
		JPanel logPanel = new JPanel();
		logPanel.setName("Log");
		
		
		
		// display the generated macro
		JPanel macroPanel = new JPanel();
		macroPanel.setName("Macro");
		macroPanel.setLayout(new BorderLayout());
		
		macroArea = new JTextArea();
		JScrollPane macroAreaScrollpane = new JScrollPane(macroArea);
		macroPanel.add(macroAreaScrollpane, BorderLayout.CENTER);
		JButton buttonRun = new JButton(new RunMacroAction(controller));
		macroPanel.add(buttonRun, BorderLayout.SOUTH);
		
		JTabbedPane functionTabPane = new JTabbedPane();
		functionTabPane.add(unitSelectionPanel);
		functionTabPane.add(propertiesPanel);
		functionTabPane.add(logPanel);
		functionTabPane.add(macroPanel);
		add(functionTabPane, BorderLayout.SOUTH);
	}

	public void setMacro(String macro) {
		this.macroArea.setText(macro);
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
