package Coalesce.Framework.Persistance;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;

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
import Coalesce.Common.Exceptions.InvalidFieldException;
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

import com.database.persister.PostGresDataConnector;
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

        CoalesceSettings.Initialize(new LocalConfigurationsConnector());

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
        catch (InvalidFieldException e)
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

    @Test
    public void testConnection() throws SQLException, CoalescePersistorException
    {

        try (PostGresDataConnector conn = new PostGresDataConnector(serCon))
        {

            conn.OpenConnection();

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void saveEntity() throws CoalesceException
    {
        CoalescePostGresPersistorTest.createEntity();
        CoalescePostGresPersistorTest._coalesceFramework.SaveCoalesceEntity(_entity);
    }
    // @Test
    // public void testGetEntity() throws CoalescePersistorException
    // {
    // XsdEntity ent = new XsdEntity();
    // ent = CoalescePostGresPersistorTest._coalesceFramework.GetCoalesceEntity(_entity.getKey());
    //
    // assertTrue(ent != null);
    //
    // }

}
