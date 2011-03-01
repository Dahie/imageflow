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
package de.danielsenff.imageflow.models.parameter;

import java.util.HashMap;

/**
 * Parameter Interface
 * @author Daniel Senff
 *
 */
public interface Parameter<T> {

	/**
	 * Returns the Value of this Parameter
	 * @return
	 */
	public abstract T getValue();
	
	/**
	 * Returns the default value of this Parameter
	 * @return
	 */
	public abstract T getDefaultValue();
	
	/**
	 * Returns the name of the Parameter. 
	 * @return
	 */
	public String getDisplayName();
	
	/*
	 * Returns the type of the Parameter
	 */
	/**
	 * @return
	 */
	public String getParaType();
	
	/**
	 * Get the help description of this Parameter.
	 * @return
	 */
	public String getHelpString();
	
	/**
	 * @param parameterNumber
	 */
	public void setParameterNumber(final int parameterNumber);

	/**
	 * Returns true if this parameter can not be changed.
	 * @return
	 */
	public boolean isReadOnly();
	
	/**
	 * Returns true if this parameter should be invisible in the generated parameter forms.
	 * @return
	 */
	public boolean isHidden();

	/**
	 * Special options hash to configure parameters more flexible.
	 * @return
	 */
	public abstract HashMap<String, Object> getOptions();

	/** 
	 * Adds a Parameter ChangeListener, that calls back on changes to the
	 * parameter properties.
	 * @param listener
	 */
	public void addParamChangeListener(ParamChangeListener listener);

	/** 
	 * Remove Parameter ChangeListener.
	 * @param listener
	 */
	public void removeParamChangeListener(ParamChangeListener listener);
	
}
