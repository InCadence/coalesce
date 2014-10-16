package com.incadencecorp.coalesce.framework.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.CoalesceAssert;
import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntitySyncShell;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntitySyncShellTest;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor.EntityMetaData;
import com.incadencecorp.unity.local.connector.LocalConfigurationsConnector;

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

/**
 * Provides an extendable JUnit base class for testing the interface logic defined in
 * {@link com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor}. All classes that extend this JUnit base class
 * must provide {@link com.incadencecorp.coalesce.framework.persistance.ServerConn} and
 * {@link com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor} instances that the base class will use to run
 * the interface tests against. The following methods must be implemented by the subclass in addition to all abstract
 * methods.
 * 
 * <pre>
 * 
 * 
 * &#064;BeforeClass
 * public static void setUpBeforeClass() throws CoalesceException
 * {
 * 
 *     &lt;SubclassPersistor&gt; tester = new &lt;SubclassPersistor&gt;();
 * 
 *     CoalescePersistorBaseTest.setupBeforeClassBase(tester);
 * 
 * }
 * 
 * &#064;AfterClass
 * public static void tearDownAfterClass()
 * {
 *     &lt;SubclassPersistor&gt; tester = new &lt;SubclassPersistor&gt;();
 * 
 *     CoalescePersistorBaseTest.tearDownAfterClassBase(tester);
 * 
 * }
 * </pre>
 * 
 * @author InCadence
 *
 */
public abstract class CoalescePersistorBaseTest {

    /*--------------------------------------------------------------------------
    Private Variables
    --------------------------------------------------------------------------*/

    private static ServerConn _serCon;
    private static CoalesceFramework _coalesceFramework;

    private static CoalesceEntity _entity;
    private static String _entityXml;
    private static String _fieldKey;

    private static String _testTemplateKey = null;

    /*--------------------------------------------------------------------------
    JUnit Before/After functions
    --------------------------------------------------------------------------*/

    protected abstract ServerConn getConnection();

    protected abstract ICoalescePersistor getPersistor(ServerConn conn);

    protected abstract CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException;

    protected static void setupBeforeClassBase(CoalescePersistorBaseTest tester) throws CoalesceException
    {
        CoalesceSettings.initialize(new LocalConfigurationsConnector());

        _serCon = tester.getConnection();

        ICoalescePersistor persistor = tester.getPersistor(_serCon);

        _coalesceFramework = new CoalesceFramework();
        _coalesceFramework.initialize(persistor);

        CoalescePersistorBaseTest.cleanUpDatabase(tester);

        _entity = CoalescePersistorBaseTest.createEntity();
        _entityXml = _entity.toXml();

        _fieldKey = CoalescePersistorBaseTest.getCurrentStatusField(_entity).getKey();

        assertTrue(_coalesceFramework.saveCoalesceEntity(_entity));

    }

    protected static void tearDownAfterClassBase(CoalescePersistorBaseTest tester)
    {

        CoalescePersistorBaseTest.cleanUpDatabase(tester);

        _serCon = null;
        _coalesceFramework = null;

    }

    private static void cleanUpDatabase(CoalescePersistorBaseTest tester)
    {
        try (CoalesceDataConnectorBase conn = tester.getDataConnector(_serCon))
        {
            List<String> objectKeys = _coalesceFramework.getCoalesceEntityKeysForEntityId("EntityId",
                                                                                          "EntityIdType",
                                                                                          "TestEntity",
                                                                                          "Unit Test");

            for (String objectKey : objectKeys)
            {
                deleteEntity(conn, objectKey);
            }

            CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

            deleteEntity(conn, mission.getKey());

        }
        catch (Exception e)
        {
        }

    }

    @After
    public void tearDown()
    {
        if (!StringHelper.isNullOrEmpty(_testTemplateKey))
        {
            try (CoalesceDataConnectorBase conn = getDataConnector(_serCon))
            {
                conn.executeCmd("delete from CoalesceEntityTemplate where TemplateKey = '" + _testTemplateKey + "'");
            }
            catch (Exception e)
            {
            }
            finally
            {
                _testTemplateKey = null;
            }
        }
    }

