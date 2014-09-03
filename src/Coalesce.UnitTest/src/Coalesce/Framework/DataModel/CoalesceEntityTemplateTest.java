package Coalesce.Framework.DataModel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;

import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;
import Coalesce.Framework.DataModel.XsdEntity;


public class CoalesceEntityTemplateTest {

    @Test
    public void CreateCoalesceEntityTemplate()
    {

        try
        {

            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

            CoalesceEntityTemplate template = new CoalesceEntityTemplate();

            // Initialize
            fail("Not Implemented");
            template.InitializeFromEntity(entity);
            
            String templateName = template.GetName();
            assertNotNull("Failed to initialize mission entity", template);
            assertEquals("TREXMission", templateName);

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

    }
    
    @Test
    public void InitializeFromString()
    {
        try
        {
            // Create DataObjectDocument
            Document XmlDoc = null;
            XmlDoc = XmlHelper.loadXMLFrom(CoalesceTypeInstances.TESTMISSION);

            // Call Peer.
            CoalesceEntityTemplate template = new CoalesceEntityTemplate();
            template.Initialize(XmlDoc);
            
            String templateName = template.GetName();
            assertNotNull("Failed to initialize mission entity", template);
            assertEquals("TREXMission", templateName);

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void InitializeFromDocument()
    {
        try
        {
            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
            Document EntityTemplateDataObjectDocument = null;
            EntityTemplateDataObjectDocument = XmlHelper.loadXMLFrom(entity.ToXml());
            // Set DataObjectDocument
            CoalesceEntityTemplate template = new CoalesceEntityTemplate();
            template.SetDataObjectDocument(EntityTemplateDataObjectDocument);

            String templateName = template.GetName();
            assertNotNull("Failed to initialize mission entity", template);
            assertEquals("TREXMission", templateName);

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }
    
    @Test
    public void InitializeFromEntity()
    {
        try
        {
            fail("Not Implemented");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }
    
    @Test
    public void CreateNewEntity()
    {
        try
        {
            fail("Not Implemented");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

}
