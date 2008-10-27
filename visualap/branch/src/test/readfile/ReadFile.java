package test.readfile;
import common.SampledAudio;
import java.io.*;
import java.awt.image.BufferedImage;
import com.sun.image.codec.jpeg.*;
import javax.sound.sampled.*;
import java.util.HashMap;
	 
/**
 * 
 * ReadFile is a component used to read the content of files.
 * Note: visualap components shall implement Serializable and Cloneable
 * 
 * @author      javalc6
 * @version     1.0
 */



public class ReadFile implements Serializable, Cloneable {
	public static final long serialVersionUID = 621984702336837585L;
    private String aFile = "";
	private int iteration;
	private HashMap<String, Object> globalvars;
	boolean doIterate;
	private LineNumberReader intext;
	private String [] textbuf;
	private AudioInputStream ain;
	private SampledAudio audiodata;
	private int filetype;

/**
* Returns a string that provide short information about the component
* 
* @return      the short information about the component
*/
	public static String getToolTipText() {
		return "read a file";
	}

/**
* Provide global variables, including blocksize
* 
* @param globalvars The global variables available
*/
	public void setContext(HashMap<String, Object> globalvars) {
		this.globalvars = globalvars;
	}

/**
* start the iteration process
* 
* @param blocksize Number of samples, used only for sampled sources
*/
	public void start() throws IOException, UnsupportedAudioFileException {
		iteration = 0;
		doIterate = true;
		filetype = FileEditorX.fileType(aFile);
		int blocksize = (Integer) globalvars.get("blocksize");

		switch (filetype) {
			case 1:  // text file
				textbuf = new String[blocksize];
				intext = new LineNumberReader(new FileReader(aFile)); break;
			case 10:  // image file
				break;
			case 20:  // audio file
				ain=AudioSystem.getAudioInputStream(new File(aFile));
				AudioFormat format = ain.getFormat();
				DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);
				if (!AudioSystem.isLineSupported(info)) {
					AudioFormat pcm =
						new AudioFormat(format.getSampleRate(), 16,
										format.getChannels(), true, false);
					ain = AudioSystem.getAudioInputStream(pcm, ain);
				}
// allocate a buffer
				audiodata = new SampledAudio(ain.getFormat(), blocksize);
				break;
			default: throw new IOException("Invalid file type");
		}
		
	}

/**
* Perform several iterations
* 
* @return      continue iteration
*/
	public boolean iterate() {
		iteration++;
		return doIterate;
	}

/**
* Performs needed action when iterations are stopped
* 
*/
	public void stop() throws IOException {
		switch (filetype) {
			case 1:  // text file
				intext.close();
				break;
			case 10:  // image file
				break;
			case 20:  // audio file
				ain.close();
				break;
		}
	}

/**
* FileName getter
* 
* @return      FileName
*/
    public String getFile(){
        return aFile;
    }
 
/**
* FileName setter
* 
* @param newFile FileName
*/
    public void setFile(String newFile){
        aFile = newFile;
    }
 

/**
* Performs cloning of the current object
* 
* @return      the cloned object
*/
	public Object clone() {
		ReadFile cloning = new ReadFile();
		cloning.setFile(aFile);
		return cloning;
	}

/**
* Reads file content
* 
* @return      a piece of the file
*/
    public Object generate() throws IOException {
		if (!doIterate) return null; // required by design rules
		switch (filetype) {
			case 1:  // text file
				for (int k=0; k < textbuf.length; k++) {
					textbuf[k] = intext.readLine();
					if (textbuf[k] == null)	{
						doIterate = false;
						String [] result = new String[k];
						System.arraycopy(textbuf, 0, result, 0, k);
						return result;
					}
				}
				return textbuf;
			case 10:  // image file
				File f = new File(aFile);
				JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(new FileInputStream(f));
				BufferedImage image =decoder.decodeAsBufferedImage() ;
				doIterate = false;
				return image;
			case 20:  // audio file
                int bytesread=ain.read(audiodata.buffer);
                if (bytesread == -1) {
					doIterate = false;
					return null;
				}
				audiodata.length = bytesread;
				return audiodata;
			default: throw new IOException("Invalid file type");
		}
    }

}
