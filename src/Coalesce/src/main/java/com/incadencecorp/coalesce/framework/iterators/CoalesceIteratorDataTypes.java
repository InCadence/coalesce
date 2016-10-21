/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.DefaultNormalizer;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ConstraintType;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ConstraintEnumerationProviderImpl;

/**
 * This iterator returns all the data types within a given entity.
 * 
 * @author n78554
 *
 */
public class CoalesceIteratorDataTypes extends CoalesceIterator<Map<String, ECoalesceFieldDataTypes>> {

    private ICoalesceNormalizer normalizer;

    /**
     * Constructs a new instance with the provided normalizer.
     * 
     * @param normalizer
     */
    public CoalesceIteratorDataTypes(ICoalesceNormalizer normalizer)
    {
        if (normalizer == null)
        {
            normalizer = new DefaultNormalizer();
        }

        this.normalizer = normalizer;
    }

    /**
     * @param entity
     * @return a map of datatypes used by this entity.
     * @throws CoalesceException
     */
    public Map<String, ECoalesceFieldDataTypes> getDataTypes(CoalesceEntity entity) throws CoalesceException
    {
        Map<String, ECoalesceFieldDataTypes> results = new HashMap<String, ECoalesceFieldDataTypes>();

        processAllElements(entity, results);

        return results;
    }

    /**
     * @param template
     * @return a map of datatypes used by this template.
     * @throws CoalesceException
     */
    public Map<String, ECoalesceFieldDataTypes> getDataTypes(CoalesceEntityTemplate template) throws CoalesceException
    {
        return getDataTypes(template.createNewEntity());
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, Map<String, ECoalesceFieldDataTypes> param)
    {
        Map<String, String> enumerationLookups = new HashMap<String, String>();
        
        for (CoalesceFieldDefinition definition : recordset.getFieldDefinitions())
        {
            String key = normalizer.normalize(recordset, definition);
            
            param.put(key, definition.getDataType());

            // Is Enumeration?
            if (definition.getDataType() == ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE
                    || definition.getDataType() == ECoalesceFieldDataTypes.ENUMERATION_TYPE)
            {
                // Yes; Update Lookup Table
                for (CoalesceConstraint constraint : definition.getConstraints())
                {
                    if (constraint.getConstraintType() == ConstraintType.ENUMERATION)
                    {
                        ConstraintEnumerationProviderImpl provider = EnumerationProviderUtil.getProvider(ConstraintEnumerationProviderImpl.class);
                        if (provider != null)
                        {
                            provider.add(constraint);
                        }
                        
                        enumerationLookups.put(key, constraint.getValue());
                        break;
                    }
                }
            }

        }

        EnumerationProviderUtil.addLookupEntries(enumerationLookups);

        // Stop recursive processing
        return false;
    }

}
