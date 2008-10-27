/**
 * 
 */
package models;

import graph.Edges;

import java.util.HashMap;

/**
 * @author danielsenff
 *
 */
public class ConnectionList extends HashMap<Integer,Connection> {

	
	
	/**
	 * Returns the contents of this list of {@link Connection} as {@link Edges}.
	 * @return
	 */
	public Edges getEdges() {
		Edges edges = new Edges();
		for (Connection connection : this.values()) {
			edges.add(connection);
		}
		return edges;
	}
	
	
	
}
