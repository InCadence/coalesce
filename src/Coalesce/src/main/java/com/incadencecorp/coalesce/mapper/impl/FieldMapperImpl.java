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

package com.incadencecorp.coalesce.mapper.impl;

import org.apache.commons.lang3.NotImplementedException;

import com.incadencecorp.coalesce.api.ICoalesceMapper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBinaryField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceGUIDField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceGUIDListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLineStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalescePolygonField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

/**
 * This implementation maps Coalesce types to Field classes.
 * @author n78554
 *
 */
public class FieldMapperImpl implements ICoalesceMapper<CoalesceField<?>> {

    @Override
    public CoalesceField<?> map(ECoalesceFieldDataTypes dataType)
    {
        switch (dataType) {

        case STRING_TYPE:
        case URI_TYPE:
            return new CoalesceStringField();

        case DATE_TIME_TYPE:
            return new CoalesceDateTimeField();

        case FILE_TYPE:
            return new CoalesceFileField();

        case BINARY_TYPE:
            return new CoalesceBinaryField();

        case BOOLEAN_TYPE:
            return new CoalesceBooleanField();

        case BOOLEAN_LIST_TYPE:
            return new CoalesceBooleanListField();

        case INTEGER_TYPE:
            return new CoalesceIntegerField();

        case GUID_TYPE:
            return new CoalesceGUIDField();

        case GEOCOORDINATE_TYPE:
            return new CoalesceCoordinateField();

        case GEOCOORDINATE_LIST_TYPE:
            return new CoalesceCoordinateListField();

        case POLYGON_TYPE:
            return new CoalescePolygonField();

        case LINE_STRING_TYPE:
            return new CoalesceLineStringField();

        case CIRCLE_TYPE:
            return new CoalesceCircleField();

        case DOUBLE_TYPE:
            return new CoalesceDoubleField();

        case FLOAT_TYPE:
            return new CoalesceFloatField();

        case LONG_TYPE:
            return new CoalesceLongField();

        case DOUBLE_LIST_TYPE:
            return new CoalesceDoubleListField();

        case FLOAT_LIST_TYPE:
            return new CoalesceFloatListField();

        case GUID_LIST_TYPE:
            return new CoalesceGUIDListField();

        case INTEGER_LIST_TYPE:
            return new CoalesceIntegerListField();

        case LONG_LIST_TYPE:
            return new CoalesceLongListField();

        case STRING_LIST_TYPE:
            return new CoalesceStringListField();

        case ENUMERATION_TYPE:
            return new CoalesceEnumerationField();

        case ENUMERATION_LIST_TYPE:
            return new CoalesceEnumerationListField();

        default:
            throw new NotImplementedException(dataType + " not implemented");

        }
    }
}
