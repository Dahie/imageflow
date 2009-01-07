/**
 * 
 */
package imageflow.models.unit;

import graph.GList;
import graph.Node;
import imageflow.backend.Model;
import imageflow.backend.ModelListener;
import imageflow.models.Connection;
import imageflow.models.ConnectionList;
import imageflow.models.Input;
import imageflow.models.Output;
import imageflow.models.unit.UnitElement.Type;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**
 * List holding all {@link UnitElement}s. For the moment this is just an empty wrapper.
 * @author danielsenff
 *
 */
public class UnitList extends GList<Node> implements Model {

	private ConnectionList connections;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8204689428123811757L;
	private ArrayList<ModelListener> listeners;

	/**
	 * 
	 */
	public UnitList() {
		this.listeners = new ArrayList<ModelListener>();
		this.connections = new ConnectionList();
	}

	/**
	 * Reads the contents of a flow-XML-file.
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public void readUnitList(File file) throws FileNotFoundException {
		UnitElement[] unitElement = null;

		if(!file.exists()) throw new FileNotFoundException("reading failed, the file as not found");
		int numUnits = 0;

		// setup of units
		try {
			System.out.println("Reading xml-description");

			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(file);

			Element root = doc.getRootElement();

			// read units
			Element unitsElement = root.getChild("Units");

			if (unitsElement != null) {  
				List<Element> unitsList = unitsElement.getChildren();
				Iterator<Element> unitsIterator = unitsList.iterator();
				numUnits = unitsList.size() + 1;
				unitElement = new UnitElement[numUnits];

				// loop Ÿber alle Units
				while (unitsIterator.hasNext()) { 
					Element actualUnitElement = (Element) unitsIterator.next();
					int unitID 		= Integer.parseInt(actualUnitElement.getChild("UnitID").getValue());
					int xPos 		= Integer.parseInt(actualUnitElement.getChild("XPos").getValue());
					int yPos		= Integer.parseInt(actualUnitElement.getChild("YPos").getValue());
					UnitDescription unitDescription = new UnitDescription(actualUnitElement.getChild("UnitDescription"));

					// create unit
					unitElement[unitID] = UnitFactory.createProcessingUnit(unitDescription, new Point(xPos, yPos));
					unitElement[unitID].setDisplayUnit(unitDescription.getIsDisplayUnit());
					add(unitElement[unitID]);
				}
			}

			// read connections
			Element connectionsElement = root.getChild("Connections");

			if (connectionsElement != null) {  
				List<Element> connectionsList = connectionsElement.getChildren();
				Iterator<Element> connectionsIterator = connectionsList.iterator();

				// loop Ÿber alle connections
				while (connectionsIterator.hasNext()) { 
					Element actualConnectionElement = (Element) connectionsIterator.next();
					int fromUnitID 			= Integer.parseInt(actualConnectionElement.getChild("FromUnitID").getValue());
					int fromOutputNumber 	= Integer.parseInt(actualConnectionElement.getChild("FromOutputNumber").getValue());
					int toUnitID 			= Integer.parseInt(actualConnectionElement.getChild("ToUnitID").getValue());
					int toInputNumber 		= Integer.parseInt(actualConnectionElement.getChild("ToInputNumber").getValue());
					Connection con = new Connection(unitElement[fromUnitID], fromOutputNumber, unitElement[toUnitID], toInputNumber);
					addConnection(con);
				}
			}
		}
		catch (Exception e) {
			System.err.println("Invalid XML-File!");
			e.printStackTrace();
		}	
	}


	@Override
	public boolean add(Node node, String label) {
		notifyModelListeners();
		return super.add(node, label);
	}

	@Override
	public boolean add(Node o) {
		notifyModelListeners();
		return super.add(o);
	}


	@Override
	public Node remove(int index) {
		notifyModelListeners();
		return super.remove(index);
	}

	/**
	 * Checks all Inputs if they are connected or not. 
	 * @param networkOK
	 * @return
	 */
	public boolean areAllInputsConnected() {
		// check inputs of all units
		for (final Node node : this) {
			if(node instanceof UnitElement) {
				final UnitElement unit = (UnitElement) node;

				// does the unit actually have inputs?
				if(unit.hasInputs()) {
					final ArrayList<Input> inputs = unit.getInputs();

					//check all inputs of this unit
					for (int i = 0; i < inputs.size(); i++) {
						final Input input = inputs.get(i);
						// is this input connected?
						if (!input.isConnected() 
								// is this input actually required?
								&& input.isRequiredInput()) {
							System.err.println(input + " is not connected");
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Remove this Unit from the workflow.
	 * @param unit 
	 * @return 
	 */
	public boolean remove(final Node node) {
		if(node instanceof UnitElement) {
			UnitElement unit = (UnitElement) node;
			// new connection between the nodes that this deleted node was inbetween
			replaceConnection(unit);

			// delete old connections
			unbindUnit(unit);
		}

		// remove unit itself
		boolean remove = super.remove(node);
		notifyModelListeners();
		return remove;
	}


	/**
	 * Removes all connections to this {@link UnitElement}.
	 * @param unit
	 */
	public void unbindUnit(final UnitElement unit) {
		// find connections which are attached to this unit
		ConnectionList connections = getConnections();
		for (int i = 0; i < connections.size(); i++) {
			Connection connection = (Connection) connections.get(i);
			if(connection.isConnectedToUnit(unit)) {
				// delete connections
				connections.remove(connection);
				i--;
			}
		}
	}


	private void replaceConnection(final UnitElement unit) {
		// replacing maks only sense, when it has inputs and outputs
		if(!unit.hasInputsConnected() || !unit.hasOutputsConnected()) {
			return;
		}

		// get the outputs of the currently connected inputs
		int numberConnectedInputs = unit.getInputsCount();
		ArrayList<Output> connectedOutputs = new ArrayList<Output>(numberConnectedInputs);
		// we use a vector, add stuff to the end and iterate over this without indicies
		// check if pins are connected and ignore, if not
		for (int i = 0; i < numberConnectedInputs; i++) {
			Input input = unit.getInput(i);
			if(input.isConnected()) {
				connectedOutputs.add(input.getFromOutput());
			}
		}


		// same for inputs
		int numberConnectedOutputs = unit.getOutputsCount();
		ArrayList<Input> connectedInputs = new ArrayList<Input>(numberConnectedOutputs);
		for (int i = 0; i < numberConnectedOutputs; i++) {
			Output output = unit.getOutput(i);
			if(output.isConnected()) 
				connectedInputs.add(output.getToInput()); 
		}

		// now we create new connections based on the lists of 
		// formerly connected outputs and inputs.
		// if it doesn't match, discard

		// get the longer list, inputs or outputs
		int numPins = Math.min(connectedInputs.size(), connectedOutputs.size());
		for (int i = 0; i < numPins; i++) {
			getConnections().add(connectedInputs.get(i), connectedOutputs.get(i));
		}
	}


	/**
	 * Returns true, if any {@link UnitElement} in this UnitList is set as a displayUnit
	 * @return
	 */
	public boolean hasUnitAsDisplay() {
		for (int i = 0; i < size(); i++) {
			final UnitElement unit = (UnitElement) get(i);
			if(unit.isDisplayUnit()) 
				return true;
		}
		return false;
	}

	/**
	 * Returns true, if any source in this UnitList is set as a displayUnit
	 * @return
	 */
	public boolean hasSourcesAsDisplay() {
		for (int i = 0; i < size(); i++) {
			final UnitElement unit = (UnitElement) get(i);
			if(unit.getType() == Type.SOURCE && unit.isDisplayUnit()) 
				return true;
		}
		return false;
	}


	public void addModelListener(ModelListener listener) {
		if (! this.listeners.contains(listener)) {
			this.listeners.add(listener);
			notifyModelListener(listener);
		}
	}

	public void notifyModelListener(ModelListener listener) {
		listener.modelChanged(this);
	}

	public void notifyModelListeners() {
		for (final ModelListener listener : this.listeners) {
			notifyModelListener(listener);
		}
	}

	public void removeModelListener(ModelListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Get the {@link ConnectionList}
	 * @return
	 */
	public ConnectionList getConnections() {
		return connections;
	}

	/**
	 * Add a {@link Connection}
	 * @param con
	 * @return
	 */
	public boolean addConnection(Connection con) {
		return this.connections.add(con);
	}

	/**
	 * Resets all marks to zero.
	 * @param units
	 */
	public void unmarkUnits() {
		for (Node node : this) {
			if(node instanceof UnitElement) {
				UnitElement unit = (UnitElement) node;
				unit.setMark(0);
			}
			
		}
	}

	@Override
	public void clear() {
		super.clear();
		connections.clear();
		notifyModelListeners();
	}

}
