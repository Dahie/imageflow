/**
 * 
 */
package de.danielsenff.imageflow.models.unit;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import visualap.Node;
import de.danielsenff.imageflow.models.MacroElement;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.datatype.DataType;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.BooleanParameter;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;

/**
 * @author senff
 *
 */
public class WorkflowXMLBuilder {

	private UnitList unitList;
	private Collection<ConnectionDelegate> connectionDelegates;
	private Collection<GroupUnitElement> groupUnits;
	private HashMap<Integer, UnitElement> newNodes;

	public WorkflowXMLBuilder(UnitList units) {
		this.unitList = units;
		this.connectionDelegates = new Vector();
		this.newNodes = new HashMap<Integer, UnitElement>();
		this.groupUnits = new Vector<GroupUnitElement>();
	}


	/**
	 * Reads the contents of a flow-XML-file.
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public void read(File file) throws FileNotFoundException {

		if(!file.exists()) throw new FileNotFoundException("reading failed, the file as not found");

		// setup of units
		try {
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(file);

			Element root = doc.getRootElement();

			System.out.println("Read nodes ...");
			
			// read units
			Element unitsElement = root.getChild("Units");
			readUnits(newNodes, file, unitsElement);

			
			
			// at this point we have all units in the workflow, however no connections
			// groups are missing their inputs and outputs
			//
			
			System.out.println("Process Connection Delegates ...");
			
			// process ConnectionDelegates
			for (ConnectionDelegate conndel : connectionDelegates) {
				conndel.list.add(readConnection(newNodes, conndel));
			}
			
			System.out.println("Process Groups and add inputs and outputs ...");
			
			// process groups
			for (GroupUnitElement unit : groupUnits) {
				unit.dealWithConnections(getConnectionList());
			}
			
			System.out.println("Read global connections ...");
			
			// read connections
			Element connectionsElement = root.getChild("Connections");
			readConnections(newNodes, connectionsElement);
		}
		catch (Exception e) {
			System.err.println("Invalid XML-File!");
			e.printStackTrace();
		}	
	}

	private void readUnits(HashMap<Integer, UnitElement> newNodes,
			File file, Element unitsElement) {
		if (unitsElement != null) {  
			List<Element> unitsList = unitsElement.getChildren();
			Iterator<Element> unitsIterator = unitsList.iterator();

			// loop over alle Units
			while (unitsIterator.hasNext()) { 
				Element actualUnitElement = unitsIterator.next();
				Node node = readUnit(actualUnitElement, file);
				getUnitList().add(node);
			}
		}
	}

	private Node readUnit(final Element actualUnitElement, 
			File file) {
		Node node;
		int xPos = Integer.parseInt(actualUnitElement.getChild("XPos").getValue());
		int yPos = Integer.parseInt(actualUnitElement.getChild("YPos").getValue());
		String label = "";
		if(actualUnitElement.getChild("Label") != null)
			label = actualUnitElement.getChild("Label").getValue();

		Element unitDescriptionElement = actualUnitElement.getChild("UnitDescription");
		if(actualUnitElement.getChild("UnitID") != null &&
				unitDescriptionElement != null) {

			int unitID 	= Integer.parseInt(actualUnitElement.getChild("UnitID").getValue());
			UnitDescription unitDescription = 
				new UnitDescription(file, unitDescriptionElement);

			UnitElement unitElement;
			
			Element unitsElement = unitDescriptionElement.getChild("Units");
			Element internalConnectionsElement = unitDescriptionElement.getChild("InternalConnections");
			Element externalConnectionsElement = unitDescriptionElement.getChild("ExternalConnections");
			Element originalConnectionsElement = unitDescriptionElement.getChild("OriginalConnections");
			if(internalConnectionsElement != null 
					&& originalConnectionsElement != null 
					&& unitsElement != null ) {
				
				/*
				 * GroupUnit
				 */
				
				unitElement = new GroupUnitElement(new Point(xPos, yPos), label);
				HashMap<Integer, UnitElement> embeddedNodes = new HashMap<Integer, UnitElement>();
				
