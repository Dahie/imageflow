/**
 * 
 */
package models;

import models.unit.UnitList;

/**
 * Ignore 
 * @author danielsenff
 *
 */
public class Check extends visualap.Check {

	protected UnitList nodeL;
	protected ConnectionList EdgeL;
	
	/**
	 * @param nodeL
	 * @param EdgeL
	 */
	public Check(UnitList nodeL, ConnectionList edgeL) {
		super(nodeL, edgeL);
		this.nodeL = nodeL;
		this.EdgeL = edgeL;
	}
	/*protected void updateBranch(Pin from, Pin to) {
		int marka, markb;
		marka = from.getMark();
		markb = to.getMark();
		if (marka == 0)
			if (markb == 0) {
//				from.setMark(nextMark);
//				to.setMark(nextMark);
//				nextMark++;
			} else from.setMark(markb);
		else if (markb == 0) 
				to.setMark(marka);
			else if (marka != markb) {
					for (Node s : nodeL) {
						UnitElement t = (UnitElement)s;
						for (int i = 0; i < t.getInputsMaxCount(); i++) {
							if (t.getInput(i).getMark() == markb)
								t.getInput(i).setMark(marka);
						}			
						for (int i = 0; i < t.getOutputsMaxCount(); i++) {
							if (t.getOutput(i).getMark() == markb)
								t.getOutput(i).setMark(marka);
						}								
					}						
					lostMark++;
				}
	}*/
	
	/*@Override
	protected void createUnmarkedNodeList() {
		for (Node s : this.nodeL) {
			UnitElement t = (UnitElement)s;
			for (int i = 0; i < t.getInputsActualCount(); i++) {
				t.getInput(i).setMark(0);
			}			
			for (int i = 0; i < t.getOutputsMaxCount(); i++) {
				t.getOutput(i).setMark(0);
			}			
		}
	}*/
	
	/* (non-Javadoc)
	 * @see visualap.Check#bubbleAllPins(int, visualap.Backward[], visualap.Vertex[], int)
	 */
	/*protected int bubbleAllPins(int nout, Backward[] out, Vertex[] vertexL,
			int i) throws CheckException {
		NodeBean t = (NodeBean) nodeL.get(i);
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
		return nout;
	}*/
	
}
