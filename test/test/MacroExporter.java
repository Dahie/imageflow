package test;


import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JButton;

import de.danielsenff.imageflow.controller.GraphController;
import de.danielsenff.imageflow.models.unit.UnitList;



public class MacroExporter {

	
	
	public static void main(String[] args) {
		
		// example UnitList
		GraphController controller = new GraphController(); 
		controller.setupExample1();
		UnitList unitList = controller.getUnitElements();
		
		XMLEncoder e;
		try {
			e = new XMLEncoder(
					new BufferedOutputStream(
							new FileOutputStream("Test.xml")));
			e.writeObject(new JButton("test"));
			e.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
	}
}
