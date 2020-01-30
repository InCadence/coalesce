package com.incadencecorp.coalesce.framework.persistance.neo4j;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.search.api.EFilterEnumerationModes;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.apache.commons.lang3.StringUtils;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FilterCapabilities;
import org.geotools.filter.FunctionImpl;
import org.geotools.util.ConverterFactory;
import org.geotools.util.Converters;
import org.geotools.util.factory.Hints;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.*;
import org.opengis.filter.expression.*;
import org.opengis.filter.spatial.*;
import org.opengis.filter.temporal.*;
import org.opengis.temporal.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Encodes a filter into a cypher. It should hopefully be generic enough that
 * any graph database using cypher will work with it. This encoder should
 * eventually be able to encode all filters except Geometry Filters.
 */
public class Neo4jFilterToCypher implements FilterVisitor, ExpressionVisitor {

    /**
     * Error message for exceptions
     */
    protected static final String IO_ERROR = "io problem writing filter";

    /**
     * Filter factory
     */
    protected static final FilterFactory FF = CommonFactoryFinder.getFilterFactory(null);

    /**
     * Standard java logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jFilterToCypher.class);

    /** */
    private static final EFilterEnumerationModes MODE = EFilterEnumerationModes.ENUMVALUE;

    /**
     * Maps a node label to a node variable name.
     */
    protected Map<String, String> labelToNode = new HashMap<>();

    /**
     * Where to write the constructed string from visiting the filters.
     */
    protected Writer out;

    /**
     * The filter types that this class can encode
     */
    protected FilterCapabilities capabilities;

    /**
     * Inline flag, controlling whether "WHERE" will prefix the Cypher encoded
     * filter
     */
    protected boolean inline;

    /**
     * The default property name replacement.
     */
    private String defaultName = null;

    private String entityname = null;
    private String currentProperty;
    private SimpleFeatureType featureType;

