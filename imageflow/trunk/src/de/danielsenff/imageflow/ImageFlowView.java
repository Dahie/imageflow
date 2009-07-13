package de.danielsenff.imageflow;

import ij.IJ;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.ScrollPane;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeNode;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import visualap.Node;
import visualap.Selection;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.controller.MacroFlowRunner;
import de.danielsenff.imageflow.gui.DelegatesPanel;
import de.danielsenff.imageflow.gui.GPanelPopup;
import de.danielsenff.imageflow.gui.GraphPanel;
import de.danielsenff.imageflow.gui.InsertUnitMenu;
import de.danielsenff.imageflow.models.Delegate;
import de.danielsenff.imageflow.models.Model;
import de.danielsenff.imageflow.models.ModelListener;
import de.danielsenff.imageflow.models.Selectable;
import de.danielsenff.imageflow.models.SelectionList;
import de.danielsenff.imageflow.models.SelectionListener;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.ProxyInput;
import de.danielsenff.imageflow.models.connection.ProxyOutput;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;
import de.danielsenff.imageflow.models.unit.UnitList;
import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;
import de.danielsenff.imageflow.tasks.ExportMacroTask;
import de.danielsenff.imageflow.tasks.GenerateMacroTask;
import de.danielsenff.imageflow.tasks.ImportGraphTask;
import de.danielsenff.imageflow.tasks.LoadFlowGraphTask;
import de.danielsenff.imageflow.tasks.RunMacroTask;
import de.danielsenff.imageflow.tasks.SaveFlowGraphTask;



/**
 * Controller of one workspace. Contains all necessary data of an opened 
 * graph.
 * @author danielsenff
 *
 */
public class ImageFlowView extends FrameView {

//	private static final Logger logger = Logger.getLogger(DocumentEditorView.class.getName());
	private JDialog aboutBox;
	
	private UnitList units;
	private ConnectionList connections;
	private GraphController graphController;

	private GraphPanel graphPanel;
	private HashMap<TreeNode,Delegate> unitDelegates;

	private File file;

	private boolean modified = false;
	private boolean selected = false;
	private boolean paste = false;

	private boolean showlog = false;

	private JCheckBoxMenuItem chkBoxDisplayUnit;

	private JCheckBoxMenuItem chkBoxCollapseIcon;

	

	
	
