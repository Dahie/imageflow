package de.danielsenff.imageflow.controller.gui;
/*
 * Baba XP - Extreme Programming Projects Manager
 * Copyright (c) 2004 Baba XP
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
 *
 * http://www.gnu.org/copyleft/gpl.html
 *
 * @author Vitor Fernando Pamplona - vitor@javafree.com.br
 *
 */

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * Class that loads an file.
 *
 * @author <a href="mailto:vitor@javafree.com.br">Vitor Fernando Pamplona</a>
 *
 * @since 23/11/2004
 * @version $Id: FileChooserField.java,v 1.1 2004/12/06 19:34:05 vfpamp Exp $
 */
public class FileChooserField extends JComponent implements  ActionListener{

    JTextField text;
    JButton chooser;
    String name;
    String ext;
    String nomExt;
    
    /**
     * 
     */
    public FileChooserField(String label, String hint, String ext, String nomExt) {
        super();
        
        this.ext = ext;
        this.nomExt = nomExt;
        
        this.text = new JTextField(label);
        this.chooser = new JButton("...");
        chooser.setToolTipText("Procurar arquivo");
        chooser.setMargin(new Insets(0,0,0,0));
        
        this.setLayout(new BorderLayout(2,2));
        this.add(text, BorderLayout.CENTER);
        this.add(chooser, BorderLayout.LINE_END);
        
        chooser.addActionListener(this);
    }
    
    /**
     * Name for this field. Used to generate others labels.
     * @return The name of this component
     */
    public String getName() {
        return name;
    }
    /**
     * Name for this field. Used to generate others labels.
     * @param name the name of this component
     */
    public void setName(String name) {
        this.name = name;
    }

    public void actionPerformed(ActionEvent e) {
        File loadFile = FileChooser.loadFile(this, name, ext, nomExt);
        System.out.println(loadFile);
		text.setText(loadFile.toString());
    }
    
    public String getText() {
        return text.getText();
    }

    public void setText(String texto) {
        text.setText(texto);
    }

    public static void main(String[] args) {
		new Window();
	}


}

class Window extends JFrame {

	public Window() {
		setSize(200,200);
		setVisible(true);
		add(new FileChooserField("choose file", "choose any file", "txt", "text"));
		pack();
	}
	
}