package models;

import imageflow.models.Connection;
import imageflow.models.ConnectionList;
import imageflow.models.unit.UnitElement;
import imageflow.models.unit.UnitFactory;

import java.awt.Dimension;

import junit.framework.TestCase;

public class MacroTests extends TestCase {

	
	public void testMatchingImageTitles() {
		
		final UnitElement source = UnitFactory.createBackgroundUnit(new Dimension(12, 12));
		final UnitElement blur = UnitFactory.createGaussianBlurUnit();
		final UnitElement noise = UnitFactory.createAddNoiseUnit();
		
		final Connection connection1 = new Connection(source, 1, blur, 1);
		final Connection connection2 = new Connection(blur, 1, noise, 1);
		ConnectionList connList = new ConnectionList();
		assertTrue(connList.add(connection1));
		assertTrue(connList.add(connection2));
		
		assertTrue("is conn1 connected", connection1.isConnected());
		assertTrue("is conn2 connected", connection2.isConnected());
		
		assertTrue("status check 1", (connection1.checkConnection() == Connection.Status.OK) );
		String outputImageTitleSource = source.getOutput(0).getImageTitle();
		System.out.println(outputImageTitleSource);
		String inputImageTitleBlur = blur.getInput(0).getImageTitle();
//		System.out.println(inputImageTitleBlur);
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
