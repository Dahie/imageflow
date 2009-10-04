package de.danielsenff.imageflow.models.unit;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jdom.Element;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.controller.DelegatesController;
import de.danielsenff.imageflow.models.datatype.DataTypeFactory;
import de.danielsenff.imageflow.models.parameter.ChoiceParameter;
import de.danielsenff.imageflow.models.unit.UnitDescription.Input;
import de.danielsenff.imageflow.models.unit.UnitDescription.Output;
import de.danielsenff.imageflow.models.unit.UnitDescription.Para;
import de.danielsenff.imageflow.models.unit.UnitModelComponent.Size;
import de.danielsenff.imageflow.utils.Tools;

/**
 * Interface for NodeDescriptions
 * @author dahie
 *
 */
public interface NodeDescription {

	boolean hasInputs();
	boolean hasOutputs();
	boolean hasParameters();
	
	public String getHelpString();

	String getUnitName();

	boolean getIsDisplayUnit();

	Color getColor();

	
	
}