    /**
     * Default constructor.
     */
    public Neo4jFilterToCypher()
    {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param out the cypher writer.
     */
    public Neo4jFilterToCypher(Writer out)
    {
        this.out = out;
    }

    /**
     * Sets the writer the encoder will write to.
     *
     * @param out the writer used for filter conversion.
     */
    public void setWriter(Writer out)
    {
        this.out = out;
    }

    /**
     * Sets the writer to skip adding the 'WHERE' prefix.
     *
     * @param inline if the prefix should be skipped
     */
    public void setInline(boolean inline)
    {
        this.inline = inline;
    }

    /**
     * Sets the featuretype the encoder is encoding sql for.
     * <p>
     * This is used for context for attribute expressions when encoding to sql.
     * </p>
     *
     * @param featureType
     */
    public void setFeatureType(SimpleFeatureType featureType)
    {
        this.featureType = featureType;
    }

    /**
     * Sets the default property name replacement. If a label mapping does not
     * exist and a default has been set, then the default will be used.
     *
     * @param name the default node name
     */
    public void setDefaultLabelMapping(String name)
    {
        defaultName = name;
    }

    /**
     * Adds a mapping from a node label to a node variable. Any properties that
     * contain the node label will be replaced with the node variable.
     *
     * @param label the label being replaced on a property
     * @param node  the node name
     */
    public void addLabelMapping(String label, String node)
    {
        labelToNode.put(label, node);
    }

    /**
     * Encodes an OGS filter as a cypher expression.
     *
     * @param filter
     * @return
     * @throws CoalesceException
     */
    public String encodeToString(Filter filter) throws CoalesceException
    {
        StringWriter out = new StringWriter();
        this.out = out;
        this.encode(filter);
        return out.getBuffer().toString();
    }

    /**
     * Encodes an OGS filter as a cypher expression.
     *
     * @param filter
     * @throws CoalesceException
     */
    public void encode(Filter filter) throws CoalesceException
    {
        if (out == null)
        {
            throw new CoalesceException("Can't encode to a null writer.");
        }
        if (getCapabilities().fullySupports(filter))
        {

            try
            {
                if (!inline)
                {
                    out.write("WHERE ");
                }

                filter.accept(this, null);

            }
            catch (IOException ioe)
            {
                LOGGER.warn("Unable to export filter", ioe);
                throw new CoalesceException("Problem writing filter: ", ioe);
            }
        }
        else
        {
            throw new CoalesceException("Filter type not supported");
        }
    }

    public String getEntityName()
    {
        return entityname;
    }

    /**
     * Describes the capabilities of this encoder.
     * <p>
     * Performs lazy creation of capabilities.
     * </p>
     * If you're subclassing this class, override createFilterCapabilities to
     * declare which filtercapabilities you support. Don't use this method.
     *
     * @return The capabilities supported by this encoder.
     */
    public final synchronized FilterCapabilities getCapabilities()
    {
        if (capabilities == null)
        {
            capabilities = createFilterCapabilities();
        }

        return capabilities; // maybe clone? Make immutable somehow
    }

    /**
     * Sets the capabilities of this filter.
     *
     * @return FilterCapabilities for this Filter
     */
    protected FilterCapabilities createFilterCapabilities()
    {
        FilterCapabilities filterCapabilities = new FilterCapabilities();

        filterCapabilities.addAll(FilterCapabilities.LOGICAL_OPENGIS);
        filterCapabilities.addAll(FilterCapabilities.SIMPLE_COMPARISONS_OPENGIS);
        filterCapabilities.addType(PropertyIsNull.class);
        filterCapabilities.addType(PropertyIsBetween.class);
        filterCapabilities.addType(PropertyIsLike.class);
        filterCapabilities.addType(Id.class);
        filterCapabilities.addType(IncludeFilter.class);
        filterCapabilities.addType(ExcludeFilter.class);

        // temporal filters
        filterCapabilities.addType(After.class);
        filterCapabilities.addType(Before.class);
        filterCapabilities.addType(Begins.class);
        filterCapabilities.addType(BegunBy.class);
        filterCapabilities.addType(During.class);
        filterCapabilities.addType(Ends.class);
        filterCapabilities.addType(EndedBy.class);
        filterCapabilities.addType(TContains.class);
        filterCapabilities.addType(TEquals.class);

        return filterCapabilities;
    }

    @Override
    public Object visit(NilExpression expression, Object extraData)
    {
        throw new UnsupportedOperationException("Nil not supported");
    }

    @Override
    public Object visit(Add expression, Object extraData)
    {
        return visit(expression, "+", extraData);
    }

    @Override
    public Object visit(Divide expression, Object extraData)
    {
        return visit(expression, "/", extraData);
    }

    @Override
    public Object visit(Multiply expression, Object extraData)
    {
        return visit(expression, "*", extraData);
    }

    @Override
    public Object visit(Subtract expression, Object extraData)
    {
        return visit(expression, "-", extraData);
    }

    /**
     * Writes the cypher for the Math Expression.
     *
     * @param expression the Math phrase to be written.
     * @param operator   The operator of the expression.
     */
    protected Object visit(BinaryExpression expression, String operator, Object extraData)
    {
        LOGGER.debug("exporting Expression Math");

        try
        {
            expression.getExpression1().accept(this, extraData);
            out.write(" " + operator + " ");
            expression.getExpression2().accept(this, extraData);
        }
        catch (IOException ioe)
        {
            throw new RuntimeException("IO problems writing expression", ioe);
        }
        return extraData;
    }

    @Override
    public Object visit(Function expression, Object extraData)
    {
        try
        {
            out.write(expression.getName() + "(");
            for (int ii=0; ii<expression.getParameters().size(); ii++)
            {
                if (ii != 0)
                {
                    out.write(",");
                }

                expression.getParameters().get(ii).accept(this, extraData);
            }
            out.write(")");
        }
        catch (IOException ioe)
        {
            throw new RuntimeException("IO problems writing expression", ioe);
        }

        return extraData;
    }

    @Override
    public Object visit(Literal expression, Object context)
    {
        // type to convert the literal to
        Class<?> target = null;
        if (context instanceof Class)
        {
            target = (Class<?>) context;
        }

        try
        {
            // evaluate the expression
            Object literal = evaluateLiteral(expression, target);

            // write out the literal allowing subclasses to override this
            // behaviour (for writing out dates and the like using the BDMS
            // custom functions)
            writeLiteral(literal);
        }
        catch (IOException e)
        {
            throw new RuntimeException("IO problems writing literal", e);
        }
        return context;
    }

    @Override
    public Object visit(PropertyName expression, Object extraData)
    {
        Class<?> target = null;
        if (extraData instanceof Class)
        {
            target = (Class<?>) extraData;
        }

        try
        {
            String encodedField = expression.getPropertyName();

            // handle destination type if necessary
            if (target != null)
            {
                out.write(checkName(cast(encodedField, target)));
            }
            else
            {
                out.write(checkName(encodedField));
            }

        }
        catch (java.io.IOException ioe)
        {
            throw new RuntimeException("IO problems writing attribute exp", ioe);
        }
        return extraData;
    }

    @Override
    public Object visitNullFilter(Object extraData)
    {
        return extraData;
    }

    @Override
    public Object visit(ExcludeFilter filter, Object extraData)
    {
        throw new UnsupportedOperationException("Excluding a filter is not supported.");
    }

    @Override
    public Object visit(IncludeFilter filter, Object extraData)
    {
        throw new UnsupportedOperationException("Including a filter is not supported.");
    }

    @Override
    public Object visit(And filter, Object extraData)
    {
        return visit((BinaryLogicOperator) filter, "AND");
    }

    @Override
    public Object visit(Id filter, Object extraData)
    {
        // this can be supported using (x in node.prop WHERE x IN
        // [id0,id1,...,idN])
        // node and prop need to be known ahead of time
        throw new UnsupportedOperationException("Identifier filter is not supported.");
    }

    @Override
    public Object visit(Not filter, Object extraData)
    {
        try
        {
            out.write("NOT (");
            filter.getFilter().accept(this, extraData);
            out.write(")");

        }
        catch (java.io.IOException ioe)
        {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
    }

    @Override
    public Object visit(Or filter, Object extraData)
    {
        return visit((BinaryLogicOperator) filter, "OR");
    }

    @Override
    public Object visit(PropertyIsBetween filter, Object extraData)
    {
        LOGGER.debug("exporting PropertyIsBetween");

        Expression expr = filter.getExpression();
        Expression lowerbounds = filter.getLowerBoundary();
        Expression upperbounds = filter.getUpperBoundary();

        Class<?> context;
        AttributeDescriptor attType = (AttributeDescriptor) expr.evaluate(featureType);
        if (attType != null)
        {
            context = attType.getType().getBinding();
        }
        else
        {
            // assume it's a string?
            context = String.class;
        }

        try
        {
            expr.accept(this, extraData);
            out.write(" > ");
            lowerbounds.accept(this, context);
            out.write(" AND ");
            expr.accept(this, extraData);
            out.write(" < ");
            upperbounds.accept(this, context);
        }
        catch (java.io.IOException ioe)
        {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
    }

    @Override
    public Object visit(PropertyIsEqualTo filter, Object extraData)
    {
        if (filter.getExpression1().toString().equalsIgnoreCase(CoalescePropertyFactory.getName().getPropertyName()))
        {
            entityname = filter.getExpression2().toString();
        }

        visit((BinaryComparisonOperator) filter, "=");
        return extraData;
    }

    @Override
    public Object visit(PropertyIsNotEqualTo filter, Object extraData)
    {
        try
        {
            out.write("NOT (");
            visit((BinaryComparisonOperator) filter, "=");
            out.write(")");

        }
        catch (java.io.IOException ioe)
        {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return null;
    }

    @Override
    public Object visit(PropertyIsGreaterThan filter, Object extraData)
    {
        visit((BinaryComparisonOperator) filter, ">");
        return extraData;
    }

    @Override
    public Object visit(PropertyIsGreaterThanOrEqualTo filter, Object extraData)
    {
        visit((BinaryComparisonOperator) filter, ">=");
        return extraData;
    }

    @Override
    public Object visit(PropertyIsLessThan filter, Object extraData)
    {
        visit((BinaryComparisonOperator) filter, "<");
        return extraData;
    }

    @Override
    public Object visit(PropertyIsLessThanOrEqualTo filter, Object extraData)
    {
        visit((BinaryComparisonOperator) filter, "<=");
        return extraData;
    }

    @Override
    public Object visit(PropertyIsLike filter, Object extraData)
    {
        char escape = filter.getEscape().charAt(0);
        char wildcard = filter.getWildCard().charAt(0);
        char single = filter.getSingleChar().charAt(0);
        boolean matchCase = filter.isMatchingCase();

        String literal = filter.getLiteral();
        Expression att = filter.getExpression();

        // hack for date values, we append some additional padding to handle
        // the matching of time/timezone/etc...
        AttributeDescriptor ad = (AttributeDescriptor) att.evaluate(featureType);
        if (ad != null && Date.class.isAssignableFrom(ad.getType().getBinding()))
        {
            literal += wildcard;
        }

        // convert the regular expression
        StringBuilder sb = new StringBuilder(literal.length() + 5);
        for (int i = 0; i < literal.length(); i++)
        {
            char chr = literal.charAt(i);
            if (chr == escape)
            {
                // emit the next char and skip it
                if (i != literal.length() - 1)
                {
                    sb.append(literal.charAt(i + 1));//
                }
                i++; // skip next char
            }
            else if (chr == single)
            {
                sb.append('?');
            }
            else if (chr == wildcard)
            {
                sb.append(".*");
            }
            else if (chr == '\'')
            {
                sb.append('\'');
                sb.append('\'');
            }
            else
            {
                sb.append(chr);
            }
        }

        String pattern = sb.toString();

        try
        {

            att.accept(this, extraData);
            out.write(" =~ \"");
            if (!matchCase)
            {
                out.write("(?i)");
            }

            out.write(pattern);
            out.write("\"");
        }
        catch (java.io.IOException ioe)
        {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
    }

    @Override
    public Object visit(PropertyIsNull filter, Object extraData)
    {
        LOGGER.debug("exporting NullFilter");

        Expression expr = filter.getExpression();

        try
        {
            expr.accept(this, extraData);
            out.write(" IS NULL ");
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
    }

    @Override
    public Object visit(PropertyIsNil filter, Object extraData)
    {
        throw new UnsupportedOperationException("isNil not supported");
    }

    // ==================
    // Spatial Properties
    // ==================

    @Override
    public Object visit(BBOX filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Beyond filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Contains filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Crosses filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Disjoint filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(DWithin filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Equals filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Intersects filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Overlaps filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Touches filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    @Override
    public Object visit(Within filter, Object extraData)
    {
        visit((BinarySpatialOperator) filter, extraData);
        return extraData;
    }

    // ===================
    // Temporal Properties
    // ===================

    @Override
    public Object visit(After after, Object extraData)
    {
        return visit((BinaryTemporalOperator) after, extraData);
    }

    @Override
    public Object visit(AnyInteracts anyInteracts, Object extraData)
    {
        return visit((BinaryTemporalOperator) anyInteracts, extraData);
    }

    @Override
    public Object visit(Before before, Object extraData)
    {
        return visit((BinaryTemporalOperator) before, extraData);
    }

    @Override
    public Object visit(Begins begins, Object extraData)
    {
        return visit((BinaryTemporalOperator) begins, extraData);
    }

    @Override
    public Object visit(BegunBy begunBy, Object extraData)
    {
        return visit((BinaryTemporalOperator) begunBy, extraData);
    }

    @Override
    public Object visit(During during, Object extraData)
    {
        return visit((BinaryTemporalOperator) during, extraData);
    }

    @Override
    public Object visit(EndedBy endedBy, Object extraData)
    {
        return visit((BinaryTemporalOperator) endedBy, extraData);
    }

    @Override
    public Object visit(Ends ends, Object extraData)
    {
        return visit((BinaryTemporalOperator) ends, extraData);
    }

    @Override
    public Object visit(Meets meets, Object extraData)
    {
        return visit((BinaryTemporalOperator) meets, extraData);
    }

    @Override
    public Object visit(MetBy metBy, Object extraData)
    {
        return visit((BinaryTemporalOperator) metBy, extraData);
    }

    @Override
    public Object visit(OverlappedBy overlappedBy, Object extraData)
    {
        return visit((BinaryTemporalOperator) overlappedBy, extraData);
    }

    @Override
    public Object visit(TContains contains, Object extraData)
    {
        return visit((BinaryTemporalOperator) contains, extraData);
    }

    @Override
    public Object visit(TEquals equals, Object extraData)
    {
        return visit((BinaryTemporalOperator) equals, extraData);
    }

    @Override
    public Object visit(TOverlaps overlaps, Object extraData)
    {
        return visit((BinaryTemporalOperator) overlaps, extraData);
    }

    /**
     * Common implementation for BinaryLogicOperator filters. This way they're
     * all handled centrally.
     *
     * @param filter    the logic statement to be turned into cypher.
     * @param extraData extra filter data. Not modified directly by this method.
     */
    protected Object visit(BinaryLogicOperator filter, Object extraData)
    {
        LOGGER.debug("exporting LogicFilter");

        String type = (String) extraData;

        try
        {
            Iterator<Filter> list = filter.getChildren().iterator();

            // AND or OR
            out.write("(");

            while (list.hasNext())
            {
                list.next().accept(this, extraData);

                if (list.hasNext())
                {
                    out.write(" " + type + " ");
                }
            }

            out.write(")");

        }
        catch (java.io.IOException ioe)
        {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
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

    private String normalize(String name)
    {
        return name.toLowerCase();
    }

    /**
     * Common implementation for BinaryComparisonOperator filters. This way
     * they're all handled centrally. DJB: note, postgis overwrites this
     * implementation because of the way null is handled. This is for
     * &lt;PropertyIsNull&gt; filters and &lt;PropertyIsEqual&gt; filters are handled. They
     * will come here with "property = null".
     *
     * @param filter    the comparison to be turned into cypher.
     * @param extraData the operator used
     */
    protected void visit(BinaryComparisonOperator filter, Object extraData)
    {
        Expression left = filter.getExpression1();
        Expression right = filter.getExpression2();

        PropertyName property;
        Expression value;
        Class<?> context;

        if (left instanceof PropertyName)
        {
            property = FF.property(normalize(((PropertyName) left).getPropertyName()));
            value = right;
        }
        else if (right instanceof PropertyName)
        {
            property = FF.property(normalize(((PropertyName) right).getPropertyName()));
            value = left;
        }
        else
        {
            throw new IllegalArgumentException("");
        }

        currentProperty = property.getPropertyName();
        context = getPropertyContext(property);

        // case sensitivity
        boolean matchCase = true;
        if (!filter.isMatchingCase())
        {
            // we only do for = and !=
            if (filter instanceof PropertyIsEqualTo || filter instanceof PropertyIsNotEqualTo)
            {
                // and only for strings
                if (String.class.equals(context))
                {
                    matchCase = false;
                }
            }
        }

        try
        {
            if (matchCase)
            {
                property.accept(this, String.class);

                out.write(" " + extraData + " ");

                if (context != null && isBinaryExpression(right))
                {
                    writeBinaryExpression(right, context);
                }
                else
                {
                    value.accept(this, context);
                }
            }
            else
            {
                FunctionImpl f = new FunctionImpl();
                f.setName("lower");
                f.setParameters(Collections.singletonList(property));
                f.accept(this, String.class);

                out.write(" " + extraData + " ");

                f.setParameters(Collections.singletonList(value));
                f.accept(this, context);
            }
        }
        catch (java.io.IOException ioe)
        {
            throw new RuntimeException(IO_ERROR, ioe);
        }
    }

    protected Object visit(BinarySpatialOperator filter, Object extraData)
    {
        throw new UnsupportedOperationException("Spatial not supported");
    }

    protected Object visit(BinaryTemporalOperator filter, Object extraData)
    {
        LOGGER.debug("exporting Cypher TemporalFilter");

        Expression e1 = filter.getExpression1();
        Expression e2 = filter.getExpression2();

        // flip if value is on the left
        boolean swapped = false;
        if (e1 instanceof Literal && e2 instanceof PropertyName)
        {
            e1 = filter.getExpression2();
            e2 = filter.getExpression1();
            swapped = true;
        }

        if (e1 instanceof PropertyName && e2 instanceof Literal)
        {
            // call the "regular" method
            return visitBinaryTemporalOperator(filter, (PropertyName) e1, (Literal) e2, swapped, extraData);
        }
        else
        {
            // call the join version
            return visitBinaryTemporalOperator(filter, e1, e2, extraData);
        }
    }

    /**
     * Handles the general case of two expressions in a binary temporal filter.
     * <p>
     * Subclasses should override if they support more temporal operators than
     * what is handled in this base class.
     * </p>
     */
    protected Object visitBinaryTemporalOperator(BinaryTemporalOperator filter,
                                                 Expression e1,
                                                 Expression e2,
                                                 Object extraData)
    {

        if (!(filter instanceof After || filter instanceof Before || filter instanceof TEquals))
        {
            throw new IllegalArgumentException("Unsupported filter: " + filter + ". Only After,Before,TEquals supported");
        }

        String op = filter instanceof After ? ">" : filter instanceof Before ? "<" : "=";

        try
        {
            e1.accept(this, extraData);
            out.write(" " + op + " ");
            e2.accept(this, extraData);
        }
        catch (IOException e)
        {
            return new RuntimeException("Error encoding temporal filter", e);
        }
        return extraData;
    }

    /**
     * Handles the common case of a PropertyName,Literal geometry binary
     * temporal operator.
     * <p>
     * Subclasses should override if they support more temporal operators than
     * what is handled in this base class.
     * </p>
     */
    protected Object visitBinaryTemporalOperator(BinaryTemporalOperator filter,
                                                 PropertyName property,
                                                 Literal temporal,
                                                 boolean swapped,
                                                 Object extraData)
    {

        Class<?> typeContext = null;
        AttributeDescriptor attType = (AttributeDescriptor) property.evaluate(featureType);
        if (attType != null)
        {
            typeContext = attType.getType().getBinding();
        }

        // check for time period
        Period period = null;
        if (temporal.evaluate(featureType) instanceof Period)
        {
            period = (Period) temporal.evaluate(featureType);
        }

        // verify that those filters that require a time period have one
        verifyTimePeriodFilter(filter, period);

        // ensure the time period is the correct argument
        verifyTimePeriodArgs(filter, swapped);

        try
        {
            if (filter instanceof After || filter instanceof Before)
            {
                String op = filter instanceof After ? " > " : " < ";
                String inv = filter instanceof After ? " < " : " > ";

                if (period != null)
                {
                    out.write("(");

                    property.accept(this, extraData);
                    out.write(swapped ? inv : op);
                    visitBegin(period, extraData);

                    out.write(" AND ");

                    property.accept(this, extraData);
                    out.write(swapped ? inv : op);
                    visitEnd(period, extraData);

                    out.write(")");
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

                    out.write(op);

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
            else if (isOpenTemporalRange(filter))
            {
                property.accept(this, extraData);
                out.write(" = ");

                if (filter instanceof Begins || filter instanceof BegunBy)
                {
                    visitBegin(period, extraData);
                }
                else
                {
                    visitEnd(period, extraData);
                }
            }
            else if (isClosedTemporalRange(filter))
            {
                property.accept(this, extraData);
                out.write(" BETWEEN ");

                visitBegin(period, extraData);
                out.write(" AND ");
                visitEnd(period, extraData);
            }
            else if (filter instanceof TEquals)
            {
                property.accept(this, extraData);
                out.write(" = ");
                temporal.accept(this, typeContext);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Error encoding temporal filter", e);
            throw new RuntimeException("Error encoding temporal filter", e);
        }

        return extraData;
    }

    private void verifyTimePeriodFilter(BinaryTemporalOperator filter, Period period)
    {
        if ((isOpenTemporalRange(filter) || isClosedTemporalRange(filter)) && period == null)
        {
            throw new IllegalArgumentException("Filter requires a time period");
        }
        if (filter instanceof TEquals && period != null)
        {
            throw new IllegalArgumentException("TEquals filter does not accept time period");
        }
    }

    private void verifyTimePeriodArgs(BinaryTemporalOperator filter, boolean swapped)
    {
        if ((filter instanceof Begins || filter instanceof Ends || filter instanceof During) && swapped)
        {
            throw new IllegalArgumentException("Time period must be second argument of Filter");
        }
        if ((filter instanceof BegunBy || filter instanceof EndedBy || filter instanceof TContains) && !swapped)
        {
            throw new IllegalArgumentException("Time period must be first argument of Filter");
        }
    }

    private boolean isOpenTemporalRange(BinaryTemporalOperator filter)
    {
        return filter instanceof Begins || filter instanceof BegunBy || filter instanceof Ends || filter instanceof EndedBy;
    }

    private boolean isClosedTemporalRange(BinaryTemporalOperator filter)
    {
        return filter instanceof During || filter instanceof TContains;
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
            int ordinal = EnumerationProviderUtil.toPosition(null, currentProperty, (String) expression.getValue());

            // TODO All types are stored as Strings within Neo4j. Until this is
            // resolved we need to
            // convert the ordinal position.
            literalValue = Integer.toString(ordinal);

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Converted ({}) Value ({}) => ({})",
                             currentProperty,
                             expression.getValue(),
                             literalValue);
            }
        }
        else
        {
            literalValue = expression.getValue();
        }

        return literalValue;

    }

    void visitBegin(Period p, Object extraData)
    {
        FF.literal(p.getBeginning().getPosition().getDate()).accept(this, extraData);
    }

    void visitEnd(Period p, Object extraData)
    {
        FF.literal(p.getEnding().getPosition().getDate()).accept(this, extraData);
    }

    /**
     * determines if the function is a binary expression
     */
    boolean isBinaryExpression(Expression e)
    {
        return e instanceof BinaryExpression;
    }

    /**
     * write out the binary expression and cast only the end result, not passing
     * any context into encoding the individual parts
     */
    void writeBinaryExpression(Expression e, Class<?> context) throws IOException
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

    protected Object evaluateLiteral(Literal expression, Class<?> target)
    {
        Object literal = null;

        // do a safe conversion
        if (target != null)
        {
            // use the target type
            if (Number.class.isAssignableFrom(target))
            {
                literal = safeConvertToNumber(expression, target);

                if (literal == null)
                {
                    literal = safeConvertToNumber(expression, Number.class);
                }
            }
            else if (Enum.class.isAssignableFrom(target))
            {
                literal = getLiteralEnumerationValue(MODE, expression);
            }
            else
            {
                literal = expression.evaluate(featureType, target);
            }
        }

        // if the target was not known, of the conversion failed, try the
        // type guessing dance literal expression does only for the following
        // method call
        if (literal == null)
        {
            literal = expression.evaluate(featureType);
        }

        // if that failed as well, grab the value as is
        if (literal == null)
        {
            literal = expression.getValue();
        }

        return literal;
    }

    /**
     * Writes out a non null, non geometry literal. The base class properly
     * handles null, numeric and booleans (true|false), and turns everything
     * else into a string. Subclasses are expected to override this shall they
     * need a different treatment (e.g. for dates)
     *
     * @param literal
     * @throws IOException
     */
    protected void writeLiteral(Object literal) throws IOException
    {
        if (literal == null)
        {
            out.write("NULL");
        }
        else if (literal instanceof Number || literal instanceof Boolean)
        {
            out.write(String.valueOf(literal));
        }
        else
        {
            // we don't know the type...just convert back to a string
            String encoding = Converters.convert(literal, String.class, null);
            if (encoding == null)
            {
                // could not convert back to string, use original l value
                encoding = literal.toString();
            }

            // single quotes must be escaped to have a valid cypher value
            String escaped = StringUtils.replace(encoding, "'", "\\'");

            LOGGER.debug("Escaped Expression = {}", escaped);

            out.write("\"" + escaped + "\"");
        }
    }

    /**
     * Does a safe conversion of expression to a number
     */
    Number safeConvertToNumber(Expression expression, Class<?> target)
    {
        return (Number) Converters.convert(expression.evaluate(featureType),
                                           target,
                                           new Hints(ConverterFactory.SAFE_CONVERSION, true));
    }

    /**
     * Gives the opportunity to subclasses to force the property to the desired
     * type. By default it simply writes out the property as-is (the property
     * must be already escaped).
     *
     * @param encodedProperty
     * @param target
     * @throws IOException
     */
    protected String cast(String encodedProperty, Class<?> target) throws IOException
    {
        return encodedProperty;
    }

    protected String checkName(String propName)
    {
        for (Map.Entry<String, String> entry : labelToNode.entrySet())
        {
            if (propName.contains(entry.getKey()))
            {
                // replace the label with the node variable
                return propName.replaceAll(entry.getKey(), entry.getValue());
            }
        }

        // replace the label with the default name
        if (defaultName != null)
        {
            // split off the property name from the end and append to the default
            String[] tokens = propName.split("\\.");
            // TODO Replace this with the normalize API
            return defaultName + "." + tokens[tokens.length - 1].replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
        }
        return propName;
    }

}
