package Coalesce.Framework.Persistance;

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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import unity.connector.local.LocalConfigurationsConnector;
import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Common.UnitTest.CoalesceAssert;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdDataObject;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdField;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;
import Coalesce.Framework.Persistance.ICoalescePersistor.EntityMetaData;
import coalesce.persister.sqlserver.SQLServerDataConnector;
import coalesce.persister.sqlserver.SQLServerPersistor;

public class CoalesceSQLServerPersistorTest {

    /*--------------------------------------------------------------------------
    Private Variables
    --------------------------------------------------------------------------*/

    private static ServerConn serCon;
    // private static SQLServerPersistor mySQLServerPersistor;
    private static CoalesceFramework _coalesceFramework;

    private static XsdEntity _entity;
    private static String _entityXml;
    private static String _fieldKey;

    private static String _testTemplateKey = null;

    /*--------------------------------------------------------------------------
    JUnit Before/After functions
    --------------------------------------------------------------------------*/

    @BeforeClass
    public static void setUpBeforeClass() throws CoalesceException
    {
        CoalesceSettings.initialize(new LocalConfigurationsConnector());

        serCon = new ServerConn();
        serCon.setServerName("127.0.0.1");
        serCon.setPortNumber(1433);
            serCon.setDatabase("coalescedatabase");

        SQLServerPersistor mySQLServerPersistor = new SQLServerPersistor();
        mySQLServerPersistor.Initialize(serCon);

        _coalesceFramework = new CoalesceFramework();
        _coalesceFramework.Initialize(mySQLServerPersistor);

        _entity = CoalesceSQLServerPersistorTest.createEntity();
        _entityXml = _entity.toXml();

        _fieldKey = CoalesceSQLServerPersistorTest.getCurrentStatusField(_entity).getKey();

        assertTrue(_coalesceFramework.SaveCoalesceEntity(_entity));

    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(serCon))
        {

            deleteEntity(conn, _entity);

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
            try (SQLServerDataConnector conn = new SQLServerDataConnector(serCon))
            {

                conn.ExecuteCmd("delete from CoalesceEntityTemplate where TemplateKey = '" + _testTemplateKey + "'");

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
        try (SQLServerDataConnector conn = new SQLServerDataConnector(serCon))
        {
            conn.openConnection();
        }
    }

    // @Test(expected = SQLException.class)
    public void testFAILConnection() throws SQLException, Exception, CoalescePersistorException
    {
        // Is this even needed?
        ServerConn serConFail = new ServerConn();
        serConFail.setServerName("192.168.1.1");
        serConFail.setPortNumber(1433);
        serConFail.setDatabase("coalescedatabase");

        try (SQLServerDataConnector conn = new SQLServerDataConnector(serConFail))
        {
            conn.openConnection();
        }
    }

    @Test
    public void testSaveEntityAndXPath() throws CoalescePersistorException
    {
        // assertTrue(_coalesceFramework.SaveCoalesceEntity(_entity));

        // Get Field from DB Using XPath
        XsdField field = _coalesceFramework.GetCoalesceFieldByFieldKey(_fieldKey);
        assertNotNull(field);

        // Get Record from Entity
        XsdRecord record = (XsdRecord) field.getParent();
        assertNotNull(record);

        // Get Record from DB Using XPath
        XsdRecord recordDB = _coalesceFramework.GetCoalesceRecord(record.getKey());
        assertNotNull(recordDB);
        assertEquals(record.getName(), recordDB.getName());

    }

    @Test
    public void testEntityTemplate() throws SAXException, IOException, CoalescePersistorException
    {
        CoalesceEntityTemplate template = testTemplate(CoalesceEntityTemplate.create(_entity));
        assertTrue(_coalesceFramework.SaveCoalesceEntityTemplate(template));

        String templateXml = template.toXml().replace("UTF-8", "UTF-16");

        String templateKey = _coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                             _entity.getSource(),
                                                                             _entity.getVersion());
        assertFalse(StringHelper.isNullOrEmpty(templateKey));

        _testTemplateKey = templateKey;

        String templateXmlFromKey = _coalesceFramework.GetCoalesceEntityTemplateXml(templateKey);

        CoalesceAssert.assertXmlEquals(templateXml, templateXmlFromKey, "UTF-16");

        String templateXmlFromAttr = _coalesceFramework.GetCoalesceEntityTemplateXml(_entity.getName(),
                                                                                     _entity.getSource(),
                                                                                     _entity.getVersion());
        CoalesceAssert.assertXmlEquals(templateXml, templateXmlFromAttr, "UTF-16");

    }

