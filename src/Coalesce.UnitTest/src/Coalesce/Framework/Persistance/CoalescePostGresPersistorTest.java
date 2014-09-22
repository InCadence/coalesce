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
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import unity.common.CallResult;
import unity.common.CallResult.CallResults;
import unity.connector.local.LocalConfigurationsConnector;
import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalesceInvalidFieldException;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;
import Coalesce.Framework.Persistance.ICoalescePersistor.EntityMetaData;

import com.database.persister.CoalesceDataConnector;
import com.database.persister.ConnectionType;
import com.database.persister.PostGresSQLPersistor;
import com.database.persister.ServerConn;

public class CoalescePostGresPersistorTest {

    private static String MODULE_NAME = "CoalescePostGresPersistorTest";

    static ServerConn serCon;
    static PostGresSQLPersistor postGresSQLPersister;
    private static CoalesceFramework _coalesceFramework;

    private static XsdEntity _entity;
    private static String _fieldKey;

    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException
    {

        CoalesceSettings.initialize(new LocalConfigurationsConnector());

        serCon = new ServerConn();
        serCon.setURL("jdbc:postgresql://localhost/CoalesceDatabase");
        serCon.setUser("root");
        serCon.setPassword("Passw0rd");

        postGresSQLPersister = new PostGresSQLPersistor();
        postGresSQLPersister.Initialize(serCon);

        postGresSQLPersister.Initialize(serCon);
        CoalescePostGresPersistorTest._coalesceFramework = new CoalesceFramework();
        CoalescePostGresPersistorTest._coalesceFramework.Initialize(postGresSQLPersister);

        CoalescePostGresPersistorTest.createEntity();

    }

