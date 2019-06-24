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

import com.incadencecorp.coalesce.common.helpers.CoalesceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This iterator is responsible for reverting to older version of an entity. It
 * iterates through each field and reverts any marked with a version higher then
 * specified.
 *
 * @author n78554
 */
public class CoalesceIteratorGetVersion extends CoalesceIterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceIteratorGetVersion.class);

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private int version;
    private List<CoalesceObject> objectsToPrune;

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Reverts the entity to specified version and removes all history leaving
     * the original entity alone.
     *
     * @param entity  to revert
     * @param version to revert to
     * @return a new instance of the entity at the correct version.
     */
    public CoalesceEntity getClonedVersion(final CoalesceEntity entity, final int version)
    {
        CoalesceEntity cloned = new CoalesceEntity();
        cloned.initialize(entity.toXml());

        getVersion(cloned, version);

        return cloned;
    }

    /**
     * Reverts the entity to specified version and removes all history.
     *
     * @param entity  to revert
     * @param version to revert to
     */
    public void getVersion(final CoalesceEntity entity, final int version)
    {
        this.version = version;
        objectsToPrune = new ArrayList<>();

        // Valid Version?
        if (!entity.isValidObjectVersion(version))
        {
            throw new IllegalArgumentException("Invalid Version");
        }

        // Set internal creator variable before pruning history
        entity.getCreatedBy();

        processAllElements(entity);

        entity.setObjectVersion(version);

        for (CoalesceObject object : objectsToPrune)
        {
            if (object.getParent() != null)
            {
                object.getParent().pruneCoalesceObject(object);
            }
        }
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    protected boolean visitCoalesceField(CoalesceField<?> field)
    {
        boolean validHistory = false;

        // Version Newer?
        if (field.getObjectVersion() > version)
        {

            // Yes; Roll Back to History
            CoalesceFieldHistory history = field.getHistoryRecord(field.getPreviousHistoryKey());

            while (history != null)
            {

                // History Valid?
                if (history.getObjectVersion() <= version)
                {
                    Map<QName, String> attributes = history.getAttributes();
                    attributes.remove(new QName("key"));

                    for (Map.Entry<QName, String> attribute : attributes.entrySet())
                    {
                        field.setAttribute(attribute.getKey().toString(), attribute.getValue());
                    }

                    validHistory = true;

                    LOGGER.debug("Rolling back {} field to version {}: {}",
                                 field.getName(),
                                 history.getObjectVersion(),
                                 history.getValue());

                    // Exit Loop
                    break;
                }

                // Get Next History Entry
                history = field.getHistoryRecord(history.getPreviousHistoryKey());

            }

            // Valid History?
            if (!validHistory)
            {
                LOGGER.debug("Rolling back {} field to null", field.getName());

                // No; Clear Field
                field.setBaseValue(null);
            }

        }

        // Clear History Records
        field.clearHistory();

        // Don't Process Children
        return false;
    }

    @Override
    protected boolean visitCoalesceLinkage(CoalesceLinkage linkage)
    {
        return visitCoalesceObjectHistory(linkage);
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity)
    {
        return visitCoalesceObjectHistory(entity);
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section)
    {
        return visitCoalesceObjectHistory(section);
    }

    @Override
    protected boolean visitCoalesceSection(CoalesceSection section)
    {
        return visitCoalesceObjectHistory(section);
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset)
    {
        return visitCoalesceObjectHistory(recordset);
    }

    @Override
    protected boolean visitCoalesceRecord(CoalesceRecord record)
    {
        return visitCoalesceObjectHistory(record);
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private boolean visitCoalesceObjectHistory(CoalesceObjectHistory object)
    {

        boolean validHistory = true;

        // Version Newer?
        if (object.getObjectVersion() > version)
        {

            validHistory = false;

            // Yes; Roll Back to History
            CoalesceHistory history = object.getHistoryRecord(object.getPreviousHistoryKey());

            while (history != null)
            {

                // History Valid?
                if (history.getObjectVersion() <= version)
                {
                    Map<QName, String> attributes = history.getAttributes();
                    attributes.remove(new QName("key"));

                    for (Map.Entry<QName, String> attribute : attributes.entrySet())
                    {
                        object.setAttribute(attribute.getKey().toString(), attribute.getValue());
                    }

                    validHistory = true;

                    LOGGER.debug("Rolling back {} to version {}: {}", object.getName(), history.getObjectVersion(), history.getStatus());

                    // Exit Loop
                    break;
                }

                // Get Next History Entry
                history = object.getHistoryRecord(history.getPreviousHistoryKey());

            }

            // Valid History?
            if (!validHistory)
            {
                LOGGER.debug("Pruning {}", object.getName());

                // Mark For Removal
                objectsToPrune.add(object);
            }

        }

        // Clear History Records
        object.clearHistory();

        // Don't Process Children
        return validHistory;
    }

}
