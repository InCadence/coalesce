import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import unity.common.CallResult;
import unity.common.CallResult.CallResults;
import unity.connector.local.LocalConfigurationsConnector;
import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalesceInvalidFieldException;
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

import com.database.persister.Neo4JDataConnector;
import com.database.persister.Neo4JPersistor;
import com.database.persister.ServerConn;

public class CoalesceNEO4JPersistorTest {

    private static String MODULE_NAME = "CoalesceNEO4JPersistorTest";

    static ServerConn serCon;
    static Neo4JPersistor neo4jPersister;
    private static CoalesceFramework _coalesceFramework;

    private static XsdEntity _entity;
    private static String _fieldKey;

    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException
    {

        CoalesceSettings.initialize(new LocalConfigurationsConnector());

        serCon = new ServerConn();
        serCon.setURL("jdbc:neo4j://localhost:7474/");

        neo4jPersister = new Neo4JPersistor();
        neo4jPersister.Initialize(serCon);

        neo4jPersister.Initialize(serCon);
        CoalesceNEO4JPersistorTest._coalesceFramework = new CoalesceFramework();
        CoalesceNEO4JPersistorTest._coalesceFramework.Initialize(neo4jPersister);

        CoalesceNEO4JPersistorTest.createEntity();

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

    @Test
    public void testConnection() throws SQLException, CoalescePersistorException
    {

        try (Neo4JDataConnector conn = new Neo4JDataConnector(serCon))
        {
            conn.openConnection();
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

}