	/**
	 * @param app
	 */
	public ImageFlowView(final Application app) {
		super(app);
		
		this.graphController = new GraphController(); 
		this.units 			= graphController.getUnitElements();
		this.connections 	= graphController.getConnections();
		this.unitDelegates = DelegatesController.getInstance().getUnitDelegates();
		
		try {
			this.getFrame().setIconImage(
					ImageIO.read(this.getClass().getResourceAsStream(
							"/de/danielsenff/imageflow/resources/iw-icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		initComponents();
		
		setFile(new File("new document"));
	}



	private void initComponents() {
		addComponents();
		
		addMenu();
		
		// register listeners
		registerModelListeners();
		getApplication().addExitListener(new ConfirmExit());
	}
	

	/**
	 * Register the ModelListeners.
	 */
	private void registerModelListeners() {
		// usually on startup this is empty
		for (Node node : units) {
			UnitFactory.registerModelListener(node);
		}
		
		units.addModelListener(new ModelListener() {
			public void modelChanged(Model model) {
				graphPanel.repaint();
				setModified(true);
			}
		});
		
		connections.addModelListener(new ModelListener() {
			public void modelChanged(Model model) {
				graphPanel.repaint();
				setModified(true);
			}
		});
		
		getSelections().addSelectionListener(new SelectionListener() {
			public void selectionChanged(Selectable selections) {
				setSelected(selections.isSelected());
			}
		});
		
		setModified(false);
	}




	
	/**
	 * Adds all components of
	 */
	private void addMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(getAction("newDocument"));
		fileMenu.add(getAction("open"));
		
		fileMenu.add(getAction("save"));
		fileMenu.add(getAction("saveAs"));
		fileMenu.add(new JSeparator());
		fileMenu.add(getAction("importGraph"));
		fileMenu.add(getAction("export"));
		fileMenu.add(new JSeparator());
		fileMenu.add(getAction("generateMacro"));
		fileMenu.add(getAction("runMacro"));
		if(!IJ.isMacintosh()) {
			fileMenu.add(new JSeparator());
			fileMenu.add(getAction("quit"));
		} else {
			/*MRJApplicationUtils.registerQuitHandler(new MRJQuitHandler()
			   {
			      public void handleQuit()
			      {
			         SwingUtilities.invokeLater(new Runnable()
			         {
			            public void run()
			            {
			               if(promptTheUser())
			                  System.exit(0);
			            }
			         });
			         throw new IllegalStateException("Stop Pending User Confirmation");
			      }
			   });*/
		}
		
		
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(getAction("cut"));
		editMenu.add(getAction("copy"));
		editMenu.add(getAction("paste"));
		editMenu.add(getAction("unbind"));
		editMenu.add(getAction("delete"));
//		editMenu.add(getAction("clear"));
		
		editMenu.add(new JSeparator());

		this.chkBoxDisplayUnit = new JCheckBoxMenuItem(getAction("setDisplayUnit")); 
		editMenu.add(chkBoxDisplayUnit);

		this.chkBoxCollapseIcon = new JCheckBoxMenuItem(getAction("setUnitComponentSize"));
		editMenu.add(chkBoxCollapseIcon);

		editMenu.add(getAction("showUnitParameters"));
		editMenu.add(getAction("group"));
		
		
		
		JMenu viewMenu = new JMenu("View");
		viewMenu.add(new JCheckBoxMenuItem(getAction("alignElements")));
		viewMenu.add(new JCheckBoxMenuItem(getAction("setDrawGrid")));
		
		JMenu debugMenu = new JMenu("Debug");
		debugMenu.add(getAction("debugPrintNodes"));
		debugMenu.add(getAction("debugPrintNodeDetails"));
		debugMenu.add(getAction("debugPrintEdges"));
		debugMenu.add(getAction("debugDrawClonedWorkflow"));
		debugMenu.add(new JSeparator());
		debugMenu.add(getAction("exampleFlow1"));
		debugMenu.add(getAction("exampleFlow2"));
		
		JMenu insertMenu = new InsertUnitMenu(graphPanel, unitDelegates.values());
		
		/*JMenu windowMenu = new JMenu("Window");
		windowMenu.add(getAction("minimize"));*/
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(getAction("openDevblogURL"));
		helpMenu.add(getAction("openImageJURL"));
		helpMenu.add(new JSeparator());
        helpMenu.add(getAction("showAboutBox"));
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(insertMenu);
		menuBar.add(debugMenu);
//		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
		
		menuBar.setVisible(true);
		setMenuBar(menuBar);
	}

	private void updateMenu() {
		if(!getSelections().isEmpty() 
				&& getSelections().size() == 1 
				&& getSelections().get(0) instanceof UnitElement) {
			boolean isCollapsedIcon = 
				((UnitElement)getSelections().get(0)).getCompontentSize() == Size.SMALL 
				? true : false;
			this.chkBoxCollapseIcon.setSelected(isCollapsedIcon);
			
			boolean isDisplayUnit = ((UnitElement)getSelections().get(0)).isDisplay();
			this.chkBoxDisplayUnit.setSelected(isDisplayUnit);	
		} else {
			this.chkBoxCollapseIcon.setSelected(false);
			this.chkBoxDisplayUnit.setSelected(false);
		}
		
	}

	
	
	/**
	 * Adds all components to the JFrame
	 */
	private void addComponents() {
		
		ResourceMap resourceMap = getResourceMap();
		
		JPanel mainPanel = new JPanel();
		
		mainPanel.setLayout(new BorderLayout());
		
		//working area aka graphpanel
		ArrayList<Delegate> delegatesArrayList = new ArrayList<Delegate>();
		delegatesArrayList.addAll(unitDelegates.values());
		GPanelPopup popup = new GPanelPopup(graphController);
		
		
		graphPanel = new GraphPanel(delegatesArrayList, popup);
		popup.setActivePanel(graphPanel);
		graphPanel.setGraphController(graphController);
		graphPanel.setPreferredSize(new Dimension(400, 300));
		graphPanel.setSelections(getSelections());

		
		/*WorkspacePanel workspacePanel = new WorkspacePanel();
		workspacePanel.setPreferredSize(new Dimension(400, 300));
		workspacePanel.add(new UnitProcessingComponent(UnitFactory.createAddNoiseUnit()));*/
		
		
		ScrollPane graphScrollpane = new ScrollPane();
		graphScrollpane.add(graphPanel);
		
//		graphScrollpane.add(workspacePanel);

		/*
		new FileDrop( null, graphPanel,  new FileDrop.Listener()
	        {   
				Point coordinates = new Point(75, 75);
				
				public void filesDropped( java.io.File[] files )
	            {   
	        		for( int i = 0; i < files.length; i++ )
	                {   
	        			//TODO check filetype
	        			
	            		// add Source-Units
	        			graphController.getUnitElements().add(
	        					UnitFactory.createSourceUnit(files[i].getAbsolutePath(), coordinates));
	        			graphPanel.repaint();
	                }   // end for: through each dropped file
	            }   // end filesDropped
	        }); // end FileDrop.Listener
		*/
		
		
		
		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		buttonPanel.setLayout(flowLayout);
		
		JButton buttonRun = new JButton(getAction("runMacro"));
		buttonPanel.add(buttonRun);
		JCheckBox chkShowLog = new JCheckBox(resourceMap.getString("showLog"));
		resourceMap.injectComponent(chkShowLog);
		chkShowLog.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				boolean selected = ((JCheckBox)e.getSource()).isSelected();
				showlog = selected;
			}});
		buttonPanel.add(chkShowLog);
		

		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		JPanel delegatesPanel = new DelegatesPanel(this.units);
		sidePane.add(delegatesPanel, BorderLayout.CENTER);
		sidePane.add(buttonPanel, BorderLayout.PAGE_END);
		
		// setting of MinimumSize is required for drag-ability of JSplitPane
		sidePane.setMinimumSize(new Dimension((int)buttonRun.getPreferredSize().getWidth()+40, 100));
		graphScrollpane.setMinimumSize(new Dimension(100, 100));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePane, graphScrollpane);
		splitPane.setEnabled(true);
		splitPane.setOneTouchExpandable(true);
		// enables continuous redrawing while moving the JSplitPane-Divider
		splitPane.setContinuousLayout(true);
		
