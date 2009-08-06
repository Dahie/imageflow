package de.danielsenff.imageflow.utils;

import java.awt.Point;
import java.util.Collection;

import visualap.GPanel;
import visualap.Node;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.NodeListener;
import de.danielsenff.imageflow.models.unit.UnitDelegate;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitFactory;

/**
 * @author danielsenff
 *
 */
public class FileDropListener implements FileDrop.Listener {

	protected Point coordinates;
	protected GPanel gpanel;
	private Collection<Node> unitList;
	private ImageFlowView ifView;

	/**
	 * @param gpanel
	 * @param ifView
	 */
	public FileDropListener(final GPanel gpanel, final ImageFlowView ifView) {
		this.gpanel = gpanel;
		this.unitList = gpanel.getNodeL();
		this.ifView = ifView;
	}
	
	
	/**
	 * This method is called when files have been successfully dropped.
	 *
	 * @param files An array of <tt>File</tt>s that were dropped.
	 * @since 1.0
	 */
	public void filesDropped( java.io.File[] files, Point point )
    {   
		point.translate(-50, -50);
		coordinates = point;
		
		for( int i = 0; i < files.length; i++ )
        {   
			//TODO check filetype
			
    		// add Source-Units
			UnitDelegate delegate= DelegatesController.getInstance().getDelegate("Image Source");
			String[] args = {files[i].getAbsolutePath()};
			UnitElement unit = UnitFactory.createProcessingUnit(delegate.getUnitDescription(), coordinates, args);
			unit.addModelListener(new NodeListener(gpanel, ifView));
			unitList.add(unit);
			
			coordinates.translate(25, 25);
        }   // end for: through each dropped file
		
		gpanel.invalidate();
		gpanel.repaint();
		
    }   // end filesDropped

	/**
	 * Set the coordinate of this click.
	 * @param point
	 */
	public void setLocation(Point point) {
		this.coordinates = point;
	}

	/**
	 * Get the coordinate of this click.
	 * @return
	 */
	public Point getLocation() {
		return this.coordinates;
	}

}   // end inner-interface Listener
