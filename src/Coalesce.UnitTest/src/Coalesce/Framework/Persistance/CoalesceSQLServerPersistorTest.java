package Coalesce.Framework.Persistance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import unity.connector.local.LocalConfigurationsConnector;
import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdField;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;
import Coalesce.Framework.Persistance.ICoalescePersistor.EntityMetaData;

import com.database.persister.CoalesceDataConnector;
import com.database.persister.ConnectionType;
import com.database.persister.SQLServerPersistor;
import com.database.persister.ServerConn;

public class CoalesceSQLServerPersistorTest {

    static ServerConn serCon;
    static SQLServerPersistor mySQLServerPersistor;
    private static CoalesceFramework _coalesceFramework;

    private static XsdEntity _entity;
    private static String _fieldKey;

    @BeforeClass
    public static void Initialize() throws SAXException, IOException, CoalesceException
    {

        CoalesceSettings.Initialize(new LocalConfigurationsConnector());

        serCon = new ServerConn();
        serCon.setUser("root");
        serCon.setPassword("Passw0rd");
        serCon.setServerName("localhost");
        serCon.setPortNumber(1433);
        serCon.setDatabase("coalescedatabase");

        mySQLServerPersistor = new SQLServerPersistor();
        mySQLServerPersistor.Initialize(serCon);

        mySQLServerPersistor.Initialize(serCon);
        CoalesceSQLServerPersistorTest._coalesceFramework = new CoalesceFramework();
        CoalesceSQLServerPersistorTest._coalesceFramework.Initialize(mySQLServerPersistor);

        CoalesceSQLServerPersistorTest.createEntity();
        CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
        // System.out.println(_entity.toXml());

    }

    private static boolean createEntity() throws CoalesceException
    {
        // Create Test Entity
        _entity = new XsdEntity();

        XsdSection section = null;
        XsdRecordset recordSet = null;
        XsdRecord record = null;

        // Create Entity
        _entity = XsdEntity.create("TestEntity", "Unit Test", "1.0.0.0", "EntityId", "EntityIdType", "");

        XsdLinkageSection.create(_entity, true);

        section = XsdSection.create(_entity, "Live Status Section", true);
        recordSet = XsdRecordset.create(section, "Live Status Recordset");
        XsdFieldDefinition.create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

        record = recordSet.addNew();
        record.setFieldValue("CurrentStatus", "Test Status");

        _fieldKey = record.getFieldByName("CurrentStatus").getKey();
        return true;
    }