				if(unitsElement != null) {

					List<Element> unitsList = unitsElement.getChildren();
					Iterator<Element> unitsIterator = unitsList.iterator();

					// loop over all Units
					while (unitsIterator.hasNext()) { 
						Element actualEmbeddedUnitElement = unitsIterator.next();
						Node embeddedNode = readUnit(actualEmbeddedUnitElement, file);
						((GroupUnitElement)unitElement).getNodes().add(embeddedNode);
						if(embeddedNode instanceof UnitElement)
							newNodes.put(((UnitElement)embeddedNode).getUnitID(), (UnitElement) embeddedNode);
						if(embeddedNode instanceof UnitElement)
							embeddedNodes.put(((UnitElement)embeddedNode).getUnitID(), (UnitElement) embeddedNode);
					}
				}

				
				if(internalConnectionsElement != null) {
					List<Element> connectionsList = internalConnectionsElement.getChildren();
					Iterator<Element> connectionsIterator = connectionsList.iterator();

					// internal connections require the hashmap with all embedded units
					while (connectionsIterator.hasNext()) { 
						Element actualConnectionElement = connectionsIterator.next();

						Connection connection = readConnection(newNodes, actualConnectionElement);
						((GroupUnitElement)unitElement).getInternalConnections().add(connection);
					}
				}

				if(originalConnectionsElement != null) {
					List<Element> connectionsList = internalConnectionsElement.getChildren();
					Iterator<Element> connectionsIterator = connectionsList.iterator();

					while (connectionsIterator.hasNext()) { 
						Element actualConnectionElement = connectionsIterator.next();
						ConnectionDelegate conDelegate = 
							new ConnectionDelegate(actualConnectionElement, 
									((GroupUnitElement)unitElement).getOriginalConnections());
						connectionDelegates.add(conDelegate);
					}
				}
				
				if(externalConnectionsElement != null) {
					List<Element> connectionsList = externalConnectionsElement.getChildren();
					Iterator<Element> connectionsIterator = connectionsList.iterator();

					// external connections require the hashmap with all embedded and existing units	
					// we need all units of the workflow :/
					while (connectionsIterator.hasNext()) { 
						Element actualConnectionElement = connectionsIterator.next();
//						Connection connection = readConnection(embeddedNodes, actualConnectionElement);
//						((GroupUnitElement)unitElement).getInternalConnections().add(connection);
						ConnectionDelegate conDelegate = 
							new ConnectionDelegate(actualConnectionElement, 
									((GroupUnitElement)unitElement).getExternalConnections());
						connectionDelegates.add(conDelegate);
					}
				}
				
				
				unitElement.setHelpString(unitDescription.helpString);
				newNodes.put(unitID, unitElement);
				groupUnits.add((GroupUnitElement) unitElement);
			
			} else {
				// create UnitElement
				unitElement = UnitFactory.createProcessingUnit(unitDescription, new Point(xPos, yPos));
				unitElement.setDisplay(unitDescription.getIsDisplayUnit());
				unitElement.setHelpString(unitDescription.helpString);
				unitElement.setLabel(label);
				newNodes.put(unitID, unitElement);
			}
			
			
