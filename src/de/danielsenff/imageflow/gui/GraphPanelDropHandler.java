/**
 * Copyright (C) 2008-2011 Daniel Senff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.danielsenff.imageflow.gui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.models.delegates.UnitDelegate;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;
import de.danielsenff.imageflow.models.unit.UnitList;


/**
 * This DropHandler handles the drop event for dragging Files and 
 * UnitDelegates onto the workspace thereby creating a new Node object in the graph.
 * @author dahie
 *
 */
public class GraphPanelDropHandler implements DropTargetListener {

	private GraphPanel gPanel;
	private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
	private GraphController graphController;
	
	public GraphPanelDropHandler(final GraphPanel panel, GraphController graphController) {
		this.gPanel = panel;
		this.graphController = graphController;
	}
	
	// mouse enters component
	public void dragEnter(final DropTargetDragEvent e) {}

	// mouse leaves component 
	public void dragExit(final DropTargetEvent e) {}

	// mouse over component
	public void dragOver(final DropTargetDragEvent e) {}

	public void drop(final DropTargetDropEvent event) {
		Point point = event.getLocation();
		point.translate(-50, -50);
		gPanel.getSelection().clear();
		
		try {
			final Transferable transferable = event.getTransferable();
			final DataFlavor[] flavors = transferable.getTransferDataFlavors();
			// accept for now
			event.acceptDrop (event.getDropAction());
			
			// solution for dragging files on linux: 
			// http://www.davidgrant.ca/drag_drop_from_linux_kde_gnome_file_managers_konqueror_nautilus_to_java_applications
			DataFlavor uriListFlavor = null;
		    try {
		      uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
		    } catch (ClassNotFoundException e) {
		      e.printStackTrace();
		    } 
			
			for (int i = 0; i < flavors.length; i++) {
				if (flavors[i].isFlavorJavaFileListType()) {
					final List files = (List) transferable.getTransferData(flavors[i]);

					processDroppedFiles(point, files);

					event.dropComplete(true);
				 } else if (transferable.isDataFlavorSupported(uriListFlavor)) {
					 String data = (String) transferable.getTransferData(uriListFlavor);
					 final List files = textURIListToFileList(data);

					 processDroppedFiles(point, files);

					 event.dropComplete(true);
				} else if (flavors[i].isFlavorSerializedObjectType()) {
					String o = (String)transferable.getTransferData(flavors[i]);
					
					UnitDelegate delegate = DelegatesController.getInstance().getDelegate( o);
					// this can return null if no unit by this name is found
					if (delegate != null) {
						this.graphController.addNode(delegate, point);
					}
					
					event.dropComplete(true);
				}
			}

			gPanel.repaint();
			return;

		} catch (final Throwable t) { t.printStackTrace(); 
			// a problem happened
			event.rejectDrop();
		}
		
	}

	private UnitList getUnitList() {
		return gPanel.getNodeL();
	}

	private void processDroppedFiles(Point point, final List files) {
		File file;
		for (int j = 0; j < files.size(); j++) {
			file = (File) files.get(j);
			
			UnitDelegate delegate= DelegatesController.getInstance().getDelegate("Image Source");
			String[] args = {file.getAbsolutePath()};
			UnitElement unit = UnitFactory.createProcessingUnit(delegate.getUnitDescription(), point, args);
			getUnitList().add(unit);
			point.x += 100;
			point.y += 100;
		}
	}

	public void dropActionChanged(final DropTargetDragEvent e) {}

	
	private static List textURIListToFileList(String data) {
	    List list = new ArrayList(1);
	    for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
	      String s = st.nextToken();
	      if (s.startsWith("#")) {
	        // the line is a comment (as per the RFC 2483)
	        continue;
	      }
	      try {
	        URI uri = new URI(s);
	        File file = new File(uri);
	        list.add(file);
	      } catch (URISyntaxException e) {
	        e.printStackTrace();
	      } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	      }
	    }
	    return list;
	  }
}
