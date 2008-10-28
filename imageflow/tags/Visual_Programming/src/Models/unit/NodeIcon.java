/**
 * 
 */
package Models.unit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * @author danielsenff
 *
 */
public class NodeIcon {
	
	//Dimensions
	protected int height = 100;
	protected int width = 100;
    int x = 5;
    int y = 5;
    int widthIconBg = width - 10;
    int heightIconBg = height - 10;
    public static int pinSize = 8;
	protected int arc = 10;	
	
	protected Color color1;
	protected Color color2;
	protected Graphics2D g2;
	
	//Texts
	protected String unitName ;
	protected String parametersLabel = "P";
	protected String infoLabel = "i";
	protected BufferedImage icon;
	

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
		this.unitName = unit.getName();
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
	 * @return
	 */
	public BufferedImage getImage() {
		BufferedImage resultImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	    Graphics2D g2 = resultImage.createGraphics();
	    paintComponent(g2);
	    return resultImage;
	}
	
	/**
	 * @return 
	 * 
	 */
	public Graphics paintComponent(Graphics2D g2) {
		
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
	    
	    g2.drawString(unitName, x+5, heightIconBg);
	    
	    Font font = g2.getFont();
	    g2.drawString(parametersLabel, x+5, y+20);
	    
	    g2.drawString(infoLabel, x+25, y+20);
	    
	    
	    //draw inputs and outputs
	    
//	    drawInputs(g2, 1);
	    
//	    drawOutputs(g2, 1);
		return g2;
	    
	    
	    //draw shadow
	    

	}

	private void drawBackground(Graphics2D g2) {
		// this color is based on the type of the unit:
	    // green for a source
	    // blue for a filter
	    // brown for a sink
		Color cTop = new Color(84, 121, 203, 255);
		Color cBottom = new Color(136, 169, 242, 255);

		switch(unit.getType()) {
		default:
		case FILTER:
			cTop = new Color(84, 121, 203, 255);
			cBottom = new Color(136, 169, 242, 255);
			break;
		case SINK:
			break;
		case SOURCE:
			cTop = new Color(134, 171, 116, 255);
			cBottom = new Color(179, 202, 176, 255);
			break;
		}

		
//		GradientPaint gradient1 = new GradientPaint(10,10,cTop,30,30,cBottom,true);
		GradientPaint gradient1 = new GradientPaint(10,10,cTop,100,30,cBottom);
	    g2.setPaint(gradient1);
	    g2.fillRoundRect(x, y, widthIconBg, heightIconBg, arc, arc);

	    g2.setStroke(new BasicStroke(1f));
	    g2.setColor(new Color(0,0,0,44));
	    g2.drawRoundRect(x, y, widthIconBg, heightIconBg, arc, arc); 
	}

	private void drawIcon(Graphics2D g2) {
		if(this.icon != null) {
			int iconWidth = icon.getWidth();
			int iconHeight = icon.getHeight();

			int x = (this.width/2)-(iconWidth/2);
			int y = (this.height/2)-(iconHeight/2);

			g2.drawImage(icon, x, y, null);
	
		}
	}

	private void drawInputs(Graphics2D g2, final int number) {
	    
	    for (int i = 0; i < number; i++) {
			g2.setColor(Color.BLACK);
			int y =  (i*heightIconBg/number) + (heightIconBg/(2*number));
			g2.fillRect(0, y, pinSize, pinSize);
		}
	}
	

	private void drawOutputs(Graphics2D g2, final int number) {
	    
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
