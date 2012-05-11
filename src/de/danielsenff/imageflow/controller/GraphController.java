/**
 * Copyright (C) 2008-2010 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.danielsenff.imageflow.controller;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditListener;

import visualap.GPanel;
import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.gui.Dashboard;
import de.danielsenff.imageflow.gui.GraphPanel;
import de.danielsenff.imageflow.imagej.MacroFlowRunner;
import de.danielsenff.imageflow.io.WorkflowXMLBuilder;
import de.danielsenff.imageflow.models.Model;
import de.danielsenff.imageflow.models.NodeListener;
import de.danielsenff.imageflow.models.SelectionList;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.ProxyInput;
import de.danielsenff.imageflow.models.connection.ProxyOutput;
import de.danielsenff.imageflow.models.datatype.ImageDataType;
import de.danielsenff.imageflow.models.delegates.UnitDelegate;
import de.danielsenff.imageflow.models.parameter.ParamChangeListener;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.unit.CommentNode;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.Node;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;
import de.danielsenff.imageflow.models.unit.UnitList;




/**
 * Controller for Workflows. 
 * @author Daniel Senff
 *
 */
public class GraphController{

	private UnitList nodes;
	/**
	 * List which stores copied Nodes.
	 */
	protected ArrayList<Node> copyNodesList;

	/**
	 * List of selected units
	 */
	private SelectionList selections;


	private UndoableEditListener listener;
	private ExecuteWorkflowListener executionListener;

	/**
	 * 
	 */
	public GraphController() {
		this.nodes = new UnitList();
		this.copyNodesList = new ArrayList<Node>();
		this.selections = new SelectionList();
		this.executionListener = new ExecuteWorkflowListener(this);
	}

	/**
	 * @return the unitElements
	 */
	public UnitList getUnitElements() {
		return this.nodes;
	}


	/**
	 * Generates the executable Macro based on the current graph.
	 * @param extendedMacro defines, if callback functions are put into macro code
	 * @param silent defines, if any results should be displayed after execution, or if the workflow should be executed silently 
	 * @return
	 */
	public String generateMacro(boolean extendedMacro, boolean silent) {
		final MacroFlowRunner macroFlowRunner = new MacroFlowRunner(this.nodes);
		return macroFlowRunner.generateMacro(extendedMacro, silent);
	}

	/**
	 * Returns current the {@link ConnectionList}
	 * @return
	 */
	public ConnectionList getConnections() {
		return this.nodes.getConnections();
	}


	/**
	 * Selections
	 * @return
	 */
	public SelectionList getSelections() {
		return this.selections;
	}

	/**
	 * Get the List of copied {@link Node};
	 * @return
	 */
	public ArrayList<Node> getCopyNodesList() {
		return copyNodesList;
	}

	/**
	 * Set the UndoableEditListener.
	 * @param l
	 */
	public void addUndoableEditListener(UndoableEditListener l) {
		listener = l; // Should ideally throw an exception if listener != null
	}

	/**
	 * Remove the UndoableEditListener.
	 * @param l
	 */
	public void removeUndoableEditListener(UndoableEditListener l) {
		listener = null;
	}

	/**
	 * Adds the given {@link Node} to the workspace. 
	 * @param node
	 * @return
	 */
	public Node addNode(Node node) {
		final ImageFlowView ifView = ((ImageFlowView)ImageFlow.getApplication().getMainView());
		final GraphPanel graphPanel = ifView.getGraphPanel();

		if (node instanceof UnitElement) {
			((UnitElement) node).addModelListener(new NodeListener(graphPanel, ifView));
			((UnitElement) node).addParamChangeListerToAllParameters(executionListener);
		}

		getUnitElements().add(node);
		return node;
	}

	/**
	 * Creates a new Node based on the given UnitDelegate and 
	 * adds it at the given location to the workspace.
	 * @param delegate
	 * @param point
	 * @return
	 */
	public Node addNode(UnitDelegate delegate, Point point) {
		UnitElement unit = delegate.buildUnit(point);
		return this.addNode(unit);
	}

	/**
	 * Removes the {@link UnitElement} from the unitList and its Connections.
	 * @param node 
	 * @return
	 */
	public boolean removeNode(final Node node) {
		// TODO remove from dashboard
		return nodes.remove(node);
	}

