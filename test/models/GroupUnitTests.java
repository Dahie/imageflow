package models;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import de.danielsenff.imageflow.models.SelectionList;
import de.danielsenff.imageflow.models.connection.Connection;
import de.danielsenff.imageflow.models.connection.ConnectionList;
import de.danielsenff.imageflow.models.connection.ProxyInput;
import de.danielsenff.imageflow.models.unit.GroupUnitElement;
import de.danielsenff.imageflow.models.unit.UnitElement;
import de.danielsenff.imageflow.models.unit.UnitList;

public class GroupUnitTests {

	
	private GroupUnitElement createGroupUnit() {

		/*
		 * init workspace 
		 */
		
		SelectionList selections = new SelectionList();
		UnitList allUnits = new UnitList();
		ConnectionList connList = allUnits.getConnections();
		
		/*
		 * init workflow 
		 */
		
		UnitElement unit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit2 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit3 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit4 = UnitFactoryExt.createAddNoiseUnit();
		
		Connection unit1unit2 = new Connection(unit1, 1, unit2, 1);
		connList.add(unit1unit2);
		Connection unit2unit3 = new Connection(unit2, 1, unit3, 1);
		connList.add(unit2unit3);
		Connection unit3unit4 = new Connection(unit3, 1, unit4, 1);
		connList.add(unit3unit4);
		
		selections.add(unit2);
		selections.add(unit3);
		
		/*
		 * create group
		 */
		
		GroupUnitElement group = new GroupUnitElement(new Point(55,55), "groupunit", selections, allUnits);
		
		return group;
	}
	
	
	@Test public void testGroupCreation() {
		GroupUnitElement group = createGroupUnit();
		
		assertEquals("nr in group", 2, group.getGroupSize());
	}
	
	/**
	 * a Group is destroyed and the single parts are restored
	 */
	@Test public void testGroupExplosion() {
		GroupUnitElement group = createGroupUnit();
		
		
	}
	
	public void testGroupAttachedUnits() {
		// TODO test that units are directly connected to each other 
		
	}
	
	
	@Test public void testProxyInputs() {
		UnitElement unit1 = UnitFactoryExt.createAddNoiseUnit();
		UnitElement unit2 = UnitFactoryExt.createFindEdgesUnit();
		UnitElement unit3 = new UnitElement("name", "imagej");
		
		Connection conn_u1u2 = new Connection(unit1, 1, unit2, 1);
		conn_u1u2.connect();
//		Input input = new Input(DataTypeFactory.createInteger(), unit1, 1);
		
		assertEquals(unit1, conn_u1u2.getFromUnit());
		assertEquals(unit2, conn_u1u2.getToUnit());
		assertEquals(unit2.getInput(0), conn_u1u2.getInput());
		
		ProxyInput pInput = new ProxyInput(unit2.getInput(0), unit3, 1);
		unit3.addInput(pInput);
		
		Connection conn_u1pu2p = new Connection(unit1.getOutput(0), pInput);
		conn_u1pu2p.connect();
		
		assertEquals(unit1, conn_u1pu2p.getFromUnit());
		assertEquals(unit2, conn_u1pu2p.getToUnit());
		assertEquals(unit2.getInput(0), conn_u1pu2p.getInput());
	}
}
