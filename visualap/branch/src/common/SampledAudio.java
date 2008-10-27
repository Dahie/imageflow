/* class SampledAudio

data carrier for sampled audio

Version 1.0, december 2007

Note: This class shall not modified. If custom class is needed, please create a new class that extends SampledAudio!

javalc6
*/
package common;
import javax.sound.sampled.*;

public class SampledAudio {
	public AudioFormat format;
	public byte[] buffer;
	public int length; // number of valid bytes in buffer

	public SampledAudio(AudioFormat format, int n) {
		this.format = format;
		buffer = new byte[n*format.getFrameSize()];
		length = 0;
	}
}
