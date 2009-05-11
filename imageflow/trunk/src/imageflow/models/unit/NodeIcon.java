/**
 * 
 */
package imageflow.models.unit;

import imageflow.models.unit.UnitModelComponent.Size;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import visualap.GPanel;


/**
 * This class draws the regular icon representation on the {@link GPanel}. 
 * @author danielsenff
 *
 */
public class NodeIcon implements UnitModelComponent {
	
	public static Dimension largeComponentDimension = new Dimension(100,100);
	public static Dimension smallComponentDimension = new Dimension(100,30);
	public static int padding = 5;
	/**
     * Size of the pins for In- and Outputs.
     */
    public static int pinSize = 8;
	/**
	 * Size of the arc of the round corners
	 */
	protected int arc = 10;	
	
	
	
	
	
	//Dimensions
	int x, y;
    
//    int widthIconBg = width - 10;
//    int heightIconBg = height - 10;
    
	
	protected Color color1;
	protected Color color2;
	protected Graphics2D g2;
	
	//Texts
//	protected String unitName ;
	protected String parametersLabel = "P";
	protected String infoLabel = "i";
	/**
	 * Functional icon of this unit. This symbol is illustration.
	 */
	protected Image icon;	
	
	/**
	 * icon for the display indicator
	 */
	protected Image displayIcon;
	String displayIconFile = "/imageflow/resources/display16.png";

	/**
	 * Associated unit of to this icon
	 */
	protected UnitElement unit;
	private int unitID;
	private Dimension dimension;
	
	
	/**
	 * @param unit 
	 * 
	 */
	public NodeIcon(final UnitElement unit) {
		this.unit = unit;
		this.unitID = unit.getUnitID();
		this.dimension = largeComponentDimension;
		
		try {
			this.displayIcon = ImageIO.read(this.getClass().getResourceAsStream(displayIconFile));
//			this.displayIcon = ImageIO.read(new File(displayIconFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(unit.getIcon() !=null) {
			this.icon = unit.getIcon();
		}
	}
	

	/**
	 * @return 
	 */
	public int getWidth() {
		return (int) this.dimension.getWidth();
	}
	
	/**
	 * @return 
	 */
	public int getHeight() {
		return (int) this.dimension.getHeight();
	}
	
	/**
	 * Returns the units icon in one of the 3 sizes: BIG, MEDIUM, SMALL
	 * @param size
	 * @return
	 */
	public BufferedImage getImage(final Size size) {
		BufferedImage resultImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	    Graphics2D g2 = resultImage.createGraphics();
	    switch(size) {
	    default:
	    case BIG:
	    	this.dimension = largeComponentDimension;
	    	paintBigIcon(g2);
	    	break;
	    case MEDIUM:
	    	paintMediumIcon(g2);
	    	break;
	    case SMALL:
	    	this.dimension = smallComponentDimension;
	    	paintSmallIcon(g2);
	    	break;
	    }
	    
	    return resultImage;
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
		
		int width = largeComponentDimension.width-2*padding;
		int height = largeComponentDimension.height-2*padding;
		
		drawElement(g2, width, height, unit.isDisplayUnit(), true, true);
		return g2;
	}


	private void drawElement(final Graphics2D g2, 
			int widthBg,
			int heightBg, 
			boolean isDisplayUnit, 
			boolean displayIcon, 
			boolean displayIndex) {
		// location and dimension
		this.x = 0 + padding;
		this.y = 0 + padding;
		
	    g2.setRenderingHint(
	    		RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);

	    
	    //draw background
		drawBackground(g2, arc, widthBg, heightBg);
	    
	    // draw icon
		if(displayIcon)
			drawIcon(g2, widthBg, heightBg);
	    
	    // draw texts
	    drawTexts(g2, widthBg, heightBg, displayIndex);
	    
	    // draw icon for display
	    if(isDisplayUnit) {
	    	int xDisplay = this.x+(widthBg/2)+20;
			int yDisplay = this.y+5;
//			g2.drawImage(this.displayIcon, xDisplay, yDisplay, null);
			g2.drawImage(this.displayIcon, xDisplay, yDisplay, xDisplay+12,yDisplay+12, 0, 0, 18, 18, null);
	    }
	}
	
	
	/**
	 * @param g22
	 */
	public Graphics2D paintMediumIcon(Graphics2D g2) {
		 g2.setRenderingHint(
		    		RenderingHints.KEY_ANTIALIASING,
		            RenderingHints.VALUE_ANTIALIAS_ON);

		 //draw background
		 drawBackground(g2, arc, 50, 50);

		 // draw icon
		 drawIcon(g2, 50, 50);
		 
		 return g2;
	}

