/**
 * 
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import visualap.GList;
import visualap.Node;
import de.danielsenff.imageflow.models.Connection;
import de.danielsenff.imageflow.models.ConnectionList;
import de.danielsenff.imageflow.models.Input;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.Model;
import de.danielsenff.imageflow.models.ModelListener;
import de.danielsenff.imageflow.models.Output;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.unit.UnitElement.Type;


/**
 * List holding all {@link UnitElement}s. For the moment this is just an empty wrapper.
 * @author danielsenff
 *
 */
public class UnitList extends GList<Node> implements Model, Cloneable {

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
	public void read(File file) throws FileNotFoundException {
//		Vector<UnitElement> newNodes = new Vector<UnitElement>();
		HashMap<Integer, UnitElement> newNodes = new HashMap<Integer, UnitElement>();

		if(!file.exists()) throw new FileNotFoundException("reading failed, the file as not found");

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
				

				// loop over alle Units
				while (unitsIterator.hasNext()) { 
					Element actualUnitElement = unitsIterator.next();
					int xPos 		= Integer.parseInt(actualUnitElement.getChild("XPos").getValue());
					int yPos		= Integer.parseInt(actualUnitElement.getChild("YPos").getValue());
					String label = "";
					if(actualUnitElement.getChild("Label") != null)
						label = actualUnitElement.getChild("Label").getValue();
						
					if(actualUnitElement.getChild("UnitID") != null &&
							actualUnitElement.getChild("UnitDescription") != null) {
						
						int unitID 	= Integer.parseInt(actualUnitElement.getChild("UnitID").getValue());
						UnitDescription unitDescription = new UnitDescription(file, actualUnitElement.getChild("UnitDescription"));

						// create unit
						UnitElement unitElement = UnitFactory.createProcessingUnit(unitDescription, new Point(xPos, yPos));
						unitElement.setDisplayUnit(unitDescription.getIsDisplayUnit());
						unitElement.setHelpString(unitDescription.helpString);
						unitElement.setLabel(label);
//						newNodes.add(unitID, unitElement);
						newNodes.put(unitID, unitElement);
						add(unitElement);
					} else {
						CommentNode comment = new CommentNode(new Point(xPos, yPos), label);
						add(comment);
					}
					
				}
			}

			// read connections
			Element connectionsElement = root.getChild("Connections");

