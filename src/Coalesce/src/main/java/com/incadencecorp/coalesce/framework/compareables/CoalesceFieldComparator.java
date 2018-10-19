/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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
package com.incadencecorp.coalesce.framework.compareables;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Compares two coalesce entities base on the specified fields.
 *
 * @author n78554
 */
public class CoalesceFieldComparator extends CoalesceComparator<CoalesceEntity> {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private List<String> namePathList;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    public CoalesceFieldComparator(String fullNamePath)
    {
        addField(fullNamePath);
    }

    public CoalesceFieldComparator(String section, String recordset, String fieldname)
    {
        addField(section, recordset, fieldname);
    }
    
    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Add addition field to compare against.
     *
     * @param fullNamePath
     */
    public void addField(String fullNamePath)
    {

        if (namePathList == null)
        {
            namePathList = new ArrayList<>();
        }

        namePathList.add(fullNamePath);
    }

    /**
     * Add addition field to compare against.
     *
     * @param section
     * @param recordset
     * @param fieldname
     */
    public void addField(String section, String recordset, String fieldname)
    {
        addField("/" + section + "/" + recordset + "/" + recordset + " Record/" + fieldname);
    }

    /**
     * Clears fields.
     */
    public void clearFields()
    {
        namePathList.clear();
    }

    /*--------------------------------------------------------------------------
    Overrides
    --------------------------------------------------------------------------*/

    @Override
    public int compare(CoalesceEntity entity1, CoalesceEntity entity2)
    {

        int result = 0;

        // Compare fields until one does not match
        for (String namePath : namePathList)
        {

            // Get Fields
            CoalesceField<?> field1 = entity1.getCoalesceFieldForNamePath(namePath);
            CoalesceField<?> field2 = entity2.getCoalesceFieldForNamePath(namePath);

            // Compare Values
            result = compare(field1, field2);

            // Equal?
            if (result != 0)
            {
                // No; Return result, otherwise compare next field.
                break;
            }

        }

        return result;

    }
    
    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private int compare(CoalesceField<?> field1, CoalesceField<?> field2)
    {

        // Valid Fields?
        if (field1 == null || field2 == null)
        {
            throw new RuntimeException("CoalesceFieldComparator - Invalid Field");
        }

        // Fields have common data types?
        if (field1.getDataType() != field2.getDataType())
        {
            throw new RuntimeException("CoalesceFieldComparator - Data Types Differ");
        }

        try
        {

            switch (field1.getDataType())
            {
            case BOOLEAN_TYPE:
                return Boolean.compare((Boolean) field1.getValue(), (Boolean) field2.getValue());

            case DATE_TIME_TYPE:
                return ((DateTime) field1.getValue()).compareTo((DateTime) field2.getValue());

            case FLOAT_TYPE:
                return Float.compare((Float) field1.getValue(), (Float) field2.getValue());

            case LONG_TYPE:
                return Long.compare((Long) field1.getValue(), (Long) field2.getValue());

            case INTEGER_TYPE:
                return Integer.compare((Integer) field1.getValue(), (Integer) field2.getValue());

            case GUID_TYPE:
                return ((UUID) field1.getValue()).compareTo((UUID) field2.getValue());

            case URI_TYPE:
            case STRING_TYPE:
                String value1 = field1.getBaseValue();
                String value2 = field2.getBaseValue();

                if (value1 == null)
                {
                    return value2 == null ? 0 : 1;
                }
                else
                {
                    return value1.compareTo(value2);
                }

            case DOUBLE_TYPE:
                return Double.compare((Double) field1.getValue(), (Double) field2.getValue());

            default:
                throw new RuntimeException(
                        "CoalesceFieldComparator - " + field1.getDataType().toString() + " Compare Not Implemented");
            }

        }
        catch (CoalesceDataFormatException e)
        {
            throw new RuntimeException(e);
        }

    }

}
