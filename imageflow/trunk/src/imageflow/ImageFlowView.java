package imageflow;

import graph.Edge;
import graph.Node;
import graph.Selection;
import imageflow.backend.DelegatesController;
import imageflow.backend.GraphController;
import imageflow.backend.MacroFlowRunner;
import imageflow.gui.DelegatesPanel;
import imageflow.gui.GPanelPopup;
import imageflow.gui.GraphPanel;
import imageflow.gui.InsertUnitMenu;
import imageflow.models.ConnectionList;
import imageflow.models.Input;
import imageflow.models.Model;
import imageflow.models.ModelListener;
import imageflow.models.Output;
import imageflow.models.Selectable;
import imageflow.models.SelectionList;
import imageflow.models.SelectionListener;
import imageflow.models.parameter.Parameter;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitFactory;
import imageflow.models.unit.UnitList;
import imageflow.tasks.ImportGraphTask;
import imageflow.tasks.LoadFlowGraphTask;
import imageflow.tasks.RunMacroTask;
import imageflow.tasks.SaveFlowGraphTask;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import javax.swing.JTextArea;
import javax.swing.tree.TreeNode;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import visualap.Delegate;
import visualap.ErrorPrinter;
import actions.CheckGraphAction;


/**
 * @author danielsenff
 *
 */
public class ImageFlowView extends FrameView {

//	private static final Logger logger = Logger.getLogger(DocumentEditorView.class.getName());
	private JDialog aboutBox;
	
	private UnitList units;
	private ConnectionList connections;
	private GraphController graphController;

	private JTextArea macroArea;
	private GraphPanel graphPanel;
	private HashMap<TreeNode,Delegate> unitDelegates;

	private File file;

	private boolean modified = false;
	private boolean selected = false;

	private SelectionList selections;

	
	
	public ImageFlowView(Application app) {
		super(app);
		
		ResourceMap resourceMap = getResourceMap();
		this.graphController = new GraphController(this);
		this.units = this.graphController.getUnitElements();
		this.connections = this.graphController.getConnections();
		this.selections = new SelectionList();
		this.unitDelegates = DelegatesController.getInstance().getUnitDelegates();
		
		initComponents();
	}



	private void initComponents() {
		addComponents();
		
		addMenu();
		
		// register listeners
		registerModelListeners();
	}
	