    private static CoalesceEntityTemplate testTemplate(CoalesceEntityTemplate template)
    {
        String templateXml = template.toXml();
        System.out.println("Template: ");
        System.out.println("*********\n" + templateXml);

        // Confirm Template
        assertNotNull(templateXml);
        assertTrue(template.getName().equalsIgnoreCase("TestEntity"));
        assertTrue(template.getSource().equalsIgnoreCase("Unit Test"));
        assertTrue(template.getVersion().equalsIgnoreCase("1.0.0.0"));

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

                    if (!attribute.getNodeName().equalsIgnoreCase("name")
                            && !attribute.getNodeName().equalsIgnoreCase("source")
                            && !attribute.getNodeName().equalsIgnoreCase("version"))
                    {
                        assertTrue(StringHelper.IsNullOrEmpty(attribute.getNodeValue()));
                    }
                    else
                    {
                        assertNotNull(attribute.getNodeValue());
                    }
                }
            }
        }

        // Create Entity from Template
        XsdEntity entity2 = template.createNewEntity();

        String entityXml = entity2.toXml();
        System.out.println("Copy of Entity made from Template: " + entity2.getKey());
        System.out.println("**********************************\n" + entityXml);

        // Confirm Entity
        assertNotNull(entityXml);
        assertNotNull(entity2.getKey());
        assertTrue(entity2.getName().equalsIgnoreCase("TestEntity"));
        assertTrue(entity2.getSource().equalsIgnoreCase("Unit Test"));
        assertTrue(entity2.getVersion().equalsIgnoreCase("1.0.0.0"));
        return template;
    }

    @Test
    public void testConnection()
    {

        try (CoalesceDataConnector conn = new CoalesceDataConnector(serCon,ConnectionType.SQLServer))
        {

            conn.OpenSSConnection();

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test(expected = SQLException.class)
    public void testFAILConnection() throws SQLException,Exception, CoalescePersistorException
    {
        // Is this even needed?
        ServerConn serConFail = new ServerConn();
        serConFail.setUser("roooott");
        serConFail.setPassword("Passw0rd");
        serConFail.setServerName("localhost");
        serConFail.setPortNumber(1433);
        serConFail.setDatabase("coalescedatabase");
        try (CoalesceDataConnector conn = new CoalesceDataConnector(serConFail,ConnectionType.SQLServer))
        {

            conn.OpenSSConnection();

        }
    }

    @Test
    public void testSaveEntityAndXPath()
    {
        try
        {
            // assertTrue(CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity));

            // Get Field from DB Using XPath
            XsdField field = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceFieldByFieldKey(_fieldKey);
            assertTrue(field != null);

            // Get Record from Entity
            XsdRecord record = (XsdRecord) field.getParent();
            assertTrue(record != null);

            // Get Record from DB Using XPath
            XsdRecord recordDB = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceRecord(record.getKey());
            assertTrue(recordDB != null);
            assertTrue(recordDB.getName().equalsIgnoreCase(record.getName()));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSaveEntityTemplate()
    {
        try
        {
            CoalesceEntityTemplate template = testTemplate(CoalesceEntityTemplate.create(_entity));
            assertTrue(CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntityTemplate(template));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityMetaData()
    {
        try
        {
            CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
            EntityMetaData objectKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityIdAndTypeForKey(_entity.getKey());
            assertTrue(objectKey.entityId != null && objectKey.entityKey != null && objectKey.entityType != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    // @Test
    // public void testGetEntity()
    // {
    // try
    // {
    // CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
    // EntityMetaData objectKey =
    // CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityIdAndTypeForKey(_entity.getKey());
    // assertTrue(objectKey.entityId != null && objectKey.entityKey != null && objectKey.entityType != null);
    // }
    // catch (Exception ex)
    // {
    // fail(ex.getMessage());
    // }
    // }

    @Test
    public void testGetEntity()
    {
        try
        {
            XsdEntity ent = new XsdEntity();
            CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity); // Why the entity needs to be
                                                                                           // saved again I have no idea, is
                                                                                           // the key changing?
            ent = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntity(_entity.getKey());

            assertTrue(ent != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testCheckLastModified()
    {
        try
        {
            DateTime lastModified;
            // Need to save the entity or the persistor will use a default date.
            CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
            // Test Entity
            lastModified = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(_entity.getKey(),
                                                                                                           "entity");
            assertTrue(DateTimeComparator.getInstance().compare(lastModified, _entity.getLastModified()) == 0);

            // Test Section
            XsdSection section = _entity.getSection("TestEntity/Live Status Section");

            assertTrue(section != null);

            lastModified = null;
            lastModified = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(section.getKey(),
                                                                                                           "section");
            assertTrue(DateTimeComparator.getInstance().compare(lastModified, section.getLastModified()) == 0);

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    //
    // @Test
    // public void testFAILCheckLastModified()
    // {
    // try
    // {
    // DateTime lastModified;
    //
    // // Test Entity
    // lastModified = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(_entity.getKey(),
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
    public void testGetEntityByIdAndType()
    {
        try
        {
            XsdEntity ent = new XsdEntity();
            ent = CoalesceSQLServerPersistorTest._coalesceFramework.GetEntity(_entity.getEntityId(),
                                                                              _entity.getEntityIdType());

            assertTrue(ent != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityByNameAndIdAndType()
    {
        try
        {
            XsdEntity ent = new XsdEntity();

            ent = CoalesceSQLServerPersistorTest._coalesceFramework.GetEntity(_entity.getName(),
                                                                              _entity.getEntityId(),
                                                                              _entity.getEntityIdType());

            assertTrue(ent != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetFieldValue()
    {

        try
        {
            assertTrue(CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity));
            String fieldValue = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);

            assertTrue(fieldValue.equals("Test Status"));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

    }

    @Test
    public void testFAILGetFieldValue()
    {

        try
        {
            // Create a new entity, but do not save the entity
            assertTrue(CoalesceSQLServerPersistorTest.createEntity());
            String fieldValue = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);
            assertNull(fieldValue);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

    }

    @Test
    public void testGetEntityKeyForEntityId()
    {
        try
        {
            String objectKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                                                                 _entity.getEntityIdType(),
                                                                                                                 _entity.getName());
            assertTrue(objectKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

   
    public void testFAILGetEntityKeyForEntityId() throws CoalescePersistorException
    {

        String objectKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId("", "", "");
        assertNull(objectKey);

    }

    @Test
    public void testGetEntityKeyForEntityIdName()
    {
        try
        {
            List<String> objectKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                                                        _entity.getEntityIdType(),
                                                                                                                        _entity.getName(),
                                                                                                                        _entity.getSource());
            assertTrue(objectKey.size() >= 1);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testFAILGetEntityKeyForEntityIdName()
    {
        try
        {
            List<String> objectKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId("",
                                                                                                                        "",
                                                                                                                        "",
                                                                                                                        "");
            assertTrue(objectKey.size() == 0);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityKeyForEntityIdSource()
    {
        try
        {
            List<String> objectKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                                                        _entity.getEntityIdType(),
                                                                                                                        _entity.getName());
            assertTrue(objectKey.size() >= 0 || objectKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testFAILGetEntityKeyForEntityIdSource()
    {
        try
        {
            List<String> objectKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId("",
                                                                                                                        "",
                                                                                                                        "");
            assertTrue(objectKey.size() == 0);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityKeysForEntityIdSource()
    {
        try
        {
            String objectKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                                                                 _entity.getEntityIdType(),
                                                                                                                 _entity.getName(),
                                                                                                                 _entity.getSource());
            assertTrue(objectKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityTemplateXML()
    {
        try
        {
            // Get Template Key
            String templateKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                                                                _entity.getSource(),
                                                                                                                _entity.getVersion());

            // Load Template by Key
            String templateXML = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(templateKey);

            assertFalse(StringHelper.IsNullOrEmpty(templateXML));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityTemplateXMLName()
    {
        try
        {
            String templateXML = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(_entity.getName(),
                                                                                                                _entity.getSource(),
                                                                                                                _entity.getVersion());
            assertFalse(StringHelper.IsNullOrEmpty(templateXML));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityTemplateKey()
    {
        try
        {
            String templateKey = CoalesceSQLServerPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                                                                _entity.getSource(),
                                                                                                                _entity.getVersion());
            assertFalse(StringHelper.IsNullOrEmpty(templateKey));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @After
    public void Finalize()
    {

    }
}
