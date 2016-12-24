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

package com.incadencecorp.coalesce.search.factory;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

import com.incadencecorp.coalesce.search.api.ESearchModifiers;
import com.incadencecorp.coalesce.search.api.ESearchTypes;
import com.incadencecorp.coalesce.search.functions.CoalesceSQLListFunction;

/**
 * This factory creates function filters.
 * 
 * @author n78554
 */
public final class CoalesceSQLListFunctionFactory {

    private CoalesceSQLListFunctionFactory()
    {
        // Do Nothing
    }

    /**
     * Prepares a filter to search a list field.
     *
     * @param ff - The {@link FilterFactory} implementation to use to generate
     *            the filter.
     * @param property - the property to execute the function on.
     * @param searchType - The type of search to perform
     * @param searchModifier - The modifier of the search
     * @param values - The {@link Literal} values to search on
     * @return {@link Filter} from the provided parameters.
     * @throws IllegalArgumentException - If the values array does not contain
     *             the expected number of arguments.
     */
    public static Filter listSearch(final FilterFactory ff,
                                    final PropertyName property,
                                    final ESearchTypes searchType,
                                    final ESearchModifiers searchModifier,
                                    Literal... values) throws IllegalArgumentException
    {
        // Check for correct number of parameters for the search type
        switch (searchType) {
        case BETWEEN:
            if (values == null || values.length != 2)
            {
                throw new IllegalArgumentException("Array must contain two values for specified search type.");
            }
            break;
        case CONTAINS:
        case CONTAINS_LIKE:
        case GREATER_THAN:
        case GREATER_THAN_OR_EQUAL:
        case LESS_THAN:
        case LESS_THAN_OR_EQUAL:
        default:
            if (values == null || values.length != 1)
            {
                throw new IllegalArgumentException("Parameter must contain one value for specified search type.");
            }
            break;
        }

        Function function = null;

        // Build comparison string based on search type
        switch (searchType) {
        case BETWEEN:
            function = ff.function(CoalesceSQLListFunction.NAME.getName(),
                                   ff.literal(property.getPropertyName()),
                                   ff.literal(searchType.toString()),
                                   ff.literal(searchModifier.toString()),
                                   values[0],
                                   values[1]);
            break;
        case CONTAINS:
        case CONTAINS_LIKE:
        case GREATER_THAN:
        case GREATER_THAN_OR_EQUAL:
        case LESS_THAN:
        case LESS_THAN_OR_EQUAL:
            function = ff.function(CoalesceSQLListFunction.NAME.getName(),
                                   ff.literal(property.getPropertyName()),
                                   ff.literal(searchType.toString()),
                                   ff.literal(searchModifier.toString()),
                                   values[0]);
            break;
        default:
            throw new IllegalArgumentException("Invalid search type value.");
        }

        // Search modifier alters comparison logic
        boolean equals = true;
        switch (searchModifier) {
        case ALL:
        case NONE:
            equals = false;
            break;
        case ANY:
        default:
            equals = true;
        }

        // Create and return filter
        Filter nullFilter = ff.not(ff.isNull(property));
        Filter searchFilter = ff.equals(function, ff.literal(equals));
        return ff.and(nullFilter, searchFilter);
    }

}
