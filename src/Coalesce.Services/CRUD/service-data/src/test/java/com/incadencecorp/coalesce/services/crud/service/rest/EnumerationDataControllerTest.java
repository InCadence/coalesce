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

package com.incadencecorp.coalesce.services.crud.service.rest;

import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLPersistorExt;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLSettings;
import com.incadencecorp.coalesce.services.crud.service.client.CrudFrameworkClientImpl;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.EnumerationDataController;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IValuesRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.entity.EnumerationCoalesceEntity;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.record.ValuesCoalesceRecord;
import com.incadencecorp.coalesce.services.search.service.client.SearchFrameworkClientImpl;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

public class EnumerationDataControllerTest {

    @Test
    public void testFilterCreation() throws Exception
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        PostGreSQLSettings.setConnector(connector);
        PostGreSQLPersistorExt persister = new PostGreSQLPersistorExt();

        CoalesceFramework framework = new CoalesceFramework();
        framework.setAuthoritativePersistor(persister);

        SearchFrameworkClientImpl search = new SearchFrameworkClientImpl(persister);
        CrudFrameworkClientImpl crud = new CrudFrameworkClientImpl(framework);

        EnumerationDataController controller = new EnumerationDataController(crud, search);

        EnumerationCoalesceEntity entity = new EnumerationCoalesceEntity();
        entity.initialize();
        entity.getMetadataRecord().setEnumname("Hello World");

        for (int ii = 0; ii < 5; ii++)
        {
            ValuesCoalesceRecord value = entity.addValuesRecord();
            value.setOrdinal(ii);
            value.setValue(String.valueOf(ii));
        }

        // persister.saveEntity(false, entity);

        for (Map.Entry<String, String> entry : controller.getEnumerations().entrySet())
        {
            System.out.println(entry.getValue());

            System.out.println("\t" + controller.getEnumeration(entry.getKey()));

            for (IValuesRecord value : controller.getEnumerationValues(entry.getKey()))
            {
                System.out.println(value.getValue());
            }
        }

        // CoalesceCodeGeneratorIterator iterator = new
        // CoalesceCodeGeneratorIterator(Paths.get("."));
        // iterator.generateCode(entity);

    }

}
