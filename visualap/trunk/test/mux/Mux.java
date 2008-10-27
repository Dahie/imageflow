// ONLY FOR DEMO PURPOSE

package test.mux;
import common.SampledAudio;
import java.io.Serializable;
import javax.sound.sampled.*;
	 
/**
 * 
 * Mux is a component used to generate stereo audio from two mono audio inputs
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     0.3
 */

public class Mux implements Serializable, Cloneable {
	public static final long serialVersionUID = -4150926241406122062L;
	public static final String version = "0.3";

	public static AudioFormat.Encoding PCM_SIGNED=AudioFormat.Encoding.PCM_SIGNED;
	public static AudioFormat.Encoding PCM_UNSIGNED=AudioFormat.Encoding.PCM_UNSIGNED;

// Constructor sets inherited properties!
    public Mux(){
    }

	public Object clone() {
		Mux cloning = new Mux();
//		cloning.setName(name);
		return cloning;
	}

    public SampledAudio silence(AudioFormat format, int n) {
		SampledAudio audiodata = new SampledAudio(format, n);
		for(int i=0; i<n; i++) audiodata.buffer[i]=0; // silence
		if ((format.getSampleSizeInBits() == 8)&&(format.getEncoding() == PCM_UNSIGNED))
			for(int i=0; i<n; i++) audiodata.buffer[i]-=128; // convert to unsigned
		audiodata.length = n;
		return audiodata;
    }

    public SampledAudio output(SampledAudio channel0, SampledAudio channel1) throws Exception {
		SampledAudio sa0, sa1;
		if ((channel0 != null)&&(channel0.length!=0)) {
			sa0 = channel0;
			if ((channel1 != null)&&(channel1.length!=0)) {
				sa1 = channel1;
				if (!sa0.format.matches(sa1.format)) throw new Exception("the same format of audio is required on both channels");
			} else sa1 = silence(sa0.format, channel0.length);
		}
		else if ((channel1 != null)&&(channel1.length!=0)) {
				sa1 = channel1;
				sa0 = silence(sa1.format, channel1.length);
			}
			else return null;
		if (sa0.format.getChannels() != 1) throw new Exception("mono audio input is required");
// check compatibility of incoming media
		AudioFormat	format = new AudioFormat(
			sa0.format.getEncoding(),
			sa0.format.getSampleRate(),
			sa0.format.getSampleSizeInBits(),
			2, // channels = 2
			sa0.format.getFrameSize()*2,
			sa0.format.getFrameRate(),
			sa0.format.isBigEndian());
		int numbytes = sa0.format.getSampleSizeInBits() / 8; // sample size in bytes
		int length;
		if (sa1.length > sa0.length)
			length = sa1.length * 2;
		else length = sa0.length * 2;
		SampledAudio out = new SampledAudio(format, length);
		out.length = length;
		for (int j = 0; j < numbytes; j++)
			for (int k = 0; k < sa0.length/numbytes; k++) {
				int i = j+k*numbytes;
				if (i < sa0.length)
					out.buffer[j+k*numbytes*2] = sa0.buffer[i];
				else out.buffer[j+k*numbytes*2] = 0;
				if (i < sa1.length)
					out.buffer[j+k*numbytes*2+numbytes] = sa1.buffer[i];
				else out.buffer[j+k*numbytes*2+numbytes] = 0;
			}
		return out;
    }

	public static String getToolTipText() {
		return "generate stereo audio from two mono audio inputs";
	}

}
