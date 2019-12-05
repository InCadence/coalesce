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

package com.incadencecorp.coalesce.search.filter.tests;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.search.filter.FilterHelper;
import com.incadencecorp.coalesce.search.filter.FilterUtil;
import com.incadencecorp.coalesce.search.filter.FilterUtil.EConfiguration;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.visitor.AbstractFilterVisitor;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.temporal.object.DefaultInstant;
import org.geotools.temporal.object.DefaultPosition;
import org.geotools.util.factory.Hints;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.During;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Test the OGC utility.
 *
 * @author n78554
 */
public class FilterUtilIT {

    private static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2(new Hints(Hints.CRS,
                                                                                             DefaultGeographicCRS.WGS84));

    private static final GeometryBuilder GB = new GeometryBuilder(DefaultGeographicCRS.WGS84);

    private static final Expression PROP = FF.property("Derek");

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterUtilIT.class);

    /**
     * Tests parsing and serializing After and Before filters.
     *
     * @throws Exception
     */
    @Test
    public void testDuringFilters() throws Exception
    {

        boolean isFailure = false;

        DateTime start = JodaDateTimeHelper.nowInUtc().minusMinutes(1);
        DateTime end = JodaDateTimeHelper.nowInUtc();

        // Create Filters
        Filter original = FF.during(PROP, FF.literal(FilterHelper.createTimePeriod(start, end)));

        // Verify
        Assert.assertTrue((boolean) original.accept(new AssertTemporialFilterVistor(), null));

        LOGGER.warn("Temperial During Test");

        for (EConfiguration version : EConfiguration.values())
        {

            // Serialize to XML and Back
            Filter processed = FilterUtil.fromXml(FilterUtil.toXml(version, original));

            if (processed != null && (boolean) processed.accept(new AssertTemporialFilterVistor(), null))
            {
                LOGGER.warn("\t(SUCCESS) {}", version);
            }
            else
            {
                LOGGER.error("\t(FAILED) {}", version);

                isFailure = true;
            }
        }

        Assert.assertFalse(isFailure);

    }

    /**
     * Tests parsing and serializing After and Before filters.
     *
     * @throws Exception
     */
    @Test
    public void testAfterBeforeFilters() throws Exception
    {

        boolean isFailure = false;

        Date date = JodaDateTimeHelper.nowInUtcAsDate();

        // Create Filters
        Filter after = FF.after(PROP, FF.literal(new DefaultInstant(new DefaultPosition(date))));
        Filter before = FF.after(PROP, FF.literal(new DefaultInstant(new DefaultPosition(date))));

        // Verify
        Assert.assertTrue((boolean) after.accept(new AssertTemporialFilterVistor(), null));
        Assert.assertTrue((boolean) before.accept(new AssertTemporialFilterVistor(), null));

        LOGGER.warn("Temperial After Test");

        for (EConfiguration version : EConfiguration.values())
        {

            // Serialize to XML and Back
            Filter processed = FilterUtil.fromXml(FilterUtil.toXml(version, after));

            if (processed != null && (boolean) processed.accept(new AssertTemporialFilterVistor(), null))
            {
                LOGGER.warn("\t(SUCCESS) {}", version);
            }
            else
            {
                LOGGER.error("\t(FAILED) {}", version);

                isFailure = true;
            }
        }

        LOGGER.warn("Temperial Before Test");

        for (EConfiguration version : EConfiguration.values())
        {

            // Serialize to XML and Back
            Filter processed = FilterUtil.fromXml(FilterUtil.toXml(version, before));

            if (processed != null && (boolean) processed.accept(new AssertTemporialFilterVistor(), null))
            {
                LOGGER.warn("\t(SUCCESS) {}", version);
            }
            else
            {
                LOGGER.error("\t(FAILED) {}", version);

                isFailure = true;
            }
        }

        Assert.assertFalse(isFailure);

    }

    private static class AssertTemporialFilterVistor extends AbstractFilterVisitor {

        @Override
        public Object visit(During during, Object extraData)
        {

            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Expression 1: {}", during.getExpression1());
                LOGGER.trace("Expression 2: {}", during.getExpression2());
            }

            return true;
        }

        @Override
        public Object visit(After after, Object extraData)
        {

            boolean isValid = false;

            if (after.getExpression1() != null && after.getExpression2() != null)
            {

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Expression 1: {}", after.getExpression1());
                    LOGGER.trace("Expression 2: {}", after.getExpression2());
                }

                isValid = after.getExpression1().toString().contains("Derek") && after.getExpression2().toString().contains(
                        "Instant");
            }

            return isValid;
        }

        @Override
        public Object visit(Before before, Object extraData)
        {

            boolean isValid = false;

            if (before.getExpression1() != null && before.getExpression2() != null)
            {

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Expression 1: {}", before.getExpression1());
                    LOGGER.trace("Expression 2: {}", before.getExpression2());
                }

                isValid =
                        before.getExpression1().toString().contains("Derek") && before.getExpression2().toString().contains(
                                "Instant");
            }

            return isValid;
        }

    }

    /**
     * Tests parsing and serializing bbox filters
     *
     * @throws Exception
     */
    @Test
    public void testBoundingBox() throws Exception
    {
        String EPSG4326 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
        CoordinateReferenceSystem coordinateReferenceSystem = CRS.parseWKT(EPSG4326);

        ReferencedEnvelope bbox = new ReferencedEnvelope(0, 0, 1, 1, coordinateReferenceSystem);

        Filter original = FF.bbox(FF.property("Derek"), bbox);

        boolean isFailure = false;

        for (EConfiguration version : EConfiguration.values())
        {
            try
            {
                String xml = FilterUtil.toXml(version, original);
                // Serialize to XML and Back
                Filter processed = FilterUtil.fromXml(xml);

                if ((boolean) processed.accept(new AssertContainsFilterVistor(), null))
                {
                    LOGGER.warn("\t(SUCCESS) {}", version);
                }
                else
                {
                    LOGGER.error("\t(FAILED) {}", version);

                    isFailure = true;
                }

            }
            catch (Exception e)
            {
                LOGGER.warn("(FAILED) {}: {}", version, e.getMessage());
                isFailure = true;
            }
        }

        Assert.assertFalse(isFailure);
    }

    /**
     * Tests parsing and serializing After and Before filters.
     *
     * @throws Exception
     */
    @Test
    public void testGeospatialFilter() throws Exception
    {

        boolean isFailure = false;

        // Create Filter
        Filter original = FF.contains("Derek", GB.createPoint(0, 0));

        // Verify
        Assert.assertTrue((boolean) original.accept(new AssertContainsFilterVistor(), null));

        LOGGER.warn("Geospatial Test");

        for (EConfiguration version : EConfiguration.values())
        {

            // Serialize to XML and Back
            Filter processed = FilterUtil.fromXml(FilterUtil.toXml(version, original));

            if ((boolean) processed.accept(new AssertContainsFilterVistor(), null))
            {
                LOGGER.warn("\t(SUCCESS) {}", version);
            }
            else
            {
                LOGGER.error("\t(FAILED) {}", version);

                isFailure = true;
            }
        }

        Assert.assertFalse(isFailure);

    }

    private static class AssertContainsFilterVistor extends AbstractFilterVisitor {

        @Override
        public Object visit(BBOX filter, Object data)
        {
            boolean isValid = false;

            if (filter.getExpression1() != null && filter.getExpression2() != null)
            {

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Expression 1: {}", filter.getExpression1());
                    LOGGER.trace("Expression 2: {}", filter.getExpression2());
                }

                isValid =
                        filter.getExpression1().toString().contains("Derek") && filter.getExpression2().toString().contains(
                                "POLYGON ((0 1, 0 1, 0 1, 0 1, 0 1))");
            }

            return isValid;// super.visit(filter, data);
        }

        @Override
        public Object visit(Contains filter, Object data)
        {

            boolean isValid = false;

            if (filter.getExpression1() != null && filter.getExpression2() != null)
            {

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Expression 1: {}", filter.getExpression1());
                    LOGGER.trace("Expression 2: {}", filter.getExpression2());
                }

                isValid =
                        filter.getExpression1().toString().contains("Derek") && filter.getExpression2().toString().contains(
                                "Point(0.0 0.0)");
            }

            return isValid;// super.visit(filter, data);
        }

    }

    /**
     * Case Insensitive Tests
     */

    /**
     * Tests parsing and serializing After and Before filters.
     *
     * @throws Exception
     */
    @Test
    public void testCaseInsensitiveFilters() throws Exception
    {

        boolean isFailure = false;

        // Create Filter
        Filter originalTrue = FF.equal(PROP, FF.literal("hello"), true);
        Filter originalFalse = FF.equal(PROP, FF.literal("hello"), false);

        // Verify
        originalTrue.accept(new AssertIsMactingCaseFilterVistor(), true);
        originalFalse.accept(new AssertIsMactingCaseFilterVistor(), false);

        LOGGER.warn("Case Insensitive Test");

        for (EConfiguration version : EConfiguration.values())
        {

            // Serialize to XML and Back
            Filter processedTrue = FilterUtil.fromXml(FilterUtil.toXml(version, originalTrue));

            Filter processedFalse = FilterUtil.fromXml(FilterUtil.toXml(version, originalFalse));

            // Verify
            if ((boolean) processedTrue.accept(new AssertIsMactingCaseFilterVistor(), null)
                    && !(boolean) processedFalse.accept(new AssertIsMactingCaseFilterVistor(), null))
            {
                LOGGER.warn("\t(SUCCESS) {}", version);
            }
            else
            {
                LOGGER.error("\t(FAILED) {}", version);

                isFailure = true;
            }

        }

        Assert.assertFalse(isFailure);

    }

    private static class AssertIsMactingCaseFilterVistor extends AbstractFilterVisitor {

        @Override
        public Object visit(PropertyIsEqualTo filter, Object extraData)
        {
            return filter.isMatchingCase();
        }

    }

}
