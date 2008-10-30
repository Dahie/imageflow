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
import graph.Edge;
import graph.Edges;
import graph.GList;
import graph.Header;
import graph.Node;
import graph.NodeBean;
import graph.NodeText;
import graph.Pin;
import graph.Selection;

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
import java.beans.ExceptionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import property.PropertySheet;

public class GPanel extends JPanel implements Printable, MouseListener, MouseMotionListener  {

	final int z_default_blocksize = 1024;
	protected ArrayList<Delegate> beans;
	protected GPanelListener parent;

	protected Point pick = null;
	protected Selection<Node> selection = new Selection<Node>();
	protected GList<Node> nodeL = new GList<Node>();
	protected Pin drawEdge;
	protected Edges EdgeL = new Edges();
	protected Point mouse;

	HashMap<String, Object> globalVars = new HashMap<String, Object>();

	boolean cursor=true; // cursor is under control?
	Rectangle rect;
	String insertBeanName;
	Class insertBean = null;
	JMenu newMenu;
	PropertySheet propertySheet = new PropertySheet(null, null, 620, 20);
	HelpWindow hWindow;

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
		globalVars.put("blocksize", z_default_blocksize); // default value for blocksize
	}

	public GPanel(ArrayList<Delegate> beans, GPanelListener parent, File file, HashSet<String> updatel) throws IOException, VersionException {
		this(beans, parent);
		readXML(file, updatel);
	}
	

	public void clear() {
		globalVars.clear(); globalVars.put("blocksize", z_default_blocksize);
		nodeL.clear();
		EdgeL.clear();
		nodeL.setChanged(false);
		selection.clear();
		repaint();
		propertySheet.setVisible(false);
	}

	public void setTitle(String title) {
		frame.setTitle(title);
	}

	public void properties(Node aNode) {
		if (aNode instanceof NodeText) {
			propertySheet.setVisible(false);
			String inputValue = JOptionPane.showInputDialog("Edit text:",((NodeText)aNode).getText()); 
			if ((inputValue != null)&&(inputValue.length() != 0)) {
				((NodeText)aNode).setText(inputValue);
				repaint();
			}
		}
		else { // aNode instanceof NodeBean
			propertySheet.setVisible(true);
			propertySheet.setTarget(((NodeBean)aNode).getObject(), ((NodeBean)aNode).getLabel());
		}
	}

// checkVersion returns true only if ver2 is greater than ver
    public boolean checkVersion(String ver, String ver2) {
		int min;
		if (ver.length() > ver2.length())
			min = ver2.length();
		else min = ver.length();
		for (int i=0; i<min; i++) {
			if (ver.charAt(i) < ver2.charAt(i))
				return true;
			else if (ver.charAt(i) == ver2.charAt(i))
					continue;
				else break;
		}
		return false;
    }

	Exception failure;
	HashSet<String> updatel;
