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

import java.nio.file.Paths;

import org.junit.Test;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.plugins.template2java.CoalesceCodeGeneratorIterator;

/**
 * These test ensure the code generator creates useable code.
 * 
 * @author Derek Clemenzi
 */
public class CoalesceCodeGeneratorIteratorTest {

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
        CoalesceEntity entity = CoalesceEntity.create("gen-test", "A B C", "0.0.25-SNAPSHOT");
        entity.initialize();

        CoalesceSection section = CoalesceSection.create(entity, "my-section");
        TestRecord.createCoalesceRecordset(section, "test-1").setMaxRecords(1);
//        TestRecord.createCoalesceRecordset(section, "test-2");

        CoalesceRecordset recordset = CoalesceRecordset.create(section, "all data types");
        for (ECoalesceFieldDataTypes type : ECoalesceFieldDataTypes.values())
        {
            CoalesceFieldDefinition.create(recordset, type.getLabel() + " field", type);
        }

        //
        CoalesceCodeGeneratorIterator it = new CoalesceCodeGeneratorIterator(Paths.get("src", "test", "resources"));
        it.generateCode(entity);
    }

    /**
     * Uses the generated code to verify an entity can be created from what is
     * generated. Code is commented out by default since it will always fail the
     * first time as the source has not been generated yet. Once you have ran
     * these test once you can uncomment this code remember to re-comment it
     * before committing.
     * 
     * TODO Ensure this unit test is commented out before committing.
     * 
     * @throws Exception
     */
    @Test
    public void testGeneratedCode() throws Exception
    {
//         GenTestEntity entity = new GenTestEntity();
//         entity.initialize();
//        
////         entity.addAllDataTypesRecord().set
//         entity.getTest1Record().getStringField().setValue("Hello World");
//        
//         System.out.println(entity.toXml());
    }

}
