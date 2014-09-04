package Coalesce.Framework.Persistance;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import unity.connector.local.LocalConfigurationsConnector;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;
import Coalesce.Framework.Persistance.ICoalescePersistor.EntityMetaData;

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
    public static void Initialize()
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

    }

    @Test
    public void TestConnection()
    {
        fail("Not yet implemented");
    }

    @Test
    public void TestSaveEntity()
    {
        try
        {
            assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity));
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
    public void TestGetEntityMetaData()
    {
        try
        {
            EntityMetaData objectKey = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityIdAndTypeForKey(_entity.GetKey());
            assertTrue(objectKey.Id != null && objectKey.Key != null && objectKey.Type != null);
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
            String templateXML = CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntityTemplateXml(_entity.GetKey());
            assertTrue(templateXML != null);
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
            assertTrue(templateXML != null);
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
            assertTrue(templateKey != null);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }
    @Test
    public void TestGetXPath()
    {
        fail("Not yet implemented");
    }

    @After
    public void Finalize()
    {

    }

}
