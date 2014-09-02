package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;

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

public class XsdEntityTest {

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception {
     * 
     * }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception {
     * 
     * }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void CreateXsdEntityFromXml()
    {

        try
        {

            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

            String title = entity.GetTitle();
            assertEquals("NORTHCOM Volunteer Background Checks, NORTHCOM Volunteer Background Checks", title);

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

    }

    @Test
    public void GetTitleWithoutXpathTest()
    {
        try
        {

            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOXPATHTITLE);

            String title = entity.GetTitle();

            assertEquals("TREX Portal", title);

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testArbitraryAttributes()
    {
        try
        {
            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

            String entityXml = entity.ToXml();

            assertTrue(entityXml.contains("anthony=\"Test\""));
            assertTrue(entity.GetAttribute("anthony").equals("Test"));

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void EmptyDateInitialization()
    {
        fail("Not yet implemented");
    }

    @Test
    public void UpdateTitleThatUsesXpathTest()
    {
        fail("Not yet implemented");
    }

    @Test
    public void UpdateTitleThatDoesNotUseXpathTest()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateTREXOperation()
    {

        try
        {
            XsdEntity entity = null;
            XsdSection section = null;
            XsdRecordset recordSet = null;

            // Create Entity
            entity = XsdEntity.Create("TREXOperation",
                                      "TREX Portal",
                                      "1.0.0.0",
                                      "",
                                      "",
                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

            // Verify Entity Creation
            assertTrue(entity.GetSource().equals("TREX Portal"));
            assertTrue(entity.GetLinkageSection() == null);

            entity.SetAttribute("testnewattribute", "test");

            // Create Linkage Section
            XsdLinkageSection.Create(entity, true);

            // Verify Link Section Creation
            assertTrue(entity.GetLinkageSection() != null);

            // Create Live Status Section
            section = XsdSection.Create(entity, "Live Status Section", true);
            recordSet = XsdRecordset.Create(section, "Live Status Recordset");

            // Verify Live Status Section Creation
            assertTrue(entity.GetSection("TREXOperation/Live Status Section") != null);

            XsdFieldDefinition.Create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

            // Create Information Section
            section = XsdSection.Create(entity, "Operation Information Section", true);
            recordSet = XsdRecordset.Create(section, "Operation Information Recordset");

            XsdFieldDefinition.Create(recordSet, "OperationName", ECoalesceFieldDataTypes.StringType);

            // Verify Information Section Creation
            assertTrue(entity.GetSection("TREXOperation/Operation Information Section") != null);

            // Serialize
            String entityXml = entity.ToXml();

            // Deserialize
            XsdEntity entity2 = new XsdEntity();
            assertTrue(entity2.Initialize(entityXml));

            // Verify Custom Attribute
            assertTrue(entity.GetAttribute("testnewattribute").equals("test"));

            // Verify Entity
            assertTrue(entity.GetSource().equals("TREX Portal"));

            // Verify Link Section
            assertTrue(entity2.GetLinkageSection() != null);

            // Verify Live Status Section
            assertTrue(entity2.GetSection("TREXOperation/Live Status Section") != null);

            // Verify Information Section
            assertTrue(entity2.GetSection("TREXOperation/Operation Information Section") != null);

        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }
    
    @Test 
    public void testFieldHistory() {
        
        // Create Entity
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        
        XsdSection section = entity.GetSection("TREXMission/Mission Information Section");
        
        fail("Not Implemented");
        
    }

}
