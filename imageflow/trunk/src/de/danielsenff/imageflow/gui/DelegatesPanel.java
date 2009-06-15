package de.danielsenff.imageflow.gui;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.Delegate;
import de.danielsenff.imageflow.models.unit.UnitDelegate;
import de.danielsenff.imageflow.models.unit.UnitList;


/**
 * Panel which displays the units that can be inserted into the workflow.
 * @author danielsenff
 *
 */
public class DelegatesPanel extends JPanel {

	private final HashMap<TreeNode, Delegate> delegates;

	/**
	 * @param unitList
	 */
	public DelegatesPanel(final UnitList unitList) {
		final DelegatesController delegatesController = DelegatesController.getInstance();
		this.delegates = delegatesController.getUnitDelegates();
		final DefaultTreeModel delegatesModel = delegatesController.getDelegatesModel();


		final JTree delegatesTree = new JTree(delegatesModel);
		delegatesTree.setRootVisible(false);
		delegatesTree.setToggleClickCount(1);
		/*delegatesTree.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(final MouseEvent e) {
				final DragGestureListener dragGestureListener = new DragGestureListener() {
					public void dragGestureRecognized(final DragGestureEvent e) {
						// Der Text des Label soll
						// JVM-intern �bertragen werden
						//						StringSelection selection = new StringSelection("test");
						final Transferable trans = new Transferable() {

							public Object getTransferData(final DataFlavor arg0)
							throws UnsupportedFlavorException,
							IOException {
								return null;
							}

							public DataFlavor[] getTransferDataFlavors() {
								return null;
							}

							public boolean isDataFlavorSupported(final DataFlavor arg0) {
								return false;
							}

						};
						e.startDrag(null, trans);
					} 
				};
				final DragSource dragSource = new DragSource();
				final DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(
						delegatesTree, DnDConstants.ACTION_MOVE, dragGestureListener);
			}

			public void mouseMoved(final MouseEvent e) {}
		});*/

		delegatesTree.addMouseListener(new MouseListener() {

			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final JTree tree = (JTree) e.getSource();

					final int selRow = tree.getRowForLocation(e.getX(), e.getY());
					final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					if(selRow != -1 && selPath.getLastPathComponent() instanceof UnitDelegate) {
						//			        	 myDoubleClick(selRow, selPath);
						final UnitDelegate ud = ((UnitDelegate)selPath.getLastPathComponent());
						Point insertPoint = UnitDelegate.POINT;
						unitList.add(ud.createUnit(insertPoint));
					}
				}
			}

			public void mouseEntered(final MouseEvent arg0) {}

			public void mouseExited(final MouseEvent arg0) {}

			public void mousePressed(final MouseEvent arg0) {}

			public void mouseReleased(final MouseEvent arg0) {}

		});
		
		
		delegatesTree.addKeyListener(new KeyListener() {

			public void keyPressed(final KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					final JTree tree = (JTree) e.getSource();
					
					if (tree.getSelectionRows() != null && tree.getSelectionRows().length > 0) {
						// with 'Enter' all selected Units were inserted, therefore used an array
						final int[] selRows = tree.getSelectionRows();
						
						final TreePath[] selPaths = new TreePath[selRows.length];
						for (int i = 0; i < selRows.length; i++) {
							selPaths[i] = tree.getPathForRow(selRows[i]);
						}
						
						final Point insertPoint = UnitDelegate.POINT;
						// counts only Units, not Folders
						int realUnitCount = 0;
						
						for (int i = 0; i < selRows.length; i++) {
							if(selRows[i] != -1 && selPaths[i].getLastPathComponent() instanceof UnitDelegate) {
								final UnitDelegate ud = ((UnitDelegate)selPaths[i].getLastPathComponent());
								unitList.add(ud.createUnit(new Point(insertPoint.x + realUnitCount * GraphPanel.GRIDSIZE,
										insertPoint.y + realUnitCount * GraphPanel.GRIDSIZE)));
								realUnitCount++;
							}
						}
					}
				}
			}

			public void keyTyped(final KeyEvent e) {}
			
			public void keyReleased(final KeyEvent e) {}

		});
		
		delegatesTree.setCellRenderer(new IFTreeCellRenderer());
		// makes delegatesPanel as big as the sidePane when resized
		this.setLayout(new BorderLayout());
		final JScrollPane scrollPane = new JScrollPane(delegatesTree);
		add(scrollPane, BorderLayout.CENTER);

	}

