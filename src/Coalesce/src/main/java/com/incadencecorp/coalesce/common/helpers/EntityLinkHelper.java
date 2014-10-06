package com.incadencecorp.coalesce.common.helpers;

import java.util.Locale;

import org.apache.commons.lang.NullArgumentException;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceDataObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.datamodel.ICoalesceDataObject;

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

/**
 * Provides helper methods for linking two {@link CoalesceEntity} objects together.
 * 
 * @author InCadence
 *
 */
public class EntityLinkHelper {

    // ----------------------------------------------------------------------//
    // Private and protected Objects
    // ----------------------------------------------------------------------//

    private static final String MODULE_NAME = "com.incadencecorp.coalesce.common.helpers.EntityLinkHelper";

    // ----------------------------------------------------------------------//
    // Factory and Initialization
    // ----------------------------------------------------------------------//

    // Make static class
    private EntityLinkHelper()
    {
    }

    // -----------------------------------------------------------------------//
    // Public Static Methods
    // -----------------------------------------------------------------------//

    /**
     * Links two entities together with a default Unclassified classification and {@link Locale#US}. If
     * <code>updateExisting</code> is <code>true</code> then the first linkage found between the two entities with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param updateExisting whether to update an existing linkage.
     * @return <code>true</code> if the linkage is create or updated successfully.
     */
    public static boolean linkEntities(CoalesceEntity entity1, ELinkTypes linkType, CoalesceEntity entity2, Boolean updateExisting)
    {

        return linkEntities(entity1, linkType, entity2, new Marking("(U)"), "", Locale.US, updateExisting);

    }

    /**
     * Links two entities together. If <code>updateExisting</code> is <code>true</code> then the first linkage found between
     * the two entities with the same {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be updated instead of creating a
     * new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param classificationMarking the classification to mark the linkage with.
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param inputLang the language to associate with the linkage.
     * @param updateExisting whether to update an existing linkage.
     * @return <code>true</code> if the linkage is create or updated successfully.
     */
    public static boolean linkEntities(CoalesceEntity entity1,
                                       ELinkTypes linkType,
                                       CoalesceEntity entity2,
                                       Marking classificationMarking,
                                       String modifiedBy,
                                       Locale inputLang,
                                       boolean updateExisting)
    {

        if (entity1 == null || entity2 == null) throw new IllegalArgumentException(MODULE_NAME + " : LinkEntities");

        // Get the LinkageSections for each Entity. Create if not found.

        // For Entity 1...
        CoalesceLinkageSection linkageSection1 = entity1.getLinkageSection();
        if (linkageSection1 == null) return false;

        // For Entity 2...
        CoalesceLinkageSection linkageSection2 = entity2.getLinkageSection();
        if (linkageSection2 == null) return false;

        EstablishLinkage(linkageSection1,
                         entity1,
                         linkType,
                         entity2,
                         classificationMarking,
                         modifiedBy,
                         inputLang,
                         updateExisting);

        EstablishLinkage(linkageSection2,
                         entity2,
                         linkType.getReciprocalLinkType(),
                         entity1,
                         classificationMarking,
                         modifiedBy,
                         inputLang,
                         updateExisting);

        return true;

    }

    /**
     * Marks the first linkage found between the two entities as deleted.
     * 
     * @param entity1 the first entity in the linkage.
     * @param entity2 the second entity in the linkage.
     * @return <code>true</code> if there are no errors. Not finding an existing linkage to delete is not considered an
     *         error.
     */
    public static boolean unLinkEntities(CoalesceEntity entity1, CoalesceEntity entity2)
    {
        return unLinkEntities(entity1, entity2, null);
    }

    /**
     * Marks the first linkage found with the specified {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} between the two
     * entities as deleted.
     * 
     * @param entity1 the first entity in the linkage.
     * @param entity2 the second entity in the linkage.
     * @return <code>true</code> if there are no errors. Not finding an existing linkage to delete is not considered an
     *         error.
     */
    public static boolean unLinkEntities(CoalesceEntity entity1, CoalesceEntity entity2, ELinkTypes linkType)
    {
        if (entity1 == null) throw new NullArgumentException("entity1");
        if (entity2 == null) throw new NullArgumentException("entity2");

        // Get the LinkageSections for each Entity. Exit if not found.

        // For Entity 1...
        CoalesceLinkageSection linkageSection1 = entity1.getLinkageSection();
        if (linkageSection1 == null) return false;

        // For Entity 2...
        CoalesceLinkageSection linkageSection2 = entity2.getLinkageSection();
        if (linkageSection2 == null) return false;

        MarkLinkageAsDeleted(linkageSection1, entity1, entity2, linkType);

        if (linkType == null)
        {
            MarkLinkageAsDeleted(linkageSection2, entity2, entity1, null);
        }
        else
        {
            MarkLinkageAsDeleted(linkageSection2, entity2, entity1, linkType.getReciprocalLinkType());
        }

        return true;
    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private static void EstablishLinkage(CoalesceLinkageSection linkageSection,
                                         CoalesceEntity entity,
                                         ELinkTypes linkType,
                                         CoalesceEntity otherEntity,
                                         Marking classificationMarking,
                                         String modifiedBy,
                                         Locale inputLang,
                                         boolean updateExisting)
    {
        if (linkageSection == null) throw new NullArgumentException("linkageSection");
        if (entity == null) throw new NullArgumentException("entity");
        if (otherEntity == null) throw new NullArgumentException("otherEntity");

        CoalesceLinkage linkage = null;
        // Do we already have the Linkage made? (Same Entities and Same LinkType)?
        for (ICoalesceDataObject cdo : linkageSection.getChildDataObjects().values())
        {
            if (cdo instanceof CoalesceLinkage)
            {

                CoalesceLinkage childLinkage = (CoalesceLinkage) cdo;
                if (childLinkage.getEntity1Key().equals(entity.getKey()) && childLinkage.getLinkType() == linkType
                        && childLinkage.getEntity2Key().equals(otherEntity.getKey()))
                {

                    // Found; Use Existing Linkage
                    linkage = childLinkage;

                    break;
                }
            }
        }

        // Update/Populate Linkage
        if (linkage != null)
        {
            if (updateExisting)
            {
                // Update/Populate Existing
                linkage.establishLinkage(entity, linkType, otherEntity, classificationMarking, modifiedBy, inputLang);
            }
        }
        else
        {
            // Create
            CoalesceLinkage newLinkage = linkageSection.createLinkage();

            // Update/Populate
            newLinkage.establishLinkage(entity, linkType, otherEntity, classificationMarking, modifiedBy, inputLang);
        }
    }

    private static boolean MarkLinkageAsDeleted(CoalesceLinkageSection linkageSection,
                                                CoalesceEntity entity,
                                                CoalesceEntity otherEntity,
                                                ELinkTypes linkType)
    {
        for (ICoalesceDataObject cdo : linkageSection.getChildDataObjects().values())
        {
            if (cdo instanceof CoalesceLinkage)
            {

                CoalesceLinkage linkage = (CoalesceLinkage) cdo;

                if (linkType == null || linkage.getLinkType() == linkType)
                {
                    if (linkage.getEntity1Key().equals(entity.getKey())
                            && linkage.getEntity2Key().equals(otherEntity.getKey()))
                    {

                        linkage.setStatus(ECoalesceDataObjectStatus.DELETED);

                        return true;
                    }
                }
            }
        }

        return false;
    }
}
