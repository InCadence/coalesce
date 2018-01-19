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

package com.incadencecorp.coalesce.services.search.service.rest;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.services.api.search.QueryResultType;
import com.incadencecorp.coalesce.services.search.service.data.controllers.SearchDataController;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.PropertyIsEqualTo;

import java.util.ArrayList;
import java.util.List;

public class SearchDataControllerTest {

    @Test
    public void testFilterCreation() throws Exception
    {
        DerbyPersistor persister = new DerbyPersistor();

        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(persister);

        SearchDataController controller = new SearchDataController(framework);

        TestEntity entity = new TestEntity();
        entity.initialize();
        entity.addRecord1().getBooleanField().setValue(false);

        framework.registerTemplates(CoalesceEntityTemplate.create(entity));
        framework.saveCoalesceEntity(entity);

        List<SearchCriteria> options = new ArrayList<>();

        SearchCriteria option = new SearchCriteria();
        option.setRecordset(TestEntity.RECORDSET1);
        option.setField("boolean");
        option.setValue("false");
        option.setComparer(PropertyIsEqualTo.NAME);
        option.setMatchCase(false);

        options.add(option);

        QueryResultType results = controller.search(options);

        Assert.assertEquals(1, results.getHits().size());
        Assert.assertEquals(entity.getKey(), results.getHits().get(0).getEntityKey());
    }

}