/*
	class IFTreeCellRenderer extends JPanel implements TreeCellRenderer {
		JLabel filename = new JLabel();
		private JLabel fileicon;
		private BufferedImage icon;


		public Component getTreeCellRendererComponent(final JTree tree, final Object value,
				final boolean isSelected, final boolean expanded, final boolean isLeaf, final int row,
				final boolean hasFocus) {

			init(value, isSelected, isLeaf);
			return this;//return component used to render
		}

		private void init(final Object value, final boolean isSelected, final boolean isLeaf) {
			String theLabel = ((MutableTreeNode)value).toString();
			String tooltip = "";
			
			setLayout(new BorderLayout());
			fileicon = new JLabel();
			if(isSelected) {	
//				setBackground(SystemColor.textHighlight);
//				filename.setForeground(SystemColor.textHighlightText);
			}else{	
//				setBackground(SystemColor.text);
//				filename.setForeground(SystemColor.textText);
			}
			filename.setText(theLabel);
			filename.setToolTipText(tooltip);
			filename.setAlignmentX(Component.LEFT_ALIGNMENT);
			
//			this.icon = drawIcon(Color.WHITE, 16, 16);
			try {
				this.icon = ImageIO.read(this.getClass().getResourceAsStream("/de/danielsenff/imageflow/resources/folder.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if (value instanceof UnitDelegate && isLeaf) {
				
				final UnitDelegate unitDelegate = (UnitDelegate)value;
				theLabel = unitDelegate.getName();
				tooltip = unitDelegate.getToolTipText();

				this.icon = drawIcon(unitDelegate.getColor(), 16, 16);
				fileicon = new JLabel(new Icon() {
					public int getIconHeight() { return 16;	}
					public int getIconWidth() { return 16; 	}
					public void paintIcon(final Component arg0, final Graphics g,
							final int arg2, final int arg3) {
						g.drawImage(icon, 0, 0, null);
					}
				});
			}

			
			fileicon.setPreferredSize(new Dimension(20, 16));
			this.add(fileicon, BorderLayout.LINE_START);
			this.add(filename, BorderLayout.CENTER);


		}

		private BufferedImage drawIcon(final Color color, final int width, final int height) {
			final BufferedImage icon = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			final Graphics2D g2 = icon.createGraphics();

			final int x=0, y=0;
			final int arc = 5;

			Color cTop = new Color(84, 121, 203, 255);
			Color cBottom = new Color(136, 169, 242, 255);

			final int delta = 20;

			final int r = color.getRed();
			final int g = color.getGreen();
			final int b = color.getBlue();

			cTop = new Color(
					(r-delta) > 255 ? 255 : r-delta,
					(g-delta) > 255 ? 255 : g-delta,
					(b-delta) > 255 ? 255 : b-delta);
			cBottom = new Color(
					(r+delta) > 255 ? 255 : r+delta,
					(g+delta) > 255 ? 255 : g+delta,
					(b+delta) > 255 ? 255 : b+delta);

			final GradientPaint gradient1 = new GradientPaint(x,y,cTop,x+10,y+10,cBottom);
			g2.setPaint(gradient1);
//			g2.fillRoundRect(x+2, y+2, width-4, height-4, arc, arc);
			g2.fillRect(x+2, y+2, width-4, height-4);

			g2.setStroke(new BasicStroke(1f));
			g2.setColor(new Color(0,0,0,44));
//			g2.drawRoundRect(x+2, y+2, width-4, height-4, arc, arc);
			g2.drawRect(x+2, y+2, width-4, height-4);

			return icon;
		}
	} */

}