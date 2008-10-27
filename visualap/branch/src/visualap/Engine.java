/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/* class Engine

This class is used to perform system running

javalc6
*/
package visualap;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 

public class Engine extends Thread {
	Vertex [] vertexL;
	CancelDialog waiting;
	Frame owner;
	StringBuffer error;

	public Engine(Frame owner) {
		this.owner = owner;
		waiting = new CancelDialog(this);
	}
	
	public void runDialog(Vertex [] vertexL, StringBuffer error) {
		this.vertexL = vertexL;
		this.error = error;
		if ((vertexL == null)||(vertexL.length == 0)) return;
		waiting.setVisible(true); // must be here!
	}

	public void run() {
		if ((vertexL == null)||(vertexL.length == 0)) return;
// start running
		for (int i = 0; i < vertexL.length; i++) {
			try {
				vertexL[i].start();
			} catch (InvocationTargetException ex) {
				Throwable ex2 = ex.getCause(); // get the effective exception that caused InvocationTargetException
				System.err.println("Engine.run: "+ex2.toString());
				error.append(ex2.toString()+" in "+vertexL[i].aNode.getLabel());
				for (int j = 0; j < i; j++) 
					vertexL[j].stop();
				waiting.setVisible(false);
				return;
			}
		}
		
// check if we have to run iterations, i.e. there is at least an object with iterate() method
		int iterations = 0;
		for (int i = 0; i < vertexL.length; i++) {
			if (vertexL[i].iterative && vertexL[i].isSource) {
				iterations++;
			}
		}
		boolean running = true;
		while (running && !isInterrupted()) {
			int stoppedIterations = 0;
			for (int i = 0; i < vertexL.length; i++) {
				for (int j = 0; j < vertexL[i].backward.length; j++)
					vertexL[i].iobuf_in[j] = vertexL[i].backward[j].obj
							.iobuf_out[vertexL[i].backward[j].index];

				int nout = 0;
				for (int j = 0; j < vertexL[i].methoda.length; j++) {
					java.lang.reflect.Method m = vertexL[i].methoda[j];
					try {
						if (m.getReturnType() != Void.TYPE) {
							vertexL[i].iobuf_out[nout]=m.invoke(vertexL[i].obj, vertexL[i].iobuf_in); 
							nout++;
						}  else m.invoke(vertexL[i].obj, vertexL[i].iobuf_in);
					} catch (IllegalAccessException ex) {
						System.err.println("Engine.run: IllegalAccessException "+ex.getMessage());
						running = false;
						error.append("\nIllegalAccessException in "+vertexL[i].aNode.getLabel());
					} catch (InvocationTargetException ex) {
						Throwable ex2 = ex.getCause(); // get the effective exception that caused InvocationTargetException
						System.err.println("Engine.run: "+ex2.toString());
						running = false;
						error.append("\n"+ex2.toString()+" in "+vertexL[i].aNode.getLabel());
					} catch (Exception ex) {
//						System.err.println("Engine.run: Exception "+ex.getMessage());
						System.err.println("Engine.run: "+ex.toString());
						running = false;
						error.append("\nException ("+ex.getMessage()+") in "+vertexL[i].aNode.getLabel());
					}
				}
				if (iterations > 0) {
					try {
						if (!vertexL[i].iterate()) stoppedIterations++; 
					} catch (InvocationTargetException ex) {
						Throwable ex2 = ex.getCause(); // get the effective exception that caused InvocationTargetException
						System.err.println("Engine.run: "+ex2.toString());
						error.append("\n"+ex2.toString()+" in "+vertexL[i].aNode.getLabel());
						running = false;
					}
					if (iterations == stoppedIterations) running = false; // stop iteration
				} else running = false;
			}
		}
// now stop running, notify all objects supporting method stop()
		for (int i = 0; i < vertexL.length; i++) {
			vertexL[i].stop();
		}
		waiting.setVisible(false);
	}

// class CancelDialog
	class CancelDialog extends JDialog {
		Engine engine;

		public CancelDialog(Engine en) {
			super(owner, owner.getTitle(), true);
			engine = en;
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
//				getToolkit().sync();
					engine.start();
				}
			});

			Container cp = getContentPane();
			cp.setLayout(new FlowLayout());
			cp.add(new JLabel("Running..."));
			JButton button = new JButton("Stop");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				if (engine != null) engine.interrupt();
				dispose(); // Closes the dialog
			}
			});
			cp.add(button);
			setSize(150,125);
			setLocationRelativeTo(owner); // center dialog
		}
	}


}