    @Test
    public void testGetEntityMetaData() throws CoalescePersistorException
    {
        EntityMetaData objectKey = _coalesceFramework.GetCoalesceEntityIdAndTypeForKey(_entity.getKey());

        assertEquals(_entity.getEntityId(), objectKey.entityId);
        assertEquals(_entity.getKey().toUpperCase(), objectKey.entityKey.toUpperCase());
        assertEquals(_entity.getEntityIdType(), objectKey.entityType);
    }

    @Test
    public void testGetEntity() throws CoalescePersistorException
    {
        XsdEntity ent = _coalesceFramework.GetCoalesceEntity(_entity.getKey());

        CoalesceAssert.assertXmlEquals(_entityXml, ent.toXml(), "UTF-8");
    }

    @Test
    public void testCheckLastModified() throws CoalescePersistorException
    {
        // Test Entity
        DateTime lastModified = _coalesceFramework.GetCoalesceEntityLastModified(_entity.getKey(), "entity");
        assertEquals(lastModified, _entity.getLastModified());

        // Test Section
        XsdSection section = _entity.getSection("TestEntity/Live Status Section");

        lastModified = null;
        lastModified = _coalesceFramework.GetCoalesceEntityLastModified(section.getKey(), "section");
        assertEquals(lastModified, section.getLastModified());

    }

    //
    // @Test
    // public void testFAILCheckLastModified()
    // {
    // try
    // {
    // DateTime last_t Entity
    // lastModified = _coalesceFramework.GetCoalesceEntityLastModified(_entity.getKey(),
    // "linkage");
    // assertTrue(DateTimeComparator.getInstance().compare(lastModified, _entity.getLastModified()) == 1);
    // }
    // catch (Exception ex)
    // {
    // fail(ex.getMessage());
    // }
    // }
    //

    @Test
    public void testGetEntityByIdAndType() throws CoalescePersistorException
    {
        XsdEntity ent = _coalesceFramework.GetEntity(_entity.getEntityId(), _entity.getEntityIdType());

        CoalesceAssert.assertXmlEquals(_entityXml, ent.toXml(), "UTF-8");
    }

    @Test
    public void testGetEntityByNameAndIdAndType() throws CoalescePersistorException
    {
        XsdEntity ent = _coalesceFramework.GetEntity(_entity.getName(), _entity.getEntityId(), _entity.getEntityIdType());

        CoalesceAssert.assertXmlEquals(_entityXml, ent.toXml(), "UTF-8");
    }

    @Test
    public void testGetFieldValue() throws CoalescePersistorException
    {
        String fieldValue = _coalesceFramework.GetCoalesceFieldValue(_fieldKey);

        assertEquals("Test Status", fieldValue);
    }

    @Test
    public void testFAILGetFieldValue() throws CoalesceException
    {
        // Create a new entity, but do not save the entity
        XsdEntity entity = CoalesceSQLServerPersistorTest.createEntity();

        String fieldValue = _coalesceFramework.GetCoalesceFieldValue(CoalesceSQLServerPersistorTest.getCurrentStatusField(entity).getKey());

        assertNull(fieldValue);

    }

    @Test
    public void testGetEntityKeyForEntityId() throws CoalescePersistorException
    {
        String objectKey = _coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                              _entity.getEntityIdType(),
                                                                              _entity.getName());

