
public class Connection {
	int id;		// the id of this connection
	
							// a connection between
	int fromOutputNumber;	// the output with this number
	int fromUnitNumber;		// of the unit with this number
							// is connected to
	int toInputNumber;		// the input of this number 
	int toUnitNumber;		// of the unit with this number
	
	
	public Connection(int fromUnitNumber, int fromOutputNumber, int toUnitNumber, int toInputNumber) {
		
		this.fromUnitNumber = fromUnitNumber;
		this.fromOutputNumber = fromOutputNumber;
		this.toUnitNumber = toUnitNumber;
		this.toInputNumber = toInputNumber;
		
		id = getID(fromUnitNumber, fromOutputNumber, toUnitNumber, toInputNumber);
	}
	
	// connect the inputs and outputs of the units
	public void connect(UnitElements[] unitElement) {
		
		unitElement[toUnitNumber].inputs[toInputNumber].setConnection(fromUnitNumber,fromOutputNumber);
		
		if(unitElement[toUnitNumber].isDisplayUnit)
			unitElement[fromUnitNumber].outputs[fromOutputNumber].doDisplay = true;
	}
	
	public static int getID(int fromUnitNumber, int fromOutputNumber, int toUnitNumber, int toInputNumber) {
		
		int id = (fromUnitNumber<<20) | (fromOutputNumber<<16) | (toUnitNumber<<4) | toInputNumber;   
		return id;
	}
	
}
