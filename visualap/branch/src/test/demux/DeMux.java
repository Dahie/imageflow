// ONLY FOR DEMO PURPOSE
package test.demux;
import common.SampledAudio;
import java.io.Serializable;
import javax.sound.sampled.*;
	 
/**
 * 
 * DeMux is a component used to split stereo audio in two mono audio.
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     1.0
 */
public class DeMux implements Serializable, Cloneable {
	public static final long serialVersionUID = 7150038053989176116L;
 
	public Object clone() {
		DeMux cloning = new DeMux();
//		cloning.setValue(value);
		return cloning;
	}

// method accessible from the outside world!
    public SampledAudio channel0(SampledAudio sa) throws Exception {
		if (sa==null) return null;
		if (sa.length==0) return null;
		if (sa.format.getChannels() != 2) throw new Exception("stereo audio input is required");
		AudioFormat	format = new AudioFormat(
			sa.format.getEncoding(),
			sa.format.getSampleRate(),
			sa.format.getSampleSizeInBits(),
			1, // channels = 1
			sa.format.getFrameSize()/2,
			sa.format.getFrameRate(),
			sa.format.isBigEndian());
		SampledAudio out = new SampledAudio(format, sa.length / 2);
		int numbytes = sa.format.getSampleSizeInBits() / 8; // sample size in bytes
		out.length = sa.length/2;
		for (int j = 0; j < numbytes; j++)
			for (int k = 0; k < sa.length/(2*numbytes); k++)
				out.buffer[j+k*numbytes] = sa.buffer[j+k*numbytes*2];
		return out;
    }
    public SampledAudio channel1(SampledAudio sa) throws Exception {
		if (sa==null) return null;
		if (sa.length==0) return null;
		if (sa.format.getChannels() != 2) throw new Exception("stereo audio input is required");
		AudioFormat	format = new AudioFormat(
			sa.format.getEncoding(),
			sa.format.getSampleRate(),
			sa.format.getSampleSizeInBits(),
			1, // channels = 1
			sa.format.getFrameSize()/2,
			sa.format.getFrameRate(),
			sa.format.isBigEndian());
		SampledAudio out = new SampledAudio(format, sa.length / 2);
		int numbytes = sa.format.getSampleSizeInBits() / 8; // sample size in bytes
		out.length = sa.length/2;
		for (int j = 0; j < numbytes; j++)
			for (int k = 0; k < sa.length/(2*numbytes); k++)
				out.buffer[j+k*numbytes] = sa.buffer[j+k*numbytes*2+numbytes];
		return out;
    }

	public static String getToolTipText() {
		return "split stereo audio in two mono audio";
	}

}
