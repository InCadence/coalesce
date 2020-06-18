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

package com.incadencecorp.coalesce.services.search.service.data.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.opengis.style.Description;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchCriteria {

    @Schema(description = "Unique identifier")
    private String key;
    @Schema(description = "Specifies the recordset that contains the field.")
    private String recordset;
    @Schema(description = "Specifies the field")
    private String field;
    @Schema(description = "Specifies the compare options that should be performed following OGC standards.")
    private String operator;
    @Schema(description = "Specifies the values that should be compared.")
    private List<String> values = new ArrayList<>();
    @Schema(description = "Specifies whether NOT should be included within this criteria.")
    private boolean isNot;
    @Schema(description = "Specifies whether this criteria should be case sensitive (Not all datastores support this)")
    private boolean matchCase;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getRecordset()
    {
        return recordset;
    }

    public void setRecordset(String recordset)
    {
        this.recordset = recordset;
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String value)
    {
        this.operator = value;
    }

    public String getValue()
    {
        return values.size() >= 1 ? values.get(0) : null;
    }

    public void setValue(String value)
    {
        setValues(Collections.singletonList(value));
    }

    public void setValues(List<String> values)
    {
        this.values.addAll(values);
    }

    public List<String> getValues()
    {
        return values;
    }

    public boolean isMatchCase()
    {
        return matchCase;
    }

    public void setMatchCase(boolean matchCase)
    {
        this.matchCase = matchCase;
    }

    public boolean isNot()
    {
        return isNot;
    }

    public void setNot(boolean not)
    {
        isNot = not;
    }
}
