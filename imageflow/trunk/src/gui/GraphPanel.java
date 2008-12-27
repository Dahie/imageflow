/**
 * 
 */
package gui;

import graph.Node;
import graph.NodeText;
import graph.Pin;

import imageflow.models.Input;
import imageflow.models.Output;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitList;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentListener;

import backend.GraphController;

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
		if (drawEdge != null) {
			Point origin = drawEdge.getLocation();
//			g2.setStroke(new BasicStroke(1f));
			for (Node node : nodeL) {
				int margin = 15;
				// check if mouse is within this dimensions of a node
				if(isWithin2DRange(mouse, node.getOrigin(), node.getDimension(), margin)) {

					// draw every pin
					for (Pin pin : ((UnitElement)node).getInputs()) {
						if(!drawEdge.getParent().equals(node)) 
							drawCompatbilityIndicator(g2, margin, pin);
					}

					for (Pin pin : ((UnitElement)node).getOutputs()) {
						if(!drawEdge.getParent().equals(node))
							drawCompatbilityIndicator(g2, margin, pin);
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
			g2.setColor(Color.GRAY);
//			g2.setStroke(dashed);
			g2.setStroke(new BasicStroke(1f));
			g2.drawRect(rectToDraw.x, rectToDraw.y, 
					rectToDraw.width - 1, rectToDraw.height - 1);
			g2.setColor(new Color(0,0,88, 100));
			g2.fillRect(rectToDraw.x, rectToDraw.y, 
					rectToDraw.width - 1, rectToDraw.height - 1);
		}
	}


	private void drawCompatbilityIndicator(Graphics2D g2, int margin, Pin pin) {
		int diameter = 15;
		Point pinLocation = pin.getLocation();
		int pinX = pinLocation.x - (diameter/2); 
		int pinY = pinLocation.y - (diameter/2);

		// draw pin marker if mouse within inner range
		int lowerYpin = pin.getLocation().y - margin;
		int upperYpin = pin.getLocation().y + margin;
		if(isWithin2DRange(mouse, pin.getLocation(), new Dimension(0,0), margin)) {

			Input input;
			Output output;
			boolean isCompatible = false;
			boolean isLoop = false;
			
			// we don't know, if the connection is created 
			// input first or output first
			if(drawEdge instanceof Output
					&& pin instanceof Input) {
				isCompatible = ((Output)drawEdge).isImageBitDepthCompatible(((Input)pin).getImageBitDepth());
				isLoop = ((Input)pin).knows(drawEdge.getParent());
			} else if (drawEdge instanceof Input
					&& pin instanceof Output) {
				isCompatible = ((Input)drawEdge).isImageBitDepthCompatible(((Output)pin).getImageBitDepth());
			}
										
			
			
			
			
			g2.setColor(
					(isCompatible && !isLoop) ? Color.green : Color.red);
			Ellipse2D.Double circle = 
				new Ellipse2D.Double(pinX, pinY, diameter, diameter);
			g2.fill(circle);
			g2.setColor(new Color(0,0,0,44));
			g2.draw(circle);
			
			

			String errorMessage = "";
			if(!isCompatible)
				errorMessage += "Incompatible bit depth";
			else if(isLoop) 
				errorMessage += "Loops are not allowed";
			
			if(errorMessage.length() != 0) 
				drawErrorMessage(g2, errorMessage, pin.getLocation());
			//TODO here also check if there are loops
			
		}
	}
	
	private void drawErrorMessage(Graphics2D g2, String text, Point origin) {
		g2.setFont(new Font("Arial", Font.PLAIN, 12));
		FontMetrics fm = g2.getFontMetrics();
		g2.setColor(new Color(200,200,200));
		if (text == null) {
			System.out.println("Node.paint: Null Pointer Exception");
			text = "*Null Pointer Exception*"; //zz now the exception is hidden, but what the problem?
		}
		Dimension dimension = new Dimension();
		dimension.setSize(fm.stringWidth(text) + 10, fm.getHeight() + 4);
		int padding = 2;
		g2.fillRoundRect(origin.x-padding, origin.y-padding, 
				dimension.width+padding, dimension.height+padding, 4, 4);

	    g2.setStroke(new BasicStroke(1f));
//	    g2.setColor(new Color(0,0,0,44));
	    g2.drawRoundRect(origin.x-padding, origin.y-padding, 
				dimension.width+padding, dimension.height+padding, 4, 4);
	    g2.setColor(Color.BLACK);
		g2.drawString(text, origin.x + 5, (origin.y + padding) + fm.getAscent());
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
	 * Returns true, if the current point is within this 2D-range.
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


	public void setGraphController(GraphController graphController) {
		this.nodeL = graphController.getUnitElements();
		this.EdgeL = graphController.getConnections();
		this.selection.clear();
	}
	
	
}