@SuppressWarnings("unchecked")
	public void readXML(File file, HashSet<String> updatelist) throws IOException, VersionException {
		updatel = updatelist;
		updatel.clear();
		propertySheet.setVisible(false);
		failure = null;
//zz bisogna gestire le exception interne: ExceptionListener in XMLDecoder
		java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(
						  new BufferedInputStream(new FileInputStream(file)));
		decoder.setExceptionListener(new ExceptionListener() {
			public void exceptionThrown(Exception e) {
//			 e.printStackTrace();
			if (e instanceof java.lang.ClassNotFoundException)	{
				updatel.add(e.getMessage());
			}
			 failure = e;
		}});
		Object [] al = null;
		GList<Node> nl;
		try {
			Object obj = decoder.readObject();
			if ((obj == null)|| !(obj instanceof Header)) 
				throw new IOException("Invalid file format");
			Header hd = (Header)obj;
			if (!hd.get("version").equals("1.0"))
				throw new IOException("Invalid version found: "+hd.get("version"));
			if (!hd.get("application").equals("VisualAp"))
				throw new IOException("Invalid application found: "+hd.get("application"));
			obj = decoder.readObject();
			if ((obj == null)|| !(obj instanceof HashMap)) 
				throw new IOException("Invalid file format");
			globalVars = (HashMap)obj; 
			obj = decoder.readObject();
			if ((obj == null)|| !(obj instanceof GList)) 
				throw new IOException("Invalid file format");
			nl = (GList<Node>)obj;  // unchecked conversion
			for (Node aNode : nl) {
				if (aNode instanceof NodeBean) {
					((NodeBean)aNode).setContext(globalVars);
					if (((NodeBean)aNode).getSerialUID() != ((NodeBean)aNode).getObjSerialUID())
						if (((NodeBean)aNode).getObject() != null)
							throw new IOException("Not valid SerialUID, found: "+((NodeBean)aNode).getObjSerialUID()+"\nRequired:"+((NodeBean)aNode).getSerialUID()+"\nLabel:"+aNode.getLabel());
// the following line has been commented after the new handling of decoder.setExceptionListener()
//							else throw new IOException("Plugin missing for label: "+aNode.getLabel());
							else continue;
					if (checkVersion(((NodeBean)aNode).getObjVersion().trim(),((NodeBean)aNode).getVersion().trim())) {
						updatel.add(((NodeBean)aNode).getObject().getClass().getName());
//						throw new IOException("Not valid version, found: "+((NodeBean)aNode).getObjVersion()+"\nRequired:"+((NodeBean)aNode).getVersion()+"\nLabel:"+aNode.getLabel());
					}
				}
			}
			if (!updatel.isEmpty()) {
				throw new VersionException("New version of component(s) is required");
			}
			al = (Object []) decoder.readObject();
			decoder.close();
		}
		catch (java.util.NoSuchElementException ex)	{
			throw new IOException("Invalid file format");
		}
		catch (java.lang.ArrayIndexOutOfBoundsException ex)	{
			throw new IOException("Invalid file format");
		}
		if (failure == null) {
			nodeL.clear();
			if (nl != null) {
				nodeL = nl;
				nodeL.updateLabels();
			}
			EdgeL.clear();
			if (al != null)
				for (Object t : al)
					EdgeL.add((String)t, nodeL.getLabels());
			repaint();
		} else {
			ErrorPrinter.dump(failure, VisualAp.getUniqueID());
			throw new IOException("Invalid file format: "+failure.toString());
		}
	}

	public void writeXML(File file) throws IOException {
		Header header = new Header("VisualAp", "Created on "+new Date().toString(), "1.0");
		nodeL.setChanged(false); // note: this statement must be performed before encoder.writeObject(nodeL);
		java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
		encoder.writeObject(header);
		encoder.writeObject(globalVars);
		encoder.writeObject(nodeL);
//		encoder.setPersistenceDelegate(Edge.class, new DefaultPersistenceDelegate(new String[]{"from", "to"}));
//		encoder.writeObject(EdgeL);
		ArrayList<String> al = new ArrayList<String>();
		for (Edge t : EdgeL)
			al.add(t.toString());
		encoder.writeObject(al.toArray());
		encoder.close();
	}

	/**
	 * paint things that eventually go on a printer
	 * @param g
	 */
    public void paintPrintable(Graphics g) {
        rect = new Rectangle();
		for (Node t : nodeL) {
			rect = rect.union(t.paint(g, this));	
		}
        setPreferredSize(rect.getSize());
		for (Edge aEdge : EdgeL) {
			Point from = aEdge.from.getLocation();
			Point to = aEdge.to.getLocation();
			g.drawLine(from.x, from.y, to.x, to.y);
		}
		revalidate();
    }

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// paint printable items
		paintPrintable(g);
		// paint non printable items
		if (drawEdge!= null)	{
			Point origin = drawEdge.getLocation();

			Graphics2D g2 = (Graphics2D) g;
			float lineWidth = 1.5f;
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
// generato quando il mouse viene premuto e subito rilasciato (click)
		if (e.getClickCount() > 1)
// > 1 se doppio click
//			if ((selection.size() == 1)&&(selection.get(0).edit())) repaint();
			if (selection.size() == 1) properties(selection.get(0));
			else selection.clear(); //zz to be handled in more completed way
    }

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
			else if (sel instanceof Pin) {
				drawEdge = (Pin) sel;
				//	System.out.println(drawEdge);
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
					if (!EdgeL.contains(drawEdge, (Pin) sel)) {
						EdgeL.add(drawEdge, (Pin) sel);
					}
				}
					
			}
			drawEdge = null;
			changeCursor(Cursor.DEFAULT_CURSOR);
			repaint();
		}
// handling of selection rectange
		else if (currentRect != null) {
			normaliseRect();
			for (Node aNode : nodeL)
				if (aNode.contained(currentRect)) {
					selection.add(aNode);
				}
			currentRect = null;
			repaint();
		}
// insert a bean if pending...
		if (insertBean != null) {
			try {
				NodeBean newItem = new NodeBean(new Point(x,y),insertBean.newInstance());
				System.out.println("bean object created");
				newItem.setContext(globalVars);
				nodeL.add(newItem,insertBeanName);
				selection.clear();
				selection.add(newItem);
				insertBean = null;
				repaint();
			} catch (Exception ex) {
				ErrorPrinter.printInfo("instantion of a new bean failed");
				ErrorPrinter.dump(ex, VisualAp.getUniqueID());
				ex.printStackTrace();
			}
			e.consume();
			changeCursor(Cursor.DEFAULT_CURSOR);
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

	private void normaliseRect() {
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

	protected String shortName(String fullName) {
		int ix = fullName.lastIndexOf('.');
		if (ix >= 0) {
			return fullName.substring(ix+1);
		} else	return fullName;
	}

// insert a bean: originated by ToolBox.java
    public void doInsert(Class bean, String beanName) {
		changeCursor(Cursor.CROSSHAIR_CURSOR);
		insertBeanName = shortName(beanName);
		insertBean = bean;
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
	public Edges getEdgeL() {
		return this.EdgeL;
	}

	/**
	 * @param edgeL the edgeL to set
	 */
	public void setEdgeL(Edges edgeL) {
		this.EdgeL = edgeL;
	}

	public HashMap<String, Object> getGlobalVars() {
		return this.globalVars;
	}

};