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
package de.danielsenff.imageflow.controller;

import ij.IJ;

import org.jdesktop.application.Task;
import org.jdesktop.application.TaskService;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.parameter.ParamChangeListener;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.tasks.RunMacroTask;

public class ExecuteWorkflowListener implements ParamChangeListener {
	private GraphController graphController;
	boolean lock = false;
	
	public ExecuteWorkflowListener(GraphController controller) {
		this.graphController = controller;
	}

	public void parameterChanged(Parameter source) {
		if (!IJ.macroRunning()) {
			lock = true;
			System.out.println("reexecute graph");
			
			ImageFlow application = ImageFlow.getApplication();
			Task convertTask = new RunMacroTask(application, graphController, false, false, false);
			TaskService ts = application.getContext().getTaskService();
			ts.execute(convertTask);
			lock = false;
		}
		else {
			System.out.println("Execution halted");
		}
	}
}
