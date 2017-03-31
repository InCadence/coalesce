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

package com.incadencecorp.coalesce.framework.persistance.postgres;

import java.util.List;

import org.opengis.filter.sort.SortBy;

import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;

/**
 * This utility class provides utility methods for proccing queries.
 * 
 * @author n78554
 */
public final class QueryHelper {

    private QueryHelper()
    {
    }

    // /**
    // * @param values
    // * @return a list of SortType converted to an array of SortBy.
    // */
    // public static SortBy[] toSortBy(List<SortByType> values) {
    //
    // FilterFactory ff = CommonFactoryFinder.getFilterFactory();
    // SortBy[] sortList;
    //
    // if (values != null) {
    // sortList = new SortBy[values.size()];
    //
    // for (int ii = 0; ii < values.size(); ii++) {
    //
    // switch (values.get(ii).getSortOrder()) {
    // case ASC:
    // sortList[ii] =
    // ff.sort(values.get(ii).getPropertyName(), SortOrder.ASCENDING);
    // break;
    // default:
    // sortList[ii] =
    // ff.sort(values.get(ii).getPropertyName(), SortOrder.DESCENDING);
    // break;
    // }
    //
    // }
    // } else {
    // sortList = new SortBy[0];
    // }
    //
    // return sortList;
    //
    // }

    /**
     * @param values
     * @return the sorting portion of the SQL.
     */
    public static String getOrderBy(List<SortBy> values)
    {

        StringBuilder sb = new StringBuilder();

        if (values != null && values.size() > 0)
        {

            for (SortBy sortBy : values)
            {

                if (sb.length() == 0)
                {
                    sb.append("ORDER BY ");
                }
                else
                {
                    sb.append(", ");
                }

                String name = sortBy.getPropertyName().getPropertyName();

                if (isEnumeration(name) && name.contains("."))
                {
                    name = name.split("[.]")[1].toLowerCase();
                }

                sb.append(String.format("%s %s", name, sortBy.getSortOrder().toSQL()));
            }

        }

        return sb.toString();
    }

    private static boolean isEnumeration(String name)
    {

        boolean isEnumerationType = false;

        if (CoalesceTemplateUtil.getDataTypes().containsKey(name))
        {
            switch (CoalesceTemplateUtil.getDataTypes().get(name)) {
            case ENUMERATION_TYPE:
                isEnumerationType = true;
                break;
            default:
                isEnumerationType = false;
                break;

            }
        }

        return isEnumerationType;

    }

}