		mainPanel.add(splitPane , BorderLayout.CENTER);
		setComponent(mainPanel);
	}

	/**
	 * @return
	 */
	public GraphController getGraphController() {
		return graphController;
	}

	/**
	 * @param graphController
	 */
	public void setGraphController(final GraphController graphController) {
		this.graphController = graphController;
		this.units 			= graphController.getUnitElements();
		this.connections 	= graphController.getConnections();
		graphPanel.setGraphController(this.graphController);
		registerModelListeners();
		graphPanel.repaint();
	}
	
	public final SelectionList getSelections() {
		return graphController.getSelections();
	}

	
	/**
	 *  Set the bound file property and update the GUI.
	 * @param file 
     */
    public void setFile(final File file) {
    	File oldValue = this.file;
    	this.file = file;
    	String appId = getResourceMap().getString("Application.id");
    	getFrame().setTitle(file.getName() + " - " + appId);
    	firePropertyChange("file", oldValue, this.file);	
    }
    

    /**
     * True if the file value has been modified but not saved.  The 
     * default value of this property is false.
     * <p>
     * This is a bound read-only property.  
     * 
     * @return the value of the modified property.
     * @see #isModified
     */
    public boolean isModified() { 
        return this.modified;
    }
    
    /**
     * Returns true if a {@link UnitElement} is selected in the workflow.
     * @return
     */
    public boolean isSelected() { 
        return this.selected;
    }
   
    
    /**
     * Sets the modified flag.
     * @param modified
     */
    public void setModified(final boolean modified) {
        boolean oldValue = this.modified;
        this.modified = modified;
        // on program start, file may not be initialised
        if(file != null){
        	String appId = getResourceMap().getString("Application.id");
            String changed = modified ? "*" : "";
        	getFrame().setTitle(file.getName() + changed +" - " + appId);	
        }
        
        
        firePropertyChange("modified", oldValue, this.modified);
    }
	
    /**
     * Returns true if a {@link UnitElement} is selected.
     * @param selected
     */
    public void setSelected(final boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        updateMenu();
        
        firePropertyChange("selected", oldValue, this.selected);
    }


	/**
	 * @return the paste
	 */
	public boolean isPaste() {
		return paste;
	}

	/**
	 * @param hasPaste the hasPaste to set
	 */
	public void setPaste(boolean hasPaste) {
		this.paste = hasPaste;
	}
	
	/**
	 * @return the showlog
	 */
	public boolean isShowlog() {
		return showlog;
	}



	/**
	 * @param showlog the showlog to set
	 */
	public void setShowlog(boolean showlog) {
		this.showlog = showlog;
	}
	
    
	/**
	 * Returns the currently loaded workflow-file.
	 * @return
	 */
	public File getFile() {
		return file;
	}
	
	
	/*
	 * Action related stuff
	 * 
	 */
	
	
	
	/**
	 * convenient Example workflow
	 */
	@Action public void exampleFlow1() {
		graphController.setupExample1();
	}
	
	/**
	 * convenient Example workflow
	 */
	@Action public void exampleFlow2() {
		graphController.setupExample2();
	}
	
	/**
	 * convenient Example workflow
	 */
	@Action public void exampleFlowXML() {
		graphController.setupExample0_XML();
	}
	
	/**
	 * Converts the current workflow into a macro and executes it in ImageJ.
	 * @return
	 */
    @Action public RunMacroTask runMacro() {
        return new RunMacroTask(this.getApplication(), graphController, this.showlog);
    }

    /**
	 * Converts the current workflow into a macro and displays it.
	 * @return
	 */
    @Action public GenerateMacroTask generateMacro() {
        return new GenerateMacroTask(this.getApplication(), graphController);
    }
	
	
	/**
	 * TODO
	 * Live preview of data processing. Not yet fully implemented.
	 */
	@Action(enabledProperty = "selected")
	public void preview() {
		final UnitElement unit = (UnitElement) graphPanel.getSelection().get(0);
		final MacroFlowRunner mfr = new MacroFlowRunner(this.units);
		if(mfr.contains(unit)) {
			unit.setDisplay(true);
			mfr.getSubMacroFlowRunner(unit).generateMacro();
			unit.setDisplay(false);
		}
		
	    /*Task task = null;
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	task = new LoadFlowGraphTask(fc.getSelectedFile());
	    }*/
//	    return task;
	}
	
	/**
	 * A number of units are collapsed into one group-unit.
	 */
	@Action(enabledProperty = "selected")
	public void group() {
		
		if(getSelections().size() == 1
				&& getSelections().get(0) instanceof GroupUnitElement) {
			GroupUnitElement group = (GroupUnitElement) getSelections().get(0);
			graphController.ungroup(group);
		} else {
			try {
				graphController.group(getSelections());
			} catch (Exception e) {
				System.out.println("Group disallowed: No conistent connections between units");
				JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), 
						"This grouping of units is not permitted.  " + '\n' +
						"The connected units need to form a conistent branch.",
						"Grouping refused", 
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	/**
	 * A selected {@link GroupUnitElement} is exploded into it's original contents.
	 */
	@Action(enabledProperty = "selected")
	public void degroup() {
		if(getSelections().size() == 1
				&& getSelections().get(0) instanceof GroupUnitElement) {
			GroupUnitElement group = (GroupUnitElement) getSelections().get(0);
			graphController.ungroup(group);
		}
	}
	
	
	/**
	 * Import workflow from XML. 
	 * The current workflow will remain and the second workflow will be added without replacement
	 * @return
	 */
	@Action public ImportGraphTask importGraph() {
	    final JFileChooser fc = new JFileChooser();
	    final String filesDesc = getResourceMap().getString("flowXMLFileExtensionDescription");
	    fc.setFileFilter(new FlowXMLFilter(filesDesc));
	    
	    ImportGraphTask task = null;
	    final int option = fc.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	task = new ImportGraphTask(fc.getSelectedFile());
	    }
	    return task;
	}
	
	/**
	 * Action that toggles the display-status of a {@link UnitElement}
	 */
	@Action(enabledProperty = "selected")
	public void setDisplayUnit() {
		for (Object selectedElement : getSelections()) {
			if(selectedElement instanceof UnitElement) {
				final UnitElement unit = (UnitElement) selectedElement;
				boolean newDisplayStatus = unit.isDisplay() ? false : true;
				unit.setDisplay(newDisplayStatus);	
			}
		}
		graphPanel.repaint();
	}
	
	/**
	 * Changes the icon size of a {@link UnitElement}. 
	 */
	@Action(enabledProperty = "selected")
	public void setUnitComponentSize() {
		for (Object selectedElement : getSelections()) {
			if(selectedElement instanceof UnitElement) {
				final UnitElement unit = (UnitElement) selectedElement;
				
				Size newSize = (unit.getCompontentSize() == Size.BIG) ? Size.SMALL : Size.BIG;
				unit.setCompontentSize(newSize);				
			}
		}
		graphPanel.repaint();
	}
	
	/**
	 * Activates to draw a grid on the workspace.
	 */
	@Action
	public void setDrawGrid() {
		boolean drawGrid = graphPanel.isDrawGrid() ? false : true;
		graphPanel.setDrawGrid(drawGrid);
		graphPanel.repaint();
	}
	
	/**
	 * Activates element alignment on the workspace.
	 */
	@Action
	public void alignElements() {
		boolean align = graphPanel.isAlign() ? false : true;
		graphPanel.setAlign(align);
		graphPanel.repaint();
	}
	
	/**
	 * Removes all connections of the selected {@link UnitElement}
	 */
	@Action(enabledProperty = "selected")
	public void unbind() {
		final Selection<Node> selection = graphPanel.getSelection();
		for (final Node unit : selection) {
			units.unbindUnit((UnitElement)unit);	
		}
		graphPanel.repaint();
	}

	/**
	 * Deletes a selected {@link UnitElement}
	 */
	@Action(enabledProperty = "selected")
	public void delete() {
		final Selection<Node> selection = graphPanel.getSelection();
		for (final Node unit : selection) {
			graphController.removeNode(unit);
		}
		graphPanel.repaint();
	}
	
	/**
	 * Clears the workflow from all {@link UnitElement}s
	 */
	@Action	public void clear() {
		this.units.clear();
	}
	
	/**
	 * Cut {@link UnitElement} from the workflow.
	 */
	@Action(enabledProperty = "selected")
	public void cut() {
		final Selection<Node> selectedUnits = graphPanel.getSelection();
		final ArrayList<Node> copyUnitsList = graphController.getCopyNodesList();
		if (selectedUnits.size() > 0) {
			// il problema java.util.ConcurrentModificationException � stato risolto introducendo la lista garbage
			final HashSet<Connection> garbage = new HashSet<Connection>();
			copyUnitsList.clear();
			for (final Node t : selectedUnits) {
				/*for (Edge c : activePanel.getEdgeL())
					if ((c.from.getParent() == t)||(t == c.to.getParent()))
						garbage.add(c);
				copyUnitsList.add(t);
				activePanel.getNodeL().remove(t);*/
				copyUnitsList.add(t);
				graphController.removeNode(t);
			}
			for (final Connection c : garbage) {
				graphController.getConnections().remove(c);
//				activePanel.getEdgeL().remove(c);
			}
			selectedUnits.clear();
			setPaste(true);
		}
		graphPanel.repaint();
	}
	
	/**
	 * Copy {@link UnitElement} from the workflow.
	 */
	@Action(enabledProperty = "selected")
	public void copy() { 
		final Selection<Node> selectedNodes = graphPanel.getSelection();
		final ArrayList<Node> copyUnitsList = graphController.getCopyNodesList();
		if (!selectedNodes.isEmpty()) {
			copyUnitsList.clear();
			for (final Node t : selectedNodes) {
				Node clone;
				try {
					clone = t.clone();
					clone.setLabel(t.getLabel());
					copyUnitsList.add(clone);
					
				} catch (final CloneNotSupportedException e) {
					e.printStackTrace();
				}	
				setPaste(true);
			}
			
		}
	}
	
	/**
	 * Paste {@link UnitElement} into the workflow.
	 */
	@Action(enabledProperty = "paste")
	public void paste() {
		final Selection<Node> selectedUnits = graphPanel.getSelection();
		final ArrayList<Node> copyUnitsList = graphController.getCopyNodesList();
		if (!copyUnitsList.isEmpty()) {
			selectedUnits.clear();
			// this is added here so that the new pasted units are selected
			selectedUnits.addAll(copyUnitsList);
			copyUnitsList.clear();
			for (final Node t : selectedUnits) {
				try {
					t.setSelected(true);
//					UnitElement clone = (UnitElement)t.clone();
					// retain a copy, in case he pastes several times
					final Node clone = t.clone();
					clone.setLabel(t.getLabel());
					graphPanel.getNodeL().add(t);
					copyUnitsList.add(clone);
				} catch(final CloneNotSupportedException ex) {
//					ErrorPrinter.printInfo("CloneNotSupportedException");
				}
			}
			graphPanel.repaint();
		}
	}
	
	/**
	 * Opens a dialog with the available {@link Input} for the selected {@link UnitElement}.
	 */
	@Action(enabledProperty = "selected")
	public void showUnitParameters() {
		final Selection<Node> selectedUnits = graphPanel.getSelection();
		for (int i = 0; i < selectedUnits.size(); i++) {
			final UnitElement unit = (UnitElement)selectedUnits.get(i);
			unit.showProperties();
		}
	}


	/**
	 * Creates a new document and an empty workflow.
	 */
	@Action public void newDocument() {

		if(isModified()) {
			int optionSave = showSaveConfirmation();
			
			if(optionSave == JOptionPane.OK_OPTION) {
				save().run();
			}else if(optionSave == JOptionPane.CANCEL_OPTION) {
				return;
			}
	    }
		graphController.getUnitElements().clear();
	    setFile(new File("new document"));
	    this.setModified(false);
	    graphPanel.repaint();
	}
	
	/**
	 * Open a workflow file from hard drive.
	 * @return
	 */
	@Action public Task open() {
		if(isModified()) {
			final int optionSave = showSaveConfirmation();
			if(optionSave == JOptionPane.OK_OPTION) {
				save().run();
			}else if(optionSave == JOptionPane.CANCEL_OPTION) {
				return null;
			}
		} 
		final JFileChooser fc = new JFileChooser();
		final String filesDesc = getResourceMap().getString("flowXMLFileExtensionDescription");
		fc.setFileFilter(new FlowXMLFilter(filesDesc));

		Task task = null;
		final int option = fc.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			this.setModified(false);
			task = new LoadFlowGraphTask(fc.getSelectedFile());
		}
		return task;			
	}
	
	private int showSaveConfirmation() {
		return JOptionPane.showConfirmDialog(ImageFlow.getApplication().getMainFrame(), 
				"The workflow has modifications which were not yet saved."
				+'\n'+"Save changes now?",
				"Save changes?", 
				JOptionPane.INFORMATION_MESSAGE);
	}
    
    /**
     * Save the current workflow, either in existing file or in a new file.
     * @return
     */
    @Action(enabledProperty = "modified")
    public SaveFlowGraphTask save() {
    	if(getFile().exists()) {
    		return new SaveFlowGraphTask(getFile());	
    	} else 
    		return saveAs();
        
    }
    
    /**
     * @return
     */
    @Action
    public SaveFlowGraphTask saveAs() {
        final JFileChooser fc = createFileChooser("saveAsFileChooser");
        final String filesDesc = getResourceMap().getString("flowXMLFileExtensionDescription");
	    fc.setFileFilter(new FlowXMLFilter(filesDesc));
        
        
        final int option = fc.showSaveDialog(getFrame());
        SaveFlowGraphTask task = null;
        if (JFileChooser.APPROVE_OPTION == option) {
        	File selectedFile = fc.getSelectedFile();
        	if(!selectedFile.getName().toLowerCase().endsWith(".xml")) {
        		selectedFile = new File(selectedFile.getAbsoluteFile()+".xml");
        	}
        	
        	if(selectedFile.exists()) {
				final int response = JOptionPane.showConfirmDialog(this.getFrame(), 
						"This file already exists. Do you want to overwrite it?",
						"Overwrite existing file?", 
						JOptionPane.OK_CANCEL_OPTION);
				if (!(response == JOptionPane.OK_OPTION)) {
					return null;
				}
        	}
        	task = new SaveFlowGraphTask(selectedFile);
            
        }
        return task;
    }
    
    /**
	 * Converts the current workflow into a macro and saves this to file.
	 * @return
	 */
	@Action public ExportMacroTask export() {
		 JFileChooser fc = createFileChooser("saveAsFileChooser");
	        String filesDesc = getResourceMap().getString("imageJMacroFileExtensionDescription");
		    fc.setFileFilter(new ImageJMacroFilter(filesDesc));
	        
	        int option = fc.showSaveDialog(getFrame());
	        ExportMacroTask task = null;
	        if (JFileChooser.APPROVE_OPTION == option) {
	        	File selectedFile = fc.getSelectedFile();
	        	if(selectedFile.exists()) {
					int response = JOptionPane.showConfirmDialog(this.getFrame(), 
							"This file already exists. Do you want to overwrite it?",
							"Overwrite existing file?", 
							JOptionPane.OK_CANCEL_OPTION);
					if (!(response == JOptionPane.OK_OPTION)) {
						return null;
					}
	        	}
	        	task = new ExportMacroTask(selectedFile, graphController);
	            
	        }
		
	    return task;
	}
    
    private JFileChooser createFileChooser(String name) {
        JFileChooser fc = new JFileChooser(this.file);
        fc.setDialogTitle(getResourceMap().getString(name + ".dialogTitle"));
        return fc;
    }
	
    /**
     * Opens a dialog with the list of {@link UnitElement}s in the workflow.
     */
    @Action public void debugPrintNodes() {
    	final JDialog dialog = new JDialog();

    	final DefaultListModel lm = new DefaultListModel();
    	for (final Node node : graphController.getUnitElements()) {
    		lm.addElement(node);	
    	}
    	final JList list = new JList(lm);
    	
		dialog.add(list);
		dialog.pack();
		dialog.setVisible(true);
    }
    
    /**
     * Opens a dialog with the list of {@link Connection} in the workflow.
     */
    @Action public void debugPrintEdges() {
    	final JDialog dialog = new JDialog();

    	final DefaultListModel lm = new DefaultListModel();
    	for (final Connection connection : graphController.getUnitElements().getConnections()) {
    		lm.addElement(connection);	
    	}
    	final JList list = new JList(lm);
    	
		dialog.add(list);
		dialog.pack();
		dialog.setVisible(true);
    }
    
    @Action public void debugDrawClonedWorkflow() {
    	final JDialog dialog = new JDialog();
    	
    	GPanelPopup popup = new GPanelPopup(graphController);
    	GraphPanel gpanel = new GraphPanel(popup, graphController);
    	UnitList cloneUnitList = graphController.getUnitElements().clone();
    	gpanel.setNodeL(cloneUnitList);
    	gpanel.setEdgeL(cloneUnitList.getConnections());
    	
    	dialog.add(gpanel);
    	dialog.setSize(400, 300);
		dialog.setVisible(true);
    }
    
    /**
     * Opens a dialog with debugging information about the selected {@link UnitElement}
     */
    @Action(enabledProperty = "selected")
    public void debugPrintNodeDetails() {
    	final Selection<Node> selectedUnits = graphPanel.getSelection();
		for (int i = 0; i < selectedUnits.size(); i++) {
			final UnitElement unit = (UnitElement)selectedUnits.get(i);
    		final JDialog dialog = new JDialog();
    		dialog.setTitle(unit.getLabel());

    		// list parameters
        	final DefaultListModel lm = new DefaultListModel();
        	for (final Parameter parameter : unit.getParameters()) {
        		lm.addElement(parameter);	
        	}
        	for (final Input input : unit.getInputs()) {
        		lm.addElement(input);
        		lm.addElement("name:"+input.getName());
        		lm.addElement("datatype: "+input.getDataType());
        		if(input.getDataType() instanceof DataTypeFactory.Image)
        			lm.addElement("imagetype def:"
        					+((DataTypeFactory.Image)input.getDataType()).getImageBitDepth());
        		lm.addElement("connected to:");
        		lm.addElement(input.getConnection());
        	}
        	for (final Output output : unit.getOutputs()) {
        		lm.addElement(output);
        		lm.addElement("name:"+output.getName());
        		lm.addElement("datatype:"+output.getDataType());
        		if(output.getDataType() instanceof DataTypeFactory.Image)
        			lm.addElement("imagetype:"
        					+((DataTypeFactory.Image)output.getDataType()).getImageBitDepth());
        		lm.addElement("connected to:");
        		for (Connection conn : output.getConnections()) {
        			lm.addElement(conn);
				}
        		
        	}
        	final JList list = new JList(lm);
        	
    		dialog.add(list);
    		dialog.pack();
    		dialog.setVisible(true);	
		}
    	
    }
    
    @Action public void showGroupContents() {
    	if(getSelections().size() == 1 && getSelections().get(0) instanceof GroupUnitElement) {
    		final JDialog dialog = new JDialog();
    		
    		
    		GroupUnitElement group = (GroupUnitElement) getSelections().get(0);
//    		group.showGroupWindow();
    		dialog.setTitle(group.getLabel());
    		
    		GPanelPopup popup = new GPanelPopup( graphController);
        	GraphPanel gpanel = new GraphPanel(popup);
    		gpanel.setNodeL(group.getNodes());
    		popup.setActivePanel(gpanel);
        	gpanel.setEdgeL(group.getInternalConnections());
        	
        	dialog.add(gpanel);
        	dialog.setSize(400, 300);
    		dialog.setVisible(true);
    	}
    }
    
    /**
     * Opens the URL to the development blog of the project.
     */
    @Action 
    public void openDevblogURL() {
    	try {
			ij.plugin.BrowserLauncher.openURL("http://imageflow.danielsenff.de");
		} catch (final IOException e) { e.printStackTrace(); }
    }
    
    /**
     * Opens the URL to the ImageJ-website.
     */
    @Action 
    public void openImageJURL() {
    	try {
			ij.plugin.BrowserLauncher.openURL("http://rsb.info.nih.gov/ij/");
		} catch (final IOException e) { e.printStackTrace(); }
    }
    
    /**
     * Minimize this frame.
     */
    @Action
    public void minimize() {
    	this.getFrame().setState(Frame.ICONIFIED);
    }

    /**
     * Displays the About-dialog
     */
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            final JFrame mainFrame = ImageFlow.getApplication().getMainFrame();
            aboutBox = new ImageFlowAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
