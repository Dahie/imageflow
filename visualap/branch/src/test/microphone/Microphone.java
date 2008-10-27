/*
IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

package test.microphone;
import common.SampledAudio;
import java.io.*;
import javax.sound.sampled.*;
import java.beans.PropertyVetoException; // will be launched if parameter value is not permitted
import java.util.HashMap;
	 
/**
 * 
 * Microphone is a component used to generate an audio tone
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     0.7
 */


public class Microphone implements Serializable, Cloneable {
	public static final long serialVersionUID = -8378699618672210113L;
	public static final String version = "0.7";

	private transient int iteration;
	private transient boolean doIterate;
	private transient HashMap<String, Object> globalvars;
	private transient SampledAudio audiodata;
	private transient TargetDataLine line=null;
	private transient int bytes2read = 0;
	
	float samplerate=44100;
	float duration=2;
	int sampleSizeInBits=8;

	public static String getToolTipText() {
		return "record an audio stream from the microphone";
	}

	public void setContext(HashMap<String, Object> globalvars) {
		this.globalvars = globalvars;
	}

	public void start() throws LineUnavailableException {
		iteration = 0;
		doIterate = true;

		AudioFormat format = new AudioFormat(samplerate,sampleSizeInBits,1,true,false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		audiodata = new SampledAudio(format, (Integer) globalvars.get("blocksize"));

		bytes2read = (int)(duration*samplerate);
				
		line = (TargetDataLine) AudioSystem.getLine(info);
		line.open(format, audiodata.buffer.length); // line.getBufferSize()
		line.start();
	}

	public void stop() {
		if (line != null) {
            line.stop();
            line.close();
            line = null;
		}
	}

	public boolean iterate() {
		iteration++;
		if (bytes2read == 0) {
			doIterate = false;
		}
		return doIterate;
	}

    //property getter method
    public float getSampleRate(){
        return samplerate;
    }
 
    //property setter method
    public void setSampleRate(float samplerate) throws PropertyVetoException{
		if ((samplerate > 100000)||(samplerate < 100))
			throw new PropertyVetoException("value must be between 100 and 100000!", null);
		this.samplerate = samplerate;
    }
 
     //property getter method
    public float getDuration(){
        return duration;
    }
 
    //property setter method
    public void setDuration(float newduration) throws PropertyVetoException{
		if (newduration < 0.1)
			throw new PropertyVetoException("duration_ms must be greater than 0.1 seconds!", null);
        duration = newduration;
    }

    //property getter method
    public int getSampleSizeInBits(){
        return sampleSizeInBits;
    }
 
    //property setter method
    public void setSampleSizeInBits(int newsampleSizeInBits) throws PropertyVetoException{
		if ((newsampleSizeInBits!=8)&&(newsampleSizeInBits!=16))
			throw new PropertyVetoException("sampleSizeInBits either 8 or 16 bits!", null);
        sampleSizeInBits = newsampleSizeInBits;
    }

	public Object clone() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.close();
			return (new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))).readObject();
		} catch (Exception ex) {
			System.err.println("Exception originated by Microphone.clone(): "+ex.getMessage());
			return null; // error during cloning
		}
	}

// method accessible from the outside world!
    public SampledAudio capture() throws LineUnavailableException {
		if (!doIterate) return null; // required by design rules
		if (bytes2read == 0) {
			doIterate = false;
			return null;
		}

		int blocksize=audiodata.buffer.length;

		if (bytes2read < blocksize)
			blocksize = bytes2read;
		int bytesread = line.read(audiodata.buffer, 0, blocksize);
		if (bytesread == -1) {
			doIterate = false;
			return null;
		}
		bytes2read -= bytesread;
		audiodata.length = bytesread;
		return audiodata;

    }

}
