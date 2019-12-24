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

package com.incadencecorp.coalesce.framework.persistance.cosmos;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.search.filter.FilterHelper;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.data.postgis.PostGISPSDialect;
import org.geotools.data.postgis.PostgisPSFilterToSql;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.util.Converters;
import org.joda.time.DateTime;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.BinaryTemporalOperator;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.temporal.Period;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Derek Clemenzi
 */
public class CosmosFilterToSql extends PostgisPSFilterToSql {

    private final ICoalesceNormalizer normalizer;
    private final GeometryJSON geojson = new GeometryJSON();

    public CosmosFilterToSql(ICoalesceNormalizer normalizer, PostGISPSDialect dialect)
    {
        super(dialect);

        this.normalizer = normalizer;
    }

    @Override
    protected Object visitBinarySpatialOperator(BinarySpatialOperator filter,
                                                PropertyName property,
                                                Literal geometry,
                                                boolean swapped,
                                                Object extraData)
    {
        try
        {
            String closingParenthesis = ")";
            if (filter instanceof Equals)
            {
                this.out.write("ST_Equals");
            }
            else if (filter instanceof Disjoint)
            {
                this.out.write("NOT (ST_Intersects");
                closingParenthesis = closingParenthesis + ")";
            }
            else if (!(filter instanceof Intersects) && !(filter instanceof BBOX))
            {
                if (filter instanceof Crosses)
                {
                    this.out.write("ST_Crosses");
                }
                else if (filter instanceof Within)
                {
                    if (swapped)
                    {
                        this.out.write("ST_Contains");
                    }
                    else
                    {
                        this.out.write("ST_Within");
                    }
                }
                else if (filter instanceof Contains)
                {
                    if (swapped)
                    {
                        this.out.write("ST_Within");
                    }
                    else
                    {
                        this.out.write("ST_Contains");
                    }
                }
                else if (filter instanceof Overlaps)
                {
                    this.out.write("ST_Overlaps");
                }
                else
                {
                    if (!(filter instanceof Touches))
                    {
                        throw new RuntimeException("Unsupported filter type " + filter.getClass());
                    }

                    this.out.write("ST_Touches");
                }
            }
            else
            {
                //this.out.write("ST_Intersects");
                this.out.write("ST_Within");
            }

            this.out.write("(");
            property.accept(this, extraData);
            this.out.write(", ");
            geometry.accept(this, extraData);
            this.out.write(closingParenthesis);

            return extraData;
        }
        catch (IOException e)
        {
            throw new RuntimeException("io problem writing filter", e);
        }

    }

    @Override
    protected void visitLiteralGeometry(Literal expression) throws IOException
    {
        // evaluate the literal and store it for later
        Geometry geometry = (Geometry) evaluateLiteral(expression, Geometry.class);

        if (geometry instanceof LinearRing)
        {
            // convert LinearRing to LineString
            final GeometryFactory factory = geometry.getFactory();
            final LinearRing linearRing = (LinearRing) geometry;
            final CoordinateSequence coordinates;
            coordinates = linearRing.getCoordinateSequence();
            geometry = factory.createLineString(coordinates);
        }
        else if (geometry instanceof Polygon)
        {
            geometry = FilterHelper.sortVertices((Polygon) geometry, false);
        }

        geojson.write(geometry, this.out);
    }

    @Override
    public Object visit(PropertyName expression, Object extraData) throws RuntimeException
    {
        return super.visit(filterFactory.property(getNormalizedPropertyName(expression)), extraData);
    }

    @Override
    public Object visit(PropertyIsBetween filter, Object extraData) throws RuntimeException
    {
        LOGGER.finer("exporting PropertyIsBetween");
        Expression expr = filter.getExpression();
        Expression lowerbounds = filter.getLowerBoundary();
        Expression upperbounds = filter.getUpperBoundary();
        AttributeDescriptor attType = (AttributeDescriptor) expr.evaluate(this.featureType);
        Class context;
        if (attType != null)
        {
            context = attType.getType().getBinding();
        }
        else
        {
            context = String.class;
        }

        try
        {
            /*
             * Between filter does not work with Azure. Therefore replace with >= amd <=.
             */
            expr.accept(this, extraData);
            this.out.write(" >= ");
            lowerbounds.accept(this, context);
            this.out.write(" AND ");
            expr.accept(this, null);
            this.out.write(" <= ");
            upperbounds.accept(this, context);
            return extraData;
        }
        catch (IOException e)
        {
            throw new RuntimeException("io problem writing filter", e);
        }
    }

