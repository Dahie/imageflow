package backend;

import graph.Edges;
import graph.GList;
import graph.Node;
import models.Input;
import models.Output;
import models.unit.UnitElement;
import visualap.Backward;
import visualap.Check;
import visualap.CheckException;
import visualap.Vertex;

public class GraphCheck extends Check {
	

		public GraphCheck(GList<Node> nodeL, Edges edgeL) {
			super(nodeL, edgeL);
		}
		
		@Override
		protected void checkMarks(int marka, int markb) {
//			for (UnitElement t : nodeL) {
			//loop through all nodes
			for (int n = 0; n < nodeL.size(); n++) {
				UnitElement t = (UnitElement)nodeL.get(n);
				
				// check for all inputs, the the mark is set
				for (int i = 0; i < t.getInputsCount(); i++) {
					Input input = t.getInput(i);
					if (input.getMark() == markb)
						input.setMark(marka);
				}
				// check for all outputs, the the mark is set
				for (int i = 0; i < t.getOutputsCount(); i++) {
					Output output = t.getOutput(i);
					if (output.getMark() == markb)
						output.setMark(marka);
				}								
			}
		}

		
		private boolean copre(Vertex a, Vertex b) {
			Backward[] backward = a.getBackward();
			for (int i = 0; i < backward.length; i++) {
				if (backward[i].getObj().equals(b))
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
