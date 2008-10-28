
public class Outputs {
	int unitNumber;			// the number of this unit 
	int outputNumber;		// the number of this output (mostly there is only one output)

	String name; 			// the name to be displayed in the context help
	String shortName = "O"; // the short name to be displayed on the unit's icon

	int outputBitDepth; 	// the int value indicates the type of the generated image 

	String imageTitle;  	// the title of the image generated from this output
	String imageID; 		// the id of the image generated from this output
	public boolean doDisplay; // indicates that this output should be shown
		
	
	public Outputs(int unitNumber, int outputNumber) {
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

	
}
