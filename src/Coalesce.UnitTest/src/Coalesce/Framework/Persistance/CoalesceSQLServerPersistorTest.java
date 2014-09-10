package Coalesce.Framework.Persistance;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import unity.connector.local.LocalConfigurationsConnector;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;

import com.database.persister.MySQLPersistor;
import com.database.persister.SQLServerPersistor;
import com.database.persister.ServerConn;


public class CoalesceSQLServerPersistorTest {
    static ServerConn serCon;
    static SQLServerPersistor mySQLServerPersistor;
    private static CoalesceFramework _coalesceFramework;

    private static XsdEntity _entity;
    private static String _fieldKey;
    
    @BeforeClass
    public static void Initialize() throws SAXException, IOException, CoalescePersistorException
    {

        CoalesceSettings.Initialize(new LocalConfigurationsConnector());

        serCon = new ServerConn();
        serCon.setURL("jdbc:sqlserver://localhost");
        serCon.setDatabase("database=CoalesceDatabase");
        serCon.setIntegratedSecurity(true);

        mySQLServerPersistor = new SQLServerPersistor();
        mySQLServerPersistor.Initialize(serCon);

        mySQLServerPersistor.Initialize(serCon);
        CoalesceSQLServerPersistorTest._coalesceFramework = new CoalesceFramework();
        CoalesceSQLServerPersistorTest._coalesceFramework.Initialize(mySQLServerPersistor);

        CoalesceSQLServerPersistorTest.createEntity();
        CoalesceSQLServerPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
    }
    private static boolean createEntity()
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
        return true;
    }
    @Test
    public void test()
    {
        fail("Not yet implemented");
    }

}
