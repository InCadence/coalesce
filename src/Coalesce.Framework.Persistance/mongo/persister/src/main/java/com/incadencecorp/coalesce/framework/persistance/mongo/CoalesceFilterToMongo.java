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

package com.incadencecorp.coalesce.framework.persistance.mongo;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.geotools.data.mongodb.CollectionMapper;
import org.geotools.data.mongodb.FilterToMongo;
import org.geotools.data.mongodb.GeoJSONMapper;
import org.geotools.data.mongodb.MongoGeometryBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.BinaryTemporalOperator;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;

/**
 * Extends the {@link FilterToMongo} implementation adding temporal support and normalization of Coalesce property names.
 *
 * @author Derek Clemenzi
 */
public class CoalesceFilterToMongo extends FilterToMongo {

    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();
    private SimpleFeatureType featureType;

    public CoalesceFilterToMongo()
    {
        super(new GeoJSONMapper());
    }

    public CoalesceFilterToMongo(CollectionMapper mapper)
    {
        super(mapper);
    }

    public CoalesceFilterToMongo(CollectionMapper mapper, MongoGeometryBuilder geometryBuilder)
    {
        super(mapper, geometryBuilder);
    }

    @Override
    public void setFeatureType(SimpleFeatureType featureType)
    {
        super.setFeatureType(featureType);

        this.featureType = featureType;
    }

    /**
     * Super class assumed GeoJson was always stored in the geometry field. Need to override this behaviour.
     */
    @Override
    public Object visit(PropertyName expression, Object extraData) throws RuntimeException
    {
        return MongoConstants.normalize(expression);
    }

    @Override
    public Object visit(After after, Object extraData)
    {
        return this.visitBinaryTemporalOperator(after, extraData);
    }

    @Override
    public Object visit(Before before, Object extraData)
    {
        return this.visitBinaryTemporalOperator(before, extraData);
    }

    @Override
    public Object visit(Begins begins, Object extraData)
    {
        return this.visitBinaryTemporalOperator(begins, extraData);
    }

    @Override
    public Object visit(BegunBy begunBy, Object extraData)
    {
        return this.visitBinaryTemporalOperator(begunBy, extraData);
    }

    @Override
    public Object visit(During during, Object extraData)
    {
        return this.visitBinaryTemporalOperator(during, extraData);
    }

    @Override
    public Object visit(EndedBy endedBy, Object extraData)
    {
        return this.visitBinaryTemporalOperator(endedBy, extraData);
    }

    @Override
    public Object visit(Ends ends, Object extraData)
    {
        return this.visitBinaryTemporalOperator(ends, extraData);
    }

    @Override
    public Object visit(Meets meets, Object extraData)
    {
        return this.visitBinaryTemporalOperator(meets, extraData);
    }

    @Override
    public Object visit(MetBy metBy, Object extraData)
    {
        return this.visitBinaryTemporalOperator(metBy, extraData);
    }

    private Object visitBinaryTemporalOperator(BinaryTemporalOperator filter, Object extraData)
    {
        if (filter == null)
        {
            throw new NullPointerException("Null filter");
        }

        Expression e1 = filter.getExpression1();
        Expression e2 = filter.getExpression2();

        if (e1 instanceof Literal && e2 instanceof PropertyName)
        {
            e1 = filter.getExpression2();
            e2 = filter.getExpression1();
        }

        return this.visitBinaryTemporalOperator(filter, (PropertyName) e1, (Literal) e2, extraData);
    }

    private Object visitBinaryTemporalOperator(BinaryTemporalOperator filter,
                                               PropertyName property,
                                               Literal temporal,
                                               Object extraData)
    {
        BasicDBObject output = this.asDBObject(extraData);

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
        else
        {
            String prop = (String) property.accept(this, extraData);

            if (filter instanceof TEquals)
            {
                output.put(prop, new BasicDBObject("$eq", temporal.accept(this, typeContext)));
            }
            else if (filter instanceof During || filter instanceof TContains)
            {
                Object start = this.visitInstant(period.getBeginning(), extraData);
                Object end = this.visitInstant(period.getEnding(), extraData);

                BasicDBList items = new BasicDBList();
                items.add(new BasicDBObject(prop, new BasicDBObject("$gte", start)));
                items.add(new BasicDBObject(prop, new BasicDBObject("$lte", end)));

                output.put("$and", items);
            }
            else if (filter instanceof Begins || filter instanceof BegunBy)
            {
                output.put(prop, new BasicDBObject("$eq", this.visitInstant(period.getBeginning(), extraData)));
            }
            else if (filter instanceof Ends || filter instanceof EndedBy)
            {
                output.put(prop, new BasicDBObject("$eq", this.visitInstant(period.getEnding(), extraData)));
            }
            else if (filter instanceof After)
            {
                output.put(prop, new BasicDBObject("$gt", temporal.accept(this, typeContext)));
            }
            else if (filter instanceof Before)
            {
                output.put(prop, new BasicDBObject("$lt", temporal.accept(this, typeContext)));
            }
        }

        return output;
    }

    private Object visitInstant(Instant instant, Object extraData)
    {
        return JodaDateTimeHelper.parseDateTime((String) FF.literal(instant.getPosition().getDate()).accept(this,
                                                                                                            extraData)).toDate();
    }

}
