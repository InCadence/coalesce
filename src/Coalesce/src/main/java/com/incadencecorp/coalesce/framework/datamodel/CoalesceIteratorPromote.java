/*-----------------------------------------------------------------------------'
 Copyright 2015 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.incadencecorp.coalesce.common.helpers.CoalesceIterator;

/**
 * This iterator is responsible for promoting older version of an entity. It
 * iterates through each field and set any with a version higher then specified
 * with the value of the specified version or lower. Values which are replace
 * are inserted into the history and the overall entity's version is
 * incremented.
 * 
 * @author n78554
 */
public class CoalesceIteratorPromote extends CoalesceIterator {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private int versionOfPromote;
    private int versionToPromote;
    private String user;
    private String ip;

    private static final List<String> ATTRIBUTES_TO_IIGNORE = Arrays.asList(new String[] {
            "previoushistorykey", "modifiedby", "modifiedbyip", "objectversion", "key"
    });

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Reverts the entity to specified version and removes all history leaving
     * the original entity alone.
     * 
     * @param entity
     * @param version
     * @param user
     * @param ip
     * @return a new instance of the entity at the correct version.
     */
    public CoalesceEntity promoteClone(final CoalesceEntity entity, final int version, final String user, final String ip)
    {
        CoalesceEntity cloned = new CoalesceEntity();
        cloned.initialize(entity.toXml());

        promote(cloned, version, user, ip);

        return cloned;
    }

    /**
     * Reverts the entity to specified version and removes all history.
     * 
     * @param entity
     * @param version
     * @param user
     * @param ip
     */
    public void promote(final CoalesceEntity entity, final int version, final String user, final String ip)
    {
        // Promote Current? (Do nothing if yes)
        if (version < entity.getObjectVersion())
        {
            // No; Is Valid Version?
            if (!entity.isValidObjectVersion(version))
            {
                throw new IllegalArgumentException("Invalid Version");
            }

            versionOfPromote = entity.getObjectVersion() + 1;
            versionToPromote = version;

            this.user = user;
            this.ip = ip;

            processAllElements(entity);

            entity.setObjectVersion(versionOfPromote);
        } 
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

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

    @Override
    protected boolean visitCoalesceField(CoalesceField<?> field)
    {
        boolean validHistory = false;

        // Version Newer?
        if (field.getObjectVersion() > versionToPromote)
        {
            // Create History
            field.setSuspendHistory(false);
            field.createHistory(user, ip, versionOfPromote);
            field.setSuspendHistory(true);

            // Yes; Roll Back to History
            CoalesceFieldHistory history = field.getHistoryRecord(field.getPreviousHistoryKey());

            while (history != null)
            {

                // History Valid?
                if (history.getObjectVersion() <= versionToPromote)
                {
                    Map<QName, String> attributes = history.getAttributes();

                    // Remove attributes that should not be copied
                    for (String attributeToIgnore : ATTRIBUTES_TO_IIGNORE)
                    {
                        attributes.remove(new QName(attributeToIgnore));
                    }

                    // Apply History Attributes as Current
                    for (Map.Entry<QName, String> attribute : attributes.entrySet())
                    {
                        field.setAttribute(attribute.getKey().toString(), attribute.getValue());
                    }

                    validHistory = true;

                    // Exit Loop
                    break;
                }

                // Get Next History Entry
                history = field.getHistoryRecord(history.getPreviousHistoryKey());

            }

            // Valid History?
            if (!validHistory)
            {
                // No; Clear Field
                field.setBaseValue(null);
            }
            
            field.updateLastModified();

        }

        // Don't Process Children
        return false;
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private boolean visitCoalesceObjectHistory(CoalesceObjectHistory object)
    {

        boolean validHistory = false;

        // Version Newer?
        if (object.getObjectVersion() > versionToPromote)
        {
            // Create History
            object.setSuspendHistory(false);
            object.createHistory(user, ip, versionOfPromote);
            object.setSuspendHistory(true);

            // Yes; Roll Back to History
            CoalesceHistory history = object.getHistoryRecord(object.getPreviousHistoryKey());

            while (history != null)
            {

                // History Valid?
                if (history.getObjectVersion() <= versionToPromote)
                {
                    Map<QName, String> attributes = history.getAttributes();

                    // Remove attributes that should not be copied
                    for (String attributeToIgnore : ATTRIBUTES_TO_IIGNORE)
                    {
                        attributes.remove(new QName(attributeToIgnore));
                    }

                    // Apply History Attributes as Current
                    for (Map.Entry<QName, String> attribute : attributes.entrySet())
                    {
                        object.setAttribute(attribute.getKey().toString(), attribute.getValue());
                    }

                    validHistory = true;

                    // Exit Loop
                    break;
                }

                // Get Next History Entry
                history = object.getHistoryRecord(history.getPreviousHistoryKey());

            }

            // Valid History?
            if (!validHistory)
            {
                // Mark as Deleted
                object.setStatus(ECoalesceObjectStatus.DELETED);
            }
            
            object.updateLastModified();

        }

        // Don't Process Children
        return true;
    }

}
