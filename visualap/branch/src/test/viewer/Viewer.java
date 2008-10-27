// ONLY FOR DEMO PURPOSE
// todo: perform handling of concurrency in VPanel.paintComponent()
package test.viewer;
import common.SampledAudio;
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.Vector;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.*;
	 
/**
 * 
 * Viewer is a component used to display input data
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     1.0
 */


public class Viewer implements Serializable, Cloneable {
	public static final long serialVersionUID = 6529006835049087633L;
	private static int hpos = 0;
	private transient JFrame frame=null;
	private transient TPanel textpanel;
	private transient VPanel gpanel;
	private static int counter = 1;


	public static String getToolTipText() {
		return "display input data";
	}

	public void start() {
		if (frame == null) {
			frame = new JFrame("Viewer");
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setLocation(hpos, 530); hpos += 100;
			//Display the window.
			frame.setVisible(true);
		}
		textpanel = new TPanel();
		gpanel = new VPanel(); // used if an image is received
		frame.setContentPane(textpanel);
        frame.pack();
		frame.invalidate();
	}

    //property getter method
 
    //property setter method
 
	// clone()
	public Object clone() {
		Viewer cloning = new Viewer();
//		cloning.setValue(value);
		return cloning;
	}

	public void dispose() {
		if (frame != null) {
			hpos = 0;
			frame.dispose();
		}
	}

// method accessible from the outside world!
    public void display(Object data) {
		if (data == null)
			textpanel.append("null");
		else if (data instanceof String)
				textpanel.append((String)data+"\n");
		else if (data instanceof String[])
				for (int k=0; k < ((String[])data).length; k++)
					textpanel.append(((String[])data)[k]+"\n");
		else if (data instanceof Integer)
				textpanel.append((Integer)data+"\n");
		else if (data instanceof BufferedImage) {
				gpanel.setImage((BufferedImage)data);
				frame.setContentPane(gpanel);
				frame.pack();
				frame.invalidate();
			}
		else if (data instanceof SampledAudio) {
				if (((SampledAudio)data).length == 0) return;
				gpanel.setSamples((SampledAudio) data);
				frame.setContentPane(gpanel);
				frame.pack();
				frame.invalidate();
			}
		else textpanel.append("Unsupported data type\n");
	}
}

class VPanel extends JPanel {
	BufferedImage backBuffer = null;
	String message = null;
	Vector<Line2D.Double> lines = new Vector<Line2D.Double>();
	private final ReentrantLock lock = new ReentrantLock();


	public void setImage(BufferedImage backBuffer) {
		this.backBuffer = backBuffer;
		int cWidth = backBuffer.getWidth();
		int cHeight = backBuffer.getHeight();
		setPreferredSize(new Dimension(cWidth,cHeight));
	}

	public void setSamples(SampledAudio data) {
		if (data == null) return;
		if (data.length == 0) return;
		backBuffer = null;
		setPreferredSize(new Dimension(128,128));
//		message = "Unsupported format";
		lines.clear();
		int w = 128;
		int h = 128;
		int[] audioData = null;
		if (data.format.getSampleSizeInBits() == 16) {
			 audioData = new int[data.length / 2];
			 if (data.format.isBigEndian()) {
				for (int i = 0; i < data.length / 2; i++) {
					 int MSB = (int) data.buffer[2*i];
					 int LSB = (int) data.buffer[2*i+1];
					 audioData[i] = MSB << 8 | (255 & LSB);
				 }
			 } else {
				 for (int i = 0; i < data.length / 2; i++) {
					 int LSB = (int) data.buffer[2*i];
					 int MSB = (int) data.buffer[2*i+1];
					 audioData[i] = MSB << 8 | (255 & LSB);
				 }
			 }
		 } else if (data.format.getSampleSizeInBits() == 8) {
			 audioData = new int[data.length];
			 if (data.format.getEncoding().toString().startsWith("PCM_SIGN")) {
				 for (int i = 0; i < data.length; i++) {
					 audioData[i] = data.buffer[i];
				 }
			 } else {
				 for (int i = 0; i < data.length; i++) {
					 audioData[i] = data.buffer[i] - 128;
				 }
			 }
		}

		int frames_per_pixel = data.length / data.format.getFrameSize()/w;
		byte my_byte = 0;
		double y_last = 0;
		int numChannels = data.format.getChannels();
		for (double x = 0; x < w; x++) {
			int idx = (int) (frames_per_pixel * numChannels * x);
			if (data.format.getSampleSizeInBits() == 8) {
				 my_byte = (byte) audioData[idx];
			} else {
				 my_byte = (byte) (128 * audioData[idx] / 32768 );
			}
			double y_new = (double) (h * (128 - my_byte) / 256);
			lines.add(new Line2D.Double(x, y_last, x, y_new));
			y_last = y_new;
		}


	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension d = getSize();
		int w = d.width;
		int h = d.height;
		if (lines.size() > 0) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setBackground(Color.black);
			g2.clearRect(0, 0, w, h);
			g2.setColor(Color.green);
			if (message!=null) g2.drawString(message, 5, 20);
			try {
				for (int i = 0; i < lines.size(); i++) {
					g2.draw((Line2D)lines.get(i));
				}
			} catch (Exception ex) {
// error may occur due to concurrency: just skip
			}
		}
		else if (backBuffer != null) g.drawImage(backBuffer, 0, 0, this); 
	}
};

class TPanel extends JPanel {
	private JTextArea textArea;
	public TPanel() {
		textArea = new JTextArea(10, 20);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea,
									   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									   JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		add(scrollPane);
		setOpaque(true); //content panes must be opaque
	}
	public void append(String str) {
		textArea.append(str);
	}

};
