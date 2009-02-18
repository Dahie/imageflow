package imageflow.models.unit;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import imageflow.models.MacroElement;
import imageflow.models.Output;
import imageflow.models.parameter.StringParameter;

import java.awt.Point;
import java.io.File;

public class SourceUnitElement extends UnitElement {

	public SourceUnitElement(Point origin, String unitName,
			MacroElement macroElement) {
		super(origin, unitName, macroElement);
	}
	
	public SourceUnitElement(Point origin, String unitName,
			String macroString) {
		super(origin, unitName, macroString);
	}

	@Override
	public void showProperties() {
		super.showProperties();

		String path = (String) parameters.get(0).getValue();
		if(new File(path).exists()) {
			int bitdepth = getBitDepth();
			System.out.println("bitdepth: "+ bitdepth);

			// change bitdepth for all outputs
			for (Output output : outputs) {
				output.setOutputBitDepth(bitdepth);
			}

		} else {
			System.out.println("file doesn't exist");
		}
		notifyModelListeners();
	}


	/**
	 * for source units only
	 * @return
	 */
	public int getBitDepth() {
		final String path = ((StringParameter)parameters.get(0)).getValue();
		System.out.println("path of image: " +path);
		if(new File(path).exists()) {
			final ImagePlus imp = IJ.openImage(path);
			imp.close();
			final int bitDepth = imp.getBitDepth();
			return bitDepth;
		}
		return -1;
	}
	
	public File getFile() {
		final String path = ((StringParameter)parameters.get(0)).getValue();
		return new File(path);
	}

}
