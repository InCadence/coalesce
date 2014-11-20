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
 *
 */
public class CoalesceParameter {

    private String _value;
    private int _type;

    public CoalesceParameter(String value)
    {
        this(value, Types.CHAR);
    }

    public CoalesceParameter(DateTime value)
    {
        this(JodaDateTimeHelper.toMySQLDateTime(value));
    }

    public CoalesceParameter(String value, int type)
    {
        if (value != null)
        {
            value = value.trim();
        }
        
        _value = value;
        _type = type;
    }

    public String getValue()
    {
        return _value;
    }

    public int getType()
    {
        return _type;
    }

}
