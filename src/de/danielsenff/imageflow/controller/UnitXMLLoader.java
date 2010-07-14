package de.danielsenff.imageflow.controller;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JMenu;
import javax.swing.tree.MutableTreeNode;

/**
 * Interface for UnitXMLLoader, different methods of loading XML-unit-definitions into the system. 
 * @author dahie
 *
 */
public interface UnitXMLLoader {

	void readDelegates(MutableTreeNode node, JMenu menu, URL url) throws MalformedURLException;
}
