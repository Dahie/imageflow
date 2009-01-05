/**
 * 
 */
package imageflow.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.CellRendererPane;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 * @author danielsenff
 *
 */
public class InsertUnitPanel extends JPanel {

	/**
	 * 
	 */
	public InsertUnitPanel() {

		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(400, 80));
		
		JList unitList = new JList();
		
		
		DefaultListModel model = new DefaultListModel(); 
			
		/*model.addElement("unit1");
		model.addElement("unit1");
		model.addElement("unit1");
		model.addElement("unit1");
		model.addElement("unit1");*/
		unitList.setModel(model);
//		unitList.setCellRenderer(cellRenderer)
		this.add(unitList);
	}
	
	private class NodeCellRenderer extends CellRendererPane{
		
	}
	
}
