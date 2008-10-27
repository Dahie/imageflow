/**
 * 
 */
package gui;

import graph.Node;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ListIterator;

import visualap.GPanel;
import Models.Input;
import Models.unit.UnitList;

/**
 * @author danielsenff
 *
 */
public class GraphPanel extends GPanel {

	
	protected UnitList units;
	
	/**
	 * @param beans
	 * @param parent
	 */
	public GraphPanel(UnitList units) {
		super(null, null);
	}

	
	/* (non-Javadoc)
	 * @see visualap.GPanel#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// generato nell'istante in cui il mouse viene premuto
				int x = e.getX();
				int y = e.getY();
		// qui è obbligatorio un iteratore che scandisce la lista al contrario!
				for (ListIterator<Node> it = nodeL.listIterator(nodeL.size()); it.hasPrevious(); ) {
					Node aNode = it.previous();
					Object sel = aNode.contains(x,y);
		// check selected element, is it a Node?
					if (sel instanceof Node) {
						pick = new Point(x,y);
						if (!selection.contains(aNode)) {
							selection.clear();
							selection.add(aNode);
						}
						for (Node iNode : selection)
							iNode.drag(true);
						repaint();
						e.consume();
						changeCursor(Cursor.MOVE_CURSOR);
						return;
					}
		// check selected element, is it a Pin?
					else if (sel instanceof Input) {
						drawEdge = (Input) sel;
//			System.out.println(drawEdge);
						mouse = new Point (x,y);
						changeCursor(Cursor.CROSSHAIR_CURSOR);
						return;
					}
				}
				selection.clear();
				parent.showFloatingMenu(e);
			//	e.consume();

			// handling of selection rectange 
				currentRect = new Rectangle(x, y, 0, 0);
				updateDrawableRect(getWidth(), getHeight());
				repaint();
			}
	
}
