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

package com.incadencecorp.coalesce.services.crud.service.data.model;

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.services.crud.service.data.api.ICoalesceEnumerationValue;

public class EnumerationValuePojo implements ICoalesceEnumerationValue {

    private int oridinal;
    private String value;
    private ECoalesceObjectStatus status = ECoalesceObjectStatus.ACTIVE;
    private String description = ""; 
    private Map<String, String> associatedValues = new HashMap<String, String>();
    
    @Override
    public int getOrdinal() throws CoalesceDataFormatException
    {
        return oridinal;
    }

    @Override
    public void setOrdinal(int value)
    {
        oridinal = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }

    @Override
    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public ECoalesceObjectStatus getStatus()
    {
        return status;
    }

    @Override
    public void setStatus(ECoalesceObjectStatus value)
    {
        status = value;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public void setDescription(String value)
    {
        description = value;
    }

    @Override
    public Map<String, String> getAssociatedValues() throws CoalesceDataFormatException
    {
        return associatedValues;
    }

    @Override
    public void setAssociatedValues(Map<String, String> value)
    {
        associatedValues.clear();
        associatedValues.putAll(value);
    }

}