//			add(unitElement);
			node = unitElement;
			
		} else {
			CommentNode comment = new CommentNode(new Point(xPos, yPos), label);
//			add(comment);
			node = comment;
		}
		
		return node;
	}
	
	
	private class ConnectionDelegate {
		public int fromUnitID, fromUnitOutput;
		public int toUnitID, toUnitInput;
		// to add this list
		public Collection<Connection> list;
		
		public ConnectionDelegate(int fromUnitID, int fromUnitOutput,
				int toUnitID, int toUnitInput, Collection<Connection> list) {
			this.fromUnitID = fromUnitID;
			this.fromUnitOutput = fromUnitOutput;
			this.toUnitID = toUnitID;
			this.toUnitInput = toUnitInput;
			this.list = list;
		}
		
		public ConnectionDelegate(Element connectionElement, Collection<Connection> list) {
			this(Integer.parseInt(connectionElement.getChild("FromUnitID").getValue()), 
				Integer.parseInt(connectionElement.getChild("FromOutputNumber").getValue()),
				Integer.parseInt(connectionElement.getChild("ToUnitID").getValue()),
				Integer.parseInt(connectionElement.getChild("ToInputNumber").getValue()),
				list
			);
		}
		
	} 
	

	private void readConnections(HashMap<Integer, UnitElement> newNodes, Element connectionsElement) {
		//unitsElement != null &&
		if ( connectionsElement != null) {  
			List<Element> connectionsList = connectionsElement.getChildren();
			Iterator<Element> connectionsIterator = connectionsList.iterator();

			while (connectionsIterator.hasNext()) { 
				Element actualConnectionElement = connectionsIterator.next();
				getUnitList().addConnection(readConnection(newNodes, actualConnectionElement));
			}
		}
	}


	private static Connection readConnection(HashMap<Integer, UnitElement> newNodes,
			Element actualConnectionElement) {
		int fromUnitID = 
			Integer.parseInt(actualConnectionElement.getChild("FromUnitID").getValue());
		int fromOutputNumber = 
			Integer.parseInt(actualConnectionElement.getChild("FromOutputNumber").getValue());
		int toUnitID = 
			Integer.parseInt(actualConnectionElement.getChild("ToUnitID").getValue());
		int toInputNumber = 
			Integer.parseInt(actualConnectionElement.getChild("ToInputNumber").getValue());
		
		Connection con = new Connection(newNodes.get(fromUnitID), fromOutputNumber, 
								newNodes.get(toUnitID), toInputNumber);
		return con;
	}
	
	private static Connection readConnection(HashMap<Integer, UnitElement> newNodes,
			ConnectionDelegate connDel) {
		int fromUnitID = connDel.fromUnitID;
		int fromOutputNumber = connDel.fromUnitOutput;
		int toUnitID = connDel.toUnitID;
		int toInputNumber = connDel.toUnitInput;
		
		Connection con = new Connection(newNodes.get(fromUnitID), fromOutputNumber, 
								newNodes.get(toUnitID), toInputNumber);
		return con;
	}

	
	/**
	 * @param file
	 * @throws IOException
	 */
	public void write(final File file) throws IOException {
		Element root = new Element("FlowDescription");
		Document flowGraph = new Document(root);

		Element units = new Element("Units");
		root.addContent(units);

		for (Node node : getUnitList()) {
			Element unitElement = new Element("Unit");
			units.addContent(unitElement);

			writeXMLNode(node, unitElement);
		} // end node

		// now for connections
		Element connectionsElement = new Element("Connections");
		root.addContent(connectionsElement);

		for (Connection connection : getConnectionList()) {
			writeXMLConnection(connectionsElement, connection);
		}

		// output
		FileOutputStream fos = new FileOutputStream(file);
		new XMLOutputter(Format.getPrettyFormat()).output(flowGraph, fos);
	}


	private void writeXMLNode(Node node, Element unitElement) {
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

			Element unitDescription = writeXMLUnitElement(unit, unitElement);

			if(node instanceof GroupUnitElement) {
				GroupUnitElement group = (GroupUnitElement)node;

				// groupUnits have additional fields for
				// unitlist included in group
				Element embeddedUnitsElement = new Element("Units");
				unitDescription.addContent(embeddedUnitsElement);
				for (Node embeddedNode : group.getNodes()) {
					Element embeddedUnitElement = new Element("Unit");
					embeddedUnitsElement.addContent(embeddedUnitElement);
					writeXMLNode(embeddedNode, embeddedUnitElement);
				}

				// list of internal connections
				Element internalConnectionsElement = new Element("InternalConnections");
				unitDescription.addContent(internalConnectionsElement);
				for (Connection connection : group.getInternalConnections()) {
					writeXMLConnection(internalConnectionsElement, connection);	
				}

				// list of external connections
				Element externalConnectionsElement = new Element("ExternalConnections");
				unitDescription.addContent(externalConnectionsElement);
				for (Connection connection : group.getExternalConnections()) {
					writeXMLConnection(externalConnectionsElement, connection);	
				}				
				
				// list of original connections
				Element originalConnectionsElement = new Element("OriginalConnections");
				unitDescription.addContent(originalConnectionsElement);
				for (Connection connection : group.getOriginalConnections()) {
					writeXMLConnection(originalConnectionsElement, connection);	
				}
			}
		} // end unitelement
	}


	private Element writeXMLUnitElement(UnitElement unit, Element unitElement) {
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
			dataTypeElement.addContent(dataType.getClass().getSimpleName());
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
			dataType.addContent(output.getDataType().getClass().getSimpleName());
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
		return unitDescription;
	}


	private void writeXMLConnection(Element connectionsElement, Connection edge) {
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


	/**
	 * Returns the UnitList processed by this Builder.
	 * @return the unitList
	 */
	public UnitList getUnitList() {
		return unitList;
	}

	/**
	 * Returns the Connections in the UnitList processed by this Builder.
	 * @return
	 */
	public ConnectionList getConnectionList() {
		return getUnitList().getConnections();
	}

}
