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

package com.incadencecorp.coalesce.plugins.template2java;

import java.util.Date;
import java.util.UUID;

import com.incadencecorp.coalesce.api.ICoalesceMapper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircle;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * This implementation maps Coalesce types to Java classes.
 * 
 * @author n78554
 */
public class ReturnTypeMapper implements ICoalesceMapper<String> {

    @Override
    public String map(ECoalesceFieldDataTypes type)
    {
        Class<?> clazz = null;

        switch (type) {
        case BOOLEAN_TYPE:
            clazz = Boolean.class;
            break;
        case BOOLEAN_LIST_TYPE:
            clazz = boolean.class;
            break;
        case DATE_TIME_TYPE:
            clazz = Date.class;
            break;
        case DOUBLE_TYPE:
            clazz = Double.class;
            break;
        case DOUBLE_LIST_TYPE:
            clazz = double.class;
            break;
        case FLOAT_TYPE:
            clazz = Float.class;
            break;
        case FLOAT_LIST_TYPE:
            clazz = float.class;
            break;
        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            clazz = Integer.class;
            break;
        case ENUMERATION_LIST_TYPE:
        case INTEGER_LIST_TYPE:
            clazz = int.class;
            break;
        case LONG_TYPE:
            clazz = Long.class;
            break;
        case LONG_LIST_TYPE:
            clazz = long.class;
            break;
        case GEOCOORDINATE_LIST_TYPE:
            clazz = MultiPoint.class;
            break;
        case GEOCOORDINATE_TYPE:
            clazz = Point.class;
            break;
        case LINE_STRING_TYPE:
            clazz = LineString.class;
            break;
        case CIRCLE_TYPE:
            clazz = CoalesceCircle.class;
            break;
        case POLYGON_TYPE:
            clazz = Polygon.class;
            break;
        case GUID_TYPE:
        case GUID_LIST_TYPE:
            clazz = UUID.class;
            break;
        case STRING_TYPE:
        case STRING_LIST_TYPE:
        case URI_TYPE:
            clazz = String.class;
            break;
        case FILE_TYPE:
        case BINARY_TYPE:
            clazz = Object.class;
            break;
        default:
            clazz = String.class;
            break;
        }
        
        String result = clazz.getSimpleName();
        
        if (type.isListType()) {
            result = result + "[]";
        }
        return result;

    }
}
