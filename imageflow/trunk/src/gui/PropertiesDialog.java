package gui;

import ij.gui.GenericDialog;

import java.awt.Frame;

public class PropertiesDialog extends GenericDialog {

	public PropertiesDialog(String title, Frame parent) {
		super(title, parent);
	}

	@Override
	public void addStringField(String label, String defaultText) {
		// TODO Auto-generated method stub
		super.addStringField(label, defaultText);
	}
	

	
}