	/**
	 * Ungroup the contents of a GroupUnit
	 * @param group
	 */
	public void ungroup(final GroupUnitElement group) {
		ungroup(group, getUnitElements());
	}

	/**
	 * @param group
	 * @param units
	 */
	public static void ungroup(final GroupUnitElement group, final UnitList units) {

		int deltaX = group.getOrigin().x - 25;
		int deltaY = group.getOrigin().y - 25;
		for (Node node : group.getNodes()) {
			int x = node.getOrigin().x, y = node.getOrigin().y;
			node.getOrigin().setLocation(x+deltaX, y+deltaY);

			for (Input input : ((UnitElement)node).getInputs()) {
				input.setLocked(false);
			}
			for (Output output : ((UnitElement)node).getOutputs()) {
				output.setLocked(false);
			}
		}



		units.addAll(group.getNodes());
		ConnectionList connections = units.getConnections();

		/*
		 * reconnect inputs
		 */
		for (Input input : group.getInputs()) {
			if(input instanceof ProxyInput) {
				ProxyInput pInput = (ProxyInput)input;
				if(pInput.isConnected()) {
					Output connectedOutput = pInput.getFromOutput();
					Input originalInput = pInput.getEmbeddedInput();

					Connection connection = new Connection(connectedOutput, originalInput);
					connections.add(connection);
				}
			}
		}

		/*
		 *  reconnect outputs
		 */
		Collection<Connection> tmpConn = new Vector<Connection>();
		for (Output output : group.getOutputs()) {
			if(output instanceof ProxyOutput) {
				ProxyOutput pOutput = (ProxyOutput)output;
				if(pOutput.isConnected()) {
					Output originalOutput = pOutput.getEmbeddedOutput();
					if(originalOutput.getDataType() instanceof ImageDataType) {
						ImageDataType imageDataType = (ImageDataType)originalOutput.getDataType();
						imageDataType.setParentUnitElement(originalOutput.getParent());
						imageDataType.setParentPin(originalOutput);
					}

					for (Connection	connection : pOutput.getConnections()) {
						Connection newConn = new Connection(originalOutput, connection.getInput());
						tmpConn.add(newConn);
					}
				}
			}
		}
		// write connections into actual connectionlist
		for (Connection connection : tmpConn) {
			connections.add(connection);	
		}

		/*
		 * reconnect connection within the group
		 */

		for (Connection connection : group.getInternalConnections()) {
			connections.add(connection);
		}

		units.remove(group);
	}

	public void group() throws Exception {
		if(!getSelections().isEmpty()) {
			GroupUnitElement group = new GroupUnitElement(new Point(34, 250), "Group");
			group.putUnits(getSelections(), getUnitElements());
			getUnitElements().add(group);
			selections.clear();
			selections.add(group);	
		}
		/*if (listener != null) {
			listener.undoableEditHappened(new UndoableEditEvent(this, new UndoableEdit() {

				public boolean addEdit(UndoableEdit anEdit) {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean canRedo() {	return true; }

				public boolean canUndo() {	return true; }

				public void die() {}

				public String getPresentationName() {
					return null;
				}

				public String getRedoPresentationName() {
					return "Regroup";
				}

				public String getUndoPresentationName() {
					return "Ungroup";
				}

				public boolean isSignificant() { return true; }

				public void redo() throws CannotRedoException {
					// TODO Auto-generated method stub

				}

				public boolean replaceEdit(UndoableEdit anEdit) {
					// TODO Auto-generated method stub
					return false;
				}

				public void undo() throws CannotUndoException {
					ungroup(group);
				}

			}));
		}*/
	}

	/**
	 * Reads the contents of a flow-XML-document.
	 * @param url The document to load.
	 */
	public void read(final URL url) {
		WorkflowXMLBuilder workflowbuilder = new WorkflowXMLBuilder(this);
		workflowbuilder.read(url);
	}

	/**
	 * Write the workflow to a XML-file
	 * @param file
	 * @throws IOException
	 */
	public void write(final File file) throws IOException {
		WorkflowXMLBuilder workflowbuilder = new WorkflowXMLBuilder(this);
		workflowbuilder.write(file);
	}

