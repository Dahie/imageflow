/**
 * 
 */
package gui;

import graph.Node;
import graph.Pin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import models.unit.UnitElement;
import models.unit.UnitList;
import visualap.Delegate;
import visualap.GPanel;
import visualap.GPanelListener;
import backend.GraphController;

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
	public GraphPanel(ArrayList<Delegate> beans, GPanelListener parent) {
		super(beans, parent);
	}


	/* (non-Javadoc)
	 * @see visualap.GPanel#paintComponent(java.awt.Graphics)
	 */
/*	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// paint printable items
		paintPrintable(g);
		
		Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(
	    		RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
		
		// paint non printable items
		if (drawEdge!= null)	{
			Point origin = drawEdge.getLocation();
//			g2.setStroke(new BasicStroke(1f));
			for (Node node : nodeL) {
				if(isWithinRange(mouse.x, node.getOrigin().x, 
						node.getOrigin().x+node.getDimension().width)
					&& isWithinRange(mouse.y, node.getOrigin().y, 
						node.getOrigin().y+node.getDimension().height)) {
					g2.setColor(new Color(120,120,120));
//					g2.setStroke(new BasicStroke(1f));
					g2.drawRect(node.getOrigin().x, node.getOrigin().y, 
							node.getDimension().width, node.getDimension().height);
					
					for (Pin pin : ((UnitElement)node).getInputs()) {
//						if() {
							
//						}
					}
					
				}
			}
			
			float lineWidth = 1.0f;
//		    g2.setStroke(new BasicStroke(lineWidth));
		    g2.drawLine(origin.x, origin.y, mouse.x, mouse.y);
		    g2.draw(new Line2D.Double(origin.x, origin.y, mouse.x, mouse.y));
		}
		//If currentRect exists, paint a box on top.
		if (currentRect != null) {
			//Draw a rectangle on top of the image.
			g2.setXORMode(Color.white); //Color of Edge varies
			//depending on image colors
//			g2.setColor(Color.GRAY);
			g2.setStroke(dashed);
			g2.drawRect(rectToDraw.x, rectToDraw.y, 
					rectToDraw.width - 1, rectToDraw.height - 1);
		}
	}*/
	
	
	public static boolean isWithinRange(int compareValue, int startValue, int endValue) {
		return (compareValue > startValue 
				&& compareValue < endValue);
	}


	/**
	 * @param units2
	 */
	public void setNodeL(UnitList units2) {
		super.nodeL = units2;
	}
	
}