	/**
	 * Register the Modellisteners.
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
		
		selections.addSelectionListener(new SelectionListener() {
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
		fileMenu.add(getAction("generateMacro"));
		fileMenu.add(getAction("save"));
		fileMenu.add(getAction("saveAs"));
		fileMenu.add(getAction("importGraph"));
//		if(!IJ.isMacintosh()) {
			fileMenu.add(new JSeparator());
			fileMenu.add(getAction("quit"));
//		}
		
		
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(getAction("cut"));
		editMenu.add(getAction("copy"));
		editMenu.add(getAction("paste"));
		editMenu.add(getAction("delete"));
		editMenu.add(getAction("clear"));
		editMenu.add(new JSeparator());
		editMenu.add(getAction("setDisplayUnit"));
		editMenu.add(getAction("showUnitParameters"));
		
		JMenu debugMenu = new JMenu("Debug");
		debugMenu.add(getAction("debugPrintNodes"));
		debugMenu.add(getAction("debugPrintNodeDetails"));
		debugMenu.add(new JSeparator());
		debugMenu.add(getAction("exampleFlow1"));
		debugMenu.add(getAction("exampleFlow2"));
//		debugMenu.add(getAction("exampleFlowXML"));
		
		JMenu insertMenu = new InsertUnitMenu(graphPanel, unitDelegates.values());
		
		JMenu windowMenu = new JMenu("Window");
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(getAction("openDevblogURL"));
		helpMenu.add(getAction("openImageJURL"));
		helpMenu.add(new JSeparator());
        helpMenu.add(getAction("showAboutBox"));
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(insertMenu);
		menuBar.add(debugMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
		
		menuBar.setVisible(true);
		setMenuBar(menuBar);
	}

	/**
	 * Adds all components to the Jframe
	 */
	private void addComponents() {
		
		JPanel mainPanel = new JPanel();
		
		mainPanel.setLayout(new BorderLayout());
		
		//working area aka graphpanel
		ArrayList<Delegate> delegatesArrayList = new ArrayList<Delegate>();
		delegatesArrayList.addAll(unitDelegates.values());
		GPanelPopup popup = new GPanelPopup(unitDelegates.values(), graphController);
//		GPanelPopup popup = new GPanelPopup(graphController);
		
		
		graphPanel = new GraphPanel(delegatesArrayList, popup);
		popup.setActivePanel(graphPanel);
		graphPanel.setSize(400, 300);
		graphPanel.setGraphController(graphController);
		graphPanel.setPreferredSize(new Dimension(400, 300));
		graphPanel.setSelections(this.selections);

		
		
		ScrollPane graphScrollpane = new ScrollPane();
		graphScrollpane.add(graphPanel);

		/*
		new FileDrop( null, graphPanel,  new FileDrop.Listener()
	        {   
				Point coordinates =new Point(75, 75);
				
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
		JButton buttonRun = new JButton(getAction("generateMacro"));
		buttonPanel.add(buttonRun);
//		JButton buttoncheck = new JButton(new CheckGraphAction(graphController));
//		buttonPanel.add(buttoncheck);

		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		JPanel delegatesPanel = new DelegatesPanel(this.units);
		sidePane.add(delegatesPanel, BorderLayout.CENTER);
		sidePane.add(buttonPanel, BorderLayout.PAGE_END);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setLeftComponent(sidePane);
		splitPane.setRightComponent(graphScrollpane);
		splitPane.setEnabled(true);
//		splitPane.setDividerLocation(200);
		splitPane.setOneTouchExpandable(true);
		
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
		this.units = this.graphController.getUnitElements();
		this.connections = this.graphController.getConnections();
		graphPanel.setGraphController(this.graphController);
		graphPanel.repaint();
	}
	
	/**
	 *  Set the bound file property and update the GUI.
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
    
    public boolean isSelected() { 
        return this.selected;
    }
   
    
    public void setModified(final boolean modified) {
        boolean oldValue = this.modified;
        this.modified = modified;
        firePropertyChange("modified", oldValue, this.modified);
    }
	
    public void setSelected(final boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        firePropertyChange("selected", oldValue, this.selected);
    }
    
	public File getFile() {
		return file;
	}
	
	
	/*
	 * Action related stuff
	 * 
	 */
	
	
	
	@Action public void exampleFlow1() {
		graphController.setupExample1();
	}
	
	@Action public void exampleFlow2() {
		graphController.setupExample2();
	}
	
	@Action public void exampleFlowXML() {
		graphController.setupExample0_XML();
	}
	
	@Action public Task generateMacro() {
	    return new RunMacroTask(getApplication(), graphController);
	}
	
	@Action public Task exportMacro() {
	    return new RunMacroTask(getApplication(), graphController);
	}
	

	
	@Action public void newDocument() {
	    graphController.getUnitElements().clear();
	    this.file = new File("new document");
	}
	
	@Action public Task open() {
	    JFileChooser fc = new JFileChooser();

	    Task task = null;
	    int option = fc.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	task = new LoadFlowGraphTask(fc.getSelectedFile());
	    }
	    return task;
	}
	
	@Action(enabledProperty = "selected")
	public void preview() {
		UnitElement unit = (UnitElement) graphPanel.getSelection().get(0);
		MacroFlowRunner mfr = new MacroFlowRunner(this.units);
		if(mfr.contains(unit)) {
			unit.setDisplayUnit(true);
			mfr.getSubMacroFlowRunner(unit).generateMacro();
			unit.setDisplayUnit(false);
		}
		
	    /*Task task = null;
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	task = new LoadFlowGraphTask(fc.getSelectedFile());
	    }*/
//	    return task;
	}
	