			if (unitsElement != null && connectionsElement != null) {  
				List<Element> connectionsList = connectionsElement.getChildren();
				Iterator<Element> connectionsIterator = connectionsList.iterator();

				// loop �ber alle connections
				while (connectionsIterator.hasNext()) { 
					Element actualConnectionElement = connectionsIterator.next();
					int fromUnitID 			= Integer.parseInt(actualConnectionElement.getChild("FromUnitID").getValue());
					int fromOutputNumber 	= Integer.parseInt(actualConnectionElement.getChild("FromOutputNumber").getValue());
					int toUnitID 			= Integer.parseInt(actualConnectionElement.getChild("ToUnitID").getValue());
					int toInputNumber 		= Integer.parseInt(actualConnectionElement.getChild("ToInputNumber").getValue());
					Connection con 			= new Connection(newNodes.get(fromUnitID), fromOutputNumber, 
												newNodes.get(toUnitID), toInputNumber);
					addConnection(con);
				}
			}
		}
		catch (Exception e) {
			System.err.println("Invalid XML-File!");
			e.printStackTrace();
		}	
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public void write(final File file) throws IOException {
//		final SAXBuilder sb = new SAXBuilder();
		Element root = new Element("FlowDescription");
		Document flowGraph = new Document(root);

		Element units = new Element("Units");
		root.addContent(units);

		for (Node node : this) {

			Element unitElement = new Element("Unit");
			units.addContent(unitElement);

			// unit details 
			
			Element xPos = new Element("XPos");
			xPos.addContent(node.getOrigin().x+"");
			unitElement.addContent(xPos);
			Element yPos = new Element("YPos");
			yPos.addContent(node.getOrigin().y+"");
			unitElement.addContent(yPos);
			Element label = new Element("Label");
			label.addContent(node.getLabel());
			unitElement.addContent(label);


			if(node instanceof UnitElement) {

				UnitElement unit = (UnitElement) node;
				
				Element unitID = new Element("UnitID");
				unitID.addContent(unit.getUnitID()+"");
				unitElement.addContent(unitID);

				// unit description analog to the Unit-XML
				Element unitDescription = new Element("UnitDescription");
				unitElement.addContent(unitDescription);

				Element general = new Element("General");
				unitDescription.addContent(general);

				Element unitName = new Element("UnitName");
				unitName.addContent(unit.getUnitName());
				general.addContent(unitName);
				Element pathToIcon = new Element("PathToIcon");
				pathToIcon.addContent(unit.getIconPath());
				general.addContent(pathToIcon);
				Element imageJSyntax = new Element("ImageJSyntax");
				imageJSyntax.addContent(((MacroElement)unit.getObject()).getImageJSyntax());
				general.addContent(imageJSyntax);
				Element color = new Element("Color");
				String colorHex = Integer.toHexString( unit.getColor().getRGB() );
				color.addContent("0x"+colorHex.substring(2));
				general.addContent(color);
				Element iconSize = new Element("IconSize");
				iconSize.addContent(unit.getCompontentSize().toString());
				general.addContent(iconSize);
				Element helpStringU = new Element("HelpString");
				helpStringU.addContent(unit.getHelpString());
				general.addContent(helpStringU);


				// deal with all parameters
				Element parameters = new Element("Parameters");
				unitDescription.addContent(parameters);
				for (Parameter parameter : unit.getParameters()) {
					Element parameterElement = new Element("Parameter");
					parameters.addContent(parameterElement);	

					Element name = new Element("Name");
					name.addContent(parameter.getDisplayName());
					parameterElement.addContent(name);

					Element dataType = new Element("DataType");
					dataType.addContent(parameter.getParaType());
					parameterElement.addContent(dataType);

					Element value = new Element("Value");
					value.addContent(parameter.getValue()+"");
					

					Element helpStringP = new Element("HelpString");
					helpStringP.addContent(parameter.getHelpString());
					parameterElement.addContent(helpStringP);
					
					//special parameters
					
					if(parameter instanceof ChoiceParameter) {
						
						value = new Element("Value");
						ChoiceParameter choiceParameter = (ChoiceParameter)parameter;
						String choiceString =  choiceParameter.getChoicesString();
						value.addContent(choiceString);
						
						Element choiceNumber = new Element("ChoiceNumber");
						choiceNumber.addContent(choiceParameter.getChoiceIndex()+"");
						parameterElement.addContent(choiceNumber);	
					}
					parameterElement.addContent(value);
					
					if(parameter instanceof BooleanParameter) {
						Element trueString = new Element("TrueString");
						trueString.addContent(((BooleanParameter)parameter).getTrueString());
						parameterElement.addContent(trueString);	
					}
				}

				// deal with inputs
				Element inputs = new Element("Inputs");
				unitDescription.addContent(inputs);
				for (Input input : unit.getInputs()) {
					Element inputElement = new Element("Input");
					inputs.addContent(inputElement);	

					Element name = new Element("Name");
					name.addContent(input.getName());
					inputElement.addContent(name);

					Element shortName = new Element("ShortName");
					shortName.addContent(input.getShortDisplayName());
					inputElement.addContent(shortName);

					
					Element dataTypeElement = new Element("DataType");
					DataType dataType = input.getDataType();
					dataTypeElement.addContent(dataType.getClass().getName());
					inputElement.addContent(dataTypeElement);
					
					if(dataType instanceof DataTypeFactory.Image) {
						Element imageType = new Element("ImageType");
						imageType.addContent(""+((DataTypeFactory.Image)dataType).getImageBitDepth());
						inputElement.addContent(imageType);	
					}
					

					Element needToCopyInput = new Element("NeedToCopyInput");
					String boolNeedCopy = input.isNeedToCopyInput() ? "true" : "false"; 
					needToCopyInput.addContent(boolNeedCopy);
					inputElement.addContent(needToCopyInput);
				}

				// deal with inputs
				Element outputs = new Element("Outputs");
				unitDescription.addContent(outputs);
				for (Output output : unit.getOutputs()) {
					Element outputElement = new Element("Output");
					outputs.addContent(outputElement);	

					Element name = new Element("Name");
					name.addContent(output.getName());
					outputElement.addContent(name);

					Element shortName = new Element("ShortName");
					shortName.addContent(output.getShortDisplayName());
					outputElement.addContent(shortName);

					Element dataType = new Element("DataType");
					dataType.addContent(output.getDataType().getClass().getName());
					outputElement.addContent(dataType);
					
					if(output.getDataType() instanceof DataTypeFactory.Image) {
						Element imageType = new Element("ImageType");
						imageType.addContent(""+((DataTypeFactory.Image)output.getDataType()).getImageBitDepth());
						outputElement.addContent(imageType);	
					}
					
					

					// In case of plugins with multiple outputs
					// I want to leave the option to display only selected outputs
					Element doDisplay = new Element("DoDisplay");
					String boolIsDisplay = output.isDoDisplay() ? "true" : "false"; 
					doDisplay.addContent(boolIsDisplay);
					outputElement.addContent(doDisplay);
				}
			}
		}

		
		// now for connections
		Element connectionsElement = new Element("Connections");
		root.addContent(connectionsElement);
		
		for (Connection edge : connections) {
			Connection conn = (Connection) edge;
			
			Element connectionElement = new Element("Connection");
			connectionsElement.addContent(connectionElement);
			
			Element fromUnitID = new Element("FromUnitID");
			fromUnitID.addContent(""+ ((UnitElement)conn.getFromUnit()).getUnitID());
			connectionElement.addContent(fromUnitID);
			
			Element fromOutputNumber = new Element("FromOutputNumber");
			fromOutputNumber.addContent(""+conn.getOutput().getIndex());
			connectionElement.addContent(fromOutputNumber);
			
			Element toUnitID = new Element("ToUnitID");
			toUnitID.addContent(""+((UnitElement)conn.getToUnit()).getUnitID());
			connectionElement.addContent(toUnitID);
			
			Element toInputNumber = new Element("ToInputNumber");
			toInputNumber.addContent(""+conn.getInput().getIndex());
			connectionElement.addContent(toInputNumber);
		}



		// output
//		new XMLOutputter(Format.getPrettyFormat()).output(flowGraph, System.out);
		FileOutputStream fos = new FileOutputStream(file);
//		System.out.println(file);
		new XMLOutputter(Format.getPrettyFormat()).output(flowGraph, fos);
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
								&& input.isRequired()) {
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
	 * Iterates over all units in the list to find the given ID.
	 * @param id
	 * @return
	 */
	public Node getUnit(final int id) {
		for (Node node : this) {
			AbstractUnit unit = (AbstractUnit) node;
			if(unit.getUnitID() == id) 
				return unit;
		}
		return null;
	}
	
	
	/**
	 * Remove this Unit from the workflow.
	 * @param unit 
	 * @return 
	 */
	@Override
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
		// replacing makes only sense, when it has inputs and outputs
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
				for (Connection connection : output.getConnections()) {
					Input toInput = connection.getInput();
					connectedInputs.add(toInput);
				}
				
				 
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
			if(unit.getUnitType() == Type.SOURCE && unit.isDisplayUnit()) 
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
	 * Adds the {@link Connection}s of a {@link ConnectionList} to this ConnectionList.
	 * They are added, the current ConnectionList is not replaced.
	 * @param newConnList
	 */
	public void addConnectionList(ConnectionList newConnList) {
//		this.connections
		for (Connection conn : newConnList) {
			this.connections.add(conn);
		}
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

	public int getSize() {
		return super.size();
	}

}
