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
package de.danielsenff.imageflow.controller;

import ij.ImagePlus;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import de.danielsenff.imageflow.gui.BICanvas;
import de.danielsenff.imageflow.gui.DashWidget;
import de.danielsenff.imageflow.gui.FormPanel;
import de.danielsenff.imageflow.gui.RoundedPanel;
import de.danielsenff.imageflow.models.Model;
import de.danielsenff.imageflow.models.ModelListener;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.connection.OutputObjectChangeListener;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.datatype.ImageDataType;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.unit.Node;
import de.danielsenff.imageflow.models.unit.UnitElement;

// TODO rename
public class ParameterWidgetController {

	
	/**
	 * Create a {@link JToolBar} with all required widgets based on 
	 * the Parameters of the given unit.
	 * @param unit
	 * @return
	 */
	public static JToolBar createToolbarFromUnit(UnitElement unit) {
		
		JToolBar dash = new JToolBar(unit.getLabel());
		
		FormPanel formPanel = new FormPanel();
		formPanel.setBackground(unit.getColor());
		Collection<Parameter> parameters = unit.getParameters();
		
		for (final Parameter parameter : parameters) {
			if (!parameter.isHidden()) {
				formPanel.add(parameter);
			}
		}
		
		dash.add(formPanel);
		return dash;
	}
	
	public static DashWidget createWidgetFromUnit(UnitElement unit) {
		
		FormPanel formPanel = new FormPanel();
		formPanel.setBackground(unit.getColor());
		
		final JLabel title = new JLabel(unit.getUnitName());
		title.setForeground(Color.WHITE);
		title.setAlignmentX(0.5f);
		formPanel.add(title);
		for (final Parameter parameter : unit.getParameters()) {
			formPanel.add(parameter);
		}
		
		unit.addModelListener(new ModelListener() {
			
			public void modelChanged(Model model) {
				if(model instanceof Node)
					title.setText(((Node)model).getLabel());
			}
		});
		
		DashWidget dash = new DashWidget(formPanel);
		dash.setBackground(unit.getColor());
		dash.add(formPanel);
		return dash;
	}

	public static JPanel createPreviewWidgetFromUnit(UnitElement unit) {
		
		JPanel formPanel = new FormPanel();
		formPanel.setBackground(unit.getColor());
		final JLabel title = new JLabel(unit.getLabel() + " - Preview");
		title.setForeground(Color.WHITE);
		formPanel.add(title);
		
		unit.addModelListener(new ModelListener() {
			
			public void modelChanged(Model model) {
				if(model instanceof Node)
					title.setText(((Node)model).getLabel() + " - Preview");
			}
		});
		
		for (Output output : unit.getOutputs()) {
			final BICanvas canvas = new BICanvas();
			JScrollPane jScrollPane = new JScrollPane(canvas);
			jScrollPane.setPreferredSize(new Dimension(250, 250));
			jScrollPane.setWheelScrollingEnabled(true);
			if (output.getDataType() instanceof ImageDataType) {
				
				output.addOutputObjectListener(new OutputObjectChangeListener() {
					public void outputObjectChanged(Output output) {
						if (output.getDataType() instanceof ImageDataType
								&& output.getOutputObject() instanceof ImagePlus) {
							ImagePlus imageplus = (ImagePlus) output.getOutputObject();
							canvas.setSourceBI(imageplus.getImage());
						}
					}
				});
				
				formPanel.add(jScrollPane);
			} else if (output.getDataType() instanceof DataTypeFactory.Integer
					|| output.getDataType() instanceof DataTypeFactory.Double
					|| output.getDataType() instanceof DataTypeFactory.Number) {
				final JLabel imagePreview = new JLabel();
				output.addOutputObjectListener(new OutputObjectChangeListener() {
					
					public void outputObjectChanged(Output output) {
						if (output.getDataType() instanceof ImageDataType
								&& output.getOutputObject() instanceof ImagePlus) {
							ImagePlus imageplus = (ImagePlus) output.getOutputObject();
							imagePreview.setIcon(new ImageIcon(imageplus.getImage()));
							imagePreview.setText("");
						} else if (output.getDataType() instanceof DataTypeFactory.Number) {
							if(output.getOutputObject() instanceof Integer) {
								Integer value = (Integer) output.getOutputObject();
								imagePreview.setText("Result:"+  value);
							} else if(output.getOutputObject() instanceof Double) {
								Double value = (Double) output.getOutputObject();
								DecimalFormat decimal = new DecimalFormat("0.00");
								decimal.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
								imagePreview.setText("Result:"+  decimal.format(value));
							}
						} else if (output.getDataType() instanceof DataTypeFactory.Integer
								&& output.getOutputObject() instanceof Integer) {
							Integer value = (Integer) output.getOutputObject();
							imagePreview.setText("Result:"+  value);
						}
						
					}
				});
				
				if (output.getOutputObject() == null) {
					imagePreview.setText("No result yet");
				}
				formPanel.add(imagePreview);
				
				
			// TODO } else if (output.getDataType() instanceof DataTypeFactory.String) {
				
			} 
		}
		
		JPanel dash = new RoundedPanel();
		dash.setBackground(unit.getColor());
		dash.add(formPanel);
		return dash;
	}
}
