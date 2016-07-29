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

package com.incadencecorp.coalesce.framework.iterators;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceHistory;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;

/**
 * This abstract class recursively iterates over an entity. If you override a
 * visit method and return false all of its children wont be visited.
 * 
 * @author n78554
 * @param <T>
 *
 */
public abstract class CoalesceIterator<T> {

    /**
     * Iterates through a CoalesceEntity and calls visit on each element.
     * 
     * @param entity
     */
    protected final void processAllElements(CoalesceEntity entity, T param) throws CoalesceException
    {
        process(entity, false, param);
    }

    /**
     * Iterates through a CoalesceEntity and calls visit on each active element.
     * 
     * @param entity
     */
    protected final void processActiveElements(CoalesceEntity entity, T param) throws CoalesceException
    {
        process(entity, true, param);
    }

    private boolean process(CoalesceObject coalesceObject, boolean onlyActive, T param) throws CoalesceException
    {

        boolean isSuccessful = false;

        if (!onlyActive || !coalesceObject.isMarkedDeleted())
        {
            isSuccessful = visit(coalesceObject, param);

            // Successful?
            if (isSuccessful)
            {
                // Yes; Iterate Through Children
                for (CoalesceObject childObject : coalesceObject.getChildCoalesceObjects().values())
                {
                    process(childObject, onlyActive, param);
                }
            }

        }

        return isSuccessful;

    }

    private boolean visit(CoalesceObject coalesceObject, T param) throws CoalesceException
    {
        boolean isSuccessful = false;

        if (coalesceObject instanceof CoalesceEntity)
        {
            isSuccessful = visitCoalesceEntity((CoalesceEntity) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceLinkageSection)
        {
            isSuccessful = visitCoalesceLinkageSection((CoalesceLinkageSection) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceLinkage)
        {
            isSuccessful = visitCoalesceLinkage((CoalesceLinkage) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceSection)
        {
            isSuccessful = visitCoalesceSection((CoalesceSection) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceRecordset)
        {
            isSuccessful = visitCoalesceRecordset((CoalesceRecordset) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceFieldDefinition)
        {
            isSuccessful = visitCoalesceFieldDefinition((CoalesceFieldDefinition) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceRecord)
        {
            isSuccessful = visitCoalesceRecord((CoalesceRecord) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceField)
        {
            isSuccessful = visitCoalesceField((CoalesceField<?>) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceFieldHistory)
        {
            isSuccessful = visitCoalesceFieldHistory((CoalesceFieldHistory) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceConstraint)
        {
            isSuccessful = visitCoalesceContraint((CoalesceConstraint) coalesceObject, param);
        }
        else if (coalesceObject instanceof CoalesceHistory)
        {
            isSuccessful = visitCoalesceHistory((CoalesceHistory) coalesceObject, param);
        }

        return isSuccessful;

    }

    protected boolean visitCoalesceEntity(CoalesceEntity entity, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceLinkage(CoalesceLinkage linkage, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceSection(CoalesceSection section, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceFieldDefinition(CoalesceFieldDefinition definition, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceRecord(CoalesceRecord record, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceField(CoalesceField<?> field, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceFieldHistory(CoalesceFieldHistory history, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceContraint(CoalesceConstraint constraint, T param) throws CoalesceException
    {
        return true;
    }

    protected boolean visitCoalesceHistory(CoalesceHistory history, T param) throws CoalesceException
    {
        return true;
    }

}
