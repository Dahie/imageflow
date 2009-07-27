package interfaces;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import models.UnitFactoryExt;

import org.junit.Test;

import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.unit.UnitElement;

public class DisplayableTests {


	@Test public void testHasDisplayBranch() {
		
		// test output-only
		UnitElement sourceUnit = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));
		UnitElement sourceUnit2 = UnitFactoryExt.createBackgroundUnit(new Dimension(12, 12));;

		// test input/output case
		UnitElement filterUnit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement filterUnit2 = UnitFactoryExt.createAddNoiseUnit();

		ConnectionList connList = new ConnectionList();
		Connection conn1 = new Connection(sourceUnit, 1, filterUnit1, 1);
		connList.add(conn1);
		Connection conn2 = new Connection(filterUnit1, 1, filterUnit2, 1);
		connList.add(conn2);
		
		assertFalse(sourceUnit.hasDisplayBranch());
		assertFalse(sourceUnit2.hasDisplayBranch());
		assertFalse(filterUnit1.hasDisplayBranch());
		assertFalse(filterUnit2.hasDisplayBranch());

		filterUnit1.setDisplay(true);
		
		assertFalse(sourceUnit.isDisplay());
		assertTrue(sourceUnit.hasDisplayBranch());
		assertFalse(sourceUnit2.isDisplay());
		assertFalse(sourceUnit2.hasDisplayBranch());
		assertTrue(filterUnit1.isDisplay());
		assertTrue(filterUnit1.hasDisplayBranch());
		assertFalse(filterUnit2.isDisplay());
		assertFalse(filterUnit2.hasDisplayBranch());
		
		Connection conn1b = new Connection(sourceUnit2, 1, filterUnit1, 1);
		
		connList.add(conn1b);
		
		assertFalse(sourceUnit.hasDisplayBranch());
		assertFalse(sourceUnit2.isDisplay());
		assertTrue(sourceUnit2.hasDisplayBranch());
		assertTrue(filterUnit1.isDisplay());
		assertTrue(filterUnit1.hasDisplayBranch());
		assertFalse(filterUnit2.isDisplay());		
		assertFalse(filterUnit2.hasDisplayBranch());
	}
	
	
}
