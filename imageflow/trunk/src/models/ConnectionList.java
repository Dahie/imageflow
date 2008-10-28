/**
 * 
 */
package models;

import graph.Edges;
import graph.Pin;
import models.unit.UnitElement;

/**
 * @author danielsenff
 *
 */
public class ConnectionList extends Edges {
	
	/* (non-Javadoc)
	 * @see graph.Edges#add(graph.Pin, graph.Pin)
	 */
	@Override
	public boolean add(Pin from, Pin to) {
		
		System.out.println("add connection");
		// check if connection is between input and output, not 2 outputs or two inputs
		if((from instanceof Output && to instanceof Input) ) {
			// connect from Input to Output
			
			Connection connection = new Connection(((UnitElement)from.getParent()), from.getIndex(), 
					((UnitElement)to.getParent()), to.getIndex());
			System.out.println("new connection: from Unit: " + from.getParent() 
					+ " to Unit: " + to.getParent() 
					+ " at Input" + to.getIndex());
			return super.add(connection);
		
		} else if (  (from instanceof Input && to instanceof Output) ) {
			// connect from Output to Input
			
			Connection connection = new Connection(((UnitElement)to.getParent()), to.getIndex(), 
					((UnitElement)from.getParent()), from.getIndex());
			System.out.println("new connection: from Unit: " + from.getParent() 
					+ " to Unit: " + to.getParent() 
					+ " at Input" + to.getIndex());
			return super.add(connection);
		}
		
		System.out.println("disallowed connection");
		return false;
		
	}
	
	/**
	 * @param connection
	 * @return
	 */
	public boolean add(final Connection connection) {
		Input input = connection.getToUnit().getInput(connection.toInputNumber-1);
		input.setConnection(connection.fromUnitNumber, connection.fromOutputNumber);
		return super.add(connection);
	}

	
	
}
