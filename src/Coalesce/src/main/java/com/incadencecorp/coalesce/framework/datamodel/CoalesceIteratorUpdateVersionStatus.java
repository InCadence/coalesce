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

import com.incadencecorp.coalesce.common.helpers.CoalesceIterator;

/**
 * This iterator is responsible for deleting and restoring versions of an
 * entity. It iterate through each field and set the status to active / deleted
 * if the versions match. This does not increment the over all version of the
 * entity.
 * 
 * @author n78554
 *
 */
public class CoalesceIteratorUpdateVersionStatus extends CoalesceIterator {

    /*--------------------------------------------------------------------------
    Private Memebers
    --------------------------------------------------------------------------*/

    private int version;
    private ECoalesceObjectStatus status;

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Deletes the specified version.
     * 
     * @param entity
     * @param version
     * @return a new instance of the entity at the correct version.
     */
    public CoalesceEntity deleteClone(final CoalesceEntity entity, final int version)
    {
        // Cannot delete / restore original or invalid versions
        if (version == 1 || !entity.isValidObjectVersion(version))
        {
            throw new IllegalArgumentException("Invalid Version");
        }

        CoalesceEntity cloned = new CoalesceEntity();
        cloned.initialize(entity.toXml());

        update(cloned, version, ECoalesceObjectStatus.DELETED);

        return cloned;
    }

    /**
     * Deletes the specified version.
     * 
     * @param entity
     * @param version
     */
    public void delete(final CoalesceEntity entity, final int version)
    {
        // Cannot delete / restore original or invalid versions
        if (version == 1 || !entity.isValidObjectVersion(version))
        {
            throw new IllegalArgumentException("Invalid Version");
        }

        update(entity, version, ECoalesceObjectStatus.DELETED);
    }

    /**
     * Restores the specified version.
     * 
     * @param entity
     * @param version
     * @return a new instance of the entity at the correct version.
     */
    public CoalesceEntity restoreClone(final CoalesceEntity entity, final int version)
    {
        // Valid Version
        if (version > entity.getObjectVersion() || version < 1)
        {
            throw new IllegalArgumentException("Invalid Version");
        }

        CoalesceEntity cloned = new CoalesceEntity();
        cloned.initialize(entity.toXml());

        update(cloned, version, ECoalesceObjectStatus.ACTIVE);

        return cloned;
    }

    /**
     * Restores the specified version.
     * 
     * @param entity
     * @param version
     */
    public void restore(final CoalesceEntity entity, final int version)
    {
        // Valid Version
        if (version > entity.getObjectVersion() || version < 1)
        {
            throw new IllegalArgumentException("Invalid Version");
        }

        update(entity, version, ECoalesceObjectStatus.ACTIVE);
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity)
    {
        return update(entity);
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section)
    {
        return update(section);
    }

    @Override
    protected boolean visitCoalesceLinkage(CoalesceLinkage linkage)
    {
        return update(linkage);
    }

    @Override
    protected boolean visitCoalesceSection(CoalesceSection section)
    {
        return update(section);
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset)
    {
        return update(recordset);
    }

    @Override
    protected boolean visitCoalesceFieldDefinition(CoalesceFieldDefinition definition)
    {
        return update(definition);
    }

    @Override
    protected boolean visitCoalesceRecord(CoalesceRecord record)
    {
        return update(record);
    }

    @Override
    protected boolean visitCoalesceField(CoalesceField<?> field)
    {
        return update(field);
    }

    @Override
    protected boolean visitCoalesceFieldHistory(CoalesceFieldHistory history)
    {
        return update(history);
    }

    @Override
    protected boolean visitCoalesceContraint(CoalesceConstraint constraint)
    {
        return update(constraint);
    }

    @Override
    protected boolean visitCoalesceHistory(CoalesceHistory history)
    {
        return update(history);
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private boolean update(CoalesceObject object)
    {
        if (object.getObjectVersion() == version)
        {
            object.setObjectVersionStatus(status);
        }

        return true;
    }

    private void update(final CoalesceEntity entity, final int version, final ECoalesceObjectStatus status)
    {
        this.version = version;
        this.status = status;

        processAllElements(entity);
    }

}
