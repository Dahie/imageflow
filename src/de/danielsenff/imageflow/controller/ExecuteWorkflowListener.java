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
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;
import org.jdesktop.application.TaskService;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.models.parameter.ParamChangeListener;
import de.danielsenff.imageflow.models.parameter.Parameter;
import de.danielsenff.imageflow.tasks.RunMacroTask;

/**
 * This class is fall callbacks from a changed {@link Parameter} to kick off a 
 * new execution of the workflow.
 * @author dahie
 *
 */
public class ExecuteWorkflowListener implements ParamChangeListener {
	private final GraphController graphController;
	boolean lock = false;
	
	public ExecuteWorkflowListener(final GraphController controller) {
		this.graphController = controller;
	}

	public void parameterChanged(final Parameter source) {
		if (!lock) {
			lock = true;
			System.out.println("reexecute graph");
			
			final ImageFlow application = ImageFlow.getApplication();
			Task workflowExecutionTask = new RunMacroTask(application, graphController, false, false, false, true);
			workflowExecutionTask.addTaskListener(new TaskListener() {

				public void cancelled(TaskEvent arg0) {
					lock = false;
				}

				public void doInBackground(TaskEvent arg0) {}

				public void failed(TaskEvent arg0) {
					lock = false;
				}

				public void finished(TaskEvent arg0) {
					lock = false;
				}

				public void interrupted(TaskEvent arg0) {
					lock = false;
				}

				public void process(TaskEvent arg0) {}

				public void succeeded(TaskEvent arg0) {
					lock = false;	
				}
			});
			final TaskService ts = application.getContext().getTaskService();
			ts.execute(workflowExecutionTask);
		} else {
			System.out.println("Execution halted");
		}
	}
}
