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

import static org.junit.Assert.*;

import org.junit.Test;

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
        
        System.out.println(entity.toXml());

    }

    private void assertCreateHistory(CoalesceObjectHistory object)
    {

        object.createHistory("Derek", "127.0.0.1", null);

        object.setStatus(ECoalesceObjectStatus.DELETED);
        object.setAttribute("derek", "hello world");

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

}
