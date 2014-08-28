package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import org.junit.Test;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import unity.core.runtime.CallResult;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public class XsdEntityTest {

	/*
	 * @BeforeClass public static void setUpBeforeClass() throws Exception {
	 * 
	 * }
	 * 
	 * @AfterClass public static void tearDownAfterClass() throws Exception { }
	 * 
	 * @Before public void setUp() throws Exception {
	 * 
	 * }
	 * 
	 * @After public void tearDown() throws Exception { }
	 */

	@Test
	public void CreateXsdEntityFromXml() {

		try {

			CallResult rst;

			XsdEntity entity = XsdEntity
					.Create(CoalesceTypeInstances.TestMission);
			;

			String title = entity.GetTitle();
			assertEquals(
					"NORTHCOM Volunteer Background Checks, NORTHCOM Volunteer Background Checks",
					title);

			String xml = entity.ToXml();

		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

	@Test
	public void GetTitleWithoutXpathTest() {
		CallResult rst;

		XsdEntity entity = XsdEntity
				.Create(CoalesceTypeInstances.TestMission);

		String title = entity.GetTitle();

		String entityXml = entity.ToXml();
		
		
		//assertEquals(entityXml, CoalesceTypeInstances.TestMission);
		assertEquals("TREX Portal", title);

	}

	@Test
	public void EmptyDateInitialization() {
		fail("Not yet implemented");
	}

	@Test
	public void UpdateTitleThatUsesXpathTest() {
		fail("Not yet implemented");
	}

	@Test
	public void UpdateTitleThatDoesNotUseXpathTest() {
		fail("Not yet implemented");
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void CreateTREXOperation() {

		XsdEntity entity = null;
		XsdSection section = null;
		XsdRecordset recordSet = null;

		// Create Entity


		
		
		entity = XsdEntity
				.Create("TREXOperation",
						"TREX Portal",
						"1.0.0.0",
						"",
						"",
						"TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

		assertTrue(entity.GetSource() == "TREX Portal");

		XsdLinkageSection.Create(entity, true);

		// Create Live Status Section
		section = XsdSection.Create(entity, "Live Status Section", true);
		recordSet = XsdRecordset.Create(section, "Live Status Recordset");
		
		XsdFieldDefinition.Create(recordSet, "CurrentStatus",
				ECoalesceFieldDataTypes.StringType);

		// Create Information Section
		section = XsdSection.Create(entity, "Operation Information Section",
				true);
		recordSet = XsdRecordset.Create(section,
				"Operation Information Recordset");
		XsdFieldDefinition.Create(recordSet, "OperationName",
				ECoalesceFieldDataTypes.StringType);


		String entityXml = entity.ToXml();
		
		String test = null;

	}

	
}

/*
public class OperationEntity() extends XsdEntity {
	
	public Initialize() {
		
		XsdEntity entity = null;
		XsdSection section = null;
		XsdRecordset recordSet = null;
		
		entity.SetName("TREXOperation");
		entity.SetSource("TREX Portal");
		entity.SetVersion("1.0.0.1");
		entity.SetEntityId("");
		entity.SetEntityIdType("");
		
		entity.
		
		XsdLinkageSection.Create(this, true);

		// Create Live Status Section
		section = XsdSection.Create(this, "Live Status Section", true);
		recordSet = XsdRecordset.Create(section, "Live Status Recordset");
		
		XsdFieldDefinition.Create(recordSet, "CurrentStatus",
				ECoalesceFieldDataTypes.StringType);

		// Create Information Section
		section = XsdSection.Create(this, "Operation Information Section",
				true);
		recordSet = XsdRecordset.Create(section,
				"Operation Information Recordset");
		XsdFieldDefinition.Create(recordSet, "OperationName",
				ECoalesceFieldDataTypes.StringType);
		
	}
	
}
*/