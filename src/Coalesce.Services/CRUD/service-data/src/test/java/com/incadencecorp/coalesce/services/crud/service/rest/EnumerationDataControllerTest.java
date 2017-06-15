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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLPersistorExt;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLSettings;
import com.incadencecorp.coalesce.services.crud.service.client.CrudFrameworkClientImpl;
import com.incadencecorp.coalesce.services.crud.service.data.api.ICoalesceEnumerationValue;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.EnumerationDataController;
import com.incadencecorp.coalesce.services.crud.service.data.model.CoalesceEnumeration;
import com.incadencecorp.coalesce.services.crud.service.data.model.EnumerationValuePojo;
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

        CoalesceEnumeration entity = new CoalesceEnumeration();
        entity.initialize();
        entity.setEnumName("Hello World");
        
        List<EnumerationValuePojo> values = new ArrayList<EnumerationValuePojo>();
        
        for (int ii=0; ii<5; ii++) {
            EnumerationValuePojo value = new EnumerationValuePojo();
            value.setOrdinal(ii);
            value.setValue(String.valueOf(ii));
            
            values.add(value);
        }
        
//        entity.addValues(values);

//        persister.saveEntity(false, entity);

        for (Map.Entry<String, String> entry : controller.getEnumerationList().entrySet())
        {
            System.out.println(entry.getValue());

            JSONObject json = controller.getEnumeration(entry.getKey());
                System.out.println("\t" + json.toString());
        }

    }

}
