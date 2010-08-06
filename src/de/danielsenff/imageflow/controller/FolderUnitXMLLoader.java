package de.danielsenff.imageflow.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class FolderUnitXMLLoader implements UnitDelegateLoader {

	private DelegatesController delegatesController;
	
	public FolderUnitXMLLoader(DelegatesController delegatesController) {
		this.delegatesController = delegatesController;
	}
	
	public void readDelegates(MutableTreeNode node, JMenu menu, URL url)
	throws MalformedURLException {
		File folder;
		try {
			folder = new File(url.toURI());
		} catch (URISyntaxException e) {
			folder = new File(url.getPath());
		}
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if(file.isDirectory()
					&& !file.isHidden()
					&& !file.getName().startsWith(".")) {
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(file.getName());
				JMenu subMenu = new JMenu(file.getName());
				readDelegates(subNode, subMenu, file.toURI().toURL());
				((DefaultMutableTreeNode) node).add(subNode);
				menu.add(subMenu);
			} else if (file.isFile()
					&& isXML(file)
					&& !file.getName().startsWith(".")) {
				URL fileURL = file.toURI().toURL();
				delegatesController.addUnitDelegate(node, menu, fileURL);
			}
		}
	}
	

	/**
	 * Returns true if the checked file file has the ,xml-extension.
	 * @param file
	 * @return
	 */
	private boolean isXML(File file) {
		return file.getName().toLowerCase().contains(".xml");
	}
	
}
