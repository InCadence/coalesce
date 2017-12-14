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

package com.incadencecorp.coalesce.framework.persistance.neo4j;

import com.incadencecorp.coalesce.api.ICoalesceMapper;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

import java.sql.Types;

/**
 * @author Derek Clemenzi
 */
public class Neo4jTypeMapper implements ICoalesceMapper<Integer> {

    @Override
    public Integer map(ECoalesceFieldDataTypes type)
    {
        switch (type) {

        case DATE_TIME_TYPE:
            return Types.DATE;
        case GUID_TYPE:
            return Types.OTHER;
        case GEOCOORDINATE_LIST_TYPE:
        case GEOCOORDINATE_TYPE:
        case LINE_STRING_TYPE:
        case POLYGON_TYPE:
        case CIRCLE_TYPE:
            return Types.VARCHAR;
        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            return Types.INTEGER;
        case DOUBLE_TYPE:
            return Types.DOUBLE;
        case FLOAT_TYPE:
            return Types.FLOAT;
        case BOOLEAN_TYPE:
            return Types.BOOLEAN;
        case LONG_TYPE:
            return Types.BIGINT;
        default:
            return Types.CHAR;
        }
    }

}
