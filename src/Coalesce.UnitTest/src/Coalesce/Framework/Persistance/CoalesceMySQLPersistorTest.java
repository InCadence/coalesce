package Coalesce.Framework.Persistance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

import unity.connector.local.LocalConfigurationsConnector;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Common.Exceptions.CoalesceInvalidFieldException;
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

import com.database.persister.MySQLDataConnector;
import com.database.persister.MySQLPersistor;
import com.database.persister.ServerConn;

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

public class CoalesceMySQLPersistorTest {

    private static String MODULE_NAME = "CoalesceMySQLPersistorTest";

    static ServerConn serCon;
    static MySQLPersistor mySQLPersistor;
    private static CoalesceFramework _coalesceFramework;

    private static XsdEntity _entity;
    private static String _fieldKey;

    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException
    {

        CoalesceSettings.Initialize(new LocalConfigurationsConnector());

        serCon = new ServerConn();
        serCon.setURL("jdbc:mysql://localhost:3306/coalescedatabase");
        serCon.setUser("root");
        serCon.setPassword("Passw0rd");

        mySQLPersistor = new MySQLPersistor();
        mySQLPersistor.Initialize(serCon);

        mySQLPersistor.Initialize(serCon);
        CoalesceMySQLPersistorTest._coalesceFramework = new CoalesceFramework();
        CoalesceMySQLPersistorTest._coalesceFramework.Initialize(mySQLPersistor);

        CoalesceMySQLPersistorTest.createEntity();
    }

