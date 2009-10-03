package de.danielsenff.imageflow.gui;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.models.NodeListener;
import de.danielsenff.imageflow.models.unit.UnitDelegate;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;

/**
 * Listener vor {@link DelegatesPanel} related stuff
 * @author dahie
 *
 */
public class DelegatesTreeListener implements MouseListener, KeyListener {

	private ImageFlowView ifView;
	
	public DelegatesTreeListener(ImageFlowView ifView) {
		ifView = ifView;
	}
	
	private UnitList getNodes() {
		return ifView.getNodes();
	}
	
	private GraphPanel getGraphPanel() {
		return ifView.getGraphPanel();
	}
	
	private ImageFlowView getView() {
		return ifView;
	}
	
	public void mouseClicked(final MouseEvent e) {
		if (e.getClickCount() == 2) {
			final JTree tree = (JTree) e.getSource();

			final int selRow = tree.getRowForLocation(e.getX(), e.getY());
			final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
			if(selRow != -1 && selPath.getLastPathComponent() instanceof UnitDelegate) {
				//			        	 myDoubleClick(selRow, selPath);
				final UnitDelegate ud = ((UnitDelegate)selPath.getLastPathComponent());
				Point insertPoint = UnitDelegate.POINT;
				UnitElement node = ud.createUnit(insertPoint);
				((UnitElement)node).addModelListener(new NodeListener(getGraphPanel(), getView()));
				getNodes().add(node);
			}
		}
	}

	public void mouseEntered(final MouseEvent arg0) {}
	public void mouseExited(final MouseEvent arg0) {}
	public void mousePressed(final MouseEvent arg0) {}
	public void mouseReleased(final MouseEvent arg0) {}
	
	
	
	/*
	 * KeyListener
	 */
	
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
						UnitElement node = ud.createUnit(new Point(insertPoint.x + realUnitCount * GraphPanel.GRIDSIZE,
								insertPoint.y + realUnitCount * GraphPanel.GRIDSIZE));
						((UnitElement)node).addModelListener(new NodeListener(getGraphPanel(), getView()));
						getNodes().add(node);
						realUnitCount++;
					}
				}
			}
		}
	}

	public void keyTyped(final KeyEvent e) {}
	public void keyReleased(final KeyEvent e) {}
}
