package test.writefile;
import java.beans.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * 
 * This class provides a custom editor to handle file property for WriteFile
 * 
 * @author      javalc6
 * @version     1.0
 */
public class FileEditorW extends PropertyEditorSupport {

	protected File f;
  
	public Object getValue() {
		String pwd=System.getProperty("user.dir");
//		System.out.println(System.getProperty("user.dir"));
//		System.out.println(f.toString());
		if (f.toString().startsWith(pwd))
			return f.toString().substring(pwd.length()+1);
		return f.toString();
	}

	public void setValue(Object obj) {
		f = new File(obj.toString());
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	public boolean isPaintable() {
		return true;
	}

	public void paintValue(Graphics g, Rectangle box) {
		String filename = f.getName();
		if (filename.length() == 0)
			filename = "Choose File...";
		Font mono = new Font("Arial", Font.PLAIN, 14);
		FontMetrics fm = g.getFontMetrics();
		g.setFont(mono);
		g.drawString(filename, box.x + 5, box.y + 3 + fm.getAscent());
		g.setColor(new Color(200, 200, 200));
		g.draw3DRect(box.x, box.y, box.width - 3, box.height - 3, true);
	}
 
	public Component getCustomEditor() {
		Button b = new Button("Choose File...");
		b.addActionListener(new FileChooser());
		return b;
	}

	static public int fileType(String str) {
// returns 0 if unknown file type
// returns 1 if text file
// returns 10 if image file
// returns 20 if audio file
		if (str.endsWith (".java")) return 1;
		if (str.endsWith (".txt")) return 1;
		if (str.endsWith (".jpg")) return 10;
		if (str.endsWith (".wav")) return 20;
		return 0;
	}
  
	class FileChooser implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser(".");
			fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
				public String getDescription() { return "text, image and audio files"; }
				public boolean accept(File f) {
					if (f.isDirectory ()) return true;
					return (fileType(f.getName().toLowerCase()) != 0);
				}
			});
			int returnVal = fc.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				if (file != null) {
					setValue(file);  
					firePropertyChange();
				}
			}
		}
	}

}