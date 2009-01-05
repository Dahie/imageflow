package imageflow.gui;
import graph.Node;
import helper.FileDrop;
import imageflow.backend.DelegatesController;
import imageflow.backend.GraphController;
import imageflow.backend.Model;
import imageflow.backend.ModelListener;
import imageflow.models.ConnectionList;
import imageflow.models.unit.CommentNode;
import imageflow.models.unit.UnitFactory;
import imageflow.models.unit.UnitList;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.ScrollPane;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import visualap.Delegate;
import actions.CheckGraphAction;
import actions.CopyUnitAction;
import actions.Example0_XML_Action;
import actions.Example1Action;
import actions.Example2Action;
import actions.PasteUnitAction;
import actions.ShowUnitParametersAction;

/**
 * 
 */

/**
 * depricated
 * @author danielsenff
 *
 */
public class Applicationframe extends JFrame {

	public static String TITLE = "ImageFlow for ImageJ";
	
	private UnitList units;
	private ConnectionList connections;
	private GraphController graphController;

	private JTextArea macroArea;

	private GraphPanel graphPanel;

	private ArrayList<Delegate> unitDelegates;
	
	

	
	/**
	 * 
	 */
	public Applicationframe(UnitList units, ConnectionList edges) {
		this.units = units;
		this.connections = edges;
		init();
	}
	
	public Applicationframe() {
		this.graphController = new GraphController();
		this.units = this.graphController.getUnitElements();
		this.connections = this.graphController.getConnections();
		init();
	}

	/**
	 * 
	 */
	private void init() {
		
		this.setTitle(TITLE);
		this.setName(TITLE);
		this.setSize(900, 450);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		addComponents();
		
		addMenu();
		
		this.setVisible(true);
		
		// register listeners
		registerModelListeners();
		
//		this.pack();
	}

	/**
	 * Register the Modellisteners.
	 */
	private void registerModelListeners() {
		// usually on startup this is empty
		for (Node node : units) {
			((CommentNode)node).addModelListener(
					new ModelListener () {
						public void modelChanged (final Model hitModel)	{
							graphPanel.invalidate();
						}
					});
		}
		
		units.addModelListener(new ModelListener() {
			public void modelChanged(Model model) {
				graphPanel.repaint();
			}
		});
		
		connections.addModelListener(new ModelListener() {
			public void modelChanged(Model model) {
				graphPanel.repaint();
			}
		});
	}

	/**
	 * Adds all components of
	 */
	private void addMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new Example0_XML_Action(graphController));
		fileMenu.add(new Example1Action(graphController));
		fileMenu.add(new Example2Action(graphController));
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new CopyUnitAction(graphPanel.getSelection(), graphController.getCopyNodesList()));
		editMenu.add(new PasteUnitAction(graphController.getCopyNodesList(), graphPanel));
		JMenu insertMenu = new InsertUnitMenu(graphPanel, unitDelegates);
		JMenu windowMenu = new JMenu("Window");
		JMenu helpMenu = new JMenu("Help");
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(insertMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
		
		menuBar.setVisible(true);
		setJMenuBar(menuBar);
	}

	/**
	 * Adds all components to the Jframe
	 */
	private void addComponents() {
		
		unitDelegates = DelegatesController.getInstance().getUnitDelegates();
		
		
		this.setLayout(new BorderLayout());
		
		//working area aka graphpanel
		
		GPanelPopup popup = new GPanelPopup(unitDelegates, graphController);
		graphPanel = new GraphPanel(unitDelegates , popup);
		popup.setActivePanel(graphPanel);
		graphPanel.setSize(400, 300);
		graphPanel.setNodeL(units);
		graphPanel.setEdgeL(connections);
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
	        			graphController.getUnitElements().add(UnitFactory.createSourceUnit(
	        					files[i].getAbsolutePath(), coordinates));
	        			graphPanel.repaint();
	                }   // end for: through each dropped file
	            }   // end filesDropped
	        }); // end FileDrop.Listener
		
		
		
		this.add(graphScrollpane, BorderLayout.CENTER);
		
		// area for selecting unit to insert them in the graphpanel
//		JPanel unitSelectionPanel = new JPanel();
//		unitSelectionPanel.setName("Insert filter");
//		
//		JPanel selectUnitPanel = new InsertUnitPanel();
//		
//		
//		JScrollPane selectUnitScrollpane = new JScrollPane(selectUnitPanel); 
//		unitSelectionPanel.add(selectUnitScrollpane);
		
		//properties of the selected node
		JPanel propertiesPanel = new JPanel();
		propertiesPanel.setName("Properties");
		JButton buttonPara = new JButton(new ShowUnitParametersAction(graphPanel.getSelection()));
		propertiesPanel.add(buttonPara, BorderLayout.SOUTH);
		
		
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
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		
		JButton buttonRun = new JButton(new RunMacroAction(graphController));
		buttonPanel.add(buttonRun);
		
		JButton buttoncheck = new JButton(new CheckGraphAction(graphController));
		buttonPanel.add(buttoncheck);
		macroPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JTabbedPane functionTabPane = new JTabbedPane();
//		functionTabPane.add(unitSelectionPanel);
		functionTabPane.add(propertiesPanel);
		functionTabPane.add(logPanel);
		functionTabPane.add(macroPanel);
		add(functionTabPane, BorderLayout.SOUTH);
	}

	public void setMacro(String macro) {
		this.macroArea.setText(macro);
	}
	
}
