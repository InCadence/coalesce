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

package com.incadencecorp.coalesce.framework.util.tests;

import com.incadencecorp.coalesce.framework.compareables.CoalesceFieldComparator;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.util.CoalesceCompressionUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * These test are for {@link CoalesceCompressionUtil}
 *
 * @author Derek Clemenzi
 */
public class CoalesceCompressionUtilTest {

    /**
     * This test ensures that a compressed entity can be decompressed.
     */
    @Test
    public void testCompression() throws Exception
    {
        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();
        entity.addRecord1().getStringField().setValue("Hello World");

        CoalesceFieldComparator comparator = new CoalesceFieldComparator(entity.addRecord1().getStringField().getNamePath());

        // Compress
        byte[] bytes = CoalesceCompressionUtil.compress(entity);

        Assert.assertTrue(bytes.length < entity.toXml().length());

        CoalesceEntity entity2 = CoalesceCompressionUtil.decompress(bytes, CoalesceEntity.class);

        Assert.assertEquals(0, comparator.compare(entity, entity2));
        Assert.assertEquals(entity.toXml(), entity2.toXml());
    }

}
