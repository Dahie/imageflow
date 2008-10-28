package Models;

import java.awt.Point;

import graph.Node;
import graph.Pinnable;

public class Output implements Pinnable{
	protected int unitNumber;			// the number of this unit
	protected Node unit;
	protected int outputNumber;		// the number of this output (mostly there is only one output)

	protected String name; 			// the name to be displayed in the context help
	protected String shortName = "O"; // the short name to be displayed on the unit's icon

	protected int outputBitDepth; 	// the int value indicates the type of the generated image 

	protected String imageTitle;  	// the title of the image generated from this output
	protected String imageID; 		// the id of the image generated from this output
	protected boolean doDisplay; // indicates that this output should be shown
		
	
	public Output(int unitNumber, int outputNumber) {
		this.unitNumber = unitNumber;
		this.outputNumber = outputNumber;
		this.imageTitle = "Unit_" + unitNumber + "_Output_" + outputNumber;
		this.imageID = "ID_Unit_" + unitNumber + "_Output_" + outputNumber;
	}



	public void setupOutput(String name, String shortname, int outputBitDepth) {
		this.name = name;
		this.shortName = shortname;
		this.outputBitDepth = outputBitDepth;
	}


	public void setDoDisplay(boolean doDisplay) {
		this.doDisplay = doDisplay;
	}


	/**
	 * Returns whether or not this output displays it's image. 
	 * @return
	 */
	public boolean isDoDisplay() {
		return doDisplay;
	}

	public String getImageTitle() {
		return this.imageTitle;
	}
	
	/**
	 * 
	 * @return the imageID
	 */
	public String getImageID() {
		return this.imageID;
	}



	/* (non-Javadoc)
	 * @see graph.Pinnable#getIndex()
	 */
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}



	/* (non-Javadoc)
	 * @see graph.Pinnable#getLocation()
	 */
	public Point getLocation() {
		return null;
	}



	/* (non-Javadoc)
	 * @see graph.Pinnable#getMark()
	 */
	public int getMark() {
		return 0;
	}



	/* (non-Javadoc)
	 * @see graph.Pinnable#getName()
	 */
	public String getName() {
		return this.name;
	}



	/* (non-Javadoc)
	 * @see graph.Pinnable#getParent()
	 */
	public Node getParent() {
		return this.unit;
	}



	/* (non-Javadoc)
	 * @see graph.Pinnable#setMark(int)
	 */
	public void setMark(int mark) {
		
	}
	
}
