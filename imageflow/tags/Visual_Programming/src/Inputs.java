
public class Inputs {
	int unitNumber;		// the number of this unit 
	int inputNumber;	// the number of this input 

	String displayName; 	// the name to be displayed in the context help
	String shortDisplayName = "I"; // the short name to be displayed on the unit's icon
	
	String imageID; 		// the id of the image connected to this input
	String imageTitle;  	// the title of the image connected this output
	
	int inputImageBitDepth; // the int value indicates the acceptable image types 
	
	boolean needToCopyInput; 		// flag indicating if the image at this input needs to be duplicated

	// connected to this input:
	int fromUnitNumber;
	int fromOutputNumber;
	
	public void setConnection(int fromUnitNumber, int fromOutputNumber) {
		this.fromUnitNumber = fromUnitNumber;
		this.fromOutputNumber = fromOutputNumber;
		this.imageTitle = "Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
		this.imageID = "ID_Unit_" + fromUnitNumber + "_Output_" + fromOutputNumber;
	}

	public void setupInput(String displayName, String shortDisplayName, int inputImageBitDepth, boolean needToCopyInput) {
		this.displayName = displayName;
		this.shortDisplayName = shortDisplayName;
		this.inputImageBitDepth = inputImageBitDepth;
		this.needToCopyInput = needToCopyInput;
	}

	public Inputs(int unitNumber, int inputNumber) {
		this.unitNumber = unitNumber;
		this.inputNumber = inputNumber;
	}
}
