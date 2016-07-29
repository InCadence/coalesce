/**
 * ///-----------SECURITY CLASSIFICATION: UNCLASSIFIED------------------------
 * /// Copyright 2016 - Lockheed Martin Corporation, All Rights Reserved /// ///
 * Notwithstanding any contractor copyright notice, the government has ///
 * Unlimited Rights in this work as defined by DFARS 252.227-7013 and ///
 * 252.227-7014. Use of this work other than as specifically authorized by ///
 * these DFARS Clauses may violate government rights in this work. /// /// DFARS
 * Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16) /// Unlimited
 * Rights. The Government has the right to use, modify, /// reproduce, perform,
 * display, release or disclose this computer software /// in whole or in part,
 * in any manner, and for any purpose whatsoever, /// and to have or authorize
 * others to do so. /// /// Distribution Statement D. Distribution authorized to
 * the Department of /// Defense and U.S. DoD contractors only in support of US
 * DoD efforts. /// Other requests shall be referred to the ACINT Modernization
 * Program /// Management under the Director of the Office of Naval
 * Intelligence. ///
 * -------------------------------UNCLASSIFIED---------------------------------
 */

package com.incadencecorp.coalesce.framework.persistance.postgres.mappers;

import java.sql.Types;

import com.incadencecorp.coalesce.api.ICoalesceMapper;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

/**
 * This implementation returns the data type to be used when creating parameters
 * for PostGreSQL
 * 
 * @author n78554
 *
 */
public class StoredProcedureArgumentMapper implements ICoalesceMapper<Integer> {

    @Override
    public Integer map(ECoalesceFieldDataTypes type)
    {
        switch (type) {

        case DATE_TIME_TYPE:
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
