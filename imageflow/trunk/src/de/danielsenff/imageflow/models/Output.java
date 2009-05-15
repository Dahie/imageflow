package de.danielsenff.imageflow.models;

import graph.Node;
import graph.Pin;
import ij.plugin.filter.PlugInFilter;

import java.awt.Point;
import java.util.Collection;
import java.util.Vector;

import de.danielsenff.imageflow.models.unit.UnitElement;


/**
 * @author danielsenff
 *
 */
public class Output extends Pin implements Connectable {
	

	/**
	 * the number of this unit
	 */
//	protected int unitNumber;
	/**
	 * the number of this output (mostly there is only one output)
	 */
//	protected int outputNumber;

	/**
	 * the name to be displayed in the context help
	 */
	protected String name;


	/**
	 * the short name to be displayed on the unit's icon
	 */
	protected String shortDisplayName = "O";

	/**
	 * the int value indicates the type of the generated image
	 */
	protected int imageType; 

	/**
	 * the title of the image generated from this output
	 */
	protected String imageTitle; 
	/**
	 * the id of the image generated from this output
	 */
	protected String imageID; 
	/**
	 * indicates that this output should be shown
	 */
	protected boolean doDisplay;
	/**
	 * Input to which this Output is connected
	 */
	protected Input to;
	
	Vector<Connection> connections;
	
	
	/**
	 * @param nodeParent
	 * @param outputNumber
	 */
	public Output(final UnitElement nodeParent, 
			final int outputNumber) {
		super("output", outputNumber, nodeParent.getOutputsCount(), nodeParent);
		this.connections = new Vector<Connection>();
		generateID(((UnitElement)this.parent).getUnitID(), getIndex());
	}

	/**
	 * Sets the connection between this input and an output.
	 * @param fromUnitNumber
	 * @param fromOutputNumber
	 */
	private void generateID(final int unitNumber, final int outputNumber) {
		this.imageTitle = "Unit_" + unitNumber + "_Output_" + outputNumber;
		this.imageID = "ID_Unit_" + unitNumber + "_Output_" + outputNumber;
	}

	
	/**
	 * Sets the connection between this input and an output.
	 * @param toUnit
	 * @param toOutputNumber 
	 */
	public void connectTo(final UnitElement toUnit, final int toInputNumber) {
		connectTo(toUnit.getInput(toInputNumber-1));
	}
	
	public void connectTo(final Pin toInput) {
		this.to = (Input)toInput;
		Connection conn = new Connection(this, this.to);
		addConnection(conn);
	}
	
	public void addConnection(Connection conn) {
		this.connections.add(conn);
	}

	public Collection<Connection> getConnections() {
		return this.connections;
	}
	
	/**
	 * @param name
	 * @param shortname
	 * @param outputBitDepth
	 */
	public void setupOutput(final String name, final String shortname, final int outputBitDepth) {
		this.name = name;
		this.shortDisplayName = shortname;
		this.imageType = outputBitDepth;
	}


	/**
	 * Activates to display the image at this output.
	 * @param doDisplay
	 */
	public void setDoDisplay(final boolean doDisplay) {
		this.doDisplay = doDisplay;
	}


	/**
	 * Returns whether or not this output displays it's image. 
	 * @return
	 */
	public boolean isDoDisplay() {
		return doDisplay;
	}

	/**
	 * Title of the image in the macro.
	 * @return
	 */
	public String getImageTitle() {
		return this.imageTitle;
	}
	



	/**
	 * Get the abbreviated DisplayName
	 * @return
	 */
	public String getShortDisplayName() {
		return this.shortDisplayName;
	}
	
	/**
	 * 
	 * @return the imageID
	 */
	public String getImageID() {
		return this.imageID;
	}

	/* (non-Javadoc)
	 * @see graph.Pin#getLocation()
	 */
	@Override
	public Point getLocation() {
		int height = parent.getDimension().height;
		int nump = ((UnitElement) parent).getOutputsCount();
		this.nump = nump;
		int y =  (i*height / super.nump ) - (height/(2*super.nump)) + parent.getOrigin().y;
		Point point = new Point(parent.getOrigin().x+parent.getDimension().width, y);
		return point;
	}