//        ImageFlow.getApplication().show(aboutBox);
        aboutBox.setVisible(true);
    }
    
	private javax.swing.Action getAction(String actionName) {
		ActionMap actionMap = getContext().getActionMap(ImageFlowView.class, this);
	    return actionMap.get(actionName);
	}

	/**
	 * The GraphPanel of this View-instance.
	 * @return
	 */
	public GraphPanel getGraphPanel() {
		return this.graphPanel;
	}
	

    private class ConfirmExit implements Application.ExitListener {
        public boolean canExit(EventObject e) {
            if (isModified()) {
//                String confirmExitText = getResourceMap().getString("confirmTextExit", getFile());
//                int option = JOptionPane.showConfirmDialog(getFrame(), confirmExitText);
            	int option = showSaveConfirmation();
                if(option == JOptionPane.YES_OPTION) {
                	save().run();
                } else if (option == JOptionPane.CANCEL_OPTION) {
                	return false;
                }  
            }
            return true;
        }
        public void willExit(EventObject e) { 
        	System.exit(0);
        }
        
        
    }


    /** This is a substitute for FileNameExtensionFilter, which is
     * only available on Java SE 6.
     */
    private static class FlowXMLFilter extends FileFilter {

        private final String description;

        FlowXMLFilter(String description) {
            this.description = description;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String fileName = f.getName();
            int i = fileName.lastIndexOf('.');
            if ((i > 0) && (i < (fileName.length() - 1))) {
                String fileExt = fileName.substring(i + 1);
                if ("xml".equalsIgnoreCase(fileExt)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
    

    /** This is a substitute for FileNameExtensionFilter, which is
     * only available on Java SE 6.
     */
    private static class ImageJMacroFilter extends FileFilter {

        private final String description;

        ImageJMacroFilter(String description) {
            this.description = description;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String fileName = f.getName();
            int i = fileName.lastIndexOf('.');
            if ((i > 0) && (i < (fileName.length() - 1))) {
                String fileExt = fileName.substring(i + 1);
                if ("xml".equalsIgnoreCase(fileExt)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }


}
