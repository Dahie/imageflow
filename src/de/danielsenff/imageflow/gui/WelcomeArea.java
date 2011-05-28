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
package de.danielsenff.imageflow.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.HorizontalAlignment;

/**
 * Paints the introduction graphics on the empty {@link GraphPanel}.
 * @author dahie
 *
 */
public class WelcomeArea {

	private BufferedImage iwIcon;
	
	private BufferedImage introStep1;
	private BufferedImage introStep2;
	private BufferedImage introStep3;
	private BufferedImage introStep4;
	
	public static final int margin = 21;
	public static final int marginSmall = 9;
	private Dimension boxDimension;
	private int yBox;
	private int outerRightEdge;
	private int upperEdgeBox;
	
	public WelcomeArea() {
		String defaultPath = "/de/danielsenff/imageflow/resources/";
//			getResourceMap().getString("Background.image");
		
		try {
			this.iwIcon = readResource(defaultPath + "iw-icon.png");
			this.introStep1 = readResource(defaultPath + "intro_step1.png");
			this.introStep2 = readResource(defaultPath + "intro_step2.png");
			this.introStep3 = readResource(defaultPath + "intro_step3.png");
			this.introStep4 = readResource(defaultPath + "intro_step4.png");
			
			// pre calculating some layout basics
			this.boxDimension = new Dimension(340, this.introStep2.getHeight()+marginSmall*2);
			this.upperEdgeBox = 120;
			this.yBox = upperEdgeBox+boxDimension.height+margin;
			this.outerRightEdge = margin*2+boxDimension.width*2;
		} catch (final IOException e) {
			e.printStackTrace();
			// TODO use dummy image 
		}
		
	}
	
	private BufferedImage readResource(String path) throws IOException {
		return ImageIO.read(this.getClass().getResourceAsStream(path));
	}
	
	/**
	 * draw a nice message to promote creating a graph
	 * @param g2d
	 */
	public void draw(final Graphics2D g2d) {
		final int x = 50;
		final int y = 80;

		g2d.drawImage(this.iwIcon, margin, margin, null);
		
		
		final String headline = "Create your workflow";
//			getResourceMap().getString("Intro.headline"); 
		final String description = /*"Add new units to the graph by using the" + '\n'
			+"context menu units on this canvas." + '\n' + "   " + '\n'*/
			 "A workflow is constructed from a Source-Unit and requires a Display-Unit." + '\n'
			+ "The Display-Unit is the image that will be displayed after running the workflow.";
		

		g2d.setColor(Color.GRAY);
		
		final Font font = g2d.getFont();
		final Font headlineFont = new Font(font.getFamily(), Font.BOLD, 24);
		TextBlock headlineText = TextUtilities.createTextBlock(headline, headlineFont, Color.GRAY);
		headlineText.setLineAlignment(HorizontalAlignment.RIGHT);
		headlineText.draw(g2d, this.outerRightEdge, margin, TextBlockAnchor.TOP_RIGHT);
		g2d.setFont(new Font(font.getFamily(), Font.PLAIN, 12));
		// TODO you can/should probably move a lot of this out of the paint method
		
		TextBlock welcomeText = TextUtilities.createTextBlock(description, g2d.getFont(), Color.BLACK);
		welcomeText.setLineAlignment(HorizontalAlignment.RIGHT);
		welcomeText.draw(g2d, this.outerRightEdge, 65, TextBlockAnchor.TOP_RIGHT);
		
		drawIntroBox(g2d, "Add image processing nodes \nto the workspace.", new Point(margin, this.upperEdgeBox), boxDimension, this.introStep1);
		drawIntroBox(g2d, "Build your workflow by \nconnecting the processing \nnodes.", new Point(margin*2+boxDimension.width, this.upperEdgeBox), boxDimension, this.introStep2);
		drawIntroBox(g2d, "Set the nodes as display of \nwhich you want to get \nthe resulting image.", new Point(margin, yBox), boxDimension, this.introStep3);
		drawIntroBox(g2d, "Execute your workflow to get \nyour results.", new Point(margin*2+boxDimension.width, yBox), boxDimension, this.introStep4);
	}
	
	private void drawIntroBox(final Graphics2D g2d, String text, 
			Point position, Dimension dimension, BufferedImage image) {
		g2d.drawRoundRect(position.x, position.y, dimension.width, dimension.height, 24, 24);
		
		g2d.drawImage(image, position.x+marginSmall, position.y+marginSmall, null);
		
		TextBlock intro1Text = TextUtilities.createTextBlock(text, g2d.getFont(), Color.BLACK);
		intro1Text.setLineAlignment(HorizontalAlignment.LEFT);
		intro1Text.draw(g2d, position.x+marginSmall*2+image.getWidth(), position.y+marginSmall+image.getHeight(), TextBlockAnchor.BOTTOM_LEFT);
	}
}
