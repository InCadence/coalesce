/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;

/**
 * These test ensure proper SQL creation from filters.
 * 
 * @author n78554
 */
public class ElasticSearchFilterTest {

    private static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2(new Hints(Hints.CRS,
                                                                                             DefaultGeographicCRS.WGS84));

    /**
     * This test ensures that before and less generate the same SQL.
     * 
     * @throws Exception
     */
    @Test
    public void testBeforeLess() throws Exception
    {

        Filter before = FF.before(FF.property("prop"),
                                  FF.literal(JodaDateTimeHelper.toPostGestSQLDateTime(JodaDateTimeHelper.nowInUtc())));

        Filter less = FF.less(FF.property("prop"),
                              FF.literal(JodaDateTimeHelper.toPostGestSQLDateTime(JodaDateTimeHelper.nowInUtc())));

        PostgisPSFilterToSql fitlerToSql = new PostgisPSFilterToSql(null);

        Assert.assertEquals(fitlerToSql.encodeToString(less), fitlerToSql.encodeToString(before));

    }

    /**
     * This test ensures that after and greater generate the same SQL.
     * 
     * @throws Exception
     */
    @Test
    public void testAfterGreater() throws Exception
    {

        Filter after = FF.after(FF.property("prop"),
                                FF.literal(JodaDateTimeHelper.toPostGestSQLDateTime(JodaDateTimeHelper.nowInUtc())));

        Filter greater = FF.greater(FF.property("prop"),
                                    FF.literal(JodaDateTimeHelper.toPostGestSQLDateTime(JodaDateTimeHelper.nowInUtc())));

        PostgisPSFilterToSql fitlerToSql = new PostgisPSFilterToSql(null);

        Assert.assertEquals(fitlerToSql.encodeToString(greater), fitlerToSql.encodeToString(after));

    }

}