    /*
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

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

    @Test
    public void testConnection() throws SQLException, CoalescePersistorException
    {

        try (MySQLDataConnector conn = new MySQLDataConnector(serCon))
        {

            conn.OpenConnection();

        }
    }

    @Test(expected = SQLException.class)
    public void testFAILConnection() throws SQLException, CoalescePersistorException
    {
        // Is this even needed?
        ServerConn serConFail = new ServerConn();
        serConFail.setURL("jdbc:mysql//localhost:3306/coalescedatabase");
        serConFail.setPassword("Passw0rd");
        serConFail.setUser("rot");
        try (MySQLDataConnector conn = new MySQLDataConnector(serConFail))
        {

            conn.OpenConnection();

        }
    }

    @Test
    public void testSaveEntityAndXPath() throws CoalescePersistorException
    {
        assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity));

        // Get Field from DB Using XPath
        XsdField field = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceFieldByFieldKey(_fieldKey);
        assertTrue(field != null);

        // Get Record from Entity
        XsdRecord record = (XsdRecord) field.getParent();
        assertTrue(record != null);

        // Get Record from DB Using XPath
        XsdRecord recordDB = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceRecord(record.getKey());
        assertTrue(recordDB != null);
        assertTrue(recordDB.getName().equalsIgnoreCase(record.getName()));

    }

    @Test
    public void testSaveEntityTemplate() throws CoalescePersistorException, SAXException, IOException
    {
        CoalesceEntityTemplate template = testTemplate(CoalesceEntityTemplate.Create(_entity));
        assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntityTemplate(template));

    }

    @Test
    public void testGetEntityMetaData() throws CoalescePersistorException
    {
        CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
        EntityMetaData objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityIdAndTypeForKey(_entity.getKey());
        assertTrue(objectKey.entityId != null && objectKey.entityKey != null && objectKey.entityType != null);

    }

    @Test
    public void testGetEntity() throws CoalescePersistorException
    {
        XsdEntity ent = new XsdEntity();
        ent = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntity(_entity.getKey());

        assertTrue(ent != null);

    }

    @Test
    public void testCheckLastModified() throws CoalescePersistorException
    {
        DateTime lastModified;

        // Test Entity
        lastModified = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(_entity.getKey(),
                                                                                                   "entity");
        assertTrue(DateTimeComparator.getInstance().compare(lastModified, _entity.getLastModified()) == 0);

        // Test Section
        XsdSection section = _entity.getSection("TestEntity/Live Status Section");

        assertTrue(section != null);

        lastModified = null;
        lastModified = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(section.getKey(),
                                                                                                   "section");
        assertTrue(DateTimeComparator.getInstance().compare(lastModified, section.getLastModified()) == 0);

    }

    @Test
    public void testFAILCheckLastModified() throws CoalescePersistorException
    {
        DateTime lastModified;

        // Test Entity
        lastModified = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(_entity.getKey(),
                                                                                                   "linkage");
        assertTrue(DateTimeComparator.getInstance().compare(lastModified, _entity.getLastModified()) == 1);

    }

    @Test
    public void testGetEntityByIdAndType() throws CoalescePersistorException
    {
        XsdEntity ent = new XsdEntity();
        ent = CoalesceMySQLPersistorTest._coalesceFramework.GetEntity(_entity.getEntityId(), _entity.getEntityIdType());

        assertTrue(ent != null);

    }

    @Test
    public void testGetEntityByNameAndIdAndType() throws CoalescePersistorException
    {
        XsdEntity ent = new XsdEntity();

        ent = CoalesceMySQLPersistorTest._coalesceFramework.GetEntity(_entity.getName(),
                                                                      _entity.getEntityId(),
                                                                      _entity.getEntityIdType());

        assertTrue(ent != null);

    }

    @Test
    public void testGetFieldValue() throws CoalescePersistorException
    {
        assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity));
        String fieldValue = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);

        assertTrue(fieldValue.equals("Test Status"));

    }

    @Test
    public void testFAILGetFieldValue() throws CoalesceException
    {
        // Create a new entity, but do not save the entity
        assertTrue(CoalesceMySQLPersistorTest.createEntity());
        String fieldValue = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);
        assertNull(fieldValue);

    }

    @Test
    public void testGetEntityKeyForEntityId() throws CoalescePersistorException
    {
        String objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                                                         _entity.getEntityIdType(),
                                                                                                         _entity.getName());
        assertTrue(objectKey != null);

    }

    @Test
    public void testFAILGetEntityKeyForEntityId() throws CoalescePersistorException
    {
        String objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId("", "", "");
        assertTrue(objectKey == null);

    }

    @Test
    public void testGetEntityKeyForEntityIdName() throws CoalescePersistorException
    {
        List<String> objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                                                _entity.getEntityIdType(),
                                                                                                                _entity.getName(),
                                                                                                                _entity.getSource());
        assertTrue(objectKey.size() >= 1);

    }

    @Test
    public void testFAILGetEntityKeyForEntityIdName() throws CoalescePersistorException
    {
        List<String> objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId("",
                                                                                                                "",
                                                                                                                "",
                                                                                                                "");
        assertTrue(objectKey.size() == 0);

    }

    @Test
    public void testGetEntityKeyForEntityIdSource() throws CoalescePersistorException
    {
        List<String> objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                                                _entity.getEntityIdType(),
                                                                                                                _entity.getName());
        assertTrue(objectKey.size() >= 0 || objectKey != null);

    }

    @Test
    public void testFAILGetEntityKeyForEntityIdSource() throws CoalescePersistorException
    {
        List<String> objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId("", "", "");
        assertTrue(objectKey.size() == 0);

    }

    @Test
    public void testGetEntityKeysForEntityIdSource() throws CoalescePersistorException
    {
        String objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                                                         _entity.getEntityIdType(),
                                                                                                         _entity.getName(),
                                                                                                         _entity.getSource());
        assertTrue(objectKey != null);

    }

    @Test
    public void testGetEntityTemplateXML() throws CoalescePersistorException
    {
        // Get Template Key
        String templateKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                                                        _entity.getSource(),
                                                                                                        _entity.getVersion());

        // Load Template by Key
        String templateXML = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(templateKey);

        assertFalse(StringHelper.IsNullOrEmpty(templateXML));

    }

    @Test
    public void testGetEntityTemplateXMLName() throws CoalescePersistorException
    {
        String templateXML = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(_entity.getName(),
                                                                                                        _entity.getSource(),
                                                                                                        _entity.getVersion());
        assertFalse(StringHelper.IsNullOrEmpty(templateXML));

    }

    @Test
    public void testGetEntityTemplateKey() throws CoalescePersistorException
    {
        String templateKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                                                        _entity.getSource(),
                                                                                                        _entity.getVersion());
        assertFalse(StringHelper.IsNullOrEmpty(templateKey));

    }

    private static CoalesceEntityTemplate testTemplate(CoalesceEntityTemplate template)
    {
        String templateXml = template.toXml();
        System.out.println("Template: ");
        System.out.println("*********\n" + templateXml);

        // Confirm Template
        assertNotNull(templateXml);
        assertTrue(template.GetName().equalsIgnoreCase("TestEntity"));
        assertTrue(template.GetSource().equalsIgnoreCase("Unit Test"));
        assertTrue(template.GetVersion().equalsIgnoreCase("1.0.0.0"));

        // Confirm Values
        NodeList nodeList = template.GetDataObjectDocument().getElementsByTagName("*");

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
        XsdEntity entity2 = template.CreateNewEntity();

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

    @SuppressWarnings("unused")
    private static void createEntity(String entName,
                                     String entSource,
                                     String entVersion,
                                     String entID,
                                     String entTypeID,
                                     String entTitle,
                                     String sectName,
                                     String recordsetName,
                                     String fieldDefName,
                                     String fieldName) throws CoalesceException
    {
        // Create Test Entity
        _entity = new XsdEntity();

        XsdSection section = null;
        XsdRecordset recordSet = null;
        XsdRecord record = null;

        // Create Entity
        _entity = XsdEntity.create(entName, entSource, entVersion, entID, entTypeID, entTitle);

        XsdLinkageSection.create(_entity, true);

        section = XsdSection.create(_entity, sectName, true);
        recordSet = XsdRecordset.create(section, recordsetName);
        XsdFieldDefinition.create(recordSet, fieldDefName, ECoalesceFieldDataTypes.StringType);

        record = recordSet.addNew();
        record.setFieldValue(fieldDefName, fieldName);

        _fieldKey = record.getFieldByName(fieldDefName).getKey();

    }

}
