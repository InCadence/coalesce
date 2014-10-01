package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.xerces.impl.dv.util.Base64;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.GeneratedJAXB.Entity;
import Coalesce.Framework.GeneratedJAXB.Entity.Linkagesection;
import Coalesce.Framework.GeneratedJAXB.Entity.Linkagesection.Linkage;
import Coalesce.Framework.GeneratedJAXB.Entity.Section;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field.Fieldhistory;


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

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void entityDeserializationMissionTest()
    {

        Object desObj = XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);
        assertNotNull("Failed to deserialize mission entity", desObj);
        assertTrue("Deserialized object no an Entity", desObj instanceof Entity);

    }

    @Test
    public void entityManualCreationTest()
    {

        Entity entity = new Entity();

        assertNull(entity.getLinkagesection());

        Linkagesection linkageSection = new Linkagesection();
        entity.setLinkagesection(linkageSection);

        assertEquals(linkageSection, entity.getLinkagesection());
        assertNotNull(linkageSection.getLinkage());
        assertTrue(linkageSection.getLinkage().isEmpty());

        List<Linkage> linkageList = linkageSection.getLinkage();

        Linkage linkage = new Linkage();

        linkage.setEntity1Key("1");

        linkage.setEntity2Key("2");

        linkageList.add(linkage);

        assertEquals(1, linkageList.size());
        assertEquals(linkage, linkageSection.getLinkage().get(0));

        // String xml2 = Serialize(entity, "");

    }

    @Test
    public void getLastModifiedTest()
    {

        Object desObj = XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        Entity entity = (Entity) desObj;

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
    public void getDateCreatedTest()
    {

        Object desObj = XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        Entity entity = (Entity) desObj;

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
    public void setLastModifiedDefinedTest()
    {

        Object desObj = XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        Entity entity = (Entity) desObj;

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
    public void setLastModifiedSerializedTest()
    {

        Entity entity = (Entity) XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        DateTime setLastModified = new DateTime(2222, 12, 5, 11, 44, 55, 666, DateTimeZone.UTC);

        entity.setLastmodified(setLastModified);

        String xml = XmlHelper.serialize(entity);
        assertTrue(!StringHelper.isNullOrEmpty(xml));

        Object desSerializedObj = XmlHelper.deserialize(xml, Entity.class);

        Entity desSerEntity = (Entity) desSerializedObj;

        DateTime lastModified = desSerEntity.getLastmodified();

        assertEquals("2222-12-05T11:44:55.666Z", lastModified.toString());

    }

    @Test
    public void setDateCreatedTest()
    {

        Entity entity = new Entity();

        Object desObj = XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        entity = (Entity) desObj;

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
    public void testFieldHistory()
    {
        Entity missionEntity = (Entity) XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        Field field = missionEntity.getSection().get(1).getRecordset().get(0).getRecord().get(0).getField().get(0);

        List<Fieldhistory> fieldHistory = field.getFieldhistory();

        assertEquals("00BB7A9F-4F37-46E9-85EB-9280ED3619CC", fieldHistory.get(0).getKey());
        assertEquals("3FA9F3E1-23D9-49C6-BCA3-CE84135223A5", fieldHistory.get(1).getKey());

        Fieldhistory newFieldHistory = new Fieldhistory();
        newFieldHistory.setKey("11111111-AAAA-BBBB-CCCC-222222222222");
        newFieldHistory.setPrevioushistorykey("3FA9F3E1-23D9-49C6-BCA3-CE84135223A5");
        fieldHistory.add(newFieldHistory);

        assertEquals(newFieldHistory, field.getFieldhistory().get(2));

        String serializedEntity = XmlHelper.serialize(missionEntity);

        Entity desMissionEntity = (Entity) XmlHelper.deserialize(serializedEntity, Entity.class);

        Fieldhistory desFieldHistory = desMissionEntity.getSection().get(1).getRecordset().get(0).getRecord().get(0).getField().get(0).getFieldhistory().get(2);

        assertEquals("11111111-AAAA-BBBB-CCCC-222222222222", desFieldHistory.getKey());
        assertEquals("3FA9F3E1-23D9-49C6-BCA3-CE84135223A5", desFieldHistory.getPrevioushistorykey());

    }

    @Test
    public void testClassificationMarkings()
    {
        // TODO: Add specific testing when attribute type changes to a complex type from a string
    }

    @Test
    public void testBinaryTypes() throws UnsupportedEncodingException
    {
        Entity newEntity = new Entity();

        Section newSection = new Section();
        newEntity.getSection().add(newSection);

        Recordset newRecordset = new Recordset();
        newSection.getRecordset().add(newRecordset);

        Record newRecord = new Record();
        Field newField = new Field();
        newField.setDatatype(ECoalesceFieldDataTypes.BinaryType.getLabel());

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        String value = Base64.encode(dataBytes);
        newField.setValue(value);

        newRecord.getField().add(newField);
        newRecordset.getRecord().add(newRecord);

        String serializedEntity = XmlHelper.serialize(newEntity);
        Entity desEntity = (Entity) XmlHelper.deserialize(serializedEntity, Entity.class);

        Field desField = desEntity.getSection().get(0).getRecordset().get(0).getRecord().get(0).getField().get(0);

        String rawData = desField.getValue();

        assertEquals("VGVzdGluZyBTdHJpbmc=", rawData);

        byte[] bytes = Base64.decode(rawData);

        assertArrayEquals(dataBytes, bytes);
        assertEquals(ECoalesceFieldDataTypes.BinaryType.getLabel(), desField.getDatatype());

    }

    @Test
    public void testRecordCreation()
    {
        Entity newEntity = new Entity();

        Section newSection = new Section();
        newEntity.getSection().add(newSection);

        Recordset newRecordset = new Recordset();
        newSection.getRecordset().add(newRecordset);

        Record newRecord = new Record();

        assertNull(newRecord.getDatecreated());
        assertNull(newRecord.getLastmodified());
        assertTrue(newRecord.getField().isEmpty());
        assertNull(newRecord.getName());
        assertNull(newRecord.getKey());
        assertTrue(newRecord.getOtherAttributes().isEmpty());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        DateTime yesterday = now.minusDays(1);
        String guid = GUIDHelper.getGuidString(UUID.randomUUID());
        QName qName = new QName("uri:thing", "lc", "prf");

        newRecord.setDatecreated(yesterday);
        newRecord.setLastmodified(now);
        newRecord.getField().add(new Field());
        newRecord.setName("New Record");
        newRecord.setKey(guid);
        newRecord.getOtherAttributes().put(qName, "Other Attribute");

        newRecordset.getRecord().add(newRecord);

        assertEquals(1, newRecordset.getRecord().size());
        assertEquals(newRecord, newRecordset.getRecord().get(0));

        String serializedEntity = XmlHelper.serialize(newEntity);
        Entity desEntity = (Entity) XmlHelper.deserialize(serializedEntity, Entity.class);

        Record desRecord = desEntity.getSection().get(0).getRecordset().get(0).getRecord().get(0);

        assertEquals(yesterday, desRecord.getDatecreated());
        assertEquals(now, desRecord.getLastmodified());
        assertEquals(1, desRecord.getField().size());
        assertEquals("New Record", desRecord.getName());
        assertEquals(guid, desRecord.getKey());
        assertEquals(1, desRecord.getOtherAttributes().size());
        assertEquals("Other Attribute", desRecord.getOtherAttributes().get(qName));

    }

}
