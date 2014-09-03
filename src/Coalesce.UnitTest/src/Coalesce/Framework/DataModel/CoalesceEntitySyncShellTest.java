package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import org.junit.Test;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.DataModel.CoalesceEntitySyncShell;
import Coalesce.Framework.DataModel.XsdEntity;


public class CoalesceEntitySyncShellTest {
    
    @Test
    public void CreateFromEntity()
    {

        try
        {

            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

            CoalesceEntitySyncShell SyncShell = new CoalesceEntitySyncShell();
            //CoalesceEntitySyncShell.InitializeFromEntity(entity);

            // Initialize
            SyncShell.InitializeFromEntity(entity);
            // Evaluate
            assertEquals(entity.GetEntityId(), ((XsdEntity) SyncShell.GetEntityNode()).GetEntityId());

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
            CoalesceEntitySyncShell shell = new CoalesceEntitySyncShell();
            shell.Initialize(CoalesceTypeInstances.TESTMISSION);
            
            assertNotNull("Failed to initialize mission entity", shell);
            assertEquals(((XsdEntity) shell.GetEntityNode()).GetEntityId(), entity.GetEntityId());
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
        CoalesceEntitySyncShell shell = new CoalesceEntitySyncShell();
        shell.Initialize(entity.ToXml()); //(entity.GetDataObjectDocument());
        
        assertNotNull("Failed to initialize mission entity", shell);
        assertEquals(((XsdEntity) shell.GetEntityNode()).GetEntityId(), entity.GetEntityId());
    }

    @Test
    public void InitializeByEntity()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        CoalesceEntitySyncShell shell = new CoalesceEntitySyncShell();
        shell.InitializeFromEntity(entity);
        
        assertNotNull("Failed to initialize mission entity", shell);
        assertEquals(((XsdEntity) shell.GetEntityNode()).GetEntityId(), entity.GetEntityId());
    }
    
    @Test
    public void Clone()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        CoalesceEntitySyncShell shell = new CoalesceEntitySyncShell();
        shell.InitializeFromEntity(entity);
        
        CoalesceEntitySyncShell clone = new CoalesceEntitySyncShell();
        clone = shell.Clone(shell);
        assertNotNull("Failed to initialize mission entity", clone);
        assertEquals(shell.GetDataObjectDocument(), clone.GetDataObjectDocument());
    }
    
    @Test
    public void GetRequiredChangesSyncShell()
    {
        try
        {
//            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
//            CoalesceEntitySyncShell shell = new CoalesceEntitySyncShell();
//            shell.InitializeFromEntity(entity);
//            
//            GetRequiredChangesSyncShell
            
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
