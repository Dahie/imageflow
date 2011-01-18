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
 * unused right now, at the moment we have only Strings.
 * @author dahie
 *
 */
public class TextParameter extends StringParameter {

	public TextParameter(String displayName, 
			final String stringValue,
			final String helpString,
			final HashMap<String, Object> options) {
		super(displayName, stringValue, helpString, options);
		this.paraType = "Text";
	}

}
