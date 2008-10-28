/**
 * 
 */
package Models.unit;

import graph.Node;
import graph.NodeAbstract;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;


/**
 * @author danielsenff
 *
 */
public class NodeUnit extends NodeAbstract {
	protected transient Image icon=null;
	protected String label;
	
	
	
	/**
	 *  constructor not to be used, XMLEncoder/XMLDecoder
	 */
	protected NodeUnit() {
		super();
	}

	/**
	 * basic constructor
	 * @param origin
	 */
	public NodeUnit(Point origin) {
		super(origin);
	}

	/**
	 * constructor including object
	 * Note: the class of the object shall implement Cloneable interfaces.
	 * @param origin
	 * @param obj
	 */
	public NodeUnit(Point origin, Object obj) {
		super(origin);
		setObject(obj);
		setSerialUID(getObjSerialUID());
		setVersion(getObjVersion());
	}
	
	
	/* (non-Javadoc)
	 * @see graph.NodeAbstract#setObject(java.lang.Object)
	 */
	@Override
	public void setObject(Object obj) {
		this.obj = obj;
		UnitElement unit = (UnitElement) obj;
		icon = new NodeIcon(unit).getImage();
		label = unit.getName();
		dimension.setSize(icon.getWidth(null), icon.getHeight(null));
	}

	/* (non-Javadoc)
	 * @see graph.Node#clone()
	 */
	@Override
	public Node clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see graph.Node#paint(java.awt.Graphics, java.awt.image.ImageObserver)
	 */
	@Override
	public Rectangle paint(Graphics g, ImageObserver io) {
		Color saveColor = g.getColor();
		Font saveFont = g.getFont();
		if (icon == null) {
			// obj != null, icon == null
			/*g.setFont(new Font("Arial", Font.PLAIN, 14));
			FontMetrics fm = g.getFontMetrics();*/
			g.setColor(selected ? Color.red : new Color(250, 220, 100));
//			dimension.setSize(fm.stringWidth(label) + 10, fm.getHeight() + 4);
			g.fillRect(origin.x, origin.y, dimension.width, dimension.height);
			g.setColor(Color.black);
			g.drawRect(origin.x, origin.y, dimension.width-1, dimension.height-1);
//			g.drawString(label, origin.x + 5, (origin.y + 2) + fm.getAscent());
		} else {
			
			// obj != null
			if (selected) {
				g.setColor(Color.red);
				g.drawRect(origin.x-2, origin.y-2, dimension.width+4, dimension.height+4);
			}
			g.drawImage(icon, origin.x, origin.y, dimension.width, dimension.height, io);
		}
		
		
		//draw inputs
		UnitElement unit = (UnitElement) obj;
		int numberInputs = unit.getInputsActualCount();
		for (int i = 0; i < numberInputs; i++) {
			g.setColor(Color.BLACK);
			int y =  alignY(numberInputs, i, icon.getHeight(null));
			g.fillRect(origin.x, origin.y+y, NodeIcon.pinSize, NodeIcon.pinSize);
		}

		//draw outputs
		int numberOutputs = unit.getOutputsCount();
		for (int i = 0; i < numberOutputs; i++) {
			g.setColor(Color.BLACK);

			int x = (icon.getWidth(null) - 8)+origin.x;
			int y = alignY(numberOutputs, i, icon.getHeight(null))+origin.y;
			
			Polygon po=new Polygon(); 
			System.out.println(" x"+x+" y"+y);
			po.addPoint(x, y); //top
			po.addPoint(x + NodeIcon.pinSize, y + (NodeIcon.pinSize/2)); //pointy
			po.addPoint(x, y+NodeIcon.pinSize); //bottom
			g.fillPolygon(po);
			g.drawPolygon(po);
		}

			
			
		// during draggin
		if (dragging != null) {
			g.setColor(Color.black);
			g.drawRect(dragging.x, dragging.y, dragging.width-1, dragging.height-1);
		}
		
		return new Rectangle(origin, dimension);
	}

	private int alignY(int numberOutputs, int i, int height) {
		int value = (i*height/numberOutputs) + (height/(2*numberOutputs));
		return value - (NodeIcon.pinSize / 2);
	}
	
	/**
	 * Checks if there is an input or an output at this mouse coordinates. 
	 */
	public Object contains(int x, int y) {
		UnitElement unit = (UnitElement) obj;
		System.out.println(" x"+x+" y"+y);
		//TODO map this on my input/output positions
		
		int tolerance = 3;
		if ((x >= origin.x - tolerance)&&(x < origin.x + tolerance))	{
			int inputsActualCount = unit.getInputsActualCount();
			for (int i = 0; i < inputsActualCount; i++) {
//				int lower_y = origin.y-3 +(dimension.height*i+dimension.height/2)/inputsActualCount;
				int lower_y = alignY(inputsActualCount, i, dimension.height);
				if ((y >= lower_y)&&(y <= lower_y + 6)) return unit.getInput(i);
			}
		}
		if ((x >= origin.x + dimension.width - tolerance)&&(x < origin.x + dimension.width + tolerance))	{
			int outputsCount = unit.getOutputsCount();
			System.out.println("within area");
			for (int i = 0; i < outputsCount; i++) {
//				int lower_y = origin.y-3 +(dimension.height*i+dimension.height/2)/outputsCount;
				int lower_y = alignY(outputsCount, i, dimension.height)+origin.y;
				System.out.println(lower_y);
				if ((y >= lower_y)&&(y <= lower_y + tolerance*2)) {
					System.out.println("right within area");
					return unit.getOutput(i);
				}
			}
		}
		return super.contains(x,y);
	}


}
