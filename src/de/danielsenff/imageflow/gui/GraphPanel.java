/**
 * 
 */
package de.danielsenff.imageflow.gui;

import ij.IJ;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import visualap.GList;
import visualap.GPanel;
import visualap.GPanelListener;
import visualap.Node;
import visualap.Pin;
import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.Delegate;
import de.danielsenff.imageflow.models.SelectionList;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.unit.CommentNode;
import de.danielsenff.imageflow.models.unit.SourceUnitElement;
import de.danielsenff.imageflow.models.unit.UnitDescription;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;
import de.danielsenff.imageflow.models.unit.UnitList;
import de.danielsenff.imageflow.utils.Tools;

/**
 * Graphical workspace on which the units are drawn and which handles the mouse actions.
 * @author danielsenff
 *
 */
public class GraphPanel extends GPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Draw a small grid on the {@link GraphPanel}
	 */
	protected boolean drawGrid = false;

	
	/**
	 * size of the grid
	 */
	public static int GRIDSIZE = 30; 
	/**
	 * Auto align nodes
	 */
	protected boolean align = false;

	private BufferedImage iwIcon;
	
	
	
	

	private String iwFilePath;

	/**
	 * @param panel
	 */
	public GraphPanel(final GPanelListener panel) {
		this(new ArrayList<Delegate>(), panel);
	}
	
	/**
	 * @param panel
	 * @param graphController
	 */
	public GraphPanel(final GPanelListener panel, final GraphController graphController) {
		this(new ArrayList<Delegate>(), panel);
		
		setGraphController(graphController);
	}
	
	/**
	 * @param delegates 
	 * @param parent
	 */
	public GraphPanel(final ArrayList<Delegate> delegates, final GPanelListener parent) {
		super(delegates, parent);
		
		this.iwFilePath = 
			"/de/danielsenff/imageflow/resources/iw-logo.png";
//			getResourceMap().getString("Background.image");

    	JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    	if(!IJ.isMacintosh())
    		this.setBorder(BorderFactory.createLoweredBevelBorder());
		
		final DropTargetListener dropTargetListener = new DropTargetListener() {
			// Die Maus betritt die Komponente mit
			// einem Objekt
			public void dragEnter(final DropTargetDragEvent e) {}

			// Die Komponente wird verlassen 
			public void dragExit(final DropTargetEvent e) {}

			// Die Maus bewegt sich �ber die Komponente
			public void dragOver(final DropTargetDragEvent e) {}

			public void drop(final DropTargetDropEvent e) {
				try {
					final Transferable tr = e.getTransferable();
					final DataFlavor[] flavors = tr.getTransferDataFlavors();
					for (int i = 0; i < flavors.length; i++)
						if (flavors[i].isFlavorJavaFileListType()) {
							// Zun�chst annehmen
							e.acceptDrop (e.getDropAction());
							final List files = (List) tr.getTransferData(flavors[i]);

							/*
							 * TODO unused
							 */
							for (int j = 0; j < files.size(); j++) {
								File file = (File) files.get(j);
								
								UnitDescription sourceUnitDescription = 
									new UnitDescription(
										new URL(DelegatesController.getInstance().getResourcesBase(),
											"xml_units/ImageSource_Unit.xml"));
								final SourceUnitElement sourceUnit = 
									(SourceUnitElement) UnitFactory.createProcessingUnit(sourceUnitDescription, new Point(30,100));
								sourceUnit.setFilePath(file.getAbsolutePath());
								getNodeL().add(sourceUnit);
							}
							
							e.dropComplete(true);
							return;
						}
				} catch (final Throwable t) { t.printStackTrace(); }
				// Ein Problem ist aufgetreten
				e.rejectDrop();
			}

			// Jemand hat die Art des Drops (Move, Copy, Link)
			// ge�ndert
			public void dropActionChanged(final DropTargetDragEvent e) {}

		};
		final DropTarget dropTarget = new DropTarget(this, dropTargetListener);
		this.setDropTarget(dropTarget);
		
		
		try {
			this.iwIcon = ImageIO.read(this.getClass().getResourceAsStream(iwFilePath));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
	}


	@Override
	protected void paintPrintableConnection(final Graphics g, final Connection connection) {
		final Point from = connection.getInput().getLocation();
		final Point to = connection.getOutput().getLocation();
		g.setColor(  (connection.isCompatible()) ? Color.BLACK : Color.RED );
		g.drawLine(from.x, from.y, to.x, to.y);
		
		if(connection.isLocked()) {
			final int dX = Math.abs(from.x - to.x)/2 + Math.min(from.x, to.x);
			final int dY = Math.abs(from.y - to.y)/2 + Math.min(from.y, to.y);
			final Point origin = new Point(dX, dY);
			
			g.setColor(Color.BLACK);
			final Ellipse2D.Double circle = 
				new Ellipse2D.Double(origin.getX()-5, origin.getY()-5, 10, 10);
			((Graphics2D)g).draw(circle);
			g.fillRect(origin.x-5, origin.y, 10, 10);
		}
		
		if(!connection.isCompatible()) {
			final int dX = Math.abs(from.x - to.x)/2 + Math.min(from.x, to.x);
			final int dY = Math.abs(from.y - to.y)/2 + Math.min(from.y, to.y);
			final Point origin = new Point(dX, dY);
			drawErrorMessage((Graphics2D) g, "incompatible data type", origin);
		}
	}
	

	/* (non-Javadoc)
	 * @see visualap.GPanel#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(final Graphics g) {
//		super.paint(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if(!getUnitList().isEmpty()) {

			//paint grid
			paintGrid(g2);
			
			// paint printable items
			paintPrintable(g2);

			// paint non printable items
			if (drawEdge != null) {
				// drag new connection
				
				for (final Node node : nodeL) {
					int margin = 18;
					// get units margin, could be lesser than 18 if unit has many pins
					if (node instanceof UnitElement
							&& (!drawEdge.getParent().equals(node)) ) {
						margin = ((UnitElement) node).getPinTolerance();
						// check if mouse is within this dimensions of a node#
						if(isWithin2DRange(mouse, node.getOrigin(), node.getDimension(), margin)) {

							// draw every pin
							for (final Pin pin : ((UnitElement)node).getInputs()) {
								drawCompatbilityIndicator(g2, margin, pin);
							}

							for (final Pin pin : ((UnitElement)node).getOutputs()) {
								drawCompatbilityIndicator(g2, margin, pin);
							}	
						}
					}
				}

				final Point origin = drawEdge.getLocation();
				g2.setColor(Color.BLACK);
				g2.drawLine(origin.x, origin.y, mouse.x, mouse.y);
				g2.draw(new Line2D.Double(origin.x, origin.y, mouse.x, mouse.y));
			}
			//If currentRect exists, paint a box on top.
			if (currentRect != null) {
				//Draw a rectangle on top of the image.
				//depending on image colors
				g2.setColor(new Color(0,0,255, 80));
				//			g2.setStroke(dashed);
				g2.setStroke(new BasicStroke(1f));
				g2.drawRect(rectToDraw.x, rectToDraw.y, 
						rectToDraw.width - 1, rectToDraw.height - 1);
				g2.setColor(new Color(0,0,255, 40));
				g2.fillRect(rectToDraw.x, rectToDraw.y, 
						rectToDraw.width - 1, rectToDraw.height - 1);
			}	
		} else {
			//draw a nice message to promote creating a graph

			drawWelcome(g2);
		}
	}

	/**
	 * draw a nice message to promote creating a graph
	 * @param g2
	 */
	private void drawWelcome(final Graphics2D g2) {
		final int x = 50;
		final int y = 80;

		g2.drawImage(this.iwIcon, 25, 25, null);
		
		final String headline = 
			"Create your workflow";
//			getResourceMap().getString("Intro.headline"); 
		final String description = "Add new units to the graph by using the" + '\n'
			+"context menu units on this canvas." + '\n' + "   " + '\n'
			+ "A workflow is constructed from a Source-Unit and requires a Display-Unit." + '\n'
			+ "The Display-Unit is the image that will be displayed after running the workflow.";

		final Vector<String> lines = tokenizeString(description, "\n");
		
		g2.setColor(Color.GRAY);
		
		// scale font on big lengths
		final int fontsize = 24;
		final int fontsizeOriginal = 12;
		final Font font = g2.getFont();
		final Font newFont = new Font(font.getFamily(), Font.BOLD, fontsize);
		g2.setFont(newFont);
		// and if even now to small, then cut
		g2.drawString(headline, x+5, y+15);
		g2.setFont(new Font(font.getFamily(), Font.PLAIN, fontsizeOriginal));
		int lineOffset = 45;
		for (final String line : lines) {
			lineOffset +=20;
			g2.drawString(line, x+5, y+lineOffset);	
		}
	}

	/**
	 * @return the align
	 */
	public boolean isAlign() {
		return align;
	}

	/**
	 * @param align the align to set
	 */
	public void setAlign(final boolean align) {
		this.align = align;
		if(align) {
			alignElements();
		}
	}

	
	/**
	 * aligns the elements to the grid
	 */
	public void alignElements() {
		for (final Node node : getUnitList()) {
			alignElement(node);
		}
	}

	/**
	 * @param node
	 */
	private void alignElement(final Node node) {
		final Point origin = node.getOrigin();
		int x = origin.x;
		int y = origin.y;
		
		final int row = y / GRIDSIZE;
		final int column = x / GRIDSIZE;
		
		x = column * GRIDSIZE + 10;
		y = row * GRIDSIZE + 10;
		
		origin.x = x;
		origin.y = y;
	}

	/**
	 * @return
	 */
	protected Collection<Node> getUnitList() {
		return this.nodeL;
	}

	public ResourceMap getResourceMap() {
		return Application.getInstance().getContext().getResourceMap(GraphPanel.class);
	}

	/** 
	 * paints a simple grid on the canvas
	 * @param g
	 */
	private void paintGrid(final Graphics g) {
		if(drawGrid) {
			g.setColor(new Color(240, 240, 240));
			for (int x = 0; x < this.getWidth(); x+=GRIDSIZE) {
				g.drawLine(x, 0, x, getHeight());
			}
			for (int y = 0; y < this.getHeight(); y+=GRIDSIZE) {
				g.drawLine(0, y, getWidth(), y);
			}	
		}
	}

	private void drawCompatbilityIndicator(final Graphics2D g2, final int margin, final Pin pin) {
		final int diameter = 15;
		final Point pinLocation = pin.getLocation();
		final int pinX = pinLocation.x - (diameter/2); 
		final int pinY = pinLocation.y - (diameter/2);

		// draw pin marker if mouse within inner range
		if(isWithin2DRange(mouse, pin.getLocation(), new Dimension(0,0), margin)) {

			boolean isCompatible = false;
			boolean isLoop = false;
			boolean isLocked = false;

			if( !(drawEdge instanceof Output && pin instanceof Output)
					&& !(drawEdge instanceof Input && pin instanceof Input)) {

				// we don't know, if the connection is created 
				// input first or output first
//				isCompatible = drawEdge.isCompatible(pin);
				isCompatible = pin.isCompatible(drawEdge);
				if(drawEdge instanceof Output && pin instanceof Input) {
					isLoop = ((Input)pin).isConnectedInOutputBranch(drawEdge.getParent());
				}
				if (drawEdge instanceof Input && pin instanceof Output) {
					isLoop = ((Output)pin).existsInInputSubgraph(drawEdge.getParent());
				}
				if(drawEdge.isLocked() || pin.isLocked() ) {
					isLocked = true;
				}

				g2.setColor((isCompatible && !isLoop) ? Color.green : Color.red);
				final Ellipse2D.Double circle = 
					new Ellipse2D.Double(pinX, pinY, diameter, diameter);
				g2.fill(circle);
				g2.setColor(new Color(0,0,0,44));
				g2.draw(circle);

				String errorMessage = "";
				if(!isCompatible)
					errorMessage += "Incompatible data type \n";
				if(isLoop) 
					errorMessage += "Loops are not allowed\n";
				if(isLocked) 
					errorMessage += "Pin is locked\n";

				if(errorMessage.length() != 0) 
					drawErrorMessage(g2, errorMessage, pin.getLocation());
			}
		}
	}

	private void drawErrorMessage(final Graphics2D g2, final String text, final Point origin) {
		
		final Vector<String> lines = tokenizeString(text, "\n");
		
		g2.setColor(Color.RED);
		final Ellipse2D.Double circle = 
			new Ellipse2D.Double(origin.getX()-3, origin.getY()-3, 5, 5);
		g2.fill(circle);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 12));
		final FontMetrics fm = g2.getFontMetrics();
		
		
		
		
		final Dimension dimension = new Dimension();
		String longestLine = "";
		for (final String line : lines) {
			if(line.length() > longestLine.length())
				longestLine = line;
		}
		
		dimension.setSize(fm.stringWidth(longestLine) + 10, (fm.getHeight() + 4)*lines.size());
		final int padding = 2;
		
		g2.setColor(new Color(255,255,180));
		g2.fillRoundRect(origin.x-padding, origin.y-padding, 
				dimension.width+padding, dimension.height+padding, 4, 4);
		g2.setStroke(new BasicStroke(1f));
		g2.setColor(new Color(255,0,0,44));
		g2.drawRoundRect(origin.x-padding, origin.y-padding, 
				dimension.width+padding, dimension.height+padding, 4, 4);
		
		
		g2.setColor(Color.BLACK);
		
		final int lineheight = fm.getHeight() + 5;
		int yT = (origin.y + padding) + fm.getAscent();
		for (final String line : lines) {
			g2.drawString(line, origin.x + 5, yT);
			yT += lineheight;
		}
	}

	private Vector<String> tokenizeString(final String text, final String token) {
		final StringTokenizer stringTokenizer = new StringTokenizer(text, token);
		final Vector<String> lines = new Vector<String>();
		while(stringTokenizer.hasMoreTokens()) {
			lines.add(stringTokenizer.nextToken());
		}
		return lines;
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
		return (compareValue > startValue) 
		&& (compareValue < endValue);
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
	
	@Override
	public void mouseReleased(final MouseEvent e) {
		super.mouseReleased(e);
		if(align)
			alignElements();
		this.requestFocusInWindow(true);
	}

	/**
	 * Replace the current {@link UnitList} with a different one.
	 * @param units2
	 */
	public void setNodeL(final GList<Node> units2) {
		super.nodeL = units2;
	}

	/**
	 * If true, a grid is drawn on the workspace
	 * @return
	 */
	public boolean isDrawGrid() {
		return drawGrid;
	}

	public void setDrawGrid(final boolean drawGrid) {
		this.drawGrid = drawGrid;
	}
	
	@Override
	public void properties(final Node node) {
		if (node instanceof CommentNode) {
			final String inputValue = JOptionPane.showInputDialog("Edit text:",((CommentNode)node).getText()); 
			if ((inputValue != null) && (inputValue.length() != 0)) {
				((CommentNode)node).setText(inputValue);
				repaint();
			}
		}
		else { 
			final UnitElement unit = (UnitElement) node;
			unit.showProperties();
		}
	}

	public void setGraphController(final GraphController graphController) {
		this.selection.clear();
		this.nodeL = graphController.getUnitElements();
		this.connectionList = graphController.getConnections();
		this.selection = graphController.getSelections();
	}

	public void setSelections(final SelectionList selections) {
		this.selection = selections;
	}

}
