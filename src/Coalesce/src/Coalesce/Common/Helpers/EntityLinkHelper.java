package Coalesce.Common.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
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

    private static final String MODULE = "Coalesce.Common.Helpers.EntityLinkHelper";

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
        @SuppressWarnings("unused")
        CallResult rst;

        // Get the LinkageSections for each Entity. Create if not found.

        // For Entity 1...
        XsdLinkageSection linkageSection1 = GetLinkageSection(entity1);
        if (linkageSection1 == null) return false;

        // For Entity 2...
        XsdLinkageSection linkageSection2 = GetLinkageSection(entity2);
        if (linkageSection2 == null) return false;

        rst = EstablishLinkage(linkageSection1,
                               entity1,
                               linkType,
                               entity2,
                               classificationMarking,
                               modifiedBy,
                               inputLang,
                               true);

        rst = EstablishLinkage(linkageSection2,
                               entity2,
                               linkType.GetReciprocalLinkType(),
                               entity1,
                               classificationMarking,
                               modifiedBy,
                               inputLang,
                               updateExisting);

        return true;

    }

    public boolean UnLinkEntities(XsdEntity entity1, XsdEntity entity2)
    {

        return UnLinkEntities(entity1, entity2, "U", "", "en-US");
    }

    public boolean UnLinkEntities(XsdEntity entity1, XsdEntity entity2, ELinkTypes linkType)
    {

        return UnLinkEntities(entity1, entity2, "U", "", "en-US", linkType);
    }

    public boolean UnLinkEntities(XsdEntity entity1,
                                  XsdEntity entity2,
                                  String classificationMarking,
                                  String modifiedBy,
                                  String inputLang)
    {

        // Get the LinkageSections for each Entity. Exit if not found.

        // For Entity 1...
        XsdLinkageSection linkageSection1 = entity1.GetLinkageSection();
        if (linkageSection1 == null) return false;

        // For Entity 2...
        XsdLinkageSection linkageSection2 = entity2.GetLinkageSection();
        if (linkageSection2 == null) return false;

        MarkLinkageAsDeleted(linkageSection1, entity1, entity2);

        MarkLinkageAsDeleted(linkageSection2, entity2, entity1);

        return true;

    }

    public boolean UnLinkEntities(XsdEntity entity1,
                                  XsdEntity entity2,
                                  String classificationMarking,
                                  String modifiedBy,
                                  String inputLang,
                                  ELinkTypes linkType)
    {

        // Get the LinkageSections for each Entity. Exit if not found.

        // For Entity 1...
        XsdLinkageSection linkageSection1 = entity1.GetLinkageSection();
        if (linkageSection1 == null) return false;

        // For Entity 2...
        XsdLinkageSection linkageSection2 = entity2.GetLinkageSection();
        if (linkageSection2 == null) return false;

        MarkLinkageAsDeleted(linkageSection1, entity1, entity2, linkType);

        MarkLinkageAsDeleted(linkageSection2, entity2, entity1, linkType.GetReciprocalLinkType());

        return true;

    }

    public Map<String, XsdLinkage> GetLinkages(XsdEntity entity)
    {

        return GetLinkages(entity, (String) null);

    }

    public Map<String, XsdLinkage> GetLinkages(XsdEntity entity, String forEntityName)
    {
        Map<String, XsdLinkage> linkages = new HashMap<String, XsdLinkage>();

        // Get Linkage Section
        XsdLinkageSection linkageSection = entity.GetLinkageSection();
        if (linkageSection == null) return null;

        for (ICoalesceDataObject cdo : linkageSection.GetChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage linkage = (XsdLinkage) cdo;
                if (forEntityName == null || linkage.GetEntity2Name().equalsIgnoreCase(forEntityName))
                {
                    linkages.put(cdo.GetKey(), linkage);
                }
            }
        }

        return linkages;

    }

    public Map<String, XsdLinkage> GetLinkages(XsdEntity entity, ELinkTypes forLinkType, String forEntityName)
    {

        List<ELinkTypes> forLinkTypes = new ArrayList<ELinkTypes>();
        forLinkTypes.add(forLinkType);

        return GetLinkages(entity, forLinkTypes, forEntityName);
    }

    public Map<String, XsdLinkage> GetLinkages(XsdEntity entity, List<ELinkTypes> forLinkTypes, String forEntityName)
    {
        return GetLinkages(entity, forLinkTypes, forEntityName, null);
    }

    public Map<String, XsdLinkage> GetLinkages(XsdEntity entity,
                                               ELinkTypes forLinkType,
                                               String forEntityName,
                                               String forEntitySource)
    {

        List<ELinkTypes> forLinkTypes = new ArrayList<ELinkTypes>();
        forLinkTypes.add(forLinkType);

        return GetLinkages(entity, forLinkTypes, forEntityName, forEntitySource);

    }

    public Map<String, XsdLinkage> GetLinkages(XsdEntity entity, ELinkTypes forLinkType)
    {
        return GetLinkages(entity, forLinkType, null);
    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private static XsdLinkageSection GetLinkageSection(XsdEntity entity)
    {
        if (entity == null) return null;

        XsdLinkageSection linkageSection = entity.GetLinkageSection();

        if (linkageSection == null)
        {

            linkageSection = entity.CreateLinkageSection();

        }

        return linkageSection;

    }

    private static CallResult EstablishLinkage(XsdLinkageSection linkageSection,
                                               XsdEntity entity,
                                               ELinkTypes linkType,
                                               XsdEntity otherEntity,
                                               String classificationMarking,
                                               String modifiedBy,
                                               String inputLang,
                                               boolean updateExisting)
    {
        CallResult rst;

        if (linkageSection == null || entity == null || otherEntity == null)
        {
            return new CallResult(CallResults.FAILED, "Null objected reference", MODULE);
        }

        boolean linkageAlreadyExists = false;
        XsdLinkage linkage = null;
        // Do we already have the Linkage made? (Same Entities and Same LinkType)?
        for (ICoalesceDataObject cdo : linkageSection.GetChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage childLinkage = (XsdLinkage) cdo;
                if (childLinkage.GetEntity1Key().equals(entity.GetKey()) && childLinkage.GetLinkType() == linkType
                        && childLinkage.GetEntity2Key().equals(otherEntity.GetKey()))
                {

                    // Found; Use Existing Linkage
                    linkageAlreadyExists = true;
                    linkage = childLinkage;

                    break;
                }
            }
        }

        // Update/Populate Linkage
        if (linkageAlreadyExists)
        {
            if (updateExisting)
            {
                // Update/Populate Existing
                rst = linkage.EstablishLinkage(entity, linkType, otherEntity, classificationMarking, modifiedBy, inputLang);
                if (!rst.getIsSuccess()) return rst;

            }
        }
        else
        {
            // Create
            XsdLinkage newLinkage = linkageSection.CreateLinkage();

            // Update/Populate
            rst = newLinkage.EstablishLinkage(entity, linkType, otherEntity, classificationMarking, modifiedBy, inputLang);
            if (!rst.getIsSuccess()) return rst;
        }

        return CallResult.successCallResult;

    }

    private void MarkLinkageAsDeleted(XsdLinkageSection linkageSection, XsdEntity entity, XsdEntity otherEntity)
    {

        MarkLinkageAsDeleted(linkageSection, entity, otherEntity, null);

    }

    private void MarkLinkageAsDeleted(XsdLinkageSection linkageSection,
                                      XsdEntity entity,
                                      XsdEntity otherEntity,
                                      ELinkTypes linkType)
    {

        for (ICoalesceDataObject cdo : linkageSection.GetChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage linkage = (XsdLinkage) cdo;

                if (linkType == null || linkage.GetLinkType() == linkType)
                {
                    if (linkage.GetEntity1Key().equals(entity.GetKey())
                            && linkage.GetEntity2Key().equals(otherEntity.GetKey()))
                    {

                        linkage.SetStatus(ECoalesceDataObjectStatus.DELETED);

                        break;
                    }
                }
            }
        }
    }

    private Map<String, XsdLinkage> GetLinkages(XsdEntity entity,
                                                List<ELinkTypes> forLinkTypes,
                                                String forEntityName,
                                                String forEntitySource)
    {
        Map<String, XsdLinkage> linkages = new HashMap<String, XsdLinkage>();

        // Get Linkage Section
        XsdLinkageSection linkageSection = entity.GetLinkageSection();
        if (linkageSection == null) return null;

        for (ICoalesceDataObject cdo : linkageSection.GetChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage linkage = (XsdLinkage) cdo;
                if ((forEntityName == null || linkage.GetEntity2Name().equalsIgnoreCase(forEntityName))
                        && forLinkTypes.contains(linkage.GetLinkType())
                        && (forEntitySource == null || linkage.GetEntity2Source().equalsIgnoreCase(forEntitySource))
                        && linkage.GetStatus() != ECoalesceDataObjectStatus.DELETED)
                {
                    linkages.put(linkage.GetKey(), linkage);
                }
            }
        }

        return linkages;

    }

}
