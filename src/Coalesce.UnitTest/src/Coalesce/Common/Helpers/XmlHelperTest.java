package Coalesce.Common.Helpers;

import static org.junit.Assert.*;

import org.junit.Test;

import unity.core.runtime.CallResult;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.GeneratedJAXB.*;

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

public class XmlHelperTest {

/*	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}*/
	
	@Test
	public void DeserializeEntityTypeMission() {
	
		Entity entity = new Entity();
		
		Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, entity);
		
		assertNotNull("Failed to deserialize mission entity", desObj);
		assertTrue("Deserialized object no an Entity", desObj instanceof Entity);
		
	}
	
	@Test
	public void DeserializeLinkageSectionTypeMission() {
		
		Linkagesection entityLinkageSection = new Linkagesection();
		
		Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMissionLinkageSection, entityLinkageSection);
		
		assertNotNull("Failed to deserialize mission entity linkage section", desObj);
		assertTrue("Deserialized object no an Entity", desObj instanceof Linkagesection);
		
	}
	
	@Test
	public void DeserializeRecordSetTypeMission() {
		
		Recordset entityRecordSet = new Recordset();
		
		Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMissionRecordSet, entityRecordSet);
		
		assertNotNull("Failed to deserialize mission entity linkage section", desObj);
		assertTrue("Deserialized object no an Entity", desObj instanceof Recordset);
		
	}
	
	@Test
	public void SerializeEntityTypeMission() {
		CallResult rst;
		
		Entity entity = new Entity();
		
		entity = (Entity)XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, entity);
		
		StringBuilder xml = new StringBuilder();
		rst = XmlHelper.Serialize(entity, xml);
		
		assertTrue("Searialize failed", rst.getIsSuccess());
		assertTrue("xml empty", xml.length() > 0);
		
		/* TODO: Resolve this error
		assertEquals(4, entity.getLinkagesection().getLinkage().size());
		assertEquals(16, entity.getSection().get(1).getRecordset().get(0).getFielddefinition().size());
		assertEquals(16, entity.getSection().get(1).getRecordset().get(0).getRecord().get(0).getField().size());
		*/
		assertTrue("Fix this test case", false);
	}

	@Test
	public void SerializeLinkageSectionTypeMission() {
		CallResult rst;
		
		Linkagesection entityLinkageSection = new Linkagesection();
		
		entityLinkageSection = (Linkagesection)XmlHelper.Deserialize(CoalesceTypeInstances.TestMissionLinkageSection, entityLinkageSection);
		
		StringBuilder xml = new StringBuilder();
		rst = XmlHelper.Serialize(entityLinkageSection, xml);
		
		assertTrue("Searialize failed", rst.getIsSuccess());
		assertTrue("xml empty", xml.length() > 0);
		
		String stripped = xml.toString().replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>", "");
		assertEquals(CoalesceTypeInstances.TestMissionLinkageSection.replaceAll("\\s+", ""), stripped.replace(" ", "").replaceAll("\\s+", ""));
		
		assertEquals(4, entityLinkageSection.getLinkageNode().size());

	}
	
	@Test
	public void SerializeRecordSetTypeMission() {
		CallResult rst;
		
		Recordset entityRecordSet = new Recordset();
		
		entityRecordSet = (Recordset)XmlHelper.Deserialize(CoalesceTypeInstances.TestMissionRecordSet, entityRecordSet);
		
		StringBuilder xml = new StringBuilder();
		rst = XmlHelper.Serialize(entityRecordSet, xml);
		
		assertTrue("Searialize failed", rst.getIsSuccess());
		assertTrue("xml empty", xml.length() > 0);
		assertEquals(16, entityRecordSet.getFielddefinitionNode().size());
		/* TODO: Resolve this error
		assertEquals(16, entityRecordSet.getRecord().get(0).getField().size());
		 */
		assertTrue("Fix this test case", false);
		
	}
}
