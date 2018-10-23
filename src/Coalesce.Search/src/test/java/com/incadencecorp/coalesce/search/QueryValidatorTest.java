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

package com.incadencecorp.coalesce.search;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.framework.DefaultNormalizer;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.jobs.QueryValidator;
import org.geotools.data.Query;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

import java.util.Set;
import java.util.UUID;

/**
 * These test ensure that the template names are successfully derived from the {@link Query}
 *
 * @author Derek Clemenzi
 */
public class QueryValidatorTest {

    private static final ICoalesceNormalizer NORMALIZER = new DefaultNormalizer();
    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();
    private static final QueryValidator CHECKER = new QueryValidator();

    /**
     * Registers templates that will be used by these tests.
     */
    @BeforeClass
    public static void initialize() throws Exception
    {
        CoalesceTemplateUtil.addTemplates(CoalesceEntityTemplate.create(new TestEntity()));
    }

    /**
     * This test ensures that an empty query will return no templates.
     */
    @Test
    public void testEmptyQuery() throws Exception
    {
        Query query = new Query();

        // Verify empty query
        Set<String> results = CHECKER.getTemplateNames(query);
        Assert.assertEquals(0, results.size());
    }

    /**
     * This test ensures that the templates names can be extracted from the submitted properties.
     */
    @Test
    public void testProperties() throws Exception
    {
        Query query = new Query();
        query.setPropertyNames(new String[] { TestEntity.RECORDSET1 + CoalescePropertyFactory.SEPERATOR + "test1",
                                              TestEntity.RECORDSET1 + CoalescePropertyFactory.SEPERATOR + "test2"
        });

        Set<String> results = CHECKER.getTemplateNames(query);
        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.contains(NORMALIZER.normalize(TestEntity.NAME)));
    }

    /**
     * This test ensures that the templates names can be extracted from the submitted filter.
     */
    @Test
    public void testFilter() throws Exception
    {
        Query query = new Query();
        query.setFilter(CoalescePropertyFactory.getLinkageEntityKey(UUID.randomUUID().toString()));

        Set<String> results = CHECKER.getTemplateNames(query);
        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.contains(NORMALIZER.normalize(CoalescePropertyFactory.COALESCE_LINKAGE_TABLE)));
    }

    /**
     * This test ensures that the templates names can be extracted from the submitted sort by.
     */
    @Test
    public void testSortBy() throws Exception
    {
        Query query = new Query();
        query.setSortBy(new SortBy[] {
                FF.sort(FF.property(TestEntity.RECORDSET1 + CoalescePropertyFactory.SEPERATOR + "test1").getPropertyName(),
                        SortOrder.ASCENDING)
        });

        Set<String> results = CHECKER.getTemplateNames(query);
        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.contains(NORMALIZER.normalize(TestEntity.NAME)));
    }

    /**
     * This test ensures that the templates names can be extracted from the submitted type name.
     */
    @Test
    public void testTypeName() throws Exception
    {
        Query query = new Query();
        query.setTypeName("HELLO WORLD");

        Set<String> results = CHECKER.getTemplateNames(query);
        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.contains(NORMALIZER.normalize("HELLO WORLD")));
    }

    /**
     * This test ensures that properties from the filter, property to return, and the sort by are evaluated.
     */
    @Test
    public void testMultiple() throws Exception
    {
        Query query = new Query();
        query.setPropertyNames(new String[] { TestEntity.RECORDSET1 + CoalescePropertyFactory.SEPERATOR + "test1",
                                              TestEntity.RECORDSET1 + CoalescePropertyFactory.SEPERATOR + "test2"
        });
        query.setFilter(CoalescePropertyFactory.getLinkageEntityKey(UUID.randomUUID().toString()));

        Set<String> results = CHECKER.getTemplateNames(query);
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(NORMALIZER.normalize(CoalescePropertyFactory.COALESCE_LINKAGE_TABLE)));
        Assert.assertTrue(results.contains(NORMALIZER.normalize(TestEntity.NAME)));
    }

}
