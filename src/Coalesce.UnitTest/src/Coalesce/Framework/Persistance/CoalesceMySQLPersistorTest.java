package Coalesce.Framework.Persistance;

import static org.junit.Assert.*;

import java.io.IOException;
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

    static ServerConn serCon;
    static MySQLPersistor mySQLPersistor;
    private static CoalesceFramework _coalesceFramework;

    private static XsdEntity _entity;
    private static String _fieldKey;

    @BeforeClass
    public static void Initialize() throws SAXException, IOException
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

    private static void createEntity()
    {
        // Create Test Entity
        _entity = new XsdEntity();

        XsdSection section = null;
        XsdRecordset recordSet = null;
        XsdRecord record = null;

        // Create Entity
        _entity = XsdEntity.create("TestEntity", "Unit Test", "1.0.0.0", "", "", "");

        XsdLinkageSection.Create(_entity, true);

        section = XsdSection.Create(_entity, "Live Status Section", true);
        recordSet = XsdRecordset.Create(section, "Live Status Recordset");
        XsdFieldDefinition.create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

        record = recordSet.AddNew();
        record.SetFieldValue("CurrentStatus", "Test Status");

        _fieldKey = record.GetFieldByName("CurrentStatus").getKey();
        System.out.println("Original Sample Entity:");
        System.out.println("***********************\n" + _entity.toXml());
        
    }
    private static void createEntity(String entName, String entSource, String entVersion, String entID, String entTypeID, String entTitle, String sectName, String recordsetName, String fieldDefName, String fieldName)
    {
        // Create Test Entity
        _entity = new XsdEntity();

        XsdSection section = null;
        XsdRecordset recordSet = null;
        XsdRecord record = null;

        // Create Entity
        _entity = XsdEntity.create(entName, entSource, entVersion, entID, entTypeID, entTitle);

        XsdLinkageSection.Create(_entity, true);

        section = XsdSection.Create(_entity, sectName, true);
        recordSet = XsdRecordset.Create(section, recordsetName);
        XsdFieldDefinition.Create(recordSet, fieldDefName, ECoalesceFieldDataTypes.StringType);

        record = recordSet.AddNew();
        record.SetFieldValue(fieldDefName, fieldName);

        _fieldKey = record.GetFieldByName(fieldDefName).getKey();       
    }

    @Test
    public void testConnection()
    {

        try (MySQLDataConnector conn = new MySQLDataConnector(serCon))
        {

            conn.OpenConnection();

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSaveEntityAndXPath()
    {
        try
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
            CoalesceEntityTemplate template = testTemplate(CoalesceEntityTemplate.Create(_entity));
            assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntityTemplate(template));
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
            CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
            EntityMetaData objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityIdAndTypeForKey(_entity.getKey());
            assertTrue(objectKey.entityId != null && objectKey.entityKey != null && objectKey.entityType != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntity()
    {
        try
        {
            XsdEntity ent = new XsdEntity();
            ent = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntity(_entity.getKey());

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
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityByIdAndType()
    {
        try
        {
            XsdEntity ent = new XsdEntity();
            ent = CoalesceMySQLPersistorTest._coalesceFramework.GetEntity(_entity.getEntityId(), _entity.getEntityIdType());

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

            ent = CoalesceMySQLPersistorTest._coalesceFramework.GetEntity(_entity.getName(),
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
            assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity));
            String fieldValue = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);

            assertTrue(fieldValue.equals("Test Status"));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

    }
    @Test
    public void testFailureGetFieldValue()
    {

        try
        {
            CoalesceMySQLPersistorTest.createEntity();//    Create a new entity, but do not save the entity
            String fieldValue = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);
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
            String objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
                                                                                                             _entity.getEntityIdType(),
                                                                                                             _entity.getName());
            assertTrue(objectKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetEntityKeyForEntityIdName()
    {
        try
        {
            List<String> objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                                                    _entity.getEntityIdType(),
                                                                                                                    _entity.getName(),
                                                                                                                    _entity.getName());
            assertTrue(objectKey.size() >= 0 || objectKey != null);
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
            List<String> objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.getEntityId(),
                                                                                                                    _entity.getEntityIdType(),
                                                                                                                    _entity.getName(),
                                                                                                                    _entity.getSource());
            assertTrue(objectKey.size() >= 0 || objectKey != null);
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
            String objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.getEntityId(),
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
            String templateKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                                                            _entity.getSource(),
                                                                                                            _entity.getVersion());

            // Load Template by Key
            String templateXML = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(templateKey);

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
            String templateXML = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(_entity.getName(),
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
            String templateKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.getName(),
                                                                                                            _entity.getSource(),
                                                                                                            _entity.getVersion());
            assertFalse(StringHelper.IsNullOrEmpty(templateKey));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
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

    @After
    public void Finalize()
    {

    }
}
