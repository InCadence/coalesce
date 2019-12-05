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

package com.incadencecorp.coalesce.framework.persistance.postgres;

import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.EFilterEnumerationModes;
import com.incadencecorp.coalesce.search.api.ICoalesceExpressionVistor;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.data.jdbc.FilterToSQL;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.data.postgis.PostGISPSDialect;
import org.geotools.data.postgis.PostgisPSFilterToSql;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.Capabilities;
import org.geotools.filter.FunctionImpl;
import org.geotools.jdbc.JDBCDataStore;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.DistanceBufferOperator;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class creates the SQL statements to perform structured searches against
 * PostGreSQL.
 *
 * @author n78554
 */
public class PostGresCoalescePreparedFilter extends PostgisPSFilterToSql implements ICoalesceExpressionVistor {

    private static final String ENTITY_KEY_COL_NAME = CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey());

    private static final String SQL_COLUMNS = "coalesceentity.objectkey AS " + ENTITY_KEY_COL_NAME + "%1$s";
    private static final String COMMA_SPACE = ", ";
    private static final String COALESCEENTITY = "coalesceentity";
    private static final String QUESTION_MARK = "?";
    private static final String ST_FROM_TEXT = "ST_GeomFromText(?, %s)";
    private static final String DOT = ".";
    private static final EFilterEnumerationModes MODE = EFilterEnumerationModes.ORDINAL;
    private static final int SSRID = PostGreSQLSettings.getSRID();
    private static final boolean USE_DISPLAY_NAME = false;

    /**
     * Param 1 = Enumeration Index Param 2 = Enumeration Type Param 3 =
     * Enumeration Field
     * <p>
     * TODO Enumeration joins need to be reworked.
     */
    private static final String SQL_ENUMVALUE_JOIN = " LEFT JOIN coalesce.enumvalue AS E%1$s ON E%1$s.enumtype=lower('%2$s') AND %3$s = E%1$s.ordering";

    private static final String SQL_ENUM_ASSOCIATED_JOIN = SQL_ENUMVALUE_JOIN
            + " LEFT JOIN coalesce.associatedenumvalue AS E%1$sA ON  E%1$sA.enumvalueuuid=E%1$s.uuid AND E%1$sA.valuedescriptor = 'name'";

    private static final String SQL_ENUMVALUE_PROPERTY_NAME = "enumvalue";

    private static final String SQL_ENUMASSOCIATED_PROPERTY_NAME = "associatedenumvalue";

    private static final Logger LOGGER = LoggerFactory.getLogger(PostGresCoalescePreparedFilter.class);

    private static PostGISPSDialect gisdialect;

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private final List<String> tableList = new ArrayList<>();
    private final List<String> enumList = new ArrayList<>();
    private FilterFactory factory;

    private int offset;
    private int pageSize;
    private Capabilities capability;

    private boolean ignoreSecurity;

    private List<String> propertyNameList;
    private List<SortBy> sortByList;

    private Object currentContext;
    private String currentProperty;

    // ----------------------------------------------------------------------//
    // Constructor
    // ----------------------------------------------------------------------//

    /**
     * Defaults to the first page containing 50 records.
     *
     * @param schema
     */
    public PostGresCoalescePreparedFilter(String schema)
    {
        this(schema, 1, 50, false, gisdialect);

    }

    /**
     * @param schema
     * @param offset
     * @param pageSize
     * @param includeDeleted
     * @param dialect
     */
    public PostGresCoalescePreparedFilter(String schema,
                                          int offset,
                                          int pageSize,
                                          boolean includeDeleted,
                                          PostGISPSDialect dialect)
    {
        super(dialect);

        setDatabaseSchema(schema);

        currentSRID = SSRID;
        SRIDs.add(currentSRID);

        setOffset(offset);
        setPageSize(pageSize);

        capability = createCapabilities();
        factory = CommonFactoryFinder.getFilterFactory(null);

    }

    // ----------------------------------------------------------------------//
    // Public Overrides
    // ----------------------------------------------------------------------//

    /**
     * Modified to create the complete SQL command instead of just the WHERE
     * clause.
     */
    @Override
    public String encodeToString(Filter filter) throws FilterToSQLException
    {

        String where = "";

        if (filter != null)
        {
            where = super.encodeToString(filter);
        }

        // Filter on security?
        // TODO Implement Security
        // if (!ignoreSecurity)
        // {
        //
        // if (!StringHelper.isNullOrEmpty(where))
        // {
        // where += " AND ";
        // }
        // else
        // {
        // where += " WHERE ";
        // }
        //
        // StringBuilder sb = new StringBuilder();
        //
        // for (EMasks mask : EMasks.values())
        // {
        //
        // if (sb.length() != 0)
        // {
        // sb.append(" AND ");
        // }
        //
        // sb.append(String.format("%1$s & ?::bit(%2$s) = %1$s",
        // mask.toString(), mask.getBitmaskSize()));
        // }
        //
        // where += "(" + sb.toString() +
        // " AND (scope <> 'USER' OR creator = ?))";
        //
        // }

        return where;

        // String columns = getColumns(postfix);
        // String from = getFrom();
        //
        // countSQL = "SELECT DISTINCT COUNT(*) FROM " + from + SPACE + where;
        //
        // return "SELECT DISTINCT " + columns + " FROM " + from + SPACE + where
        // + SPACE + getSorting() + SPACE + getLimit();
    }

    @Override
    public void encode(Filter filter) throws FilterToSQLException
    {

        if (out == null)
        {
            throw new FilterToSQLException("Can't encode to a null writer.");
        }

        // TODO Cannot add Temporal support to capabilities therefore this check has been commented out.
        if (true)//capability.fullySupports(filter))
        {
            try
            {
                if (!inline)
                {
                    out.write("WHERE ");
                }

                filter.accept(this, null);

            }
            catch (java.io.IOException ioe)
            {
                throw new FilterToSQLException("Problem writing filter: ", ioe);
            }
        }
        else
        {
            throw new FilterToSQLException("Filter type not supported");
        }
    }

    /**
     * Modified to create a list of tables to JOIN based on the property
     * prefixes.
     */
    @Override
    public Object visit(PropertyName property, Object arg1)
    {
        if (!property.getPropertyName().contains(SQL_ENUMVALUE_PROPERTY_NAME))
        {
            return super.visit(factory.property(normalize(property.getPropertyName())), arg1);
        }
        else
        {
            return super.visit(property, arg1);
        }
    }

    // TODO Implemented Functions
    // @Override
    // public Object visit(Function function, Object extraData) {
    // if (function instanceof OMEGASearchFunction) {
    // try {
    // List<Expression> parameters = function.getParameters();
    // List<?> contexts = null;
    // // check context, if a list which patches parameter size list assume its
    // context
    // // to pass along to each Expression for encoding
    // if (extraData instanceof List && ((List<?>) extraData).size() ==
    // parameters.size()) {
    // contexts = (List<?>) extraData;
    // }
    //
    // // set the encoding function flag to signal we are inside a function
    // encodingFunction = true;
    //
    // ((OMEGASearchFunction) function).write(contexts, this, out,
    // databaseSchema);
    //
    // // reset the encoding function flag
    // encodingFunction = false;
    // } catch (IOException e) {
    // throw new RuntimeException(e);
    // }
    // return extraData;
    // } else {
    // return super.visit(function, extraData);
    // }
    // }

    /**
     * Modified for spatial search queries
     */
    @Override
    protected Object visitBinarySpatialOperator(BinarySpatialOperator filter, Object extraData)
    {

        if (filter == null)
        {
            throw new NullPointerException("Filter to be encoded cannot be null");
        }

        // extract the property name and the geometry literal
        Expression e1 = filter.getExpression1();
        Expression e2 = filter.getExpression2();

        if (e1 instanceof Literal && e2 instanceof PropertyName)
        {
            e1 = filter.getExpression2();
            e2 = filter.getExpression1();
        }

        if (e1 instanceof PropertyName)
        {
            // handle native srid
            currentGeometry = null;
            // currentSRID = null;
            if (featureType != null)
            {
                // going thru evaluate ensures we get the proper result even if
                // the name has not
                // been specified (convention -> the default geometry)

                e1 = filterFactory.property(normalize(((PropertyName) e1).getPropertyName(), false));

                AttributeDescriptor descriptor = (AttributeDescriptor) e1.evaluate(featureType);
                if (descriptor instanceof GeometryDescriptor)
                {
                    currentGeometry = (GeometryDescriptor) descriptor;
                    currentSRID = (Integer) descriptor.getUserData().get(JDBCDataStore.JDBC_NATIVE_SRID);
                }
            }
        }

        if (e1 instanceof PropertyName && e2 instanceof Literal)
        {
            // call the "regular" method
            return visitBinarySpatialOperator(filter,
                                              (PropertyName) e1,
                                              (Literal) e2,
                                              filter.getExpression1() instanceof Literal,
                                              extraData);
        }
        else
        {
            // call the join version
            return visitBinarySpatialOperator(filter, e1, e2, extraData);
        }

    }

    /**
     * Modified to intercept spatial search queries, alter the SQL, and write
     * them out.
     */
    @Override
    public Object visit(Literal expression, Object context)
    {

        if (!isPrepareEnabled())
        {
            return super.visit(expression, context);
        }

        Object literalValue = getLiteralValue(expression, context);

        // Store value for later
        literalValues.add(literalValue);
        SRIDs.add(currentSRID);
        dimensions.add(currentDimension);

        Class<?> clazz = null;
        if (context instanceof Class)
        {
            clazz = (Class<?>) context;
        }
        else if (literalValue != null)
        {
            clazz = literalValue.getClass();
        }
        literalTypes.add(clazz);

        try
        {
            if (literalValue == null || dialect == null)
            {
                checkLiteralValue(literalValue);
            }
            else
            {
                StringBuffer sb = new StringBuffer();
                if (Geometry.class.isAssignableFrom(literalValue.getClass()))
                {
                    int srid = currentSRID != null ? currentSRID : -1;
                    int dimension = currentDimension != null ? currentDimension : -1;
                    //dialect.prepareGeometryValue(literalValue1, dimension, srid, Geometry.class, sb);
                }
                else if (encodingFunction)
                {
                    dialect.prepareFunctionArgument(clazz, sb);
                }
                else
                {
                    sb.append(QUESTION_MARK);
                }
                out.write(sb.toString());
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return context;

    }

    @Override
    public Object visit(PropertyIsLike filter, Object extraData)
    {

        String name = filter.getExpression().toString();

        if (isEnumeration(name))
        {
            filter = filterFactory.like(filterFactory.property(String.format(getEnumProperty(), enumList.indexOf(name))),
                                        filter.getLiteral(),
                                        filter.getWildCard(),
                                        filter.getSingleChar(),
                                        filter.getEscape(),
                                        filter.isMatchingCase(),
                                        filter.getMatchAction());

        }

        return super.visit(filter, extraData);
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

            if (filter instanceof DistanceBufferOperator)
            {

                return super.visitBinarySpatialOperator(filter, property, geometry, swapped, extraData);

            }
            else
            {

                Expression e1 = property;
                Expression e2 = geometry;

                String closingParenthesis = ")";
                if (filter instanceof Equals)
                {
                    out.write("ST_Equals");
                }
                else if (filter instanceof Disjoint)
                {
                    out.write("NOT (ST_Intersects");
                    closingParenthesis += ")";
                }
                else if (filter instanceof Intersects || filter instanceof BBOX)
                {
                    out.write("ST_Intersects");
                }
                else if (filter instanceof Crosses)
                {
                    out.write("ST_Crosses");
                }
                else if (filter instanceof Within)
                {
                    if (swapped)
                        out.write("ST_Contains");
                    else
                        out.write("ST_Within");
                }
                else if (filter instanceof Contains)
                {
                    if (swapped)
                        out.write("ST_Within");
                    else
                        out.write("ST_Contains");
                }
                else if (filter instanceof Overlaps)
                {
                    out.write("ST_Overlaps");
                }
                else if (filter instanceof Touches)
                {
                    out.write("ST_Touches");
                }
                else
                {
                    throw new RuntimeException("Unsupported filter type " + filter.getClass());
                }
                out.write("(");

                e1.accept(this, extraData);
                out.write("::geometry, ");
                e2.accept(this, extraData);

                out.write(closingParenthesis);
            }

        }
        catch (IOException e)
        {
            throw new RuntimeException(IO_ERROR, e);
        }

        return extraData;
    }

    @Override
    public Class<?> getContext(String recordset, String fieldname)
    {
        return getContext(filterFactory.property(recordset + DOT + fieldname));
    }

    @Override
    public Class<?> getContext(PropertyName name)
    {

        Class<?> context = null;

        name = filterFactory.property(normalize((name).getPropertyName(), false));

        AttributeDescriptor attType = (AttributeDescriptor) name.evaluate(featureType);
        if (attType != null)
        {
            context = attType.getType().getBinding();
        }

        currentProperty = name.getPropertyName();

        return context;

    }

    /**
     * Modified version from {@link FilterToSQL} to modify behavior to handle
     * formatting of property names when comparing against the feature type.
     */
    @Override
    protected void visitBinaryComparisonOperator(BinaryComparisonOperator filter, Object extraData) throws RuntimeException
    {

        Expression left = filter.getExpression1();
        Expression right = filter.getExpression2();
        Class<?> leftContext = null, rightContext = null;

        if (left instanceof PropertyName)
        {
            left = filterFactory.property(normalize(((PropertyName) left).getPropertyName(), false));

            currentProperty = ((PropertyName) left).getPropertyName();
            rightContext = getPropertyContext((PropertyName) left);
        }
        else if (left instanceof Function)
        {
            // check for a function return type
            Class<?> ret = getFunctionReturnType2((Function) left);
            if (ret != null)
            {
                rightContext = ret;
            }
        }

        if (right instanceof PropertyName)
        {
            right = filterFactory.property(normalize(((PropertyName) right).getPropertyName(), false));

            currentProperty = ((PropertyName) right).getPropertyName();
            leftContext = getPropertyContext((PropertyName) right);
        }
        else if (right instanceof Function)
        {
            Class<?> ret = getFunctionReturnType2((Function) right);
            if (ret != null)
            {
                leftContext = ret;
            }
        }

        // case sensitivity
        boolean matchCase = true;
        if (!filter.isMatchingCase())
        {
            // we only do for = and !=
            if (filter instanceof PropertyIsEqualTo || filter instanceof PropertyIsNotEqualTo)
            {
                // and only for strings
                if (String.class.equals(leftContext) || String.class.equals(rightContext))
                {
                    matchCase = false;
                }
            }
        }

        try
        {
            if (matchCase)
            {
                if (leftContext != null && isBinaryExpression2(left))
                {
                    writeBinaryExpression2(left, leftContext);
                }
                else
                {
                    left.accept(this, leftContext);
                }

                out.write(" " + extraData + " ");

                if (rightContext != null && isBinaryExpression2(right))
                {
                    writeBinaryExpression2(right, rightContext);
                }
                else
                {
                    right.accept(this, rightContext);
                }
            }
            else
            {
                // wrap both sides in "lower"
                FunctionImpl f = new FunctionImpl();
                f.setName("lower");
                f.setParameters(Collections.singletonList(left));
                f.accept(this, Collections.singletonList(leftContext));

                out.write(" " + extraData + " ");

                f.setParameters(Collections.singletonList(right));
                f.accept(this, Collections.singletonList(rightContext));
            }

        }
        catch (java.io.IOException ioe)
        {
            throw new RuntimeException(IO_ERROR, ioe);
        }
    }

    private Class<?> getPropertyContext(PropertyName name)
    {

        Class<?> context = null;
        AttributeDescriptor attType = (AttributeDescriptor) name.evaluate(featureType);

        if (attType != null)
        {
            context = attType.getType().getBinding();
        }

        if (context != null)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Property ({}) Context ({})", name.getPropertyName(), context.toString());
            }
        }
        else
        {
            LOGGER.warn("Property ({}) Context (UNKNOWN)", name.getPropertyName());
        }

        return context;
    }

    /**
     * Copied from {@link FilterToSQL}
     */
    private Class<?> getFunctionReturnType2(Function f)
    {
        Class<?> clazz = Object.class;
        if (f.getFunctionName() != null && f.getFunctionName().getReturn() != null)
        {
            clazz = f.getFunctionName().getReturn().getType();
        }
        if (clazz == Object.class)
        {
            clazz = null;
        }
        return clazz;
    }

    /**
     * Copied from {@link FilterToSQL}
     */
    private boolean isBinaryExpression2(Expression e)
    {
        return e instanceof BinaryExpression;
    }

    /**
     * Copied from {@link FilterToSQL}
     */
    private void writeBinaryExpression2(Expression e, Class<?> context) throws IOException
    {
        Writer tmp = out;
        try
        {
            out = new StringWriter();
            out.write("(");
            e.accept(this, null);
            out.write(")");
            tmp.write(cast(out.toString(), context));

        }
        finally
        {
            out = tmp;
        }
    }

    private Object getLiteralEnumerationValue(EFilterEnumerationModes mode, Literal expression)
    {

        Object literalValue;

        if (mode == EFilterEnumerationModes.MIXED)
        {
            mode = Pattern.matches("^[0-9]+$",
                                   (String) expression.getValue()) ? EFilterEnumerationModes.ORDINAL : EFilterEnumerationModes.ENUMVALUE;
        }

        if (mode == EFilterEnumerationModes.ENUMVALUE)
        {
            // Convert to Ordinal
            literalValue = EnumerationProviderUtil.toPosition(null, currentProperty, (String) expression.getValue());

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Converted ({}) Value ({}) => ({})", currentProperty, expression.getValue(), literalValue);
            }
        }
        else
        {
            literalValue = expression.getValue();
        }

        return literalValue;

    }

    /**
     * @param expression
     * @param context
     * @return
     */
    private Object getLiteralValue(Literal expression, Object context)
    {
        Object literalValue;

        if (context == null)
        {
            context = currentContext;
        }

        // Context Specified?
        if (context instanceof Class)
        {

            if (((Class<?>) context).getName().equals(Enum.class.getName()))
            {
                literalValue = getLiteralEnumerationValue(MODE, expression);
            }
            else
            {
                literalValue = evaluateLiteral(expression, (Class<?>) context);
            }
        }
        else
        {

            Object expressionValue = expression.getValue();

            // Expression a String?
            if (expressionValue instanceof String)
            {

                String stringValue = ((String) expressionValue).trim();

                // Yes; Wrapped in Quotes?
                if (stringValue.startsWith("'") && stringValue.endsWith("'")
                        || stringValue.startsWith("\"") && stringValue.endsWith("\""))
                {

                    FilterFactory ff = CommonFactoryFinder.getFilterFactory();

                    // Strip the Quotes
                    expression = ff.literal(stringValue.substring(1, stringValue.length() - 1));

                    // Evaluate as a String
                    literalValue = evaluateLiteral(expression, String.class);

                }
                else
                {
                    literalValue = evaluateLiteral(expression, null);
                }

            }
            else
            {
                literalValue = evaluateLiteral(expression, null);
            }

        }
        return literalValue;
    }

    /**
     * @param literalValue
     * @throws IOException
     */
    private void checkLiteralValue(Object literalValue) throws IOException
    {

        if (literalValue != null)
        {

            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Checking Literal Value Class: ({})", literalValue.getClass());
            }

            if (String.class.isAssignableFrom(literalValue.getClass()))
            {
                String literalStr = (String) literalValue;

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Literal Value Type: String ({})", literalStr);
                }

                // check if String literal is a geometry value
                if (literalStr.toUpperCase().startsWith("POINT") || literalStr.toUpperCase().startsWith("MULTIPOINT")
                        || literalStr.toUpperCase().startsWith("POLYGON") || literalStr.toUpperCase().startsWith(
                        "MULTIPOLYGON") || literalStr.toUpperCase().startsWith("LINESTRING")
                        || literalStr.toUpperCase().startsWith("MULITILINESTRING"))
                {
                    out.write(String.format(ST_FROM_TEXT, currentSRID));
                }
                else
                {
                    out.write(QUESTION_MARK);
                }
            }
            else if (Geometry.class.isAssignableFrom(literalValue.getClass()))
            {

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Literal Value Type: Geometry");
                }

                out.write(String.format(ST_FROM_TEXT, currentSRID));
            }
            else
            {
                out.write(QUESTION_MARK);
            }

        }
        else
        {

            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Literal Value is Null");
            }

            out.write(QUESTION_MARK);
        }
    }

    // ----------------------------------------------------------------------//
    // Public Setters
    // ----------------------------------------------------------------------//

    /**
     * Sets the offset to be (page number - 1)*(page size). The page numbering
     * starts at 1; anything else will be treated as page 1.
     *
     * @param value
     */
    public void setOffset(int value)
    {
        offset = value < 0 ? 0 : value;
    }

    /**
     * Sets the names of the properties that this Query should retrieve as part
     * of the returned.
     *
     * @param values
     */
    public void setPropertNames(String... values)
    {

        propertyNameList = new ArrayList<>();

        if (values != null)
        {
            for (String name : values)
            {

                // Column properly formatted with a table name?
                if (name.contains(DOT))
                {

                    propertyNameList.add(normalize(name, false));

                }
            }
        }

    }

    /**
     * Sets the columns and directions that the query should be sorted on.
     *
     * @param sort
     */
    public void setSortBy(SortBy... sort)
    {

        sortByList = new ArrayList<>();

        if (sort != null)
        {
            for (SortBy sortBy : sort)
            {

                String name = sortBy.getPropertyName().getPropertyName();

                // Column properly formatted with a table name?
                if (name.contains(DOT))
                {

                    name = normalize(name, false).replaceAll("[.]", "");

                    // if (isEnumeration(name)) {
                    // // Drop the record set
                    // name = name.split("[.]")[1];
                    // }

                    sortByList.add(factory.sort(name, sortBy.getSortOrder()));
                }
            }
        }

    }

    /**
     * @return the capabilities of this implementation.
     */
    public static Capabilities createCapabilities()
    {
        Capabilities capability = new Capabilities();

        capability.addAll(Capabilities.LOGICAL);
        capability.addAll(Capabilities.LOGICAL_OPENGIS);
        capability.addAll(Capabilities.SIMPLE_COMPARISONS);
        capability.addAll(Capabilities.SIMPLE_COMPARISONS_OPENGIS);
        capability.addType(PropertyIsNull.class);
        capability.addType(PropertyIsLike.class);
        capability.addType(PropertyIsBetween.class);

        // TODO Implement Functions
        // capability.addName(ListSearchFunction.NAME.getName(),
        // ListSearchFunction.NAME.getArgumentCount());

        // temporal filters
        capability.addType(After.class);
        capability.addType(Before.class);
        capability.addType(Begins.class);
        capability.addType(BegunBy.class);
        capability.addType(During.class);
        capability.addType(Ends.class);
        capability.addType(EndedBy.class);
        capability.addType(TContains.class);
        capability.addType(TEquals.class);

        // Adding the spatial filters support
        capability.addType(BBOX.class);
        capability.addType(Contains.class);
        capability.addType(Crosses.class);
        capability.addType(Disjoint.class);
        capability.addType(Equals.class);
        capability.addType(Intersects.class);
        capability.addType(Overlaps.class);
        capability.addType(Touches.class);
        capability.addType(Within.class);
        capability.addType(DWithin.class);
        capability.addType(Beyond.class);

        return capability;
    }

    /**
     * Sets the total number of results per page. Size of less than 1 (0 or a
     * negative number) should return the maximum number of results.
     *
     * @param value
     */
    public void setPageSize(int value)
    {
        pageSize = value;
    }

    /**
     * @return whether security checking is enabled.
     */
    public boolean isIgnoreSecurity()
    {
        return ignoreSecurity;
    }

    /**
     * Sets whether security checking is enabled.
     *
     * @param ignoreSecurity
     */
    public void setIgnoreSecurity(boolean ignoreSecurity)
    {
        this.ignoreSecurity = ignoreSecurity;
    }

    // ----------------------------------------------------------------------//
    // Private Methods
    // ----------------------------------------------------------------------//

    /**
     * @return the sorting portion of the SQL.
     */
    public String getSorting()
    {
        return QueryHelper.getOrderBy(sortByList);
    }

    /**
     * @return the filtering portion of the SQL
     */
    public String getLimit()
    {
        String limit = "";

        if (pageSize > 0)
        {
            limit = String.format("LIMIT %s OFFSET %s", pageSize, offset);
        }

        return limit;
    }

    /**
     * @return the name of the table.
     */
    public String getFrom()
    {

        String currentTable;

        StringBuilder sb = new StringBuilder(databaseSchema + ".coalesceentity");

        for (String recordsetName : tableList)
        {

            currentTable = recordsetName;

            String column = "entitykey";

            // Joining Linkage Table?
            if (currentTable.toLowerCase().contains("coalescelinkage"))
            {
                // Yes; Use Entity1Key instead.
                column = "entity1key";
            }

            // Create Join
            sb.append(" LEFT JOIN " + currentTable + " ON " + currentTable + "." + column + "=coalesceentity.objectkey");
        }

        for (String enumParam : enumList)
        {
            sb.append(String.format(getEnumJoinSQL(),
                                    enumList.indexOf(enumParam),
                                    EnumerationProviderUtil.lookupEnumeration(enumParam),
                                    enumParam));
        }

        return sb.toString();
    }

    /**
     * @return the columns that should be returned as a part of the query w/o a
     * postfix.
     */
    public String getColumns()
    {
        return getColumns("");
    }

    /**
     * @param postfix
     * @return the columns that should be returned as a part of the query.
     */
    public String getColumns(String postfix)
    {

        StringBuilder sb = new StringBuilder(String.format(SQL_COLUMNS, postfix));

        if (propertyNameList != null)
        {
            for (String column : propertyNameList)
            {
                ECoalesceFieldDataTypes type = CoalesceTemplateUtil.getDataTypes().get(column);

                if (type != null && type.isGeometryType())
                {
                    column = "postgis.ST_AsText(" + column + ") AS " + column.replaceAll("[.]", "");
                }
                else if (isEnumeration(column))
                {
                    column = String.format(getEnumProperty(), enumList.indexOf(column)) + " AS " + column.replaceAll("[.]",
                                                                                                                     "");
                }
                else
                {
                    column = column + " AS " + column.replaceAll("[.]", "");
                }

                sb.append(COMMA_SPACE + column + postfix);
            }
        }

        return sb.toString();

    }

    private boolean isEnumeration(String name)
    {
        boolean isEnumerationType = false;
/* TODO This converts ordinals back into Strings when returning search results. Used by ACINT
        if (CoalesceTemplateUtil.getDataTypes().containsKey(name))
        {
            switch (CoalesceTemplateUtil.getDataTypes().get(name)) {
            case ENUMERATION_TYPE:
                isEnumerationType = true;

                if (!enumList.contains(name))
                {
                    enumList.add(name);
                }

                break;
            default:
                isEnumerationType = false;
                break;

            }
        }
*/
        return isEnumerationType;
    }

    @Override
    public String normalize(String name)
    {
        return normalize(name, true);
    }

    private String normalize(String name, boolean includeSchema)
    {

        String[] parts = name.split("[.]");

        if (parts.length == 2)
        {

            // Yes; Get table's name w/o field
            String tablename = CoalesceIndexInfo.getIndexTableName(parts[0]);
            String tablenameSchema = databaseSchema + DOT + tablename;
            String propertyName = parts[1];

            if (!tablename.contains(COALESCEENTITY) && !tableList.contains(tablenameSchema))
            {
                // Add to list of tables
                tableList.add(tablenameSchema);
            }

            if (includeSchema)
            {
                tablename = tablenameSchema;
            }

            name = tablename + DOT + propertyName.toLowerCase();

        }

        return name;

    }

    private String getEnumJoinSQL()
    {
        return USE_DISPLAY_NAME ? SQL_ENUM_ASSOCIATED_JOIN : SQL_ENUMVALUE_JOIN;
    }

    private String getEnumProperty()
    {
        return USE_DISPLAY_NAME ? "E%sA." + SQL_ENUMASSOCIATED_PROPERTY_NAME : "E%s." + SQL_ENUMVALUE_PROPERTY_NAME;
    }

    @Override
    public ECoalesceFieldDataTypes getDataType(PropertyName name)
    {
        return CoalesceTemplateUtil.getDataTypes().get(normalize(name.getPropertyName(), false));
    }

    @Override
    public FilterFactory getFilterFactory()
    {
        return filterFactory;
    }
}
