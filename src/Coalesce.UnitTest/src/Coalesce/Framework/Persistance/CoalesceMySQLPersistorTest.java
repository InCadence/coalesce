package Coalesce.Framework.Persistance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import Coalesce.Common.Helpers.JodaDateTimeHelper;
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

        // Create Test Entity
        _entity = new XsdEntity();

        XsdSection section = null;
        XsdRecordset recordSet = null;
        XsdRecord record = null;

        // Create Entity
        _entity = XsdEntity.Create("TestEntity", "Unit Test", "1.0.0.0", "", "", "");

        XsdLinkageSection.Create(_entity, true);

        section = XsdSection.Create(_entity, "Live Status Section", true);
        recordSet = XsdRecordset.Create(section, "Live Status Recordset");
        XsdFieldDefinition.Create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

        record = recordSet.AddNew();
        record.SetFieldValue("CurrentStatus", "Test Status");

        _fieldKey = record.GetFieldByName("CurrentStatus").GetKey();
        System.out.println("Original Sample Entity:");
        System.out.println("***********************\n" + _entity.ToXml());

    }

    @Test
    public void TestConnection()
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
    public void TestSaveEntity()
    {
        try
        {
            assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity));

            // Get Field from DB
            XsdField field = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceFieldByFieldKey(_fieldKey);
            assertTrue(field != null);

            // Get Record from Entity
            XsdRecord record = (XsdRecord) field.GetParent();
            assertTrue(record != null);

            // Get Record from DB
            XsdRecord recordDB = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceRecord(record.GetKey());
            assertTrue(recordDB != null);
            assertTrue(recordDB.GetName().equalsIgnoreCase(record.GetName()));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestSaveEntityTemplate()
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
    public void TestGetEntityMetaData()
    {
        try
        {
            CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
            EntityMetaData objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityIdAndTypeForKey(_entity.GetKey());
            assertTrue(objectKey.entityId != null && objectKey.entityKey != null && objectKey.entityType != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetEntity()
    {
        try
        {
            XsdEntity ent = new XsdEntity();
            ent = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntity(_entity.GetKey());

            assertTrue(ent != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestCheckLastModified()
    {
        try
        {
            DateTime lastModified;

            // Test Entity
            lastModified = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(_entity.GetKey(),
                                                                                                       "entity");
            assertTrue(DateTimeComparator.getInstance().compare(lastModified, _entity.GetLastModified()) == 0);

            // Test Section
            XsdSection section = _entity.GetSection("TestEntity/Live Status Section");

            assertTrue(section != null);

            lastModified = null;
            lastModified = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityLastModified(section.GetKey(),
                                                                                                       "section");
            assertTrue(DateTimeComparator.getInstance().compare(lastModified, section.GetLastModified()) == 0);

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetEntityByIdAndType()
    {
        try
        {
            XsdEntity ent = new XsdEntity();
            ent = CoalesceMySQLPersistorTest._coalesceFramework.GetEntity(_entity.GetEntityId(), _entity.GetEntityIdType());

            assertTrue(ent != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetEntityByNameAndIdAndType()
    {
        try
        {
            XsdEntity ent = new XsdEntity();

            ent = CoalesceMySQLPersistorTest._coalesceFramework.GetEntity(_entity.GetName(),
                                                                          _entity.GetEntityId(),
                                                                          _entity.GetEntityIdType());

            assertTrue(ent != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetFieldValue()
    {

        try
        {
            String fieldValue = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceFieldValue(_fieldKey);

            assertTrue(fieldValue.equals("Test Status"));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

    }

    @Test
    public void TestGetEntityKeyForEntityId()
    {
        try
        {
            String objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.GetEntityId(),
                                                                                                             _entity.GetEntityIdType(),
                                                                                                             _entity.GetName());
            assertTrue(objectKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetEntityKeyForEntityIdName()
    {
        try
        {
            List<String> objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.GetEntityId(),
                                                                                                                    _entity.GetEntityIdType(),
                                                                                                                    _entity.GetName(),
                                                                                                                    _entity.GetName());
            assertTrue(objectKey.size() >= 0 || objectKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetEntityKeyForEntityIdSource()
    {
        try
        {
            List<String> objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeysForEntityId(_entity.GetEntityId(),
                                                                                                                    _entity.GetEntityIdType(),
                                                                                                                    _entity.GetName(),
                                                                                                                    _entity.GetSource());
            assertTrue(objectKey.size() >= 0 || objectKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetEntityKeysForEntityIdSource()
    {
        try
        {
            String objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityKeyForEntityId(_entity.GetEntityId(),
                                                                                                             _entity.GetEntityIdType(),
                                                                                                             _entity.GetName(),
                                                                                                             _entity.GetSource());
            assertTrue(objectKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetEntityTemplateXML()
    {
        try
        {
            // Get Template Key
            String templateKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.GetName(),
                                                                                                            _entity.GetSource(),
                                                                                                            _entity.GetVersion());

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
    public void TestGetEntityTemplateXMLName()
    {
        try
        {
            String templateXML = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(_entity.GetName(),
                                                                                                            _entity.GetSource(),
                                                                                                            _entity.GetVersion());
            assertFalse(StringHelper.IsNullOrEmpty(templateXML));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetEntityTemplateKey()
    {
        try
        {
            String templateKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateKey(_entity.GetName(),
                                                                                                            _entity.GetSource(),
                                                                                                            _entity.GetVersion());
            assertFalse(StringHelper.IsNullOrEmpty(templateKey));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TestGetXPath()
    {
        // TODO Update CoalesceFramework with call to this.
        fail("Not yet implemented");
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

        String entityXml = entity2.ToXml();
        System.out.println("Copy of Entity made from Template: " + entity2.GetKey());
        System.out.println("**********************************\n" + entityXml);

        // Confirm Entity
        assertNotNull(entityXml);
        assertNotNull(entity2.GetKey());
        assertTrue(entity2.GetName().equalsIgnoreCase("TestEntity"));
        assertTrue(entity2.GetSource().equalsIgnoreCase("Unit Test"));
        assertTrue(entity2.GetVersion().equalsIgnoreCase("1.0.0.0"));
        return template;
    }

    @After
    public void Finalize()
    {

    }
}
