package Coalesce.Framework.DataModel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;

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

            CoalesceEntityTemplate EntTemp = new CoalesceEntityTemplate();

            // Initialize
            EntTemp.InitializeFromEntity(entity);
            
            String title = entity.GetTitle();
            assertEquals("NORTHCOM Volunteer Background Checks, NORTHCOM Volunteer Background Checks", title);

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
            // TODO: need loadXml function
            // Create DataObjectDocument
            Document XmlDoc = null;
            //XmlDoc.LoadXml(CoalesceTypeInstances.TESTMISSION);

            // Call Peer.
            CoalesceEntityTemplate template = new CoalesceEntityTemplate();
            template.Initialize(XmlDoc);
            
            fail("Not Implemented");

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
            Document EntityTemplateDataObjectDocument = entity.GetDataObjectDocument();
            // Set DataObjectDocument
            CoalesceEntityTemplate template = new CoalesceEntityTemplate();
            template.SetDataObjectDocument(EntityTemplateDataObjectDocument);

            String title = template.GetEntityNode().GetTitle();
            assertEquals("NORTHCOM Volunteer Background Checks, NORTHCOM Volunteer Background Checks", title);

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
