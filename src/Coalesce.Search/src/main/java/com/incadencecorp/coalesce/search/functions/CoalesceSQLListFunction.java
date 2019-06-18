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

package com.incadencecorp.coalesce.search.functions;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.util.Converters;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;

import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.search.api.ESearchModifiers;
import com.incadencecorp.coalesce.search.api.ESearchTypes;
import com.incadencecorp.coalesce.search.api.ICoalesceExpressionVistor;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchFunction;

/**
 * This function generates SQL to be used in filters.
 * 
 * @author n78554
 */
public final class CoalesceSQLListFunction implements ICoalesceSearchFunction {

    /**
     * The {@link FunctionName} object for the function.
     */
    public static final FunctionName NAME = new FunctionNameImpl("coalesceListSearch",
                                                                 Boolean.class,
                                                                 FunctionNameImpl.parameter("property", String.class),
                                                                 FunctionNameImpl.parameter("searchtype", String.class),
                                                                 FunctionNameImpl.parameter("searchmodifier", String.class),
                                                                 FunctionNameImpl.parameter("value1", Object.class),
                                                                 FunctionNameImpl.parameter("value2", Object.class));

    private final List<Expression> parameters;
    private final Literal fallback;

    /**
     * Default constructor should not be called.
     */
    public CoalesceSQLListFunction()
    {
        throw new NullPointerException("parameters required");
    }

    /**
     * Constructor accepting parameters and fallback.
     * <p>
     * Parameters must be:
     * <ul>
     * <li>recordsettablename: String
     * <li>tablename: String
     * <li>fieldname: String
     * <li>searchtype: String ({@link ESearchTypes})
     * <li>searchmodifier: String ({@link ESearchModifiers})
     * <li>value1: Object
     * <li>value2: Object
     * </ul>
     *
     * @param parameters
     * @param fallback
     */
    public CoalesceSQLListFunction(List<Expression> parameters, Literal fallback)
    {
        if (parameters == null)
        {
            throw new NullPointerException("parameters required");
        }
        if (parameters.size() != 6 && parameters.size() != 7)
        {
            throw new IllegalArgumentException(NAME.getName() + "(" + StringUtils.join(NAME.getArgumentNames(), ",")
                    + ") requires 6 or 7 parameters only");
        }
        this.parameters = parameters;
        this.fallback = fallback;
    }

    /**
     * This function is never called nor does it make sense to call it in our
     * implementation. The intent is to evaluate the function with literal
     * values without making a call to the database. An example of this behavior
     * is {@link org.geotools.filter.function.FilterFunction_lessThan}. This
     * function takes literal values and returns the result of a &lt; b. Our
     * function requires data from the database so this is not possible.
     * (non-Javadoc)
     *
     * @see org.opengis.filter.expression.Expression#evaluate(java.lang.Object)
     */
    @Override
    public Object evaluate(Object object)
    {
        return evaluate(object, Boolean.class);
    }

    @Override
    public <T> T evaluate(Object object, Class<T> context)
    {
        return Converters.convert(Boolean.TRUE, context);
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData)
    {
        return visitor.visit(this, extraData);
    }

    @Override
    public String getName()
    {
        return NAME.getName();
    }

    @Override
    public FunctionName getFunctionName()
    {
        return NAME;
    }

    @Override
    public List<Expression> getParameters()
    {
        return parameters;
    }

    @Override
    public Literal getFallbackValue()
    {
        return fallback;
    }

    @Override
    public void write(List<?> contexts, ICoalesceExpressionVistor vistor, Writer out, String databaseSchema)
            throws IOException
    {
        FilterFactory ff = vistor.getFilterFactory();

        // Get Parameters
        String recordsetTableName = vistor.normalize(parameters.get(0).toString().split("[.]")[0]);
        ECoalesceFieldDataTypes type = vistor.getDataType(ff.property(parameters.get(0).toString()));
        ESearchTypes searchTypeEnum = ESearchTypes.valueOf(parameters.get(1).toString());
        ESearchModifiers searchModifierEnum = ESearchModifiers.valueOf(parameters.get(2).toString());

        // Get expressions
        Expression fieldName = ff.literal(vistor.normalize(parameters.get(0).toString().split("[.]")[1]));
        Expression value1 = parameters.get(3);
        Class<?> context = vistor.getContext(ff.property(parameters.get(0).toString()));

        Expression value2 = null;
        if (parameters.size() == 5)
        {
            value2 = parameters.get(4);
        }

        // Build query
        out.write(databaseSchema + "." + recordsetTableName + ".objectkey IN(SELECT entitykey FROM " + databaseSchema + "."
                + type.getLabel() + " WHERE fieldname = ");

        fieldName.accept(vistor, String.class);

        out.write(" AND ");

        // /
        if (searchTypeEnum == ESearchTypes.BETWEEN)
        {
            out.write("fieldvalue ");

            if (searchModifierEnum == ESearchModifiers.ALL)
            {
                out.write("NOT ");
            }

            out.write("BETWEEN ");

            value1.accept(vistor, context);

            out.write(" AND ");

            value2.accept(vistor, context);
        }
        else
        {
            if (searchModifierEnum == ESearchModifiers.ALL)
            {
                out.write("NOT ");
            }

            out.write("fieldvalue ");

            String operator;
            switch (searchTypeEnum) {
            case CONTAINS:
                operator = "= ";
                break;
            case CONTAINS_LIKE:
                operator = "LIKE ";
                break;
            case GREATER_THAN:
                operator = "> ";
                break;
            case GREATER_THAN_OR_EQUAL:
                operator = ">= ";
                break;
            case LESS_THAN:
                operator = "< ";
                break;
            case LESS_THAN_OR_EQUAL:
                operator = "<= ";
                break;
            case BETWEEN:
            default:
                throw new IllegalArgumentException("Invalid search type value.");
            }

            out.write(operator);

            value1.accept(vistor, context);
        }

        out.write(")");
    }
}