    @Override
    protected Object visitBinaryTemporalOperator(BinaryTemporalOperator filter,
                                                 PropertyName property,
                                                 Literal temporal,
                                                 boolean swapped,
                                                 Object extraData)
    {
        Class typeContext = null;
        AttributeDescriptor attType = (AttributeDescriptor) property.evaluate(this.featureType);
        if (attType != null)
        {
            typeContext = attType.getType().getBinding();
        }

        Period period = null;
        if (temporal.evaluate(null) instanceof Period)
        {
            period = (Period) temporal.evaluate(null);
        }

        if ((filter instanceof Begins || filter instanceof BegunBy || filter instanceof Ends || filter instanceof EndedBy
                || filter instanceof During || filter instanceof TContains) && period == null)
        {
            throw new IllegalArgumentException("Filter requires a time period");
        }
        else if (filter instanceof TEquals && period != null)
        {
            throw new IllegalArgumentException("TEquals filter does not accept time period");
        }
        else if ((filter instanceof Begins || filter instanceof Ends || filter instanceof During) && swapped)
        {
            throw new IllegalArgumentException("Time period must be second argument of Filter");
        }
        else if ((filter instanceof BegunBy || filter instanceof EndedBy || filter instanceof TContains) && !swapped)
        {
            throw new IllegalArgumentException("Time period must be first argument of Filter");
        }
        else
        {
            try
            {
                if (!(filter instanceof After) && !(filter instanceof Before))
                {
                    if (!(filter instanceof Begins) && !(filter instanceof Ends) && !(filter instanceof BegunBy)
                            && !(filter instanceof EndedBy))
                    {
                        if (!(filter instanceof During) && !(filter instanceof TContains))
                        {
                            if (filter instanceof TEquals)
                            {
                                property.accept(this, extraData);
                                this.out.write(" = ");
                                temporal.accept(this, typeContext);
                            }
                        }
                        else
                        {
                            /*
                             * Between filter does not work with Azure. Therefore replace with >= amd <=.
                             */
                            property.accept(this, extraData);
                            this.out.write(" >= ");
                            this.visitBegin(period, extraData);
                            this.out.write(" AND ");
                            property.accept(this, extraData);
                            this.out.write(" <= ");
                            this.visitEnd(period, extraData);
                        }
                    }
                    else
                    {
                        property.accept(this, extraData);
                        this.out.write(" = ");
                        if (!(filter instanceof Begins) && !(filter instanceof BegunBy))
                        {
                            this.visitEnd(period, extraData);
                        }
                        else
                        {
                            this.visitBegin(period, extraData);
                        }
                    }
                }
                else
                {
                    String op = filter instanceof After ? " > " : " < ";
                    String inv = filter instanceof After ? " < " : " > ";
                    if (period != null)
                    {
                        this.out.write("(");
                        property.accept(this, extraData);
                        this.out.write(swapped ? inv : op);
                        this.visitBegin(period, extraData);
                        this.out.write(" AND ");
                        property.accept(this, extraData);
                        this.out.write(swapped ? inv : op);
                        this.visitEnd(period, extraData);
                        this.out.write(")");
                    }
                    else
                    {
                        if (swapped)
                        {
                            temporal.accept(this, typeContext);
                        }
                        else
                        {
                            property.accept(this, extraData);
                        }

                        this.out.write(op);
                        if (swapped)
                        {
                            property.accept(this, extraData);
                        }
                        else
                        {
                            temporal.accept(this, typeContext);
                        }
                    }
                }

                return extraData;
            }
            catch (IOException var11)
            {
                throw new RuntimeException("Error encoding temporal filter", var11);
            }
        }
    }

    @Override
    protected void writeLiteral(Object literal) throws IOException
    {
        if (literal == null)
        {
            this.out.write("NULL");
        }
        else if (!(literal instanceof Number) && !(literal instanceof Boolean))
        {
            if (!(literal instanceof java.sql.Date) && !(literal instanceof Timestamp))
            {
                if (literal instanceof Date)
                {
                    this.out.write("'" + JodaDateTimeHelper.toXmlDateTimeUTC(new DateTime(literal)) + "'");
                }
                else
                {
                    String encoding = Converters.convert(literal, String.class, null);
                    if (encoding == null)
                    {
                        encoding = literal.toString();
                    }

                    String escaped = encoding.replaceAll("'", "''");
                    this.out.write("'" + escaped + "'");
                }
            }
            else
            {
                this.out.write("'" + literal + "'");
            }
        }
        else
        {
            this.out.write(String.valueOf(literal));
        }

    }

    private void visitBegin(Period p, Object extraData)
    {
        filterFactory.literal(p.getBeginning().getPosition().getDate()).accept(this, extraData);
    }

    private void visitEnd(Period p, Object extraData)
    {
        filterFactory.literal(p.getEnding().getPosition().getDate()).accept(this, extraData);
    }

    private String getNormalizedPropertyName(PropertyName name)
    {
        return getNormalizedPropertyName(name.getPropertyName());
    }

    private String getNormalizedPropertyName(String name)
    {
        String normalized;

        String[] parts = name.split("[/.]");

        if (parts.length == 1)
        {
            normalized = normalizer.normalize(parts[0]);
        }
        else if (parts.length == 2)
        {
            normalized = normalizer.normalize(parts[0], parts[1]);
        }
        else
        {
            normalized = name;
        }

        return "c." + normalized;
    }
}
