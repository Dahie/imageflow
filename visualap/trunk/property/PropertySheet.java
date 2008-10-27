/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.

This source code is based on package sun.beanbox
*/



package property;

import java.beans.*;
import java.lang.reflect.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class PropertySheet extends Frame implements PropertyChangeListener {
    private PropertySheetPanel panel;
    private boolean started;

    public PropertySheet(Object bean, String label, int x, int y) {
		super("Properties - <initializing...>");
		setLayout(null);
		setBackground(Color.lightGray);	
		setBounds(x,y, 100, 100);

		panel = new PropertySheetPanel(this);

//		setVisible(true);
		setTarget(bean, label);

		started = true;
    }

   public void setTarget(Object bean, String label) {
		panel.setTarget(bean);
		if (label != null)
			setTitle("Properties - " + label);
		else setTitle("Properties");
    }

    public void doLayout() {
	// Normally we get called when propertySheetPanel.setTarget
	// has changed the size of the ScrollPane and of us.
		if (!started) {
			return;
		}
		panel.stretch();
    }

    public void propertyChange(PropertyChangeEvent evt) {
		panel.wasModified(evt);
    }
}

class PropertySheetPanel extends Panel {

    private PropertySheet frame;

    // We need to cache the targets' wrapper so we can annoate it with
    // information about what target properties have changed during design
    // time.
    private Object target;
    private PropertyDescriptor properties[];
    private PropertyEditor editors[];
    private Object values[];
    private Component views[];
    private Label labels[];

    private boolean processEvents;
    private static int hPad = 4;
    private static int vPad = 4;
    private int maxHeight = 500;
    private int maxWidth = 300;

    PropertySheetPanel(PropertySheet frame) {
		this.frame = frame;
		setLayout(null);
    }