        assertEquals(_entity.getKey().toUpperCase(), objectKey.toUpperCase());

    }

    public void testFAILGetEntityKeyForEntityId() throws CoalescePersistorException
    {

        String objectKey = _coalesceFramework.GetCoalesceEntityKeyForEntityId("", "", "");

        assertNull(objectKey);

    }

    @Test
    public void testGetEntityKeyForEntityIdName() throws CoalescePersistorException
    {
        List<String> objectKey = _coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                     _entity.getEntityIdType(),
                                                                                     _entity.getName(),
                                                                                     _entity.getSource());
        assertFalse(objectKey.isEmpty());
    }

    @Test
    public void testFAILGetEntityKeyForEntityIdName() throws CoalescePersistorException
    {
        List<String> objectKey = _coalesceFramework.GetCoalesceEntityKeysForEntityId("", "", "", "");
        assertTrue(objectKey.isEmpty());
    }

    @Test
    public void testGetEntityKeysForEntityIdSource() throws CoalescePersistorException
    {
        List<String> objectKey = _coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                     _entity.getEntityIdType(),
                                                                                     _entity.getName());
        assertFalse(objectKey.isEmpty());

        assertTrue(objectKey.contains(_entity.getKey().toUpperCase()));
    }

    @Test
    public void testFAILGetEntityKeyForEntityIdSource() throws CoalescePersistorException
    {
        List<String> objectKey = _coalesceFramework.GetCoalesceEntityKeysForEntityId("", "", "");
        assertTrue(objectKey.isEmpty());
    }

    @Test
    public void testGetEntityKeyForEntityIdSource() throws CoalescePersistorException
    {
        String objectKey = _coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                              _entity.getEntityIdType(),
                                                                              _entity.getName(),
                                                                              _entity.getSource());
        assertEquals(_entity.getKey().toUpperCase(), objectKey);

    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private static XsdEntity createEntity() throws CoalesceException
    {
        XsdEntity entity = XsdEntity.create("TestEntity", "Unit Test", "1.0.0.0", "EntityId", "EntityIdType", "");

        XsdLinkageSection.create(entity, true);

        XsdSection section = XsdSection.create(entity, "Live Status Section", true);
        XsdRecordset recordSet = XsdRecordset.create(section, "Live Status Recordset");
        XsdFieldDefinition.create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

        XsdRecord record = recordSet.addNew();
        record.setFieldValue("CurrentStatus", "Test Status");

        return entity;

    }

    private static XsdField getCurrentStatusField(XsdEntity entity)
    {
        XsdField field = (XsdField) entity.getDataObjectForNamePath("TestEntity/Live Status Section/Live Status Recordset/Live Status Recordset Record/CurrentStatus");

        return field;

    }

    private static void deleteEntity(SQLServerDataConnector conn, XsdDataObject xdo)
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
            CoalesceSQLServerPersistorTest.pergeTestRecords(conn, table, xdo.getKey());
        }

        for (XsdDataObject child : xdo.getChildDataObjects().values())
        {
            CoalesceSQLServerPersistorTest.deleteEntity(conn, child);
        }
    }

    private static void pergeTestRecords(SQLServerDataConnector conn, String tableName, String key)
    {
        try
        {
            conn.ExecuteCmd("delete from " + tableName + " where ObjectKey = '" + key + "'");
        }
        catch (SQLException e)
        {
        }
    }

    private static CoalesceEntityTemplate testTemplate(CoalesceEntityTemplate template)
    {
        String templateXml = template.toXml();
        //System.out.println("Template: ");
        //System.out.println("*********\n" + templateXml);

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
        XsdEntity entity2 = template.createNewEntity();

        String entityXml = entity2.toXml();
        //System.out.println("Copy of Entity made from Template: " + entity2.getKey());
        //System.out.println("**********************************\n" + entityXml);

        // Confirm Entity
        assertFalse("Entity not generated", StringHelper.isNullOrEmpty(entityXml));
        assertTrue("Not a valid GUID", GUIDHelper.isValid(entity2.getKey()));

        assertEquals("TestEntity", entity2.getName());
        assertEquals("Unit Test", entity2.getSource());
        assertEquals("1.0.0.0", entity2.getVersion());

        return template;

    }

}
