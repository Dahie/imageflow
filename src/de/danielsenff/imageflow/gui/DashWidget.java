package de.danielsenff.imageflow.gui;

import java.awt.event.ContainerListener;

public class DashWidget extends RoundedPanel {

	private FormPanel formPanel;
	
	public DashWidget(FormPanel formPanel) {
		this.formPanel = formPanel;
	}
	
	
	/**
	 * @return the formPanel
	 */
	public final FormPanel getFormPanel() {
		return formPanel;
	}

	/**
	 * @param formPanel the formPanel to set
	 */
	public final void setFormPanel(FormPanel formPanel) {
		this.formPanel = formPanel;
	}

	public void addFormPanelListener(ContainerListener containerListener) {
		this.formPanel.addContainerListener(containerListener);
	}
	
}
