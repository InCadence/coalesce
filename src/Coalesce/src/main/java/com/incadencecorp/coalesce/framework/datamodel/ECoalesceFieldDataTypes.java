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

package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Data types supported by Coalesce.
 * 
 * @author n78554
 */
@XmlType(name = "ECoalesceFieldDataTypes", namespace = "http://framework.coalesce.incadencecorp.com/datamodel")
@XmlEnum
public enum ECoalesceFieldDataTypes
{

    STRING_TYPE("string"), 
    STRING_LIST_TYPE("stringlist"), 
    DATE_TIME_TYPE("datetime"), 
    URI_TYPE("uri"), 
    BINARY_TYPE("binary"), 
    BOOLEAN_TYPE("boolean"), 
    BOOLEAN_LIST_TYPE("booleanlist"), 
    INTEGER_TYPE("integer"), 
    INTEGER_LIST_TYPE("integerlist"), 
    GUID_TYPE("guid"), 
    GUID_LIST_TYPE("guidlist"), 
    GEOCOORDINATE_TYPE("geocoordinate"), 
    GEOCOORDINATE_LIST_TYPE("geocoordinatelist"), 
    LINE_STRING_TYPE("linestring"), 
    POLYGON_TYPE("polygon"), 
    CIRCLE_TYPE("circle"),
    FILE_TYPE("file"), 
    DOUBLE_TYPE("double"), 
    DOUBLE_LIST_TYPE("doublelist"), 
    FLOAT_TYPE("float"), 
    FLOAT_LIST_TYPE("floatlist"), 
    LONG_TYPE("long"), 
    LONG_LIST_TYPE("longlist"),
    ENUMERATION_TYPE("enum"),
    ENUMERATION_LIST_TYPE("enumlist");

    private String _label;

    /**
     * A mapping between the string representation and its corresponding Status
     * to facilitate lookup by code.
     */
    private static Map<String, ECoalesceFieldDataTypes> _mapping;

    private ECoalesceFieldDataTypes(String label)
    {
        this._label = label;
    }

    /**
     * @return the Label property of the ECoalesceFieldDataTypes type.
     */
    public String getLabel()
    {
        return _label;
    }

    /**
     * @return whether the data type is a list type.
     */
    public boolean isListType()
    {
        return _label.endsWith("list") && this.compareTo(GEOCOORDINATE_LIST_TYPE) != 0;
    }

    /**
     * @param coalesceType allowed object is {@link String }
     * @return the ECoalesceFieldDataTypes type for the String type parameter.
     */
    public static ECoalesceFieldDataTypes getTypeForCoalesceType(String coalesceType)
    {
        ECoalesceFieldDataTypes value = getMapping().get(coalesceType.trim().toLowerCase());

        if (value == null)
            value = ECoalesceFieldDataTypes.STRING_TYPE;

        return value;
    }


    private static synchronized Map<String, ECoalesceFieldDataTypes> getMapping()
    {

        if (_mapping == null)
        {

            _mapping = new HashMap<String, ECoalesceFieldDataTypes>();

            for (ECoalesceFieldDataTypes s : values())
            {
                _mapping.put(s._label.trim().toLowerCase(), s);
            }

        }

        return _mapping;
    }

}
