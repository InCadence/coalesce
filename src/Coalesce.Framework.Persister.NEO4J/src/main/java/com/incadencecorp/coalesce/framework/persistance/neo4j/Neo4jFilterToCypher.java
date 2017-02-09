package com.incadencecorp.coalesce.framework.persistance.neo4j;

import static org.geotools.filter.capability.FunctionNameImpl.parameter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.filter.FilterCapabilities;
import org.geotools.filter.FunctionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.util.ConverterFactory;
import org.geotools.util.Converters;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.BinaryTemporalOperator;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;
import org.opengis.temporal.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;

/**
 * Encodes a filter into a cypher. It should hopefully be generic enough that
 * any graph database using cypher will work with it. This encoder should
 * eventually be able to encode all filters except Geometry Filters.
 */
public class Neo4jFilterToCypher implements FilterVisitor, ExpressionVisitor {

    /** Error message for exceptions */
    protected static final String IO_ERROR = "io problem writing filter";

    /** Filter factory */
    protected static final FilterFactory FF = CommonFactoryFinder.getFilterFactory(null);

    /** Standard java logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jFilterToCypher.class);

    /** Maps a node label to a node variable name. */
    protected Map<String, String> labelToNode = new HashMap<>();

    /** Where to write the constructed string from visiting the filters. */
    protected Writer out;

    /** The filter types that this class can encode */
    protected FilterCapabilities capabilities;

    /**
     * Inline flag, controlling whether "WHERE" will prefix the Cypher encoded
     * filter
     */
    protected boolean inline;

    /** The default property name replacement. */
    private String defaultName = null;

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
     * @param node the node name
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
     * @throws Exception
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
     * @param operator The operator of the expression.
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
        throw new UnsupportedOperationException("Function filter is not supported.");
    }

    @Override
    public Object visit(Literal expression, Object context)
    {
        LOGGER.debug("exporting LiteralExpression");

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
        LOGGER.debug("exporting PropertyName");

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
        AttributeDescriptor attType = (AttributeDescriptor) expr.evaluate(null);
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
        AttributeDescriptor ad = (AttributeDescriptor) att.evaluate(null);
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
     * @param filter the logic statement to be turned into cypher.
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

    /**
     * Common implementation for BinaryComparisonOperator filters. This way
     * they're all handled centrally. DJB: note, postgis overwrites this
     * implementation because of the way null is handled. This is for
     * <PropertyIsNull> filters and <PropertyIsEqual> filters are handled. They
     * will come here with "property = null".
     *
     * @param filter the comparison to be turned into cypher.
     * @param extraData the operator used
     */
    protected void visit(BinaryComparisonOperator filter, Object extraData)
    {
        LOGGER.debug("exporting Cypher ComparisonFilter");

        Expression left = filter.getExpression1();
        Expression right = filter.getExpression2();
        Class<?> leftContext = null;
        Class<?> rightContext = null;
        if (left instanceof PropertyName)
        {
            // aha! It's a propertyname, we should get the class and pass it in
            // as context to the tree walker.
            AttributeDescriptor attType = (AttributeDescriptor) left.evaluate(null);
            if (attType != null)
            {
                rightContext = attType.getType().getBinding();
            }
        }

        if (right instanceof PropertyName)
        {
            AttributeDescriptor attType = (AttributeDescriptor) right.evaluate(null);
            if (attType != null)
            {
                leftContext = attType.getType().getBinding();
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

        String type = (String) extraData;

        try
        {
            if (matchCase)
            {
                if (leftContext != null && isBinaryExpression(left))
                {
                    writeBinaryExpression(left, leftContext);
                }
                else
                {
                    left.accept(this, leftContext);
                }

                out.write(" " + type + " ");

                if (rightContext != null && isBinaryExpression(right))
                {
                    writeBinaryExpression(right, rightContext);
                }
                else
                {
                    right.accept(this, rightContext);
                }
            }
            else
            {
                // wrap both sides in "lower"
                FunctionImpl f = new FunctionImpl() {

                    {
                        functionName = new FunctionNameImpl("lower",
                                                            parameter("lowercase", String.class),
                                                            parameter("string", String.class));
                    }
                };
                f.setName("lower");

                f.setParameters(Arrays.asList(left));
                f.accept(this, Arrays.asList(leftContext));

                out.write(" " + type + " ");

                f.setParameters(Arrays.asList(right));
                f.accept(this, Arrays.asList(rightContext));
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
        AttributeDescriptor attType = (AttributeDescriptor) property.evaluate(null);
        if (attType != null)
        {
            typeContext = attType.getType().getBinding();
        }

        // check for time period
        Period period = null;
        if (temporal.evaluate(null) instanceof Period)
        {
            period = (Period) temporal.evaluate(null);
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
            else
            {
                literal = expression.evaluate(null, target);
            }
        }

        // check for conversion to number
        if (target == null)
        {
            // we don't know the target type, check for a conversion to a number

            Number number = safeConvertToNumber(expression, Number.class);
            if (number != null)
            {
                literal = number;
            }
        }

        // if the target was not known, of the conversion failed, try the
        // type guessing dance literal expression does only for the following
        // method call
        if (literal == null)
        {
            literal = expression.evaluate(null);
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
            String escaped = encoding.replaceAll("'", "\'");
            out.write("\"" + escaped + "\"");
        }
    }

    /**
     * Does a safe conversion of expression to a number
     */
    Number safeConvertToNumber(Expression expression, Class<?> target)
    {
        return (Number) Converters.convert(expression.evaluate(null), target, new Hints(ConverterFactory.SAFE_CONVERSION,
                                                                                        true));
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

        // Can be phased out when DSSNeo4jPersistor.KEY is modified to be
        // DSSPropertyFactory.getEntityKey().getPropertyName()
        if (propName.compareToIgnoreCase(CoalescePropertyFactory.getEntityKey().getPropertyName()) == 0)
        {
            propName = Neo4JPersistor.KEY;
        }

        // replace the label with the default name
        if (defaultName != null)
        {
            // split off the property name from the end and append to the
            // default
            String[] tokens = propName.split("\\.");
            return defaultName + "." + tokens[tokens.length - 1];
        }
        return propName;
    }

}
