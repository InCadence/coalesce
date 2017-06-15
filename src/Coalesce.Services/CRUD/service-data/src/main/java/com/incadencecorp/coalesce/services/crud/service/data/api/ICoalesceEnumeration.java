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

package com.incadencecorp.coalesce.services.crud.service.data.api;

import java.util.List;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;


public interface ICoalesceEnumeration<T extends ICoalesceEnumerationValue> {

    String getEnumName();

    void setEnumName(String value);

    String getDescription();

    void setDescription(String value);

    DateTime getLastModified();

    void setLastModified(DateTime value);

    String getModifiedBy();

    void setModifiedBy(String value);

    Marking getClassification();

    void setClassification(Marking value);

    List<T> getValues();

    void addValues(List<T> values) throws CoalesceDataFormatException;
    
    void updateValue(T value) throws CoalesceDataFormatException;

}
