/**
 * 
 */
package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import models.unit.NodeIcon;
import models.unit.UnitFactory;



/**
 * @author danielsenff
 *
 */
public class IconPreview extends JFrame {

	private BufferedImage bi;



	/**
	 * 
	 */
	public IconPreview() {

		
		NodeIcon icon = new NodeIcon(UnitFactory.createGaussianBlurUnit());
		bi = icon.getImage();
		
		
		setTitle("Icon Test");
		setSize(400,200);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	@Override
	public void paint(Graphics g) {
		
		int width =  bi.getWidth(), height =  bi.getHeight();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height+25);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);   
        g.drawImage(bi, 0, 25, null);
  	
	}
	
	
	
	public static void main(String[] args) {
	
		new IconPreview();
		
	}
	
}
