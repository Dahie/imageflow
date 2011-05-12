package de.danielsenff.imageflow.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class UnitPropertiesDialog extends PropertiesDialog {

	
	public UnitPropertiesDialog(final UnitElement unit, Point point) {
		super(unit.getLabel() + " - Parameters", ImageFlow.getApplication().getMainFrame());
		if(unit.getHelpString() != null) {
			addMessage(unit.getHelpString());
		}
		
		// label field 
		JTextField fldName = new JTextField(unit.getLabel());
		fldName.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent event) {}
			
			public void keyReleased(KeyEvent event) {
				String newLabel = ((JTextComponent) event.getSource()).getText();
				unit.setLabel(newLabel);
			}
			public void keyPressed(KeyEvent event) {}
		});
		addForm("Name", fldName);
		
		addDisplayCheckbox(unit);
		//addDisplaySilentCheckbox(gd);
		
		addSeparator();	
		
		unit.addCustomWidgets(this);
		
		// add Parameter Widgets
		if (!unit.getParameters().isEmpty()) {
			addParameterWidgets(unit);
			addSeparator();
		}
		
		addComponent(new UnitElementInfoPanel(unit));
		
		// show properties window
		if (point == null) 
			showDialog();
		else
			showDialog(point);
		
		
	}
	
	protected void addParameterWidgets(final UnitElement unit) {
		for (final Parameter parameter : unit.getParameters()) {
			add(parameter);
		}
	}

	private void addDisplayCheckbox(final UnitElement unit) {
		JPanel panel = new JPanel();
		JCheckBox chkDisplay = new JCheckBox("Display result");
		chkDisplay.setToolTipText("After the workflow has been executed, nodes that are set to 'display' are displayed as a result.");
		chkDisplay.setSelected(unit.isDisplay());
		chkDisplay.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				boolean newValue = ((JCheckBox)event.getSource()).isSelected();
				unit.setDisplay(newValue);
			}
		});
		panel.add(chkDisplay);
		
		
		ActionMap actionMap = ImageFlow.getApplication().getContext().getActionMap(
				ImageFlowView.class, ImageFlow.getApplication().getMainView());
		
		
		JButton addToDashboard = new JButton(actionMap.get("addToDashboard"));
		addToDashboard.setPreferredSize(new Dimension(110, 20));
		panel.add(addToDashboard);
		
		JButton addPreviewToDashboard = new JButton(actionMap.get("addOutputToDashboard"));
		addPreviewToDashboard.setPreferredSize(new Dimension(110, 20));
		panel.add(addPreviewToDashboard);
		addForm("", panel);
	}
	
	private void addDisplaySilentCheckbox(final UnitElement unit) {
		JCheckBox chkDisplay = new JCheckBox("Display result silently");
		chkDisplay.setToolTipText("After the workflow has been executed, nodes that are set to 'display' are displayed as a result.");
		chkDisplay.setSelected(unit.isDisplaySilent());
		chkDisplay.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				boolean newValue = ((JCheckBox)event.getSource()).isSelected();
				unit.setDisplaySilent(newValue);
			}
		});
		addForm("", chkDisplay);
	}

	
	
	class DashboardButtonsPanel extends JPanel {
		
	}
	
}
