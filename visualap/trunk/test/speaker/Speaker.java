// ONLY FOR DEMO PURPOSE

package test.speaker;
import common.SampledAudio;
import java.io.Serializable;
import javax.sound.sampled.*;	 

/**
 * 
 * Speaker is a component used to play the incoming audio stream
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     1.0
 */


public class Speaker implements Serializable, Cloneable {
	public static final long serialVersionUID = -1982168458360538704L;
	private int iteration;
	SourceDataLine sdl=null;

	public void start() {
		iteration = 0;
	}

	public void stop() {
		if (sdl != null) {
			sdl.drain();
			sdl.stop();
			sdl.close();
		}
	}

	public Object clone() {
		Speaker cloning = new Speaker();
//		cloning.setValue(value);
		return cloning;
	}

// method accessible from the outside world!
    public void playback(SampledAudio sa) throws LineUnavailableException {
		if (sa==null) return;
		if (sa.length==0) return;
		if (iteration == 0)	{
			sdl = AudioSystem.getSourceDataLine(sa.format);
			sdl.open(sa.format);
			sdl.start();	
		}
		sdl.write(sa.buffer,0,sa.length);
		iteration++;
	}

	public static String getToolTipText() {
		return "play the incoming audio stream";
	}

}
