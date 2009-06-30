/*
Version 1.0, 30-12-2007, First release
Version 1.1, 03-02-2008, added component <version> handling, prepared for MDI support

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
 */

/* used by VisualAp.java

javalc6

todo:
- migliorare gestione delle exception interne: ExceptionListener in XMLDecoder
- estendere <selection> ad altri oggetti per esempio: Edge
 */
package visualap;


import ij.IJ;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;

import de.danielsenff.imageflow.models.Delegate;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;

public class GPanel extends JPanel implements Printable, MouseListener, MouseMotionListener  {

	protected ArrayList<Delegate> beans;
	protected GPanelListener parent;

	protected Point pick = null;
	protected Selection<Node> selection = new Selection<Node>();
	protected GList<Node> nodeL = new GList<Node>();
	protected Pin drawEdge;
	protected ConnectionList connectionList = new ConnectionList();
	protected Point mouse;


	boolean cursor=true; // cursor is under control?
	protected Rectangle rect;
	JMenu newMenu;

	// handling of selection rectange
	protected Rectangle currentRect = null;
	protected Rectangle rectToDraw = null;
	protected Rectangle previousRectDrawn = new Rectangle();
	final static float dash1[] = {5.0f};
	protected final static BasicStroke dashed = new BasicStroke(1.0f, 
			BasicStroke.CAP_BUTT, 
			BasicStroke.JOIN_MITER, 
			10.0f, dash1, 0.0f);
	// MDI Support 
	JInternalFrame frame;

	public GPanel(ArrayList<Delegate> beans, GPanelListener parent) {
		this.beans = beans;
		this.parent = parent;
		addMouseListener(this);
		addMouseMotionListener(this);
		setBackground(Color.white);
	}



	public void clear() {
		nodeL.clear();
		connectionList.clear();
		nodeL.setChanged(false);
		selection.clear();
		repaint();
	}

	public void setTitle(String title) {
		frame.setTitle(title);
	}


	/**
	 * paint things that eventually go on a printer
	 * @param g
	 */
	public void paintPrintable(Graphics g) {
		rect = new Rectangle();
		for (Node t : getNodeL()) {
			rect = rect.union(t.paint(g, this));
		}
		setPreferredSize(rect.getSize());
		for (Connection aEdge : connectionList) {
			paintPrintableConnection(g, aEdge);
		}
	}



