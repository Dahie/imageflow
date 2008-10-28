import ij.IJ;
import ij.ImagePlus;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class UnitElements {
	
	static int ids;
	
	int unitID;						// the id of this unit	
	String unitName;       			// name of this unit (will be shown) 
	BufferedImage icon;           	// icon image
	Color color;				  	// unit color
	String infoText;			  	// help text, describing the unit's functionality
	
	boolean isDisplayUnit;			// flag indicating if this unit is a display unit 
		
	// all arrays start at 1, this will make it easy to detect unconnected inputs and outputs
	Inputs[] inputs;				// input array
	Outputs[] outputs;				// output array
	Parameters[] parameters;       	// parameters that control the functionality of the unit
	
	String unitsImageJSyntax;		// the syntax that is to be used for this unit 
	
	UnitElements(
			String unitName,
			String unitsImageJSyntax,
			int numInputs, 
			int numOutputs, 
			int numParameters) {
		
		ids++;
		this.unitID = ids;
		this.unitName = unitName;
		this.unitsImageJSyntax = unitsImageJSyntax;
		this.inputs = new Inputs[numInputs+1]; 
		
		for (int i = 1; i < inputs.length; i++) {
			inputs[i] = new Inputs(unitID,i);
		}
		
		this.outputs = new Outputs[numOutputs+1]; 
		for (int i = 1; i < outputs.length; i++) {
			outputs[i] = new Outputs(unitID,i);
		}
		
		this.parameters = new Parameters[numParameters+1];
		for (int i = 1; i < parameters.length; i++) {
			parameters[i] = new Parameters(i);
		}
	}
	
	// for source units only
	public int getBitDepth() {
		String path = parameters[1].stringValue;
		ImagePlus imp = IJ.openImage(path);
		imp.close();
		int bitDepth = imp.getBitDepth();
		return bitDepth;
	}
		
}

