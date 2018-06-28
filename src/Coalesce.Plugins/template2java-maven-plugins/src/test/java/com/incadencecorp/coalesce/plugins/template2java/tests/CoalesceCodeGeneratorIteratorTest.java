/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.plugins.template2java.tests;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.plugins.template2java.CoalesceCodeGeneratorIterator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * These test ensure the code generator creates useable code.
 *
 * @author Derek Clemenzi
 */
public class CoalesceCodeGeneratorIteratorTest {

    /**
     * Test initialization
     */
    @BeforeClass
    public static void initialize()
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY, "src/test/resources");
    }

    /**
     * This test creates an entity with two record sets of {@link TestRecord}.
     * The first can only contain a single entry and the second can contain
     * multiple.
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        CoalesceSection section = CoalesceSection.create(entity, "my-section");
        TestRecord.createCoalesceRecordset(section, "OEtest-1").setMaxRecords(1);
        TestRecord.createCoalesceRecordset(section, "test-2");

        CoalesceRecordset recordset = CoalesceRecordset.create(section, "all data types");
        for (ECoalesceFieldDataTypes type : ECoalesceFieldDataTypes.values())
        {
            CoalesceFieldDefinition.create(recordset, type.getLabel() + " field", type);
        }

        //
        CoalesceCodeGeneratorIterator it = new CoalesceCodeGeneratorIterator(Paths.get("target"));
        it.generateCode(entity);
    }

    /**
     * Uses the generated code to verify an entity can be created from what is
     * generated. Code is commented out by default since it will always fail the
     * first time as the source has not been generated yet. Once you have ran
     * these test once you can uncomment this code remember to re-comment it
     * before committing.
     * <p>
     * TODO Ensure this unit test is commented out before committing.
     *
     * @throws Exception
     */
    @Test
    public void testGeneratedCode() throws Exception
    {
        /*
        UnitTestCoalesceEntity entity = new UnitTestCoalesceEntity();
        entity.initialize();
        entity.getTest1Record().getStringField().setValue("Hello World");
        entity.addAllDataTypesRecord().setBooleanField(false);
        entity.addAllDataTypesRecord().setBooleanField(true);
        System.out.println(entity.toXml());
        //*/
    }

}