    synchronized void setTarget(Object targ) {
		frame.removeAll();	

		removeAll();

		if (targ == null) {
			setVisible(false);
			setLayout(null);
			return;
		}

		// We make the panel invisivle during the reconfiguration
		// to try to reduce screen flicker.

		// As a workaround for #4056424, we avoid maling the panel
		// invisible first time though, during startup.
		if (target != null) {
			setVisible(false);
		}

		target = targ;

		try {
			BeanInfo bi = Introspector.getBeanInfo(target.getClass());
			properties = bi.getPropertyDescriptors();
		} catch (IntrospectionException ex) {
			error("PropertySheet: Couldn't introspect", ex);
			return;
		}

		editors = new PropertyEditor[properties.length];
		values = new Object[properties.length];
		views = new Component[properties.length];
		labels = new Label[properties.length];

		for (int i = 0; i < properties.length; i++) {

			// Don't display hidden or expert properties.
			if (properties[i].isHidden() || properties[i].isExpert()) {
			continue;
			}

			String name = properties[i].getDisplayName();
			Class type = properties[i].getPropertyType();
			Method getter = properties[i].getReadMethod();
			Method setter = properties[i].getWriteMethod();

			// Only display read/write properties.
			if (getter == null || setter == null) {
				continue;
			}
		
			Component view = null;

			try {
				Object args[] = { };
				Object value = getter.invoke(target, args);
				values[i] = value;

				PropertyEditor editor = null;
				Class pec = properties[i].getPropertyEditorClass();
			if (pec != null) {
				try {
					editor = (PropertyEditor)pec.newInstance();
				} catch (Exception ex) {
				// Drop through.
				}
			}
			if (editor == null) {
				editor = PropertyEditorManager.findEditor(type);
			}
				editors[i] = editor;

				// If we can't edit this component, skip it.
				if (editor == null) {
				// If it's a user-defined property we give a warning.
				String getterClass = properties[i].getReadMethod().getDeclaringClass().getName();
				if (getterClass.indexOf("java.") != 0) {
					System.err.println("Warning: Can't find public property editor for property \""
					 + name + "\".  Skipping.");
				}
				continue;
				}

			// Don't try to set null values:
			if (value == null) {
				// If it's a user-defined property we give a warning.
				String getterClass = properties[i].getReadMethod().getDeclaringClass().getName();
				if (getterClass.indexOf("java.") != 0) {
					System.err.println("Warning: Property \"" + name 
					+ "\" has null initial value.  Skipping.");	
				}
				continue;
			}

				editor.setValue(value);
				editor.addPropertyChangeListener(frame);

			// Now figure out how to display it...
			if (editor.isPaintable() && editor.supportsCustomEditor()) {
				view = new PropertyCanvas(frame, editor);
			} else if (editor.getTags() != null) {
				view = new PropertySelector(editor);
			} else if (editor.getAsText() != null) {
				String init = editor.getAsText();
				view = new PropertyText(editor);
			} else {
				System.err.println("Warning: Property \"" + name 
					+ "\" has non-displayabale editor.  Skipping.");
				continue;
			}

			} catch (InvocationTargetException ex) {
			System.err.println("Skipping property " + name + " ; exception on target: " + ex.getTargetException());
			ex.getTargetException().printStackTrace();
			continue;
			} catch (Exception ex) {
			System.err.println("Skipping property " + name + " ; exception: " + ex);
			ex.printStackTrace();
			continue;
			}

			labels[i] = new Label(name, Label.RIGHT);
			add(labels[i]);

			views[i] = view;
			add(views[i]);
		}

		frame.add(this);
		doLayout(true);

		processEvents = true;

		Insets ins = frame.getInsets();
		
		int frameWidth = getSize().width + ins.left + ins.right;
		int frameHeight = getSize().height + ins.top + ins.bottom;

		// Do we need to add a scrollpane ?
		boolean needPane = false;
		if (frameWidth > maxWidth) {
			needPane = true;
			frameWidth = maxWidth;
		}
		if (frameHeight > maxHeight) {
			needPane = true;
			frameHeight = maxHeight;
		}

		if (needPane) {
			// Put us in a ScrollPane.

			// Note that the exact ordering of this code is
			// very important in order to get correct behaviour
			// on win32.  Don't modify unless you have a lot of
			// spare time to test/debug it.

			frame.remove(this);

			frameWidth = frameWidth + 30;
			if (frameWidth > maxWidth) {
				frameWidth = maxWidth;
			}
			frameHeight = frameHeight + 30;
			if (frameHeight > maxHeight) {
				frameHeight = maxHeight;
			}

			ScrollPane pane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
			pane.setBounds(ins.left, ins.top,
					frameWidth - (ins.left + ins.right),
					frameHeight - (ins.top + ins.bottom));

			frame.setSize(frameWidth,frameHeight);	
			pane.add(this);
			frame.add(pane);

			pane.doLayout();

		} else {
			frame.setSize(frameWidth,frameHeight);	
			//We don't need a ScrollPane.
			setLocation(ins.left, ins.top);
			frame.add(this);
		}

		setVisible(true);
		
    }

    void stretch() {
	// This gets called when a user explicitly resizes the frame.

	Component child = null;
	try {
	    child = frame.getComponent(0);
	} catch (Exception ex) {
	    // frame has no active children;
	    return;
	}
	Dimension childSize = child.getSize();
	Dimension frameSize = frame.getSize();
	Insets ins = frame.getInsets();
	int vpad = ins.top + ins.bottom;
	int hpad = ins.left + ins.right;

	// If the frame size hasn't changed, do nothing.
	if (frameSize.width == (childSize.width + hpad) &&
		frameSize.height == (childSize.height + vpad)) {
	    return;
	}

	// We treat the new frame sizes as a future maximum for our own
	// voluntary size changes.
	maxHeight = frameSize.height;
	maxWidth = frameSize.width;

	// If we've gotten smaller, force new layout.
	if (frameSize.width < (childSize.width + hpad) ||
			frameSize.height < (childSize.height + vpad)) {
	    // frame has shrunk in at least one dimension.
	    setTarget(target); //zz it was: targetWrapper
	} else {
	   // Simply resize the contents.  Note that this won't make
	   // any ScrollPane go away, that will happen on the next
	   // focus change.
 	   child.setSize(frameSize.width - hpad, frameSize.height - vpad);
	    
	}
    }

