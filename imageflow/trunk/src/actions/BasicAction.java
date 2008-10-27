package actions;

import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import Controller.Controller;
import Helper.OS;
import Helper.ResourceLoader;



abstract public class BasicAction extends AbstractAction {

//	ResourceBundle bundle = Controller.getBundle();
	
	/**
	 * @param controller
	 */
	public BasicAction(final GraphController controller) {
		this(controller, true);
	}

	public BasicAction(final GraphController controller, final boolean hasBundle) {
//		this.controller = controller;

		if (hasBundle){
			String name = getClass().getName();
			putValue(NAME, bundle.getString(name+".name"));
			String string = bundle.getString(name+".description");
			putValue(SHORT_DESCRIPTION, string);
			
			String hotkey;
			if (OS.isMacOS()) { 
				hotkey = bundle.getString(name+".hotkey_osx");
			} else { 
				hotkey = bundle.getString(name+".hotkey");
			}
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(hotkey));
			if(!bundle.getString(name+".icon").equals("null")){
				putValue(SMALL_ICON, ResourceLoader.getResourceIcon(bundle.getString(name+".icon")));
			
			}
//			putValue(MNEMONIC_KEY, new Integer('S'));
		}
	}

}