	protected void paintPrintableConnection(Graphics g, Connection aEdge) {
		Point from = aEdge.getInput().getLocation();
		Point to = aEdge.getOutput().getLocation();
		g.drawLine(from.x, from.y, to.x, to.y);
	}


	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// paint printable items
		paintPrintable(g);
		// paint non printable items
		if (drawEdge!= null)	{
			Point origin = drawEdge.getLocation();

			Graphics2D g2 = (Graphics2D) g;
			float lineWidth = 1.0f;
			g2.setStroke(new BasicStroke(lineWidth));
			g2.drawLine(origin.x, origin.y, mouse.x, mouse.y);
			g2.draw(new Line2D.Double(origin.x, origin.y, mouse.x, mouse.y));
		}
		//If currentRect exists, paint a box on top.
		if (currentRect != null) {
			Graphics2D g2 = (Graphics2D) g;
			//Draw a rectangle on top of the image.
			g2.setXORMode(Color.white); //Color of Edge varies
			//depending on image colors
			g2.setStroke(dashed);
			g2.drawRect(rectToDraw.x, rectToDraw.y, 
					rectToDraw.width - 1, rectToDraw.height - 1);
		}
	}



	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			if (selection.size() == 1) properties(selection.get(0));
			else selection.clear(); //zz to be handled in more completed way
		}
	}

	protected void properties(Node node) {}



	public void mousePressed(MouseEvent e) {
		// generato nell'istante in cui il mouse viene premuto
		int x = e.getX();
		int y = e.getY();
		// qui � obbligatorio un iteratore che scandisce la lista al contrario!
		for (ListIterator<Node> it = nodeL.listIterator(nodeL.size()); it.hasPrevious(); ) {
			Node aNode = it.previous();
			Object sel = aNode.contains(x,y);
			// 	check selected element, is it a Node?
			if (sel instanceof Node) {
				pick = new Point(x,y);
				if (!selection.contains(aNode)) {
					if(!e.isControlDown()
							|| e.isMetaDown() && IJ.isMacintosh()) {
						selection.clear();
					}
					selection.add(aNode);
				} else {
					if(e.isControlDown()
							|| e.isMetaDown() && IJ.isMacintosh())
						selection.remove(aNode);
				} 
				
				for (Node iNode : selection)
					if(!e.isPopupTrigger())
						iNode.drag(true);
				repaint();
				parent.showFloatingMenu(e);
				e.consume();
				changeCursor(Cursor.MOVE_CURSOR);
				return;
			}
			// check selected element, is it a Pin?
			else if (sel instanceof Pin) {
				drawEdge = (Pin) sel;
				//	System.out.println(drawEdge);
				mouse = new Point (x,y);
				changeCursor(Cursor.CROSSHAIR_CURSOR);
				return;
			} 
			// change
			else {
				selection.clear();
			}


		}
		parent.showFloatingMenu(e);	

		selection.clear();

		//	e.consume();

		// handling of selection rectange 
		currentRect = new Rectangle(x, y, 0, 0);
		updateDrawableRect(getWidth(), getHeight());
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		// generato quando il mouse viene rilasciato, anche a seguito di click
		int x = e.getX();
		int y = e.getY();
		if (pick != null) {
			for (Node iNode : selection) {
				if (cursor) iNode.translate(x-pick.x, y-pick.y);
				iNode.drag(false);
			}
			pick = null;
			repaint();
			e.consume();
			changeCursor(Cursor.DEFAULT_CURSOR);
		}
		else if (drawEdge != null)	{
			// insert new Edge if not already present in EdgeL
			for (ListIterator<Node> it = nodeL.listIterator(nodeL.size()); it.hasPrevious(); ) {
				Node aNode = it.previous();
				Object sel = aNode.contains(x,y);
				if ((sel instanceof Pin)&&(!drawEdge.equals(sel))) {
					if (!connectionList.containsConnection(drawEdge, (Pin) sel)) {
						connectionList.add(drawEdge, (Pin) sel);
					}
				}

			}
			drawEdge = null;
			changeCursor(Cursor.DEFAULT_CURSOR);
			repaint();
		}
		// handling of selection rectangle
		else if (currentRect != null) {
			normalizeRect();
			for (Node aNode : nodeL)
				if (aNode.contained(currentRect)) {
					selection.add(aNode);
				}
			currentRect = null;
			repaint();
		}
		
		parent.showFloatingMenu(e);
		//	e.consume();
	}

	public void mouseDragged(MouseEvent e) {
		// generato quando il mouse premuto viene spostato, vari eventi sono generati durante il trascinamento
		if (pick!= null) {
			for (Node iNode : selection)
				iNode.drag(e.getX()-pick.x, e.getY()-pick.y);
			repaint();
			e.consume();
		}
		else if (drawEdge != null)	{
			mouse.x = e.getX(); mouse.y = e.getY();
			repaint();
			e.consume();
		}
		// handling of selection rectange
		else if (currentRect != null) updateSize(e);
	}

	public void mouseMoved(MouseEvent e) {
		// generato quando il mouse viene spostato senza essere premuto
	}
	public void mouseEntered(MouseEvent e) {
		// generato quando il mouse entra nella finestra
		cursor = true;
	}
	public void mouseExited(MouseEvent e) {
		// generato quando il mouse esce dalla finestra
		cursor = false;
	}

	protected void changeCursor(int cursor) {
		setCursor(Cursor.getPredefinedCursor(cursor));
	}

	void updateSize(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		currentRect.setSize(x - currentRect.x, y - currentRect.y);
		updateDrawableRect(getWidth(), getHeight());
		Rectangle totalRepaint = rectToDraw.union(previousRectDrawn);
		repaint(totalRepaint.x, totalRepaint.y,
				totalRepaint.width, totalRepaint.height);
	}

	protected void updateDrawableRect(int compWidth, int compHeight) {
		int x = currentRect.x;
		int y = currentRect.y;
		int width = currentRect.width;
		int height = currentRect.height;

		//Make the width and height positive, if necessary.
		if (width < 0) {
			width = 0 - width;
			x = x - width + 1; 
			if (x < 0) {
				width += x; 
				x = 0;
			}
		}
		if (height < 0) {
			height = 0 - height;
			y = y - height + 1; 
			if (y < 0) {
				height += y; 
				y = 0;
			}
		}
		//The rectangle shouldn't extend past the drawing area.
		if ((x + width) > compWidth) {
			width = compWidth - x;
		}
		if ((y + height) > compHeight) {
			height = compHeight - y;
		}

		//Update rectToDraw after saving old value.
		if (rectToDraw != null) {
			previousRectDrawn.setBounds(
					rectToDraw.x, rectToDraw.y, 
					rectToDraw.width, rectToDraw.height);
			rectToDraw.setBounds(x, y, width, height);
		} else {
			rectToDraw = new Rectangle(x, y, width, height);
		}
	}

	private void normalizeRect() {
		int x = currentRect.x;
		int y = currentRect.y;
		int width = currentRect.width;
		int height = currentRect.height;

		//Make the width and height positive, if necessary.
		if (width < 0) {
			width = - width;
			x = x - width + 1; 
			if (x < 0) {
				width += x; 
				x = 0;
			}
		}
		if (height < 0) {
			height = - height;
			y = y - height + 1; 
			if (y < 0) {
				height += y; 
				y = 0;
			}
		}
		currentRect.setBounds(x, y, width, height);
	}

	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
		if (pi >= 1) {
			return Printable.NO_SUCH_PAGE;
		}
		g.translate((int)pf.getImageableX(),(int)pf.getImageableY());
		paintPrintable(g);
		return Printable.PAGE_EXISTS;
	}

	public String shortName(String fullName) {
		int ix = fullName.lastIndexOf('.');
		if (ix >= 0) {
			return fullName.substring(ix+1);
		} else	return fullName;
	}

	/**
	 * @return the nodeL
	 */
	public GList<Node> getNodeL() {
		return this.nodeL;
	}

	/**
	 * @param nodeL the nodeL to set
	 */
	public void setNodeL(GList<Node> nodeL) {
		this.nodeL = nodeL;
	}

	/**
	 * @return the selection
	 */
	public Selection<Node> getSelection() {
		return this.selection;
	}

	/**
	 * @return the edgeL
	 */
	public ConnectionList getEdgeL() {
		return this.connectionList;
	}

	/**
	 * @param edgeL the edgeL to set
	 */
	public void setEdgeL(ConnectionList edgeL) {
		this.connectionList = edgeL;
	}

};