package Coalesce.Common.Helpers;

import org.apache.commons.lang.NullArgumentException;

import Coalesce.Framework.DataModel.ECoalesceDataObjectStatus;
import Coalesce.Framework.DataModel.ELinkTypes;
import Coalesce.Framework.DataModel.ICoalesceDataObject;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdLinkage;
import Coalesce.Framework.DataModel.XsdLinkageSection;

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

public class EntityLinkHelper {

    // ----------------------------------------------------------------------//
    // Private and protected Objects
    // ----------------------------------------------------------------------//

    private static final String MODULE_NAME = "Coalesce.Common.Helpers.EntityLinkHelper";

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

    public static boolean LinkEntities(XsdEntity entity1, ELinkTypes linkType, XsdEntity entity2, Boolean updateExisting)
    {

        return LinkEntities(entity1, linkType, entity2, "U", "", "en-US", updateExisting);

    }

    public static boolean LinkEntities(XsdEntity entity1,
                                       ELinkTypes linkType,
                                       XsdEntity entity2,
                                       String classificationMarking,
                                       String modifiedBy,
                                       String inputLang,
                                       boolean updateExisting)
    {

        if (entity1 == null || entity2 == null) throw new IllegalArgumentException(MODULE_NAME + " : LinkEntities");
        ;

        // Get the LinkageSections for each Entity. Create if not found.

        // For Entity 1...
        XsdLinkageSection linkageSection1 = entity1.getLinkageSection();
        if (linkageSection1 == null) return false;

        // For Entity 2...
        XsdLinkageSection linkageSection2 = entity2.getLinkageSection();
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
                         linkType.GetReciprocalLinkType(),
                         entity1,
                         classificationMarking,
                         modifiedBy,
                         inputLang,
                         updateExisting);

        return true;

    }

    public static boolean UnLinkEntities(XsdEntity entity1, XsdEntity entity2)
    {
        return UnLinkEntities(entity1, entity2, null);
    }

    public static boolean UnLinkEntities(XsdEntity entity1, XsdEntity entity2, ELinkTypes linkType)
    {
        if (entity1 == null) throw new NullArgumentException("entity1");
        if (entity2 == null) throw new NullArgumentException("entity2");

        // Get the LinkageSections for each Entity. Exit if not found.

        // For Entity 1...
        XsdLinkageSection linkageSection1 = entity1.getLinkageSection();
        if (linkageSection1 == null) return false;

        // For Entity 2...
        XsdLinkageSection linkageSection2 = entity2.getLinkageSection();
        if (linkageSection2 == null) return false;

        MarkLinkageAsDeleted(linkageSection1, entity1, entity2, linkType);

        if (linkType == null)
        {
            MarkLinkageAsDeleted(linkageSection2, entity2, entity1, null);
        }
        else
        {
            MarkLinkageAsDeleted(linkageSection2, entity2, entity1, linkType.GetReciprocalLinkType());
        }

        return true;
    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private static void EstablishLinkage(XsdLinkageSection linkageSection,
                                         XsdEntity entity,
                                         ELinkTypes linkType,
                                         XsdEntity otherEntity,
                                         String classificationMarking,
                                         String modifiedBy,
                                         String inputLang,
                                         boolean updateExisting)
    {
        if (linkageSection == null) throw new NullArgumentException("linkageSection");
        if (entity == null) throw new NullArgumentException("entity");
        if (otherEntity == null) throw new NullArgumentException("otherEntity");

        XsdLinkage linkage = null;
        // Do we already have the Linkage made? (Same Entities and Same LinkType)?
        for (ICoalesceDataObject cdo : linkageSection.getChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage childLinkage = (XsdLinkage) cdo;
                if (childLinkage.GetEntity1Key().equals(entity.getKey()) && childLinkage.GetLinkType() == linkType
                        && childLinkage.GetEntity2Key().equals(otherEntity.getKey()))
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
                linkage.EstablishLinkage(entity, linkType, otherEntity, classificationMarking, modifiedBy, inputLang);
            }
        }
        else
        {
            // Create
            XsdLinkage newLinkage = linkageSection.CreateLinkage();

            // Update/Populate
            newLinkage.EstablishLinkage(entity, linkType, otherEntity, classificationMarking, modifiedBy, inputLang);
        }
    }

    private static boolean MarkLinkageAsDeleted(XsdLinkageSection linkageSection,
                                                XsdEntity entity,
                                                XsdEntity otherEntity,
                                                ELinkTypes linkType)
    {
        for (ICoalesceDataObject cdo : linkageSection.getChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage linkage = (XsdLinkage) cdo;

                if (linkType == null || linkage.GetLinkType() == linkType)
                {
                    if (linkage.GetEntity1Key().equals(entity.getKey())
                            && linkage.GetEntity2Key().equals(otherEntity.getKey()))
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
