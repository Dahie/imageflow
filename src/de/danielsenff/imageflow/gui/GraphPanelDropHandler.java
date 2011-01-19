package de.danielsenff.imageflow.gui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.zip.DataFormatException;

import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.delegates.UnitDelegate;
import de.danielsenff.imageflow.models.delegates.UnitDescription;
import de.danielsenff.imageflow.models.unit.SourceUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;

public class GraphPanelDropHandler implements DropTargetListener {

	private GraphPanel gPanel;
	
	public GraphPanelDropHandler(final GraphPanel panel) {
		this.gPanel = panel;
	}
	
	// mouse enters component
	public void dragEnter(final DropTargetDragEvent e) {}

	// mouse leaves component 
	public void dragExit(final DropTargetEvent e) {}

	// mouse over component
	public void dragOver(final DropTargetDragEvent e) {}

	public void drop(final DropTargetDropEvent e) {
		Point point = e.getLocation();
		point.translate(-50, -50);
		gPanel.getSelection().clear();
		
		try {
			final Transferable tr = e.getTransferable();
			final DataFlavor[] flavors = tr.getTransferDataFlavors();
			final List files;
			File file;
			for (int i = 0; i < flavors.length; i++)
				if (flavors[i].isFlavorJavaFileListType()) {
					// accept for now
					e.acceptDrop (e.getDropAction());
					files = (List) tr.getTransferData(flavors[i]);

					/*
					 * TODO unused
					 */
					for (int j = 0; j < files.size(); j++) {
						file = (File) files.get(j);
						
						UnitDelegate delegate= DelegatesController.getInstance().getDelegate("Image Source");
						String[] args = {file.getAbsolutePath()};
						UnitElement unit = UnitFactory.createProcessingUnit(delegate.getUnitDescription(), point, args);
						gPanel.getNodeL().add(unit);
						point.x += 100;
						point.y += 100;
					}

					e.dropComplete(true);
					return;
				} else if (flavors[i].isFlavorSerializedObjectType()) {
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					Object o = tr.getTransferData(flavors[i]);
					
					UnitDelegate delegate = DelegatesController.getInstance().getDelegate((String) o);
					UnitElement unit = UnitFactory.createProcessingUnit(delegate.getUnitDescription(), point);
					gPanel.getNodeL().add(unit);
					
					e.dropComplete(true);
					return;
				}



		} catch (final Throwable t) { t.printStackTrace(); }
		// a problem happened
		e.rejectDrop();
	}

	public void dropActionChanged(final DropTargetDragEvent e) {}

}
