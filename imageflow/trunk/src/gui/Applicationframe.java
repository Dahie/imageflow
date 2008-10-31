package gui;
import graph.Edges;
import helper.FileDrop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.ScrollPane;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import models.unit.UnitFactory;
import models.unit.UnitList;
import visualap.Delegate;
import actions.RunMacroAction;
import backend.DelegatesController;
import backend.GraphController;

/**
 * 
 */

/**
 * @author danielsenff
 *
 */
public class Applicationframe extends JFrame {

	public static String TITLE = "ImageFlow for ImageJ";
	
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
		this.setSize(600, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
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
		
		ArrayList<Delegate> unitDelegates = DelegatesController.getInstance().getUnitDelegates();
		
		
		this.setLayout(new BorderLayout());
		
		//working area aka graphpanel
		
		GPanelPopup popup = new GPanelPopup(unitDelegates, controller.getCopyNodesList());
		final GraphPanel graphPanel = new GraphPanel(unitDelegates , popup);
		popup.setActivePanel(graphPanel);
		graphPanel.setSize(400, 300);
		graphPanel.setNodeL(units);
		graphPanel.setEdgeL(edges);
		graphPanel.setPreferredSize(new Dimension(400, 300));
//		graphPanel.getSelection();
		ScrollPane graphScrollpane = new ScrollPane();
		graphScrollpane.add(graphPanel);

		
		new FileDrop( null, graphPanel, /*dragBorder,*/ new FileDrop.Listener()
	        {   
				Point coordinates =new Point(75, 75);
				
				public void filesDropped( java.io.File[] files )
	            {   
	        		for( int i = 0; i < files.length; i++ )
	                {   
	        			//TODO check filetype
	        			
	            		// add Source-Units
	        			controller.getUnitElements().add(UnitFactory.createSourceUnit(
	        					files[i].getAbsolutePath(), coordinates));
	        			graphPanel.repaint();
	                }   // end for: through each dropped file
	            }   // end filesDropped
	        }); // end FileDrop.Listener
		
		
		
		this.add(graphScrollpane, BorderLayout.CENTER);
		
		// area for selecting unit to insert them in the graphpanel
		JPanel unitSelectionPanel = new JPanel();
		unitSelectionPanel.setName("Insert filter");
		
		JPanel selectUnitPanel = new InsertUnitPanel();
		
		
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
	
}