    /*--------------------------------------------------------------------------
    Tests
    --------------------------------------------------------------------------*/

    @Test
    public void testConnection() throws CoalescePersistorException, Exception
    {
        try (CoalesceDataConnectorBase conn = getDataConnector(_serCon))
        {
            conn.openConnection();
        }
    }

    protected abstract ServerConn getInvalidConnection();

    /*
     * @Test(expected = SQLException.class) public void testFAILConnection() throws SQLException, Exception,
     * CoalescePersistorException { ServerConn serConFail = getInvalidConnection();
     * 
     * try (CoalesceDataConnectorBase conn = getDataConnector(serConFail)) { conn.openConnection(); } }
     */

    @Test
    public void testSaveMissionEntity() throws CoalescePersistorException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertTrue(_coalesceFramework.saveCoalesceEntity(entity));

    }

    @Test
    public void testSaveEntityAndXPath() throws CoalescePersistorException
    {
        // assertTrue(_coalesceFramework.SaveCoalesceEntity(_entity));

        // Get Field from DB Using XPath
        CoalesceField<?> field = _coalesceFramework.getCoalesceFieldByFieldKey(_fieldKey);
        assertNotNull(field);

        // Get Record from Entity
        CoalesceRecord record = (CoalesceRecord) field.getParent();
        assertNotNull(record);

        // Get Record from DB Using XPath
        CoalesceRecord recordDB = _coalesceFramework.getCoalesceRecord(record.getKey());
        assertNotNull(recordDB);
        assertEquals(record.getName(), recordDB.getName());

    }

    @Test
    public void testEntityTemplate() throws SAXException, IOException, CoalescePersistorException
    {
        CoalesceEntityTemplate template = testTemplate(CoalesceEntityTemplate.create(_entity));
        assertTrue(_coalesceFramework.saveCoalesceEntityTemplate(template));

        String templateXml = template.toXml();

        String templateKey = _coalesceFramework.getCoalesceEntityTemplateKey(_entity.getName(),
                                                                             _entity.getSource(),
                                                                             _entity.getVersion());
        assertFalse(StringHelper.isNullOrEmpty(templateKey));

        _testTemplateKey = templateKey;

        String templateXmlFromKey = _coalesceFramework.getCoalesceEntityTemplateXml(templateKey);

        CoalesceAssert.assertXmlEquals(templateXml, templateXmlFromKey);

        String templateXmlFromAttr = _coalesceFramework.getCoalesceEntityTemplateXml(_entity.getName(),
                                                                                     _entity.getSource(),
                                                                                     _entity.getVersion());
        CoalesceAssert.assertXmlEquals(templateXml, templateXmlFromAttr);

        CoalesceEntityTemplate persistedTemplate = _coalesceFramework.getCoalesceEntityTemplate(_entity.getName(),
                                                                                                _entity.getSource(),
                                                                                                _entity.getVersion());

        CoalesceAssert.assertXmlEquals(templateXml, persistedTemplate.toXml());

    }

    @Test
    public void testCreateEntityFromTemplate() throws CoalescePersistorException, SAXException, IOException
    {
        CoalesceEntityTemplate template = testTemplate(CoalesceEntityTemplate.create(_entity));
        
        assertTrue(_coalesceFramework.saveCoalesceEntityTemplate(template));

        String templateKey = _coalesceFramework.getCoalesceEntityTemplateKey(_entity.getName(),
                                                                             _entity.getSource(),
                                                                             _entity.getVersion());

        _testTemplateKey = templateKey;

        CoalesceEntity entity = _coalesceFramework.createEntityFromTemplate(_entity.getName(),
                                                                            _entity.getSource(),
                                                                            _entity.getVersion());

        @SuppressWarnings("unused")
        String entityXml = entity.toXml();

        CoalesceAssert.assertTemplateCreation(_entity, entity);

    }

    @Test
    public void testGetCoalesceEntitySyncShell() throws CoalescePersistorException, SAXException, IOException
    {

        CoalesceEntitySyncShell shell = _coalesceFramework.getCoalesceEntitySyncShell(_entity.getKey());

        // Validate
        assertNotNull(shell.toXml());
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(shell));

    }

    @Test
    public void testGetEntityMetaData() throws CoalescePersistorException
    {
        EntityMetaData objectKey = _coalesceFramework.getCoalesceEntityIdAndTypeForKey(_entity.getKey());

        assertEquals(_entity.getEntityId(), objectKey.getEntityId());
        assertEquals(_entity.getKey().toUpperCase(), objectKey.getEntityKey().toUpperCase());
        assertEquals(_entity.getEntityIdType(), objectKey.getEntityType());
    }

    @Test
    public void testGetEntity() throws CoalescePersistorException
    {
        CoalesceEntity ent = _coalesceFramework.getCoalesceEntity(_entity.getKey());

        CoalesceAssert.assertXmlEquals(_entityXml, ent.toXml());
    }

    @Test
    public void testCheckLastModified() throws CoalescePersistorException
    {
        // Test Entity
        DateTime lastModified = _coalesceFramework.getCoalesceEntityLastModified(_entity.getKey(), "entity");
        assertEquals(lastModified, _entity.getLastModified());

        // Test Section
        CoalesceSection section = _entity.getSection("TestEntity/Live Status Section");

        lastModified = null;
        lastModified = _coalesceFramework.getCoalesceEntityLastModified(section.getKey(), "section");
        assertEquals(lastModified, section.getLastModified());

    }

    @Test
    public void testFAILCheckLastModified() throws CoalescePersistorException
    {
        DateTime lastModified = _coalesceFramework.getCoalesceEntityLastModified(_entity.getKey(), "linkage");

        assertNull(lastModified);

    }

    @Test
    public void testGetEntityByIdAndType() throws CoalescePersistorException
    {
        CoalesceEntity ent = _coalesceFramework.getEntity(_entity.getEntityId(), _entity.getEntityIdType());

        CoalesceAssert.assertXmlEquals(_entityXml, ent.toXml());
    }

    @Test
    public void testGetEntityByNameAndIdAndType() throws CoalescePersistorException
    {
        CoalesceEntity ent = _coalesceFramework.getEntity(_entity.getName(),
                                                          _entity.getEntityId(),
                                                          _entity.getEntityIdType());

        CoalesceAssert.assertXmlEquals(_entityXml, ent.toXml());
    }

    @Test
    public void testGetFieldValue() throws CoalescePersistorException
    {
        String fieldValue = _coalesceFramework.getCoalesceFieldValue(_fieldKey);

        assertEquals("Test Status", fieldValue);
    }

    @Test
    public void testFAILGetFieldValue() throws CoalesceException
    {
        // Create a new entity, but do not save the entity
        CoalesceEntity entity = CoalescePersistorBaseTest.createEntity();

        String fieldValue = _coalesceFramework.getCoalesceFieldValue(CoalescePersistorBaseTest.getCurrentStatusField(entity).getKey());

        assertNull(fieldValue);

    }

    @Test
    public void testGetEntityKeyForEntityId() throws CoalescePersistorException
    {
        String objectKey = _coalesceFramework.getCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                              _entity.getEntityIdType(),
                                                                              _entity.getName());

        assertEquals(_entity.getKey().toUpperCase(), objectKey.toUpperCase());

    }

    @Test
    public void testFAILGetEntityKeyForEntityId() throws CoalescePersistorException
    {

        String objectKey = _coalesceFramework.getCoalesceEntityKeyForEntityId("", "", "");

        assertNull(objectKey);

    }

    @Test
    public void testGetEntityKeyForEntityIdName() throws CoalescePersistorException
    {
        List<String> objectKeys = _coalesceFramework.getCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                      _entity.getEntityIdType(),
                                                                                      _entity.getName(),
                                                                                      _entity.getSource());

        assertEquals(1, objectKeys.size());
        assertEquals(_entity.getKey().toUpperCase(), objectKeys.get(0).toUpperCase());
    }

    @Test
    public void testGetEntityKeyForEntityIdNameLists() throws CoalescePersistorException
    {
        List<String> objectKeys = _coalesceFramework.getCoalesceEntityKeysForEntityId(_entity.getEntityId()
                                                                                              + ",AnotherEntityId",
                                                                                      _entity.getEntityIdType()
                                                                                              + ",AnotherEntityIdType",
                                                                                      _entity.getName(),
                                                                                      _entity.getSource());

        assertEquals(1, objectKeys.size());
        assertEquals(_entity.getKey().toUpperCase(), objectKeys.get(0).toUpperCase());
    }

    @Test
    public void testFAILGetEntityKeyForEntityIdName() throws CoalescePersistorException
    {
        List<String> objectKey = _coalesceFramework.getCoalesceEntityKeysForEntityId("", "", "", "");
        assertTrue(objectKey.isEmpty());
    }

    @Test
    public void testGetEntityKeyForEntityIdNameMismatchLists() throws CoalescePersistorException
    {
        List<String> objectKey = _coalesceFramework.getCoalesceEntityKeysForEntityId(_entity.getEntityId() + ",EntityID",
                                                                                     _entity.getEntityIdType(),
                                                                                     _entity.getName(),
                                                                                     _entity.getSource());
        assertTrue(objectKey.isEmpty());

        objectKey = _coalesceFramework.getCoalesceEntityKeysForEntityId(_entity.getEntityId(), _entity.getEntityIdType()
                + ",EntityIDType", _entity.getName(), _entity.getSource());
        assertTrue(objectKey.isEmpty());
    }

    @Test
    public void testGetEntityKeysForEntityIdSource() throws CoalescePersistorException
    {
        List<String> objectKeys = _coalesceFramework.getCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                      _entity.getEntityIdType(),
                                                                                      _entity.getName());
        assertFalse(objectKeys.isEmpty());

        boolean found = false;
        for (String objectKey : objectKeys)
        {
            if (objectKey.equalsIgnoreCase(_entity.getKey()))
            {
                found = true;
                break;
            }
        }

        assertTrue("Object key not found in key list", found);
    }

    @Test
    public void testFAILGetEntityKeyForEntityIdSource() throws CoalescePersistorException
    {
        List<String> objectKeys = _coalesceFramework.getCoalesceEntityKeysForEntityId("", "", "");
        assertTrue(objectKeys.isEmpty());
    }

    @Test
    public void testGetEntityKeyForEntityIdSource() throws CoalescePersistorException
    {
        String objectKey = _coalesceFramework.getCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                              _entity.getEntityIdType(),
                                                                              _entity.getName(),
                                                                              _entity.getSource());
        assertEquals(_entity.getKey().toUpperCase(), objectKey.toUpperCase());

    }

    @Test
    public void getEntityTemplateMetadataTest() throws CoalescePersistorException, SAXException, IOException
    {
        // Ensure Database has a Template
        String xmlMetaData = _coalesceFramework.getCoalesceEntityTemplateMetadata();

        System.out.println(xmlMetaData);
        
        assertNotNull(xmlMetaData);
    }

    @Test
    public void testGetEntityXmlEntityIdEntityIdtype() throws CoalescePersistorException
    {
        String entityXml = _coalesceFramework.getEntityXml(_entity.getEntityId(), _entity.getEntityIdType());

        CoalesceAssert.assertXmlEquals(_entity.toXml(), entityXml);

    }

    @Test
    public void testGetEntityXmlNameEntityIdEntityIdtype() throws CoalescePersistorException
    {
        String entityXml = _coalesceFramework.getEntityXml(_entity.getName(),
                                                           _entity.getEntityId(),
                                                           _entity.getEntityIdType());

        CoalesceAssert.assertXmlEquals(_entity.toXml(), entityXml);

    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private static CoalesceEntity createEntity() throws CoalesceException
    {
        CoalesceEntity entity = CoalesceEntity.create("TestEntity", "Unit Test", "1.0.0.0", "EntityId", "EntityIdType", "");

        CoalesceLinkageSection.create(entity, true);

        CoalesceSection section = CoalesceSection.create(entity, "Live Status Section", true);
        CoalesceRecordset recordSet = CoalesceRecordset.create(section, "Live Status Recordset");
        CoalesceFieldDefinition.create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = recordSet.addNew();
        CoalesceStringField status = (CoalesceStringField) record.getFieldByName("CurrentStatus");
        status.setValue("Test Status");

        return entity;

    }

    private static CoalesceField<?> getCurrentStatusField(CoalesceEntity entity)
    {
        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TestEntity/Live Status Section/Live Status Recordset/Live Status Recordset Record/CurrentStatus");

        return field;

    }

    private static void deleteEntity(CoalesceDataConnectorBase conn, String objectKey) throws CoalescePersistorException
    {
        String entityXml = _coalesceFramework.getEntityXml(objectKey);

        CoalesceEntity entity = CoalesceEntity.create(entityXml);

        CoalescePersistorBaseTest.deleteEntity(conn, entity);

    }

    private static void deleteEntity(CoalesceDataConnectorBase conn, CoalesceDataObject xdo)
    {
        String table = "";

        switch (xdo.getType()) {
        case "entity":

            table = "CoalesceEntity";
            break;

        case "section":

            table = "CoalesceSection";
            break;

        case "recordset":

            table = "CoalesceRecordSet";
            break;

        case "fielddefinition":

            table = "CoalesceFieldDefinition";
            break;

        case "record":

            table = "CoalesceRecord";
            break;

        case "field":

            table = "CoalesceField";
            break;

        case "fieldhistory":

            table = "CoalesceFieldHistory";
            break;

        case "linkagesection":

            table = "CoalesceLinkageSection";
            break;

        case "linkage":

            table = "CoalesceLinkage";

        }

        if (!StringHelper.isNullOrEmpty(table))
        {
            CoalescePersistorBaseTest.pergeTestRecords(conn, table, xdo.getKey());
        }

        for (CoalesceDataObject child : xdo.getChildDataObjects().values())
        {
            CoalescePersistorBaseTest.deleteEntity(conn, child);
        }
    }

    private static void pergeTestRecords(CoalesceDataConnectorBase conn, String tableName, String key)
    {
        try
        {
            conn.executeCmd("delete from " + tableName + " where ObjectKey = '" + key + "'");
        }
        catch (SQLException e)
        {
        }
    }

    private static CoalesceEntityTemplate testTemplate(CoalesceEntityTemplate template)
    {
        String templateXml = template.toXml();
        // System.out.println("Template: ");
        // System.out.println("*********\n" + templateXml);

        // Confirm Template
        assertFalse(StringHelper.isNullOrEmpty(templateXml));
        assertEquals("TestEntity", template.getName());
        assertEquals("Unit Test", template.getSource());
        assertEquals("1.0.0.0", template.getVersion());

        // Confirm Values
        NodeList nodeList = template.getDataObjectDocument().getElementsByTagName("*");

        for (int jj = 0; jj < nodeList.getLength(); jj++)
        {
            Node node = nodeList.item(jj);

            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                NamedNodeMap attributeList = node.getAttributes();

                for (int ii = 0; ii < attributeList.getLength(); ii++)
                {

                    Node attribute = attributeList.item(ii);

                    if (attribute.getNodeName().equalsIgnoreCase("name")
                            || attribute.getNodeName().equalsIgnoreCase("source")
                            || attribute.getNodeName().equalsIgnoreCase("version"))
                    {
                        assertNotNull(attribute.getNodeValue());
                    }
                    else
                    {
                        assertTrue(StringHelper.isNullOrEmpty(attribute.getNodeValue()));
                    }
                }
            }
        }

        // Create Entity from Template
        CoalesceEntity entity2 = template.createNewEntity();

        String entityXml = entity2.toXml();
        // System.out.println("Copy of Entity made from Template: " + entity2.getKey());
        // System.out.println("**********************************\n" + entityXml);

        // Confirm Entity
        assertFalse("Entity not generated", StringHelper.isNullOrEmpty(entityXml));
        assertTrue("Not a valid GUID", GUIDHelper.isValid(entity2.getKey()));

        assertEquals("TestEntity", entity2.getName());
        assertEquals("Unit Test", entity2.getSource());
        assertEquals("1.0.0.0", entity2.getVersion());

        return template;

    }
}