    private void doLayout(boolean doSetSize) {
	if (views == null || labels == null) {
	    return;
	}

	// First figure out the size of the columns.
	int labelWidth = 92;
	int viewWidth = 120;

	for (int i = 0; i < properties.length; i++) {
	    if (labels[i] == null || views[i] == null) {
		continue;
	    }
	    int w = labels[i].getPreferredSize().width;
	    if (w > labelWidth) {
		labelWidth = w;
	    }
	    w = views[i].getPreferredSize().width;
	    if (w > viewWidth) {
		viewWidth = w;
	    }
	}
	int width = 3*hPad + labelWidth + viewWidth;

	// Now position all the components.
	int y = 10;
	for (int i = 0; i < properties.length; i++) {
	    if (labels[i] == null || views[i] == null) {
		continue;
	    }
	    labels[i].setBounds(hPad, y+5, labelWidth, 25);

	    Dimension viewSize = views[i].getPreferredSize();
	    int h = viewSize.height;
	    if (h < 30) {
		h = 30;
	    }
	    views[i].setBounds(labelWidth + 2*hPad, y, viewWidth, h);
	    y += (h + vPad);
	}

	y += vPad;

	if (doSetSize) {
	    setSize(width, y);
	}
    }


    public void doLayout() {
		doLayout(false);
    }


    synchronized void wasModified(PropertyChangeEvent evt) {

	if (!processEvents) {
	    return;
	}

	if (evt.getSource() instanceof PropertyEditor) {
	    PropertyEditor editor = (PropertyEditor) evt.getSource();
	    for (int i = 0 ; i < editors.length; i++) {
	        if (editors[i] == editor) {
		    PropertyDescriptor property = properties[i];
		    Object value = editor.getValue();
		    values[i] = value;
		    Method setter = property.getWriteMethod();
		    try {
		        Object args[] = { value };
		        args[0] = value;
		        setter.invoke(target, args);
		        
		    } catch (InvocationTargetException ex) {
		      if (ex.getTargetException()
		                instanceof PropertyVetoException) {
			//warning("Vetoed; reason is: " 
			//        + ex.getTargetException().getMessage());
			// temp dealock fix...I need to remove the deadlock.
			System.err.println("WARNING: Vetoed; reason is: " 
					+ ex.getTargetException().getMessage());
		      }
		      else
		        error("InvocationTargetException while updating " 
		                + property.getName(), ex.getTargetException());
		    } catch (Exception ex) {
		        error("Unexpected exception while updating " 
		                + property.getName(), ex);
	            }
	            if (views[i] != null && views[i] instanceof PropertyCanvas) {
		        views[i].repaint();
	            }
		    break;
		}
	    }
	}

	// Now re-read all the properties and update the editors
	// for any other properties that have changed.
	for (int i = 0; i < properties.length; i++) {
	    Object o;
	    try {
	        Method getter = properties[i].getReadMethod();
	        Object args[] = { };
	        o = getter.invoke(target, args);
	    } catch (Exception ex) {
		o = null;
	    }
	    if (o == values[i] || (o != null && o.equals(values[i]))) {
	        // The property is equal to its old value.
		continue;
	    }
	    values[i] = o;
	    // Make sure we have an editor for this property...
	    if (editors[i] == null) {
		continue;
	    }
	    // The property has changed!  Update the editor.
	    editors[i].setValue(o);
	    if (views[i] != null) {
		views[i].repaint();
	    }
	}

	// Make sure the target bean gets repainted.
	if (Beans.isInstanceOf(target, Component.class)) {
	    ((Component)(Beans.getInstanceOf(target, Component.class))).repaint();
	}
    }


    //----------------------------------------------------------------------
    // Log an error.

    private void error(String message, Throwable th) {
		String mess = message + ":\n" + th;
		System.err.println(message);
		th.printStackTrace();
	// Popup an ErrorDialog with the given error message.
//zz	new ErrorDialog(frame, mess);

    }

}