	/** 
	 * TODO remove this, update unit tests
	 */
	public void setupExample1() {
		////////////////////////////////////////////////////////
		// setup of units
		////////////////////////////////////////////////////////
		DelegatesController delegatesController = DelegatesController.getInstance();

		final UnitElement sourceUnit = delegatesController.getDelegate("Image Source").buildUnit(new Point(30, 100));
		final UnitElement blurUnit = delegatesController.getDelegate("Gaussian Blur").buildUnit(new Point(180, 50));
		final UnitElement mergeUnit = delegatesController.getDelegate("Image Calculator").buildUnit(new Point(320, 100));
		final UnitElement noiseUnit = delegatesController.getDelegate("Add Noise").buildUnit(new Point(450, 100));
		noiseUnit.setDisplay(true);

		CommentNode comment = UnitFactory.createComment("my usual example", new Point(30, 40));

		// some mixing, so they are not in order
		nodes.add(noiseUnit);
		nodes.add(blurUnit);
		nodes.add(sourceUnit);
		nodes.add(mergeUnit);
		nodes.add(comment);


		////////////////////////////////////////////////////////
		// setup the connections
		////////////////////////////////////////////////////////

		// add six connections
		// the conn is established on adding
		// fromUnit, fromOutputNumber, toUnit, toInputNumber
		Connection con;
		con = new Connection(sourceUnit,1,blurUnit,1);
		nodes.addConnection(con);
		con = new Connection(blurUnit,1,mergeUnit,1);
		nodes.addConnection(con);
		con = new Connection(sourceUnit,1,mergeUnit,2);
		nodes.addConnection(con);
		con = new Connection(mergeUnit,1,noiseUnit,1);
		nodes.addConnection(con);
	}

	private void showExampleLoadError(final Exception e) {
		final int type = JOptionPane.ERROR_MESSAGE;
		JOptionPane.showMessageDialog(
				ImageFlow.getApplication().getMainFrame(),
				"An error occured while loading the example!", "Could not load example", type);
	}

	/**
	 * Given the node is part of the workflow, return the subgraph
	 * hat fulfills all dependencies of this node.
	 * @param node
	 */
	public UnitList getSubgraph(UnitElement unit) {
		GraphController subgraph = new GraphController();
		UnitList subgraphList = new UnitList();
		UnitElement dependentUnit;
		try {
			recursiveAddUnitDependencies(unit, subgraphList);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return subgraphList;

	}

	private void recursiveAddUnitDependencies(UnitElement unit,
			UnitList subgraphList) throws CloneNotSupportedException {
		UnitElement dependentUnit;
		if (unit.hasRequiredInputsConnected()) {
			subgraphList.add(unit.clone());
			for (Input input : unit.getInputs()) {
				dependentUnit = input.getFromUnit();
				recursiveAddUnitDependencies(dependentUnit, subgraphList);
			}
		} else {
			System.err.println("subgraph not complete");
		}
	}

	/**
	 * Sets the data object as the result output for the Node and Output with the given ID
	 * @param nodeID
	 * @param outputId
	 * @param data
	 */
	public void setOutputData(int nodeID, int outputId, Object data) {
		// get the Node and Output object with the given IDs
		Node node = getUnitElements().getNodeByID(nodeID);
		if (node instanceof UnitElement) {
			System.out.println(node);
			Output output = ((UnitElement)node).getOutput(outputId-1);
			// write the data into the output object
			output.setOutputObject(data);
		}
	}

	private Dashboard dashboardPanel = null;

	public Dashboard getDashboard() {
		return this.dashboardPanel;
	}
	public void setDashboard(Dashboard dashboard) {
		this.dashboardPanel = dashboard;
	}

	public boolean addWidget(UnitElement selectedElement) {
		if (this.dashboardPanel != null) {
			this.dashboardPanel.addWidget(selectedElement);
			return true;
		} else
			return false;
	}

	public boolean addWidget(UnitElement selectedElement, Point location) {
		if (this.dashboardPanel != null) {
			this.dashboardPanel.addWidget(selectedElement, location);
			return true;
		} else
			return false;
	}
	
	public boolean addPreviewWidget(UnitElement selectedElement) {
		if (this.dashboardPanel != null) {
			this.dashboardPanel.addPreviewWidget(selectedElement);
			return true;
		} else
			return false;
	}

	public boolean addPreviewWidget(UnitElement selectedElement, Point location) {
		if (this.dashboardPanel != null) {
			this.dashboardPanel.addPreviewWidget(selectedElement, location);
			return true;
		} else
			return false;
	}
}

