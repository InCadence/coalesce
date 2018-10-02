/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.services.crud.service.rest;

import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphLink;
import com.incadencecorp.coalesce.services.crud.service.client.CrudFrameworkClientImpl;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.LinkageDataController;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Derek Clemenzi
 */
public class LinkageDataControllerTest {

    /**
     * This test links two entities and ensures that the linkages are retrievable via the controller.
     */
    @Test
    public void testRetrieveLinkages() throws Exception
    {
        // Setup
        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(new DerbyPersistor());

        LinkageDataController controller = new LinkageDataController(new CrudFrameworkClientImpl(framework), framework);

        // Create Entities
        CoalesceEntity entity1 = CoalesceEntity.create("name", "source", "1.0");
        CoalesceEntity entity2 = CoalesceEntity.create("name", "source", "1.0");

        // Create Linkage
        EntityLinkHelper.linkEntitiesBiDirectional(entity1, ELinkTypes.IS_PARENT_OF, entity2);

        // Save Entities
        framework.saveCoalesceEntity(entity1, entity2);

        // Retrieve Linkages
        List<GraphLink> links = controller.retrieveLinkages(entity1.getKey());

        // Verify
        Assert.assertEquals(1, links.size());
        Assert.assertEquals(entity1.getKey(), links.get(0).getSource());
        Assert.assertEquals(entity2.getKey(), links.get(0).getTarget());
        Assert.assertEquals(ELinkTypes.IS_PARENT_OF, links.get(0).getType());
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, links.get(0).getStatus());

        // Retrieve Linkages
        links = controller.retrieveLinkages(entity2.getKey());

        // Verify
        Assert.assertEquals(1, links.size());
        Assert.assertEquals(entity2.getKey(), links.get(0).getSource());
        Assert.assertEquals(entity1.getKey(), links.get(0).getTarget());
        Assert.assertEquals(ELinkTypes.IS_CHILD_OF, links.get(0).getType());
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, links.get(0).getStatus());
    }

    /**
     * This test ensures that the link and unlink action work as intended.
     */
    @Test
    public void testLinkAndUnlink() throws Exception
    {
        // Setup
        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(new DerbyPersistor());

        LinkageDataController controller = new LinkageDataController(new CrudFrameworkClientImpl(framework), framework);

        // Create Entities
        CoalesceEntity entity1 = CoalesceEntity.create("name", "source", "1.0");
        CoalesceEntity entity2 = CoalesceEntity.create("name", "source", "1.0");

        // Save Entities
        framework.saveCoalesceEntity(entity1, entity2);

        // Create Tasks
        GraphLink task1 = new GraphLink();
        task1.setType(ELinkTypes.IS_PARENT_OF);
        task1.setBiDirectional(true);
        task1.setSource(entity1.getKey());
        task1.setTarget(entity2.getKey());

        GraphLink task2 = new GraphLink();
        task2.setType(ELinkTypes.CREATED);
        task2.setBiDirectional(false);
        task2.setSource(entity1.getKey());
        task2.setTarget(entity2.getKey());

        List<GraphLink> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        controller.link(tasks);

        // Retrieve Linkages
        List<GraphLink> links = controller.retrieveLinkages(entity1.getKey());

        // Verify
        Assert.assertEquals(2, links.size());
        Assert.assertEquals(entity1.getKey(), links.get(0).getSource());
        Assert.assertEquals(entity2.getKey(), links.get(0).getTarget());
        Assert.assertEquals(ELinkTypes.IS_PARENT_OF, links.get(0).getType());
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, links.get(0).getStatus());

        Assert.assertEquals(entity1.getKey(), links.get(1).getSource());
        Assert.assertEquals(entity2.getKey(), links.get(1).getTarget());
        Assert.assertEquals(ELinkTypes.CREATED, links.get(1).getType());
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, links.get(1).getStatus());

        // Retrieve Linkages
        links = controller.retrieveLinkages(entity2.getKey());

        // Verify
        Assert.assertEquals(1, links.size());
        Assert.assertEquals(entity2.getKey(), links.get(0).getSource());
        Assert.assertEquals(entity1.getKey(), links.get(0).getTarget());
        Assert.assertEquals(ELinkTypes.IS_CHILD_OF, links.get(0).getType());
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, links.get(0).getStatus());

        controller.unlink(tasks);

        // Retrieve Linkages
        links = controller.retrieveLinkages(entity1.getKey());

        // Verify
        Assert.assertEquals(2, links.size());
        Assert.assertEquals(ECoalesceObjectStatus.DELETED, links.get(0).getStatus());
        Assert.assertEquals(ECoalesceObjectStatus.DELETED, links.get(1).getStatus());

        // Retrieve Linkages
        links = controller.retrieveLinkages(entity2.getKey());

        // Verify
        Assert.assertEquals(1, links.size());
        Assert.assertEquals(ECoalesceObjectStatus.DELETED, links.get(0).getStatus());
    }
}
