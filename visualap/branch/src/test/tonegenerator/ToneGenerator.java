// ONLY FOR DEMO PURPOSE

package test.tonegenerator;
import common.SampledAudio;
import java.io.*;
import javax.sound.sampled.*;
import java.beans.PropertyVetoException; // will be launched if parameter value is not permitted
import java.util.HashMap;
	 
/**
 * 
 * ToneGenerator is a component used to generate an audio tone
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     1.0
 */


public class ToneGenerator implements Serializable, Cloneable {
	public static final long serialVersionUID = -8514971836215086463L;
	private int iteration;
	boolean doIterate;
	private HashMap<String, Object> globalvars;
	private SampledAudio audiodata;
    private String type = "Sine";
	
	int freq=440;
	int duration_ms=2000;
	int volume=100;

	public static String getToolTipText() {
		return "generate an audio tone";
	}

	public void setContext(HashMap<String, Object> globalvars) {
		this.globalvars = globalvars;
	}

	public void start() {
		iteration = 0;
		doIterate = true;
		audiodata = new SampledAudio(new AudioFormat(44100,8,1,true,false), (Integer) globalvars.get("blocksize"));
	}

	public boolean iterate() {
		iteration++;
		return doIterate;
	}

    //property getter method
    public int getFrequency(){
        return freq;
    }
 
    //property setter method
    public void setFrequency(int newfreq) throws PropertyVetoException{
		if (newfreq > 19999)
			throw new PropertyVetoException("value must be lower than 20000!", null);
		freq = newfreq;
    }
 
     //property getter method
    public int getDuration_ms(){
        return duration_ms;
    }
 
    //property setter method
    public void setDuration_ms(int newduration_ms){
        duration_ms = newduration_ms;
    }

    //property getter method
    public int getVolume(){
        return volume;
    }
 
    //property setter method
    public void setVolume(int newvolume) throws PropertyVetoException{
		if (newvolume > 127)
			throw new PropertyVetoException("volume must be lower than 128!", null);
        volume = newvolume;
    }

    //property getter method
    public String getType(){
        return type;
    }
 
    //property setter method
    public void setType(String newType){
        type = newType;
    }

	public Object clone() {
		ToneGenerator cloning = new ToneGenerator();
		try {
			cloning.setFrequency(freq);
			cloning.setDuration_ms(duration_ms);
			cloning.setVolume(volume);
			cloning.setType(type);
		}
		catch (PropertyVetoException ignore) {	}
		return cloning;
	}

// method accessible from the outside world!
    public SampledAudio generate() {
//		for(int i=0; i<duration_ms*44100/1000; i++){
		if (!doIterate) return null; // required by design rules
		int blocksize=audiodata.buffer.length;
		int base=blocksize*iteration;
		int last = duration_ms*44100/1000-base;
		if (last > blocksize) last = blocksize;
		if (type.equals("Sine"))
			for(int i=0; i<last; i++) {
				double angle = (i+base)/44100.0*2.0*Math.PI*freq;
				audiodata.buffer[i]=(byte)(Math.sin(angle)*volume);
			}
		else if (type.equals("Square"))
			for(int i=0; i<last; i++) {
				if (((i+base)/44100.0*freq) % 1.0 < 0.5)
					audiodata.buffer[i]=(byte)-volume;
				else
					audiodata.buffer[i]=(byte)volume;
			}
		audiodata.length = last;
		doIterate = (last == blocksize);
		return audiodata;

    }

}
