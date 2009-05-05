package imageflow.models.unit;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Interface for a view-class for {@link UnitElement}-Models. 
 * This Class gives the necessary method definitions required
 * for representing units on the workspace.
 * @author danielsenff
 *
 */
public interface UnitModelComponent {

	public enum Size {BIG, MEDIUM, SMALL};
	
	/**
	 * Draws the unit-element in an image.
	 * @param size
	 * @return
	 */
	public BufferedImage getImage(final Size size);
	public BufferedImage getImage();
	
	
	public Graphics2D paintBigIcon(final Graphics2D g2);
	public Graphics2D paintMediumIcon(Graphics2D g2);
	public Graphics2D paintSmallIcon(Graphics2D g22);
	
	public Image getIcon();
	public void setIcon(final Image icon);
}
