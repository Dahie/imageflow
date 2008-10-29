/**
 * 
 */
package models;

import graph.Edge;
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
			return add(connection);
		
		} else if (  (from instanceof Input && to instanceof Output) ) {
			// connect from Output to Input
			
			Connection connection = new Connection(((UnitElement)to.getParent()), to.getIndex(), 
					((UnitElement)from.getParent()), from.getIndex());
			System.out.println("new connection: from Unit: " + from.getParent() 
					+ " to Unit: " + to.getParent() 
					+ " at Input" + to.getIndex());
			return add(connection);
		}
		
		System.out.println("disallowed connection");
		return false;
		
	}
	
	
	
	
	/**
	 * adds the connection, if the pins are already connected, the old connection is deleted
	 * @param connection
	 * @return
	 */
	public boolean add(final Connection connection) {
		Input input = connection.getToUnit().getInput(connection.toInputNumber-1);
		input.setConnection(connection.fromUnitNumber, connection.fromOutputNumber);
		
		//check if input already got a connection, if yes, delete that one
//		for (Edge node : this) {
		for (int i = 0; i < this.size(); i++) {
			Connection conn = (Connection) this.get(i);
			
			// edges is connected with this input?
			if(conn.isConnected(input)) {
				remove(conn);
				System.out.println("old connection removed");
			}
			
		}
		
		
		return super.add(connection);
	}

	
	
}