	/**
	 * Returns whether or not this Input is connected.
	 * @return 
	 */
	public boolean isConnected() {
		return !this.connections.isEmpty();
	}
	
	/**
	 * Checks if this Input is connected with the given {@link Output}
	 * @param input 
	 * @return
	 */
	public boolean isConnectedWith(Pin input) {
		
		for (Connection connection : getConnections()) {
			if(connection.getInput().equals(input))
				return true;
		}
		/*if(isConnected() && input instanceof Input) {
			return this.to.equals(input);
		}*/
		return false;
	}

	/**
	 * Returns true, if this Output has been marked.
	 * The Mark is not 0.
	 * @return
	 */
	public boolean isMarked() {
		return (this.mark == 0) ? false : true;
	}
	
	
	
	/**
	 * Gets the true Image type. This can be either the own imagetype of this output
	 * or by traversing along the graph until it gets it's initial type.
	 * @return
	 */
	public int getImageBitDepth() {
		
		if(this.imageType != -1 && this.imageType != PlugInFilter.DOES_ALL) {
			return this.imageType; 
		} else {
			
			// return type of parents input connected output
			UnitElement unitElement = (UnitElement)parent;
			
			//TODO this could be nicer, how to handle multiple inputs?
			if(unitElement.hasInputsConnected()) {
				for (Input input : unitElement.getInputs()) {
					if(input.isConnected())
						return input.getFromOutput().getImageBitDepth();
				}
				
				return -1;
				
			} else {
				// this means our output doesn't know his own capabilities
				// and because it has no inputs, it can't get them anywhere
				// this sucks
				return -1;
			}
			
		}
	}
	
	/**
	 * @return the outputBitDepth
	 */
	public int getOutputBitDepth() {
		return imageType;
	}

	/**
	 * @param outputBitDepth the outputBitDepth to set
	 */
	public void setOutputBitDepth(int outputBitDepth) {
		this.imageType = outputBitDepth;
	}
	
	/**
	 * Returns true, if the imageBitDepth in question is supported by this Input.
	 * @param imageBitDepth
	 * @return
	 */
	public boolean isImageBitDepthCompatible(final int imageBitDepth) {
//		System.out.println(getImageBitDepth()+" "+imageBitDepth);
//		System.out.println(getImageBitDepth()&imageBitDepth);
		// -1 doesn't specify so ignore for now
		
		int myImageBitDepth = getImageBitDepth();
		if(myImageBitDepth != -1 && imageBitDepth != -1) {
			return (myImageBitDepth&imageBitDepth) != 0;
		} else if (myImageBitDepth == -1 && imageBitDepth != -1) {
//			System.err.println("couldn't find type");
		}
		return false;
	}

	@Override
	public UnitElement getParent() {
		return (UnitElement)super.getParent();
	}
	
	/**
	 * Traverses through {@link Input}s to see if the {@link Node} is connected somewhere.
	 * @param goal
	 * @return
	 */
	public boolean existsInInputSubgraph(final Node goal) {
		// self reference is true
		return traverseInput(getParent(), goal);
		// if nothing helps, it's false
	}

	
	private boolean traverseInput(UnitElement element, Node goal) {
		if(element.equals(goal)) {
			return true;
		} else if (element.hasInputsConnected()) {
			for (Input input : element.getInputs()) {
				if(input.isConnected()) {
					return traverseInput(input.getFromUnit(), goal);
				}
			}
		} 
		
		return false;
	}
	
	/**
	 * Resets the this Output, so that it is unconnected.
	 */
	public void disconnectAll() {
		generateID(0, 0); // reset connection
		this.connections.clear();
	}
	
	public Connection getConnectionTo(Pin toInput) {
		for (Connection connection : getConnections()) {
			if(connection.getInput().equals(toInput)) 
				return connection;
		}
		return null;
	}
	
	public void disconnectFrom(Pin input) {
		Connection oldConnection = getConnectionTo(input);
		this.connections.remove(oldConnection);
	}
	
	
}