	private void drawTexts(final Graphics2D g2, 
			int widthIconBg, 
			int heightIconBg, 
			boolean displayIndex) {
		//draw text, status
	    g2.setColor(Color.WHITE);

	    String unitName = unit.getLabel();
	    
		// scale font on big lengths
		FontMetrics fm = g2.getFontMetrics();
	    int stringWidth = fm.stringWidth(unitName);
		int fontsize = 12;
		int fontsizeOriginal = 12;
		Font font = g2.getFont();
		while(stringWidth > widthIconBg-10) {
			fontsize--;
			Font newFont = new Font(font.getFamily(), Font.PLAIN, fontsize);
			g2.setFont(newFont);
			stringWidth = g2.getFontMetrics().stringWidth(unitName);
		}
		
		// and if even now to small, then cut
		g2.drawString(unitName, x+5, y+heightIconBg-5);
		
		// if true display unitindex
		if(displayIndex) {
			g2.setFont(new Font(font.getFamily(), font.getStyle(), fontsizeOriginal));
			g2.drawString(unitID+"", x+5, y+15);
		}
	}

	/**
	 * this color is based on the type of the unit:
	 * green for a source
	 * blue for a filter
	 * brown for a sink
	 * @param g2
	 * @param arc
	 * @param width
	 * @param height
	 */
	private void drawBackground(Graphics2D g2, int arc, int width, int height) {
		// 
		Color cTop = new Color(84, 121, 203, 255);
		Color cBottom = new Color(136, 169, 242, 255);

		int delta = 20;
		Color color = unit.getColor();
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		
		cTop = new Color(
				(r-delta) > 255 ? 255 : r-delta,
				(g-delta) > 255 ? 255 : g-delta,
				(b-delta) > 255 ? 255 : b-delta);
		cBottom = new Color(
				(r+delta) > 255 ? 255 : r+delta,
				(g+delta) > 255 ? 255 : g+delta,
				(b+delta) > 255 ? 255 : b+delta);
		
		GradientPaint gradient1 = new GradientPaint(x+10,y+10,cTop,x+100,y+30,cBottom);
	    g2.setPaint(gradient1);
	    g2.fillRoundRect(x, y, width, height, arc, arc);

	    g2.setStroke(new BasicStroke(1f));
	    g2.setColor(new Color(0,0,0,44));
	    g2.drawRoundRect(x, y, width, height, arc, arc); 
	}

	
	private void drawIcon(Graphics2D g2, int width, int height) {
		if(this.icon != null) {
			int iconWidth = icon.getWidth(null);
			int iconHeight = icon.getHeight(null);
			int xIcon = (width/2) - (iconWidth/2) + this.x;
			int yIcon = (height/2) - (iconHeight/2) + this.y;
			g2.drawImage(icon, xIcon, yIcon, null);
		}
	}

	
	/**
	 * Returns the icon illustration.
	 * @return
	 */
	public Image getIcon() {
		return icon;
	}


	/**
	 * Set the illustrating icon
	 * @param icon
	 */
	public void setIcon(final Image icon) {
		this.icon = icon;
	}

	public Graphics2D paintSmallIcon(Graphics2D g2) {
		int width = smallComponentDimension.width-2*padding;
		int height = smallComponentDimension.height-2*padding;
		drawElement(g2, width, height, unit.isDisplayUnit(), false, false);
		return g2;
	}


	public Dimension getDimension() {
		return this.dimension;
	}
	
	public void setDimension(Size size) {
		this.dimension = size == Size.BIG ? largeComponentDimension : smallComponentDimension;
	}
	
	public static Dimension getDimensionFromSize(Size size) {
		switch(size) {
		default:
		case BIG:
			return largeComponentDimension;
		case SMALL:
			return smallComponentDimension;
		}
	}


	public static Size getSizeFromString(String sizeString) {
		if(sizeString.toLowerCase().equals(Size.BIG.toString().toLowerCase())) {
			return Size.BIG;
		} else if (sizeString.toLowerCase().equals(Size.MEDIUM.toString().toLowerCase())){
			return Size.MEDIUM;
		} else if (sizeString.toLowerCase().equals(Size.SMALL.toString().toLowerCase())){
			return Size.SMALL;
		} else
			return null;
	}
}
