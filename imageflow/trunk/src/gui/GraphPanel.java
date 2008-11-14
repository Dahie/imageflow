/**
 * 
 */
package gui;

import graph.Node;
import graph.NodeText;
import graph.Pin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import models.unit.UnitElement;
import models.unit.UnitList;
import visualap.Delegate;
import visualap.GPanel;
import visualap.GPanelListener;

/**
 * @author danielsenff
 *
 */
public class GraphPanel extends GPanel {
	
	/**
	 * List of all {@link UnitElement} added to the Workflow.
	 */
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
	@Override
	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
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
				int margin = 15;
				if(isWithin2DRange(mouse, node.getOrigin(), node.getDimension(), margin)) {

					for (Pin pin : ((UnitElement)node).getInputs()) {
						int diameter = 15;
						Point pinLocation = pin.getLocation();
						int pinX = pinLocation.x - (diameter/2); 
						int pinY = pinLocation.y - (diameter/2);

						// draw pin marker if mouse within inner range
						int lowerYpin = pin.getLocation().y - margin;
						int upperYpin = pin.getLocation().y + margin;
						if(isWithinRange(mouse.y, lowerYpin, upperYpin)
							&& !pin.getParent().equals(drawEdge.getParent())) {
							
							g2.setColor(Color.green);
							
							Ellipse2D.Double circle = 
								new Ellipse2D.Double(pinX, pinY, diameter, diameter);
							g2.fill(circle);
						}
					}
				}
			}
			
			g2.setColor(Color.BLACK);
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
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		// check distance for every pin on every unit
		/*for (Iterator iterator = units.iterator(); iterator.hasNext();) {
			UnitElement unit = (UnitElement) iterator.next();
			
			for (Input input : unit.getInputs()) {
				if (drawEdge != null)	{
					mouse.x = e.getX(); mouse.y = e.getY();
					repaint();
					e.consume();
				}
				repaint();
			}
			
		}*/
		
		super.mouseDragged(e);
	}
	
	/**
	 * Returns rue if the value is within this range of values.
	 * @param compareValue
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public static boolean isWithinRange(final int compareValue, 
			final int startValue, 
			final int endValue) {
		return (compareValue > startValue 
				&& compareValue < endValue);
	}

	/**
	 * Returns true, if the current point is withing this 2D-range.
	 * The Range is defined by its coordinates, 
	 * the dimension of the rectangle and a margin around this rectangle.
	 * @param currentPoint
	 * @param origin
	 * @param dimension
	 * @param margin
	 * @return
	 */
	public static boolean isWithin2DRange(final Point currentPoint, 
			final Point origin, 
			final Dimension dimension, 
			final int margin) {
		return isWithinRange(currentPoint.x, origin.x-margin, 
				origin.x+dimension.width+margin)
			&& isWithinRange(currentPoint.y, origin.y - margin, 
				origin.y + dimension.height + margin);
	}
	
	
	
	/**
	 * Replace the current {@link UnitList} with a different one.
	 * @param units2
	 */
	public void setNodeL(UnitList units2) {
		super.nodeL = units2;
	}
	
	@Override
	public void properties(Node node) {
		if (node instanceof NodeText) {
//			propertySheet.setVisible(false);
			String inputValue = JOptionPane.showInputDialog("Edit text:",((NodeText)node).getText()); 
			if ((inputValue != null)&&(inputValue.length() != 0)) {
				((NodeText)node).setText(inputValue);
				repaint();
			}
		}
		else { // aNode instanceof NodeBean
			UnitElement unit = (UnitElement) node;
			unit.showProperties();
		}
//		super.properties(node);
	}
	
	
}
