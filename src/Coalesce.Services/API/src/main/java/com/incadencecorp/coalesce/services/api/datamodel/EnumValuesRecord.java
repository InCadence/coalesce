/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.services.api.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.datamodel.api.record.IEnumValuesRecord;
import com.incadencecorp.coalesce.datamodel.impl.pojo.record.EnumValuesPojoRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * @author derek
 */
public class EnumValuesRecord extends EnumValuesPojoRecord {

    public EnumValuesRecord()
    {
        super();
    }

    public EnumValuesRecord(IEnumValuesRecord record) throws CoalesceDataFormatException
    {
        super(record);
    }

    public Map<String, String> getAssociatedValues()
    {
        String[] keys = getAssociatedkeys();
        String[] vals = getAssociatedvalues();

        Map<String, String> results = new HashMap<>();

        for (int ii = 0; ii < keys.length && ii < vals.length; ii++)
        {
            results.put(keys[ii], vals[ii]);
        }

        return results;
    }

    public void setAssociatedValues(Map<String, String> value)
    {
        String[] keys = new String[value.size()];
        String[] vals = new String[value.size()];

        int ii = 0;

        for (Map.Entry<String, String> entry : value.entrySet())
        {

            keys[ii] = entry.getKey();
            vals[ii] = entry.getValue();

            ii++;
        }

        setAssociatedkeys(keys);
        setAssociatedvalues(vals);
    }

    @JsonIgnore
    @Override
    public String[] getAssociatedkeys()
    {
        return super.getAssociatedkeys();
    }

    @JsonIgnore
    @Override
    public void setAssociatedkeys(String[] value)
    {
        super.setAssociatedkeys(value);
    }

    @JsonIgnore
    @Override
    public String[] getAssociatedvalues()
    {
        return super.getAssociatedvalues();
    }

    @JsonIgnore
    @Override
    public void setAssociatedvalues(String[] value)
    {
        super.setAssociatedvalues(value);
    }
}
