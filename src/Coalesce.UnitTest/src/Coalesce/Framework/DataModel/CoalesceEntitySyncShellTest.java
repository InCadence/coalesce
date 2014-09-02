package Coalesce.Framework.DataModel;

import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.DataModel.CoalesceEntitySyncShell;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;


public class CoalesceEntitySyncShellTest {
    
    @Test
    public void CreateFromEntity()
    {

        try
        {

            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

            CoalesceEntitySyncShell SyncShell = new CoalesceEntitySyncShell();

            // Initialize
            SyncShell.InitializeFromEntity(Entity);
            // Evaluate
            assertEquals(entity.GetTitle(), SyncShell._EntityNode.GetTitle());

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

    }

    @Test
    public void InitializeByString()
    {
        try
        {
            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
            CoalesceEntitySyncShell shell;
            shell.Initialize(CoalesceTypeInstances.TESTMISSION);
            
            assertNotNull("Failed to initialize mission entity", shell);
            assertEquals(shell._DataObjectDocument, entity.GetDataObjectDocument());
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void InitializeByDocument()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        CoalesceEntitySyncShell shell;
        shell.Initialize(entity.GetDataObjectDocument());
        
        assertNotNull("Failed to initialize mission entity", shell);
        assertEquals(shell._DataObjectDocument, entity.GetDataObjectDocument());
    }

    @Test
    public void InitializeByEntity()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        CoalesceEntitySyncShell shell;
        shell.InitializeFromEntity(entity);
        
        assertNotNull("Failed to initialize mission entity", shell);
        assertEquals(shell._DataObjectDocument, entity.GetDataObjectDocument());
    }
    
    @Test
    public void Clone()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        CoalesceEntitySyncShell shell;
        shell.InitializeFromEntity(entity);
        
        CoalesceEntitySyncShell clone;
        clone = shell.Clone(shell);
        assertNotNull("Failed to initialize mission entity", clone);
        assertEquals(shell._DataObjectDocument, clone._DataObjectDocument);
    }
    
    @Test
    public void GetRequiredChangesSyncShell()
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
    public void PruneUnchangedNodes()
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