	@Action public Task importGraph() {
	    JFileChooser fc = new JFileChooser();

	    Task task = null;
	    int option = fc.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	    	task = new ImportGraphTask(fc.getSelectedFile());
	    }
	    return task;
	}
	
	@Action(enabledProperty = "selected")
	public void setDisplayUnit() {
		for (Iterator iterator = selections.iterator(); iterator.hasNext();) {
			UnitElement unit = (UnitElement) iterator.next();
			if(unit.isDisplayUnit()) {
				// if it is a displayUnit, deactivate
				unit.setDisplayUnit(false);
			} else {
				// if it is a displayUnit, activate
				unit.setDisplayUnit(true);
			}
		}
		graphPanel.repaint();
	}
	
	@Action(enabledProperty = "selected")
	public void unbind() {
		Selection<Node> selection = graphPanel.getSelection();
		for (Node unit : selection) {
			units.unbindUnit((UnitElement)unit);	
		}
	}

	@Action(enabledProperty = "selected")
	public void delete() {
		Selection<Node> selection = graphPanel.getSelection();
		for (Node unit : selection) {
			graphController.removeNode(unit);
		}
		graphPanel.repaint();
	}
	
	@Action	public void clear() {
		this.units.clear();
	}
	
	@Action(enabledProperty = "selected")
	public void cut() {
		Selection<Node> selectedUnits = graphPanel.getSelection();
		ArrayList<Node> copyUnitsList = graphController.getCopyNodesList();
		if (selectedUnits.size() > 0) {
			// il problema java.util.ConcurrentModificationException è stato risolto introducendo la lista garbage
			HashSet<Edge> garbage = new HashSet<Edge>();
			copyUnitsList.clear();
			for (Node t : selectedUnits) {
				/*for (Edge c : activePanel.getEdgeL())
					if ((c.from.getParent() == t)||(t == c.to.getParent()))
						garbage.add(c);
				copyUnitsList.add(t);
				activePanel.getNodeL().remove(t);*/
				copyUnitsList.add(t);
				graphController.removeNode(t);
			}
			for (Edge c : garbage) {
				graphController.getConnections().remove(c);
//				activePanel.getEdgeL().remove(c);
			}
			selectedUnits.clear();
		}
	}
	
	@Action(enabledProperty = "selected")
	public void copy() { 
		Selection<Node> selectedNodes = graphPanel.getSelection();
		ArrayList<Node> copyUnitsList = graphController.getCopyNodesList();
		if (!selectedNodes.isEmpty()) {
			copyUnitsList.clear();
			for (Node t : selectedNodes) {
				Node clone;
				try {
					clone = t.clone();
					clone.setLabel(t.getLabel());
					copyUnitsList.add(clone);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}	
				
			}
		}
	}
	
	@Action	public void paste() {
		Selection<Node> selectedUnits = graphPanel.getSelection();
		ArrayList<Node> copyUnitsList = graphController.getCopyNodesList();
		if (!copyUnitsList.isEmpty()) {
			selectedUnits.clear();
			// this is added here so that the new pasted units are selected
			selectedUnits.addAll(copyUnitsList);
			copyUnitsList.clear();
			for (Node t : selectedUnits) {
//			for (Node t : copyUnitsList) {
				try {
					
//					UnitElement clone = (UnitElement)t.clone();	
					Node clone = t.clone();
					clone.setLabel(t.getLabel());
					graphPanel.getNodeL().add(t, t.getLabel());
					copyUnitsList.add(clone);
				} catch(CloneNotSupportedException ex) {
					ErrorPrinter.printInfo("CloneNotSupportedException");
				}
			}
			graphPanel.repaint();
		}
	}
	
	@Action(enabledProperty = "selected")
	public void showUnitParameters() {
		Selection<Node> selectedUnits = graphPanel.getSelection();
		for (int i = 0; i < selectedUnits.size(); i++) {
			UnitElement unit = (UnitElement)selectedUnits.get(i);
			unit.showProperties();
		}
	}
	
    @Action
    public Task runMacro() {
        return new RunMacroTask(this.getApplication(), graphController);
    }
    
    @Action(enabledProperty = "modified")
    public Task save() {
        return new SaveFlowGraphTask(getFile());
    }
    
    @Action(enabledProperty = "modified")
    public Task saveAs() {
        JFileChooser fc = createFileChooser("saveAsFileChooser");
        int option = fc.showSaveDialog(getFrame());
        Task task = null;
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
        	task = new SaveFlowGraphTask(selectedFile);
            
        }
        return task;
    }
    
    private JFileChooser createFileChooser(String name) {
        JFileChooser fc = new JFileChooser(this.file);
        fc.setDialogTitle(getResourceMap().getString(name + ".dialogTitle"));
        String textFilesDesc = getResourceMap().getString("txtFileExtensionDescription");
//        fc.setFileFilter(new TextFileFilter(textFilesDesc));
        return fc;
    }
	
    @Action public void debugPrintNodes() {
    	JDialog dialog = new JDialog();

    	DefaultListModel lm = new DefaultListModel();
    	for (Node node : graphController.getUnitElements()) {
    		lm.addElement(node);	
    	}
    	JList list = new JList(lm);
    	
		dialog.add(list);
		dialog.pack();
		dialog.setVisible(true);
    }
    
    @Action(enabledProperty = "selected")
    public void debugPrintNodeDetails() {
    	Selection<Node> selectedUnits = graphPanel.getSelection();
		for (int i = 0; i < selectedUnits.size(); i++) {
			UnitElement unit = (UnitElement)selectedUnits.get(i);
    		JDialog dialog = new JDialog();

    		// list parameters
        	DefaultListModel lm = new DefaultListModel();
        	for (Parameter parameter : unit.getParameters()) {
        		lm.addElement(parameter);	
        	}
        	for (Input input : unit.getInputs()) {
        		lm.addElement(input);
        		lm.addElement("name:"+input.getName());
        		lm.addElement("imagetype:"+input.getImageBitDepth());
        	}
        	for (Output output : unit.getOutputs()) {
        		lm.addElement(output);
        		lm.addElement("name:"+output.getName());
        		lm.addElement("imagetype:"+output.getImageBitDepth());
        	}
        	JList list = new JList(lm);
        	
    		dialog.add(list);
    		dialog.pack();
    		dialog.setVisible(true);	
		}
    	
    }
    
    @Action 
    public void openDevblogURL() {
    	try {
			ij.plugin.BrowserLauncher.openURL("http://imageflow.danielsenff.de");
		} catch (IOException e) { e.printStackTrace(); }
    }
    @Action 
    public void openImageJURL() {
    	try {
			ij.plugin.BrowserLauncher.openURL("http://rsb.info.nih.gov/ij/");
		} catch (IOException e) { e.printStackTrace(); }
    }
    

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = ImageFlow.getApplication().getMainFrame();
            aboutBox = new ImageFlowAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        ImageFlow.getApplication().show(aboutBox);
    }
    
	private javax.swing.Action getAction(String actionName) {
//		ActionMap actionMap = Application.getInstance(ImageFlow.class).getContext().getActionMap(ImageFlowView.class, this);
		ActionMap actionMap = getContext().getActionMap(ImageFlowView.class, this);
		initActions(actionMap);
	    return actionMap.get(actionName);
	}

	private void initActions(ActionMap actionMap) {
		Selection<Node> selection = graphPanel.getSelection();
		ArrayList<Node> copyList = graphController.getCopyNodesList();
		
//		actionMap.put("checkGraph",	new CheckGraphAction(graphController));
	}



	public GraphPanel getGraphPanel() {
		return this.graphPanel;
	}

}
