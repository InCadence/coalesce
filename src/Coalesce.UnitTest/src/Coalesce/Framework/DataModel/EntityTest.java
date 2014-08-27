package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

public class EntityTest {

/*    @Before
    public void Initialize() {
        
    }
    
    @After
    public void Finalize() {
        
    }*/
    
    @Test
    public void EntityDeserializationMissionTest() {
    
        Entity entity = new Entity();
        
        Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, entity);
        assertNotNull("Failed to deserialize mission entity", desObj);
        assertTrue("Deserialized object no an Entity", desObj instanceof Entity);

    }
    
    @Test
    public void EntityManualCreationTest() {
        
        Entity entity = new Entity(); 
        
        
        Linkagesection linkageSection = new Linkagesection();
        entity.setLinkagesectionNode(linkageSection);
        
        List<Linkage> linkageList = linkageSection.getLinkageNode();
        
        Linkage linkage = new Linkage();
                
        linkage.setEntity1Key("1");
        
        linkage.setEntity2Key("2");
        
        linkageList.add(linkage);
        
        //String xml2 = Serialize(entity, "");
        
    }
    
    @Test
    public void TestEntityMerge() {
        
        Entity entity1 = null; 
        Entity entity2 = null;

        // Define XML
        
        // Deserialize from the same XML
        
        TestModifingDifferentFields(entity1, entity2);
        
        TestModifingSameField(entity1, entity2);
        
        fail("Not yet implemented");
    }
    
    private void TestModifingDifferentFields(Entity entity1, Entity entity2) {
        
        // Modify first and second entities
        
        // Merge Entities: Expected results combine changes from both entities. 

    }
    
    private void TestModifingSameField(Entity entity1, Entity entity2) {
        
        // Modify the same field in both entities
        
        // Merge Entities: Expected results last one in wins. First one should appear as field history.

    }

    @Test
    public void GetLastModifiedTest() {

        Entity entity = new Entity();
        
        Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, entity);

        entity = (Entity)desObj;
        
        DateTime lastModified = entity.getLastmodified();
        
        assertEquals(2014, lastModified.getYear());
        assertEquals(5, lastModified.getMonthOfYear());
        assertEquals(20, lastModified.getDayOfMonth());
        assertEquals(16, lastModified.getHourOfDay());
        assertEquals(17, lastModified.getMinuteOfHour());
        assertEquals(13, lastModified.getSecondOfMinute());
        assertEquals(229, lastModified.getMillisOfSecond());
        
    }
    
    @Test
    public void GetDateCreatedTest() {

        Entity entity = new Entity();
        
        Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, entity);

        entity = (Entity)desObj;
        
        DateTime dateCreated = entity.getDatecreated();
        
        assertEquals(2014, dateCreated.getYear());
        assertEquals(5, dateCreated.getMonthOfYear());
        assertEquals(2, dateCreated.getDayOfMonth());
        assertEquals(14, dateCreated.getHourOfDay());
        assertEquals(33, dateCreated.getMinuteOfHour());
        assertEquals(51, dateCreated.getSecondOfMinute());
        assertEquals(851, dateCreated.getMillisOfSecond());
        
    }

    @Test
    public void SetLastModifiedDefinedTest() {

        Entity entity = new Entity();
        
        Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, entity);

        entity = (Entity)desObj;

        DateTime setLastModified = new DateTime(2222, 12, 5, 11, 44, 55, 666, DateTimeZone.UTC);

        entity.setLastmodified(setLastModified);
        
        DateTime lastModified = entity.getLastmodified();
        
        assertEquals(2222, lastModified.getYear());
        assertEquals(12, lastModified.getMonthOfYear());
        assertEquals(5, lastModified.getDayOfMonth());
        assertEquals(11, lastModified.getHourOfDay());
        assertEquals(44, lastModified.getMinuteOfHour());
        assertEquals(55, lastModified.getSecondOfMinute());
        assertEquals(666, lastModified.getMillisOfSecond());
        
        String xmlString = lastModified.toString();
        
        assertEquals("2222-12-05T11:44:55.666Z", xmlString);

    }
    
    @Test
    public void SetLastModifiedSerializedTest() {
    
        Entity entity = new Entity();
        
        Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, entity);

        entity = (Entity)desObj;

        DateTime setLastModified = new DateTime(2222, 12, 5, 11, 44, 55, 666, DateTimeZone.UTC);

        entity.setLastmodified(setLastModified);

        StringBuilder xml = new StringBuilder();
        CallResult rst = XmlHelper.Serialize(entity, xml);
        assertTrue(rst.getIsSuccess());
        
        Entity desSerEntity = new Entity();
        Object desSerializedObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, desSerEntity);
        
        desSerEntity = (Entity)desSerializedObj;
        
        DateTime lastModified = entity.getLastmodified();
        
        assertEquals("2222-12-05T11:44:55.666Z", lastModified.toString());


    }
    
    @Test
    public void SetDateCreatedTest() {

        Entity entity = new Entity();
        
        Object desObj = XmlHelper.Deserialize(CoalesceTypeInstances.TestMission, entity);

        entity = (Entity)desObj;

        DateTime setDateCreated = new DateTime(2222, 12, 5, 11, 44, 55, 666, DateTimeZone.UTC);
            
        entity.setDatecreated(setDateCreated);
        
        DateTime dateCreated = entity.getDatecreated();
        
        assertEquals(2222, dateCreated.getYear());
        assertEquals(12, dateCreated.getMonthOfYear());
        assertEquals(5, dateCreated.getDayOfMonth());
        assertEquals(11, dateCreated.getHourOfDay());
        assertEquals(44, dateCreated.getMinuteOfHour());
        assertEquals(55, dateCreated.getSecondOfMinute());
        assertEquals(666, dateCreated.getMillisOfSecond());
        
        String xmlString = dateCreated.toString();
        
        assertEquals("2222-12-05T11:44:55.666Z", xmlString);

    }
    
    @Test
    public void TestFieldHistory() {
        fail("Not yet implemented");
    }

    @Test
    public void TestClassificationMarkings() {
        fail("Not yet implemented");
    }

    @Test
    public void TestBinaryTypes() {
        fail("Not yet implemented");
    }

    @Test
    public void TestRecordCreation() {
        fail("Not yet implemented");
    }
    
    @Test
    public void TestLinkingEntities() {
        fail("Not yet implemented");
    }

}