    private static boolean createEntity() throws CoalesceException
    {
        try
        {
            // Create Test Entity
            _entity = new XsdEntity();

            XsdSection section = null;
            XsdRecordset recordSet = null;
            XsdRecord record = null;

            // Create Entity
            _entity = XsdEntity.create("TestEntity", "Unit Test", "1.0.0.0", "", "", "");

            XsdLinkageSection.create(_entity, true);

            section = XsdSection.create(_entity, "Live Status Section", true);
            recordSet = XsdRecordset.create(section, "Live Status Recordset");
            XsdFieldDefinition.create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

            record = recordSet.addNew();
            record.setFieldValue("CurrentStatus", "Test Status");

            _fieldKey = record.getFieldByName("CurrentStatus").getKey();
            return true;
        }
        catch (CoalesceInvalidFieldException e)
        {
            CallResult.log(CallResults.FAILED_ERROR, e, MODULE_NAME);
            return false;
        }
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
    public void testConnection() throws SQLException, CoalescePersistorException
    {

        try (CoalesceDataConnector conn = new CoalesceDataConnector(serCon,ConnectionType.PostGresSQL))
        {
            conn.OpenPSConnection();
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test(expected = SQLException.class)
    public void testFAILConnection() throws SQLException,Exception, CoalescePersistorException
    {
        // Is this even needed?
        ServerConn serConFail = new ServerConn();
        serConFail.setURL("jdbc:postgres//localhost:3306/coalescedatabase");
        serConFail.setPassword("Passw0rd");
        serConFail.setUser("rotorooter");
        serConFail.setPostGres(true);
        try (CoalesceDataConnector conn = new CoalesceDataConnector(serConFail,ConnectionType.PostGresSQL))
        {

            conn.OpenPSConnection();

        }
    }

    @Test
    public void saveEntity() throws CoalesceException
    {
        CoalescePostGresPersistorTest.createEntity();
        CoalescePostGresPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
    }

    @Test
    public void testGetEntity() throws CoalescePersistorException
    {
        XsdEntity ent = new XsdEntity();
        ent = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntity(_entity.getKey());

        assertTrue(ent != null);

    }

    @Test
    public void testSaveEntityTemplate() throws CoalescePersistorException, SAXException, IOException
    {
        CoalesceEntityTemplate template = testTemplate(CoalesceEntityTemplate.create(_entity));
        assertTrue(CoalescePostGresPersistorTest._coalesceFramework.SaveCoalesceEntityTemplate(template));

    }

    @Test
    public void testGetEntityMetaData() throws CoalescePersistorException
    {
        EntityMetaData objectKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityIdAndTypeForKey(_entity.getKey());
        assertTrue(objectKey.entityId != null && objectKey.entityKey != null && objectKey.entityType != null);
    }

    @Test
    public void testCheckLastModified() throws CoalescePersistorException
    {
        DateTime lastModified;

        // Test Entity
        lastModified = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(_entity.getKey(),
                                                                                                      "entity");
        int compare = DateTimeComparator.getInstance().compare(lastModified, _entity.getLastModified());
        assertTrue(compare == 0);

        // Test Section
        XsdSection section = _entity.getSection("TestEntity/Live Status Section");

        assertTrue(section != null);

        lastModified = null;
        lastModified = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(section.getKey(),
                                                                                                      "section");
        assertTrue(DateTimeComparator.getInstance().compare(lastModified, section.getLastModified()) == 0);

    }
    @Test
    public void testFAILCheckLastModified() throws CoalescePersistorException
    {
        DateTime lastModified;

        // Test Entity
        lastModified = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(_entity.getKey(),
                                                                                                   "linkage");
        assertTrue(DateTimeComparator.getInstance().compare(lastModified, _entity.getLastModified()) == 1);

    }
    @Test
    public void testGetEntityByIdAndType() throws CoalescePersistorException
    {
        XsdEntity ent = new XsdEntity();
        ent = CoalescePostGresPersistorTest._coalesceFramework.GetEntity(_entity.getEntityId(), _entity.getEntityIdType());
        assertTrue(ent != null);
    }
    @Test
    public void testGetEntityByNameAndIdAndType() throws CoalescePersistorException
    {
        XsdEntity ent = new XsdEntity();

        ent = CoalescePostGresPersistorTest._coalesceFramework.GetEntity(_entity.getName(),
                                                                      _entity.getEntityId(),
                                                                      _entity.getEntityIdType());

        assertTrue(ent != null);

    }
    @Test
    public void testGetFieldValue() throws CoalescePersistorException
    {
        assertTrue(CoalescePostGresPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity));
        String fieldValue = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);

        assertTrue(fieldValue.equals("Test Status"));

    }
    @Test
    public void testFAILGetFieldValue() throws CoalesceException
    {
        // Create a new entity, but do not save the entity
        assertTrue(CoalescePostGresPersistorTest.createEntity());
        String fieldValue = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);
        assertNull(fieldValue);

    }
    @Test
    public void testGetEntityKeyForEntityId() throws CoalescePersistorException
    {
        String objectKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                                                         _entity.getEntityIdType(),
                                                                                                         _entity.getName());
        assertTrue(objectKey != null);

    }
    @Test
    public void testFAILGetEntityKeyForEntityId() throws CoalescePersistorException
    {
        String objectKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId("", "", "");
        assertTrue(objectKey == null);

    }
    @Test
    public void testGetEntityKeyForEntityIdName() throws CoalescePersistorException
    {
        List<String> objectKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                                                _entity.getEntityIdType(),
                                                                                                                _entity.getName(),
                                                                                                                _entity.getSource());
        assertTrue(objectKey.size() >= 1);

    }
    @Test
    public void testFAILGetEntityKeyForEntityIdName() throws CoalescePersistorException
    {
        List<String> objectKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId("",
                                                                                                                "",
                                                                                                                "",
                                                                                                                "");
        assertTrue(objectKey.size() == 0);

    }
    @Test
    public void testGetEntityKeyForEntityIdSource() throws CoalescePersistorException
    {
        List<String> objectKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                                                _entity.getEntityIdType(),
                                                                                                                _entity.getName());
        assertTrue(objectKey.size() >= 0 || objectKey != null);

    }
    @Test
    public void testFAILGetEntityKeyForEntityIdSource() throws CoalescePersistorException
    {
        List<String> objectKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId("", "", "");
        assertTrue(objectKey.size() == 0);

    }
    @Test
    public void testGetEntityKeysForEntityIdSource() throws CoalescePersistorException
    {
        String objectKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                                                         _entity.getEntityIdType(),
                                                                                                         _entity.getName(),
                                                                                                         _entity.getSource());
        assertTrue(objectKey != null);

    }
    @Test
    public void testGetEntityTemplateXML() throws CoalescePersistorException
    {
        // Get Template Key
        String templateKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                                                        _entity.getSource(),
                                                                                                        _entity.getVersion());

        // Load Template by Key
        String templateXML = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(templateKey);

        assertFalse(StringHelper.IsNullOrEmpty(templateXML));

    }
    @Test
    public void testGetEntityTemplateXMLName() throws CoalescePersistorException
    {
        String templateXML = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(_entity.getName(),
                                                                                                        _entity.getSource(),
                                                                                                        _entity.getVersion());
        assertFalse(StringHelper.IsNullOrEmpty(templateXML));

    }
    @Test
    public void testGetEntityTemplateKey() throws CoalescePersistorException
    {
        String templateKey = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                                                        _entity.getSource(),
                                                                                                        _entity.getVersion());
        assertFalse(StringHelper.IsNullOrEmpty(templateKey));

    }

}
