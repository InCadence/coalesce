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

package com.incadencecorp.coalesce.framework.iterators;

import java.util.List;
import java.util.Map;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;

/**
 * This iterator prunes fields specified via the map.
 * 
 * @author n78554
 */
public class CoalescePrunerIterator extends CoalesceIterator<Map<String, List<String>>> {

    private EMode mode = EMode.PRUNE;

    /**
     * Sets the mode this iterator will use.
     * 
     * @param value
     */
    public void setMode(EMode value)
    {
        mode = value;
    }

    /**
     * @param fieldMap Map<RecordSet Name, List<Field Name>>
     * @param entities
     * @return a copy of entities with the specified fields pruned.
     * @throws CoalesceException
     */
    public CoalesceEntity[] iterateClone(final Map<String, List<String>> fieldMap, final CoalesceEntity... entities)
            throws CoalesceException
    {
        CoalesceEntity[] results = new CoalesceEntity[entities.length];

        for (int ii = 0; ii < entities.length; ii++)
        {
            CoalesceEntity cloned = new CoalesceEntity();
            cloned.initialize(entities[ii].toXml());

            results[ii] = cloned;
        }

        iterate(fieldMap, results);

        return results;
    }

    /**
     * Modified the entities pruning the fields specified.
     * 
     * @param fieldMap Map<RecordSet Name, List<Field Name>>
     * @param entities
     * @throws CoalesceException
     */
    public void iterate(final Map<String, List<String>> fieldMap, CoalesceEntity... entities) throws CoalesceException
    {
        for (CoalesceEntity entity : entities)
        {
            if (!entity.isInitialized())
            {
                entity.initialize();
            }

            processActiveElements(entity, fieldMap);
        }
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, Map<String, List<String>> param)
            throws CoalesceException
    {
        if (param.containsKey(recordset.getName()))
        {
            for (CoalesceRecord record : recordset.getAllRecords())
            {
                for (String fieldname : param.get(recordset.getName()))
                {
                    CoalesceField<?> field = record.getFieldByName(fieldname);

                    if (field != null)
                    {
                        switch (mode) {
                        case CLEAR:
                            record.getFieldByName(fieldname).setValue(null);
                            break;
                        case PRUNE:
                            record.pruneCoalesceObject(record.getFieldByName(fieldname));
                            break;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Modes supported by this iterator.
     * 
     * @author n78554
     */
    public enum EMode
    {
        /**
         * Remove the field complete.
         */
        PRUNE,
        /**
         * Sets the field to null.
         */
        CLEAR;
    }

}
