package test;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.Macro_Runner;

/**
 * Trying to run macro is a contained environment without a running ImageJ instance
 * @author danielsenff
 *
 */
public class MacroEnvironment {

	public static void main(String[] args) {
		
		/*
		 * 	if(imagej == null)
				imagej = new ImageJ(null, ImageJ.EMBEDDED);
			IJ.log(macro);
			IJ.runMacro(macro, "");
		 */
		
		Macro_Runner mr = new Macro_Runner();
		String htr =  mr.runMacro(macroText, "");
		System.out.println(htr);
	}
	
	static String macroText = 
		
		"setBatchMode(true); \n" +

		"// open an image \n" +
		"open(\"/Users/danielsenff/zange1.png\"); \n" +
		"ID_temp = getImageID(); \n" +
		"run(\"Duplicate...\", \"title=Source1\"); \n" +
		"ID_Source1 = getImageID(); \n" +
		"selectImage(ID_temp); \n" +
		"close(); \n" +

		"// blur image\n" +
		"selectImage(ID_Source1); \n" +
		"run(\"Duplicate...\", \"title=Proc1_out1\"); \n" +
		"ID_Proc1_out1 = getImageID(); \n" +
		"run(\"Gaussian Blur...\", \"sigma=2\"); \n" +

		"// close the images that are not to be displayed \n" +
		"selectImage(ID_Source1); \n" +
		"close(); \n" +

		"setBatchMode(\"exit and display\"); ";

	
}
