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

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.danielsenff.imageflow.models.connection.*;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitModelComponent;

/**
 * Infographic about a {@link UnitElement} and it's connection.
 * @author dahie
 *
 */
public class UnitElementInfoPanel extends JPanel {

	public UnitElementInfoPanel(final UnitElement unit) {
		
		setLayout(new BorderLayout());
		
		// left column, inputs
		final JPanel inputsPanel = new JPanel();
		inputsPanel.setLayout(new BoxLayout(inputsPanel, BoxLayout.Y_AXIS));
		for (final Pin pin : unit.getInputs()) {
			final ConnectionLabel inputLabel = new ConnectionLabel(pin);
			inputLabel.setIcon(new ImageIcon());
			inputsPanel.add(inputLabel);
		}
		add(inputsPanel, BorderLayout.LINE_START);
		
		// center column, unit graphic
		final JPanel unitPanel = new JPanel();
		unitPanel.add(new UnitGraphicLabel(unit));
		add(unitPanel, BorderLayout.CENTER);
		
		// right column, outputs
		final JPanel outputsPanel = new JPanel();
		outputsPanel.setLayout(new BoxLayout(outputsPanel, BoxLayout.Y_AXIS));
		for (final Pin pin : unit.getOutputs()) {
			final ConnectionLabel outputLabel = new ConnectionLabel(pin);
			outputsPanel.add(outputLabel);
		}
		add(outputsPanel, BorderLayout.LINE_END);
		
	}
	
	class UnitGraphicLabel extends JLabel {
		private final UnitElement unit;
		
		public UnitGraphicLabel(final UnitElement unit) {
			this.unit = unit;
			setPreferredSize(unit.getDimension());
		}
		
		public void paint(final java.awt.Graphics g) {
			super.paint(g);
			final Image unitIcon = unit.getUnitComponentIcon().getImage(UnitModelComponent.Size.BIG);
			g.drawImage(unitIcon, 0, 0, null);
		}
	}
	
	class ConnectionLabel extends JLabel {
		
		public ConnectionLabel(final Pin pin) {
			String datatype;
			if (pin instanceof Input) {
				datatype = pin.isConnected() ? 
						((Input)pin).getFromOutput().getDataType().getName() 
						: pin.getDataType().getName();
			} else if (pin instanceof Output) {
				datatype = pin.getDataType().getName();
			} else {
				datatype = "unknown";
			}
			
			setText("<html><b>"+pin.getDisplayName()+"</b><br>"+datatype+"</html>");
		}
		
	}
	
}
