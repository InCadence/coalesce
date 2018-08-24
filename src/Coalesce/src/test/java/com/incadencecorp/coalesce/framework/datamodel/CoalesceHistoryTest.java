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

package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * These unit test ensures that each element retains history.
 *
 * @author n78554
 */
public class CoalesceHistoryTest {

    @Test
    public void testEntity() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        assertCreateHistory(entity);

        assertCreateHistory(entity.getSection("/" + TestEntity.TESTSECTION));

        assertCreateHistory(entity.getRecordset1());

        assertCreateHistory(entity.getRecordset1().addNew());

        assertCreateHistory(entity.getLinkageSection());

    }

    private void assertCreateHistory(CoalesceObjectHistory object)
    {

        object.createHistory("Derek", "127.0.0.1", null);

        object.setStatus(ECoalesceObjectStatus.DELETED);
        object.setAttribute("derek", "hello world");

        object.setSuspendHistory(false);
        object.createHistory("Derek1", "127.0.0.1", null);

        object.setStatus(ECoalesceObjectStatus.READONLY);

        assertEquals(2, object.getHistory().length);
        assertEquals(ECoalesceObjectStatus.READONLY, object.getStatus());
        assertEquals("Derek1", object.getModifiedBy());
        assertEquals("127.0.0.1", object.getModifiedByIP());
        assertEquals("hello world", object.getAttribute("derek"));

        CoalesceHistory history = object.getHistoryRecord(object.getPreviousHistoryKey());

        assertEquals(ECoalesceObjectStatus.DELETED, history.getStatus());
        assertEquals("Derek", history.getModifiedBy());
        assertEquals("127.0.0.1", history.getModifiedByIP());
        assertEquals("hello world", history.getAttribute("derek"));

        history = object.getHistoryRecord(history.getPreviousHistoryKey());

        assertEquals(ECoalesceObjectStatus.ACTIVE, history.getStatus());
        assertEquals("", history.getModifiedBy());
        assertEquals("", history.getModifiedByIP());
        assertEquals("00000000-0000-0000-0000-000000000000", history.getPreviousHistoryKey());
        assertEquals(null, history.getAttribute("derek"));

    }

    /**
     * This test ensures that history is not created when calling the same link method unless something changes.
     */
    @Test
    public void testLinkageHistory() throws Exception
    {
        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize();

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        EntityLinkHelper.linkEntitiesUniDirectional(entity1, ELinkTypes.IS_PARENT_OF, entity2);

        Assert.assertEquals(0, entity1.getLinkageSection().getLinkagesAsList().get(0).getHistory().length);

        EntityLinkHelper.linkEntitiesUniDirectional(entity1, ELinkTypes.IS_PARENT_OF, entity2);

        Assert.assertEquals(0, entity1.getLinkageSection().getLinkagesAsList().get(0).getHistory().length);

        EntityLinkHelper.linkEntitiesUniDirectional(entity1, ELinkTypes.IS_PARENT_OF, entity2, "my label", true);

        Assert.assertEquals(1, entity1.getLinkageSection().getLinkagesAsList().get(0).getHistory().length);

        CoalesceEntity entity3 = CoalesceEntity.create(entity1.toXml());

        entity3.getLinkageSection().getLinkagesAsList().get(0).setLinkType(ELinkTypes.CREATED);

        Assert.assertEquals(2, entity3.getLinkageSection().getLinkagesAsList().get(0).getHistory().length);
    }

}
