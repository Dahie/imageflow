package models;

import models.unit.UnitElement;
import models.unit.UnitFactory;
import junit.framework.TestCase;

public class MacroTests extends TestCase {

	
	public void testMatchingImageTitles() {
		
		final UnitElement source = UnitFactory.createSourceUnit("/Users/danielsenff/zange1.png");
		final UnitElement blur = UnitFactory.createGaussianBlurUnit();
		final UnitElement noise = UnitFactory.createAddNoiseUnit();
		
		final Connection connection1 = new Connection(source, 1, blur, 1);
		final Connection connection2 = new Connection(blur, 1, noise, 1);
		
		assertTrue("status check 1", (connection1.checkConnection() == Connection.Status.OK) );
		String outputImageTitleSource = source.getOutput(0).getImageTitle();
		System.out.println(outputImageTitleSource);
		String inputImageTitleBlur = blur.getInput(0).getImageTitle();
		System.out.println(inputImageTitleBlur);
		assertEquals("check ImageTitles generated on pins", 
				outputImageTitleSource, inputImageTitleBlur);
		
		
		
		assertTrue("status check 2", (connection2.checkConnection() == Connection.Status.OK) );
		String outputimageTitleBlur = blur.getOutput(0).getImageTitle();
		System.out.println(outputimageTitleBlur);
		String InputImageTitleNoise = noise.getInput(0).getImageTitle();
		System.out.println(InputImageTitleNoise);
		assertEquals("check ImageTitles generated on pins", 
				outputimageTitleBlur, InputImageTitleNoise);
		
	}
	
	
}
