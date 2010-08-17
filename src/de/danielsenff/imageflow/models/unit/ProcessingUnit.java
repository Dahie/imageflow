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

import java.util.Collection;

import de.danielsenff.imageflow.models.connection.Input;
import de.danielsenff.imageflow.models.connection.Output;
import de.danielsenff.imageflow.models.parameter.AbstractParameter;
import de.danielsenff.imageflow.models.parameter.Parameter;

/**
 * Interface for processing unit. Usually with inputs and outputs.
 * @author dahie
 *
 */
public interface ProcessingUnit {


	/*
	 * Outputs and Inputs
	 */
	

	/**
	 * Adds an Output-Object to the unit.
	 * @param newOutput
	 * @return
	 */
	public boolean addOutput(final Output newOutput);

	/**
	 * Get one {@link Output} at the index. Indecies start with 0;
	 * @param index
	 * @return
	 */
	public Output getOutput(final int index);

	/**
	 * Returns a list of all {@link Output}s
	 * @return
	 */
	public Collection<Output> getOutputs();

	/**
	 * Number of {@link Output}s.
	 * @return
	 */
	public int getOutputsCount();

	/**
	 * Returns the {@link Input} at the given index. Indecies start with 0.
	 * @param index
	 * @return
	 */
	public Input getInput(final int index);

	/**
	 * List of all attached {@link Input}s
	 * @return
	 */
	public Collection<Input> getInputs();

	/**
	 * Returns if this unit has inputs attached to this unit.
	 * @return
	 */
	public boolean hasInputs();
	

	/**
	 * Add new Input by Object.
	 * @param input
	 * @return
	 */
	public boolean addInput(final Input input);

	/**
	 * How many {@link Input}s are actually registered.
	 * @return
	 */
	public int getInputsCount();
	
	/*
	 * Parameters
	 */
	

	/**
	 * Add a Parameter to the unit
	 * @param parameter
	 * @return 
	 */
	public boolean addParameter(final Parameter parameter);
	
	/**
	 * Returns how many assigned {@link AbstractParameter}s this unit actually has.
	 * @return
	 */
	public int getParametersCount();

	/**
	 * List of all {@link AbstractParameter}s available for this unit
	 * @return
	 */
	public Collection<Parameter> getParameters();

	/**
	 * Parameter on index i.
	 * @param index
	 * @return
	 */
	public Parameter getParameter(final int index);
	
	
	
	
}
