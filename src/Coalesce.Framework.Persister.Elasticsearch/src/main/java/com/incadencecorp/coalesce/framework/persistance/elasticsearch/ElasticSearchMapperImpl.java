/*-----------------------------------------------------------------------------'
 Copyright 2018 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.ICoalesceMapper;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

/**
 * This implementation maps Coalesce types to Java classes that are acceptable by ElasticSearch.
 *
 * @author Chris Krentz
 */
public class ElasticSearchMapperImpl implements ICoalesceMapper<String> {

    @Override
    public String map(ECoalesceFieldDataTypes type)
    {
        switch (type)
        {
        case BOOLEAN_LIST_TYPE:
        case BOOLEAN_TYPE:
            return "boolean";

        case DOUBLE_LIST_TYPE:
        case DOUBLE_TYPE:
            return "double";

        case FLOAT_LIST_TYPE:
        case FLOAT_TYPE:
            return "float";

        case GEOCOORDINATE_TYPE:
            return "geo_point";

        case GEOCOORDINATE_LIST_TYPE:
        case LINE_STRING_TYPE:
        case POLYGON_TYPE:
        case CIRCLE_TYPE:
            return "geo_shape";

        case ENUMERATION_LIST_TYPE:
        case ENUMERATION_TYPE:
        case INTEGER_LIST_TYPE:
        case INTEGER_TYPE:
            return "integer";

        case GUID_LIST_TYPE:
        case GUID_TYPE:
        case URI_TYPE:
        case STRING_LIST_TYPE:
            return "keyword";

        case FILE_TYPE:
        case STRING_TYPE:
            return "text";

        case DATE_TIME_TYPE:
            return "date";

        case LONG_LIST_TYPE:
        case LONG_TYPE:
            return "long";

        case BINARY_TYPE:
        default:
            return null;
        }
    }

}
