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

package com.incadencecorp.coalesce.services.common.controllers;

import com.incadencecorp.coalesce.datamodel.impl.coalesce.entity.api.record.IEnumValuesRecord;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.entity.impl.coalesce.entity.EnumerationCoalesceEntity;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.entity.impl.coalesce.record.EnumValuesCoalesceRecord;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.entity.impl.pojo.record.EnumMetadataPojoRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLPersistorExt;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLSettings;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EnumerationDataControllerIT {

    @BeforeClass
    public static void initialize() throws Exception
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        PostGreSQLSettings.setConnector(connector);

        EnumerationCoalesceEntity entity = new EnumerationCoalesceEntity();
        entity.initialize();

        DerbyPersistor persister = new DerbyPersistor();
        persister.registerTemplate(CoalesceEntityTemplate.create(entity));
    }

    @Test
    public void populateEnumerations() throws Exception
    {
        DerbyPersistor persister = new DerbyPersistor();

        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(persister);

        EnumerationDataController controller = new EnumerationDataController(framework);

        List<String> keys = new ArrayList<>();
        for (EnumMetadataPojoRecord enumeration : controller.getEnumerations())
        {
            keys.add(enumeration.getKey());
        }

        CoalesceEntity[] entities = persister.getEntity(keys.toArray(new String[keys.size()]));

        for (CoalesceEntity entity : entities)
        {
            entity.markAsDeleted();
        }

        framework.saveCoalesceEntity(true, entities);

        //        persister.saveEntity(true, entities);
        //int executeUpdate = persister.executeUpdate(
        //        "DELETE FROM coalesce.CoalesceEntity WHERE ObjectKey IN ('b51ce54f-316e-4b11-a3b8-de97c8c87c93','f5a52546-1ff5-47c3-8286-2285bb787384','f2d05aa3-5cf3-429d-9a49-2a5935f92fe0','b4c47906-3de2-41ac-94ac-0376fc4ec810','c9a44c60-b20a-453c-9fe8-ca4064f2e599','ac0eef49-fdde-4898-a1d8-f457194ed319','f40316df-5dbd-4d65-8b06-8f839bb8ce1f','ffefb661-02bf-460a-815b-d7a79375142c','1c6c208e-4fd4-4d15-888f-9ca66de2d58e','962d4799-df12-452f-ada3-660857b076a7','64774f64-fb87-446c-9c81-4f44b001287c','49f38a37-5e75-4441-b061-a2e7d64f003b','13ba4366-053a-4760-8c72-92f2b1779941','feb0bc03-8c31-4859-923a-c240b169fe0f','6a7849c9-0f90-4b40-92c0-eb60f2e5f5e2','270c958f-b413-4a2c-b264-70f922956b12')");
        //System.out.println(executeUpdate);

    }

    @Test
    public void testFilterCreation() throws Exception
    {
        PostGreSQLPersistorExt persister = new PostGreSQLPersistorExt();

        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(persister);

        EnumerationDataController controller = new EnumerationDataController(framework);

        EnumerationCoalesceEntity entity = new EnumerationCoalesceEntity();
        entity.initialize();
        entity.getEnumMetadataRecord().setEnumname("Hello World");

        for (int ii = 0; ii < 5; ii++)
        {
            EnumValuesCoalesceRecord value = entity.addEnumValuesRecord();
            value.setOrdinal(ii);
            value.setValue(String.valueOf(ii));
        }

        // persister.saveEntity(false, entity);

        for (EnumMetadataPojoRecord entry : controller.getEnumerations())
        {
            System.out.println(entry.getEnumname());

            System.out.println("\t" + controller.getEnumeration(entry.getKey()));

            for (IEnumValuesRecord value : controller.getEnumerationValues(entry.getKey()))
            {
                System.out.println(value.getValue());
            }
        }

        // CoalesceCodeGeneratorIterator iterator = new
        // CoalesceCodeGeneratorIterator(Paths.get("."));
        // iterator.generateCode(entity);

    }

}
