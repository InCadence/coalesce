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

package com.incadencecorp.coalesce.mapper.impl;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

import com.incadencecorp.coalesce.api.ICoalesceMapper;
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
public class JavaMapperImpl implements ICoalesceMapper<Class<?>> {

    @Override
    public Class<?> map(ECoalesceFieldDataTypes type)
    {
        Class<?> clazz = null;

        switch (type) {
        case BOOLEAN_TYPE:
        case BOOLEAN_LIST_TYPE:
            clazz = Boolean.class;
            break;
        case DATE_TIME_TYPE:
            clazz = Date.class;
            break;
        case DOUBLE_TYPE:
        case DOUBLE_LIST_TYPE:
            clazz = Double.class;
            break;
        case FLOAT_TYPE:
        case FLOAT_LIST_TYPE:
            clazz = Float.class;
            break;
        case ENUMERATION_TYPE:
        case ENUMERATION_LIST_TYPE:
            clazz = Enum.class;
            break;
        case INTEGER_TYPE:
        case INTEGER_LIST_TYPE:
            clazz = Integer.class;
            break;
        case LONG_TYPE:
        case LONG_LIST_TYPE:
            clazz = Long.class;
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
            clazz = Point.class;
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
            clazz = String.class;
            break;
        case URI_TYPE:
            clazz = URI.class;
            break;
        case FILE_TYPE:
        case BINARY_TYPE:
            clazz = Object.class;
            break;

        }
        return clazz;
    }

}
