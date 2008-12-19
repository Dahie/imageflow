/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
 */

/* class Check

This class is used to check the system

javalc6
 */
package visualap;
import graph.Edge;
import graph.Edges;
import graph.GList;
import graph.Node;
import graph.NodeBean;
import graph.Pin;
import graph.Selection;

import java.util.ArrayList;

public class Check {
	protected ArrayList<Node> nodeL;
	protected Edges EdgeL;
	protected int nextMark, lostMark;
	protected Selection<Node> errorList;

	public Check(GList<Node> nodeL, Edges EdgeL) {
		this.nodeL = new ArrayList<Node>();
		for (Node aNode : nodeL)
			if (aNode instanceof NodeBean) 
				this.nodeL.add((NodeBean)aNode);		

		this.EdgeL = EdgeL;
		errorList = new Selection<Node>();
	}

	/** graph analysis
	the method checkSystem() perform analysis of the graph, checking if it conforms to VisualAp rules and that is acyclic
	 */
	public Vertex [] checkSystem() throws CheckException {
		errorList.clear();
		return sortSystem(buildMBranch());
	}

	public Selection<Node> getErrorList() {
		return errorList;
	}

	private void updateBranch(Pin from, Pin to) {
		int marka, markb;
		marka = from.getMark();
		markb = to.getMark();
		if (marka == 0)
			if (markb == 0) {
				from.setMark(nextMark);
				to.setMark(nextMark);
				nextMark++;
			} else from.setMark(markb);
		else if (markb == 0) 
			to.setMark(marka);
		else if (marka != markb) {
			checkMarks(marka, markb);
									
			lostMark++;
		}
	}

	/**
	 * @param marka
	 * @param markb
	 */
	protected void checkMarks(int marka, int markb) {
//		for (NodeBean t : nodeL) {
		for (int n = 0; n < nodeL.size(); n++) {
			NodeBean t = (NodeBean)nodeL.get(n);
			
			for (int i = 0; i < t.inPins.length; i++) {
				if (t.inPins[i].getMark() == markb)
					t.inPins[i].setMark(marka);
			}			
			for (int i = 0; i < t.outPins.length; i++) {
				if (t.outPins[i].getMark() == markb)
					t.outPins[i].setMark(marka);
			}								
		}
	}

	private Vertex [] buildMBranch() throws CheckException {
		if (nodeL == null) return null;
		// reset mark on all pins
		resetMarksOnPins();
		nextMark = 1; lostMark = 0;
		// update mark using list of Edges
		for (Edge t : EdgeL) {
			updateBranch(t.from,t.to);
		}
		//		if (nextMark == 1) throw new CheckException("There are no connections");
		int nout = 0;
		Backward [] pOut = new Backward[nextMark];
		Vertex [] vertexL = new Vertex [nodeL.size()];
		nout = detectCollisions(nout, pOut, vertexL);
		detectFloatingPins(pOut, vertexL);
		// compress list of vertex
		int counter = 0;
		for (int i = 0; i < vertexL.length; i++) {
			if (vertexL[i] != null) counter++;
		}
		if (counter == 0) return null;
		Vertex [] vertex = new Vertex [counter];
		int j = 0;
		for (int i = 0; i < vertexL.length; i++) {
			if (vertexL[i] != null) {
				vertex[j] = vertexL[i];
				j++;
			}
		}
		if (nout != nextMark - lostMark - 1)
			throw new CheckException("Detected input pins not connected to any output pin");
		return vertex;
	}

	/**
	 * @param nout
	 * @param pOut
	 * @param vertexL
	 * @return
	 * @throws CheckException
	 */
	protected int detectCollisions(int nout, 
			final Backward[] pOut, 
			final Vertex[] vertexL)
			throws CheckException {
		for (int i = 0; i < vertexL.length; i++) {
			NodeBean t = (NodeBean)nodeL.get(i);
			if (t.getObject() != null)
				vertexL[i] = new Vertex(t);
			else vertexL[i] = null;
			for (int j = 0; j < t.outPins.length; j++) {
				int mark = t.outPins[j].getMark();
				if (mark != 0) {
					if (pOut[mark] == null) {
						pOut[mark] = new Backward();
						pOut[mark].index = t.outPins[j].getIndex();
						pOut[mark].obj = vertexL[nodeL.indexOf(t.outPins[j].getParent())];
						nout++;
					} else {
						//						System.out.println("GPanel.checkSystem: "+t.getLabel()+" <-> "+ pOut[t.outPins[i].getMark()].getParent().getLabel());
						errorList.add(t);
						throw new CheckException("Detected collision between two or more output pins\nPlease check "+t.getLabel());
					}
				}
			}			
		}
		return nout;
	}

	/**
	 * @param pOut
	 * @param vertexL
	 * @throws CheckException
	 */
	protected void detectFloatingPins(Backward[] pOut, 
			Vertex[] vertexL)
			throws CheckException {
		for (int i = 0; i < vertexL.length; i++) {
			if (vertexL[i] != null) {
				NodeBean aNode = (NodeBean)vertexL[i].aNode;
				for (int j = 0; j < aNode.inPins.length; j++) {
					int mark = aNode.inPins[j].getMark();
					if (mark != 0) {
						vertexL[i].backward[j] = pOut[mark];
					} else {
						errorList.add(vertexL[i].aNode);
						throw new CheckException("Detected a floating input pin\nPlease check "+vertexL[i].aNode.getLabel());
					}
				}
			}			
		}
	}

	/**
	 * Reset the marks on all pins
	 */
	protected void resetMarksOnPins() {
		for (int n = 0; n < nodeL.size(); n++) {
			final NodeBean t = (NodeBean)nodeL.get(n);
			for (int i = 0; i < t.inPins.length; i++) {
				t.inPins[i].setMark(0);
			}			
			for (int i = 0; i < t.outPins.length; i++) {
				t.outPins[i].setMark(0);
			}			
		}
	}

	private boolean copre(Vertex a, Vertex b) {
		for (int i = 0; i < a.backward.length; i++) {
			if (a.backward[i].obj.equals(b))
				return true;
		}
		return false;
	}

	private Vertex [] sortSystem(Vertex [] vertexL) throws CheckException {
		if (vertexL == null) return null;
		// first check for self-loop
		for (int i = 0; i < vertexL.length; i++) {
			if (copre(vertexL[i],vertexL[i])) {
				errorList.add(vertexL[i].aNode);
				throw new CheckException("Detected a loop\nPlease check "+vertexL[i].aNode.getLabel());
			}
		}
		int first = 0;
		int last = vertexL.length - 1;
		// check for any cycles
		while (last > 0) {
			int i = first+1;
			while ((i <= last)&& !copre(vertexL[i],vertexL[first])) i++;
			if (i <= last) {
				first++;
				//				swap(i,first);
				Vertex t = vertexL[i]; vertexL[i] = vertexL[first]; vertexL[first] = t;
			} else {
				//				swap(first,last);
				Vertex t = vertexL[first]; vertexL[first] = vertexL[last]; vertexL[last] = t;
				if (first > 0) {
					first--;
					for (i = 0; i <= first; i++)
						if (copre(vertexL[i],vertexL[last])) {
							errorList.add(vertexL[i].aNode);
							throw new CheckException("Detected a cycle\nPlease check "+vertexL[i].aNode.getLabel());
						}
				}
				last--;
			}
		}
		return vertexL;
	}
}
