/**
 * Copyright (C) 2008-2010 Daniel Senff
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
package de.danielsenff.imageflow.models.unit;

import java.awt.Point;

import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.models.parameter.ParameterFactory;

/**
 * For-Class dummy, this is unused
 * @author Daniel Senff
 *
 */
public class ForGroupUnitElement extends GroupUnitElement {

	private int begin, end, step;
	
	
	public ForGroupUnitElement(Point point, String name) {
		super(point, name);
		
		// add meta parameters/inputs for loop
		// if connected take the input
		// otherwise use parameter
		initParameters();
		initInputs();
		
	}

	private void initInputs() {
		Input inputBegin = new Input(DataTypeFactory.createInteger(), this, 0, false);
		inputBegin.setupInput("Begin", "start value", false);
		addInput(inputBegin);
		
		Input inputEnd = new Input(DataTypeFactory.createInteger(), this, 0, false);
		inputEnd.setupInput("End", "end value", false);
		addInput(inputEnd);
		
		Input inputStep = new Input(DataTypeFactory.createInteger(), this, 0, false);
		inputStep.setupInput("Step", "step value", false);
		addInput(inputStep);
		
	}

	private void initParameters() {
		Parameter paraBegin = ParameterFactory.createParameter("Initialize value", "integer", new Integer(0), "Begining of the loop.", null);
		addParameter(paraBegin);
		
		Parameter paraStep = ParameterFactory.createParameter("Step size", "integer", new Integer(10), "Step of increments in the loop.", null);
		addParameter(paraStep);
		
		Parameter paraEnd = ParameterFactory.createParameter("End condition", "integer", new Integer(0), "End of the loop", null);
		addParameter(paraEnd);
	}
	
	

	@Override
	public ForGroupUnitElement clone() {
		ForGroupUnitElement groupClone = new ForGroupUnitElement(getOrigin(), getLabel());
		groupClone.getInputs().clear();
		groupClone.getParameters().clear();
		groupClone = (ForGroupUnitElement) super.initClone(groupClone);
		for (Parameter parameter : parameters) {
			cloneParameter(groupClone, parameter);
		}
		return groupClone;
	}
	
}
