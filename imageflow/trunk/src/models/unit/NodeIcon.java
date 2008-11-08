/**
 * 
 */
package models.unit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import visualap.GPanel;


/**
 * This class draws the regular icon representation on the {@link GPanel}. 
 * @author danielsenff
 *
 */
public class NodeIcon {
	
	public enum Size {BIG, MEDIUM, SMALL};
	
	//Dimensions
	protected int height = 100;
	protected int width = 100;
    int x;
    int y;
    int padding = 5;
    int widthIconBg = width - 10;
    int heightIconBg = height - 10;
    /**
     * Size of the pins for In- and Outputs.
     */
    public static int pinSize = 8;
	protected int arc = 10;	
	
	protected Color color1;
	protected Color color2;
	protected Graphics2D g2;
	
	//Texts
	protected String unitName ;
	protected String parametersLabel = "P";
	protected String infoLabel = "i";
	protected Image icon;	// icon of the node
	protected Image displayIcon; // icon for the display indicator
	String displayIconFile = "bin/res/display16.png";

	protected UnitElement unit;
	
	/**
	 * 
	 */
	private NodeIcon() {
	}
	
	/**
	 * 
	 */
	public NodeIcon(final UnitElement unit) {
		this.unit = unit;
		this.unitName = unit.getUnitName();
		
		
		
		try {
			
			this.displayIcon = ImageIO.read(new File(displayIconFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(unit.getIcon() !=null) 
			this.icon = unit.getIcon();
	}
	

	/**
	 * 
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * 
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the units icon in one of the 3 sizes: BIG, MEDIUM, SMALL
	 * @param size
	 * @return
	 */
	public BufferedImage getImage(final Size size) {
		BufferedImage resultImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	    Graphics2D g2 = resultImage.createGraphics();
	    if(size == Size.BIG) {
	    	paintBigIcon(g2);
	    } else if (size == Size.MEDIUM){
	    	paintMediumComponent(g2);
	    }
	    
	    return resultImage;
		
	}
	
	/**
	 * @param g22
	 */
	private void paintMediumComponent(Graphics2D g22) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Returns the medium unit-icon.
	 * @return
	 */
	public BufferedImage getImage() {
		return getImage(Size.BIG);
	}
	
	/**
	 * @param g2 
	 * @return 
	 */
	public Graphics2D paintBigIcon(final Graphics2D g2) {
		
		// location and dimension
		this.x = unit.getOrigin().x + padding;
		this.y = unit.getOrigin().y + padding;
		
	    g2.setRenderingHint(
	    		RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);

	    //draw background
	    drawBackground(g2);

	    // draw icon
	    drawIcon(g2);
	    
	    //draw text, status
	    g2.setColor(Color.WHITE);
	    //TODO scale font on big lengths
	    if(unitName.length() > 6) {
	    	// medium
	    } else if(unitName.length() > 12) {
	    	// small
	    }
	    // and if even now to small, then cut
	    
	    g2.drawString(unitName, x+5, y+85);
	    
	    /*Font font = g2.getFont();
	    g2.drawString(parametersLabel, x+5, y+20);
	    
	    g2.drawString(infoLabel, x+25, y+20);
	    g2.setColor(Color.BLACK);
	    g2.drawString("x", x+width-20, y+20);*/
	    
	    // draw icon for display
	    if(unit.isDisplayUnit()) {
			int xDisplay = this.x+(width/2)+16;
			int yDisplay = this.y+8;
			g2.drawImage(this.displayIcon, xDisplay, yDisplay, null);
	    }
	    
	    //draw inputs and outputs
		return g2;
	    
	    //TODO draw shadow
	}

	private void drawBackground(Graphics2D g2) {
		// this color is based on the type of the unit:
	    // green for a source
	    // blue for a filter
	    // brown for a sink
		Color cTop = new Color(84, 121, 203, 255);
		Color cBottom = new Color(136, 169, 242, 255);

		int delta = 20;
		Color color = unit.getColor();
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
	
//		switch(unit.getType()) {
//		default:
//		case FILTER:
//			r = 110;
//			g = 145;
//			b = 222;
//			break;
//		case SINK:
//			r = 186;
//			g = 154;
//			b = 94;
//			break;
//		case SOURCE:
//			r = 156;
//			g = 186;
//			b = 146;
//			break;
//		}
		cTop = new Color(r-delta, g-delta, b-delta, 255);
		cBottom = new Color(r+delta, g+delta, b+delta, 255);
		
//		GradientPaint gradient1 = new GradientPaint(10,10,cTop,30,30,cBottom,true);
		GradientPaint gradient1 = new GradientPaint(x+10,y+10,cTop,x+100,y+30,cBottom);
	    g2.setPaint(gradient1);
	    g2.fillRoundRect(x, y, widthIconBg, heightIconBg, arc, arc);

	    g2.setStroke(new BasicStroke(1f));
	    g2.setColor(new Color(0,0,0,44));
	    g2.drawRoundRect(x, y, widthIconBg, heightIconBg, arc, arc); 
	}

	private void drawIcon(Graphics2D g2) {
		if(this.icon != null) {
			int iconWidth = icon.getWidth(null);
			int iconHeight = icon.getHeight(null);

			int x = (this.width/2)-(iconWidth/2);
			int y = (this.height/2)-(iconHeight/2);

			g2.drawImage(icon, x, y, null);
	
		}
	}

	private void drawInputs(final Graphics2D g2, final int number) {
	    
	    for (int i = 0; i < number; i++) {
			g2.setColor(Color.BLACK);
			int y =  (i*heightIconBg/number) + (heightIconBg/(2*number));
			g2.fillRect(0, y, pinSize, pinSize);
		}
	}
	

	private void drawOutputs(final Graphics2D g2, final int number) {
	    
	    for (int i = 0; i < number; i++) {
			g2.setColor(Color.BLACK);
			int y =  (i*heightIconBg/number) + (heightIconBg/(2*number));
			
			int w = this.widthIconBg+3;
			Polygon po=new Polygon(); //
			po.addPoint(w+0,y+0); //top
			po.addPoint(w+pinSize,y+4); //pointy
			po.addPoint(w+0,y+pinSize); //bottom
			g2.fillPolygon(po);
			g2.drawPolygon(po);
			
		}
	}
	
	
	
	
}
