/**
 * 
 */
package com.incadencecorp.coalesce.framework.persistance;

import java.sql.Types;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;

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

/**
 * Represents parameters used when performing queries.
 */
public class CoalesceParameter {

    private String _value;
    private int _type;

    /**
     * Creates an Integer parameter as type {@link Types#INTEGER}.
     * 
     * @param value
     */
    public CoalesceParameter(Integer value)
    {
        this(value.toString(), Types.INTEGER);
    }
    
    /**
     * Creates a Boolean parameter as type {@link Types#BOOLEAN}
     * @param value
     */
    public CoalesceParameter(Boolean value)
    {
        this(value.toString(), Types.BOOLEAN);
    }

    /**
     * Creates a String parameter as type {@link Types#CHAR}.
     * 
     * @param value
     */
    public CoalesceParameter(String value)
    {
        this(value, Types.CHAR);
    }

    /**
     * Creates a formatted String parameter as type {@link Types#CHAR}.
     * 
     * @param value
     */
    public CoalesceParameter(DateTime value)
    {
        this(JodaDateTimeHelper.toMySQLDateTime(value));
    }

    /**
     * Creates a custom parameter type.
     * 
     * @param value
     * @param type {@link Types}
     */
    public CoalesceParameter(String value, int type)
    {
        if (value != null)
        {
            value = value.trim();
        }

        _value = value;
        _type = type;
    }

    /**
     * @return the parameter's value.
     */
    public String getValue()
    {
        return _value;
    }

    /**
     * @return the parameter's type.
     */
    public int getType()
    {
        return _type;
    }

}
