package com.incadencecorp.coalesce.common.helpers;

import java.util.Locale;

import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.datamodel.ICoalesceObject;

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
 * Provides helper methods for linking two {@link CoalesceEntity} objects
 * together.
 */
public final class EntityLinkHelper {

    // ----------------------------------------------------------------------//
    // Private and protected Objects
    // ----------------------------------------------------------------------//

    private static final String MODULE_NAME = EntityLinkHelper.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityLinkHelper.class);

    // ----------------------------------------------------------------------//
    // Factory and Initialization
    // ----------------------------------------------------------------------//

    // Make static class
    private EntityLinkHelper()
    {
        // Do Nothing
    }

    // -----------------------------------------------------------------------//
    // Public Static Methods
    // -----------------------------------------------------------------------//

    /**
     * Links two entities together with a default Unclassified classification
     * and {@link Locale#US}. 
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @throws CoalesceException 
     * @deprecated this method is an implicit bi-direction link.  Use one of the.
     * explicit link directions.
	 *
     */
    @Deprecated
    public static void linkEntities(CoalesceEntity entity1, ELinkTypes linkType, CoalesceEntity entity2) throws CoalesceException
    {

        linkEntitiesBiDirectional(entity1, linkType, entity2);
    }
 
    /**
     * Links two entities together with a default Unclassified classification
     * and {@link Locale#US}. 
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @throws CoalesceException 
     */
    public static void linkEntitiesBiDirectional(CoalesceEntity entity1, ELinkTypes linkType, CoalesceEntity entity2) throws CoalesceException
    {

        linkEntitiesBiDirectional(entity1, linkType, entity2, new Marking("(U)"), "", "", "", Locale.US, true, false, false);
    }

    /**
     * Links two entities together with a default Unclassified classification
     * and {@link Locale#US} in a single direction. 
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @throws CoalesceException 
     */
    public static void linkEntitiesUniDirectional(CoalesceEntity entity1, ELinkTypes linkType, CoalesceEntity entity2) throws CoalesceException
    {

        linkEntitiesUniDirectional(entity1, linkType, entity2, new Marking("(U)"), "", "", "", Locale.US, true, false, false);
    }

    /**
     * Links two entities together with a default Unclassified classification
     * and {@link Locale#US}. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param updateExisting whether to update an existing linkage.
     * @return <code>true</code> if successful.
     * @throws CoalesceException 
     * @deprecated Implied Bi directional link.  Use an explicit direction version
     */
    @Deprecated
    public static boolean linkEntities(CoalesceEntity entity1,
                                       ELinkTypes linkType,
                                       CoalesceEntity entity2,
                                       boolean updateExisting) throws CoalesceException
    {

            linkEntitiesBiDirectional(entity1, linkType, entity2, new Marking("(U)"), "", "", "", Locale.US, updateExisting, false, false);

            return true;

    }

    /**
     * Links two entities together with a default Unclassified classification
     * and {@link Locale#US}. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param updateExisting whether to update an existing linkage.
     * @return <code>true</code> if successful.
     * @throws CoalesceException 
     */
     public static boolean linkEntitiesBiDirectional(CoalesceEntity entity1,
                                       ELinkTypes linkType,
                                       CoalesceEntity entity2,
                                       boolean updateExisting) throws CoalesceException
    {

            linkEntitiesBiDirectional(entity1, linkType, entity2, new Marking("(U)"), "", "", "", Locale.US, updateExisting, false, false);

            return true;

    }

    /**
     * Links two entities together. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIp the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param updateExisting whether to update an existing linkage.
     * @param isReadOnly specifies whether the linkage should be made as read
     *            only.
     * @param canUpdateReadOnly specifies whether the link can be updated if
     *            it's already marked as read only.
     * @throws CoalesceException
     * @deprecated Implied BiDirectional link use one of the explicit BiDirectional calls
     */
    @Deprecated
    public static void linkEntities(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    String modifiedBy,
                                    String modifiedByIp,
                                    String label,
                                    boolean updateExisting,
                                    boolean isReadOnly,
                                    boolean canUpdateReadOnly) throws CoalesceException
    {
        linkEntitiesBiDirectional(entity1,
                     linkType,
                     entity2,
                     new Marking("(U)"),
                     modifiedBy,
                     modifiedByIp,
                     label,
                     Locale.US,
                     updateExisting,
                     isReadOnly,
                     canUpdateReadOnly);
    }

    /**
     * Links two entities together. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIp the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param updateExisting whether to update an existing linkage.
     * @param isReadOnly specifies whether the linkage should be made as read
     *            only.
     * @param canUpdateReadOnly specifies whether the link can be updated if
     *            it's already marked as read only.
     * @throws CoalesceException
     */
    public static void linkEntitiesBiDirectional(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    String modifiedBy,
                                    String modifiedByIp,
                                    String label,
                                    boolean updateExisting,
                                    boolean isReadOnly,
                                    boolean canUpdateReadOnly) throws CoalesceException
    {
        linkEntitiesBiDirectional(entity1,
                     linkType,
                     entity2,
                     new Marking("(U)"),
                     modifiedBy,
                     modifiedByIp,
                     label,
                     Locale.US,
                     updateExisting,
                     isReadOnly,
                     canUpdateReadOnly);
    }

    /**
     * Links two entities together. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param classificationMarking the classification to mark the linkage with.
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIp the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param inputLang the language to associate with the linkage.
     * @param updateExisting whether to update an existing linkage.
     * @throws CoalesceException
     * @deprecated Implied bidirectional link. Use an explicit bi or unidirectional call
     */
    @Deprecated
    public static void linkEntities(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    Marking classificationMarking,
                                    String modifiedBy,
                                    String modifiedByIp,
                                    String label,
                                    Locale inputLang,
                                    boolean updateExisting) throws CoalesceException
    {
        linkEntitiesBiDirectional(entity1,
                     linkType,
                     entity2,
                     classificationMarking,
                     modifiedBy,
                     modifiedByIp,
                     label,
                     inputLang,
                     updateExisting,
                     false,
                     false);
    }

    /**
     * Links two entities together. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param classificationMarking the classification to mark the linkage with.
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIp the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param inputLang the language to associate with the linkage.
     * @param updateExisting whether to update an existing linkage.
     * @throws CoalesceException
     */
    public static void linkEntitiesBiDirectional(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    Marking classificationMarking,
                                    String modifiedBy,
                                    String modifiedByIp,
                                    String label,
                                    Locale inputLang,
                                    boolean updateExisting) throws CoalesceException
    {
        linkEntitiesBiDirectional(entity1,
                     linkType,
                     entity2,
                     classificationMarking,
                     modifiedBy,
                     modifiedByIp,
                     label,
                     inputLang,
                     updateExisting,
                     false,
                     false);
    }
    
    /**
     * Links two entities together. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param classificationMarking the classification to mark the linkage with.
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIP the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param inputLang the language to associate with the linkage.
     * @param updateExisting whether to update an existing linkage.
     * @param isReadOnly specifies whether the linkage should be made as read
     *            only.
     * @param canUpdateReadOnly specifies whether the link can be updated if
     *            it's already marked as read only.
     * @throws CoalesceException
     * @deprecated This is an implicit bidirectional link.  Use one of the explicit link
     * directions
     */
    @Deprecated
    public static void linkEntities(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    Marking classificationMarking,
                                    String modifiedBy,
                                    String modifiedByIP,
                                    String label,
                                    Locale inputLang,
                                    boolean updateExisting,
                                    boolean isReadOnly,
                                    boolean canUpdateReadOnly) throws CoalesceException
    {

    	linkEntitiesBiDirectional(entity1, linkType,entity2,classificationMarking,
    			modifiedBy, modifiedByIP, label, inputLang,
    			updateExisting, isReadOnly, canUpdateReadOnly);

    }
    
    /**
     * Links two entities together. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param classificationMarking the classification to mark the linkage with.
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIP the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param inputLang the language to associate with the linkage.
     * @param updateExisting whether to update an existing linkage.
     * @param isReadOnly specifies whether the linkage should be made as read
     *            only.
     * @param canUpdateReadOnly specifies whether the link can be updated if
     *            it's already marked as read only.
     * @throws CoalesceException
     */
    public static void linkEntitiesBiDirectional(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    Marking classificationMarking,
                                    String modifiedBy,
                                    String modifiedByIP,
                                    String label,
                                    Locale inputLang,
                                    boolean updateExisting,
                                    boolean isReadOnly,
                                    boolean canUpdateReadOnly) throws CoalesceException
    {

        if (entity1 == null || entity2 == null)
            throw new IllegalArgumentException(MODULE_NAME + " : LinkEntities");

        // Don't allow linking to self.
        if (entity1.getKey().equalsIgnoreCase(entity2.getKey()))
            throw new IllegalArgumentException("Linking an object to itself is not allowed");

        // Get the LinkageSections for each Entity. Create if not found.

        // For Entity 1...
        CoalesceLinkageSection linkageSection1 = entity1.getLinkageSection();
        if (linkageSection1 == null)
            throw new IllegalArgumentException("Missing Linkage Section: " + entity1.getKey());

        // For Entity 2...
        CoalesceLinkageSection linkageSection2 = entity2.getLinkageSection();
        if (linkageSection2 == null)
            throw new IllegalArgumentException("Missing Linkage Section: " + entity1.getKey());

        establishLinkage(linkageSection1,
                         entity1,
                         linkType,
                         entity2,
                         classificationMarking,
                         modifiedBy,
                         modifiedByIP,
                         label,
                         inputLang,
                         updateExisting,
                         isReadOnly,
                         canUpdateReadOnly);

        establishLinkage(linkageSection2,
                         entity2,
                         linkType.getReciprocalLinkType(),
                         entity1,
                         classificationMarking,
                         modifiedBy,
                         modifiedByIP,
                         label,
                         inputLang,
                         updateExisting,
                         isReadOnly,
                         canUpdateReadOnly);

    }

    /**
     * Marks the first linkage found between the two entities as deleted.
     * 
     * @param entity1 the first entity in the linkage.
     * @param entity2 the second entity in the linkage.
     * @return <code>true</code> if there are no errors. Not finding an existing
     *         linkage to delete is not considered an error.
     */
    public static boolean unLinkEntities(CoalesceEntity entity1, CoalesceEntity entity2)
    {
        return unLinkEntities(entity1, entity2, null);
    }

    /**
     * Marks the first linkage found with the specified
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} between
     * the two entities as deleted.
     * 
     * @param entity1 the first entity in the linkage.
     * @param entity2 the second entity in the linkage.
     * @param linkType the type of the linkage
     * @return <code>true</code> if there are no errors. Not finding an existing
     *         linkage to delete is not considered an error.
     */
    public static boolean unLinkEntities(CoalesceEntity entity1, CoalesceEntity entity2, ELinkTypes linkType)
    {
        try
        {
            return unLinkEntities(entity1, entity2, linkType, "", "", false);
        }
        catch (CoalesceException e)
        {
            return false;
        }
    }

    /**
     * Marks the first linkage found with the specified
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} between
     * the two entities as deleted.
     * 
     * @param entity1 the first entity in the linkage.
     * @param entity2 the second entity in the linkage.
     * @param linkType the type of the linkage
     * @param modifiedBy
     * @param modifiedByIP
     * @param canUpdateReadOnly
     * @return <code>true</code> if there are no errors. Not finding an existing
     *         linkage to delete is not considered an error.
     * @throws CoalesceException
     */
    public static boolean unLinkEntities(CoalesceEntity entity1,
                                         CoalesceEntity entity2,
                                         ELinkTypes linkType,
                                         String modifiedBy,
                                         String modifiedByIP,
                                         boolean canUpdateReadOnly) throws CoalesceException

    {
        if (entity1 == null)
            throw new NullArgumentException("entity1");
        if (entity2 == null)
            throw new NullArgumentException("entity2");

        // Get the LinkageSections for each Entity. Exit if not found.

        // For Entity 1...
        CoalesceLinkageSection linkageSection1 = entity1.getLinkageSection();
        if (linkageSection1 == null)
            return false;

        // For Entity 2...
        CoalesceLinkageSection linkageSection2 = entity2.getLinkageSection();
        if (linkageSection2 == null)
            return false;

        markLinkageAsDeleted(linkageSection1, entity1, entity2, linkType, modifiedBy, modifiedByIP, canUpdateReadOnly);

        if (linkType == null)
        {
            markLinkageAsDeleted(linkageSection2, entity2, entity1, null, modifiedBy, modifiedByIP, canUpdateReadOnly);
        }
        else
        {
            markLinkageAsDeleted(linkageSection2,
                                 entity2,
                                 entity1,
                                 linkType.getReciprocalLinkType(),
                                 modifiedBy,
                                 modifiedByIP,
                                 canUpdateReadOnly);
        }

        return true;
    }
    /**
     * Links two entities together in one direction with a default Unclassified classification
     * and {@link Locale#US}. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param updateExisting whether to update an existing linkage.
     * @return <code>true</code> if successful.
     * @throws CoalesceException 
     */
    public static boolean linkEntitiesUniDirectional(CoalesceEntity entity1,
                                       ELinkTypes linkType,
                                       CoalesceEntity entity2,
                                       boolean updateExisting) throws CoalesceException
    {

      
    	linkEntitiesUniDirectional(entity1, linkType, entity2, new Marking("(U)"), "", "", "", Locale.US, updateExisting, false, false);

            return true;


    }
    
    /**
     * Links two entities together in one direction. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIp the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param updateExisting whether to update an existing linkage.
     * @param isReadOnly specifies whether the linkage should be made as read
     *            only.
     * @param canUpdateReadOnly specifies whether the link can be updated if
     *            it's already marked as read only.
     * @throws CoalesceException
     */
    public static void linkEntitiesUniDirectional(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    String modifiedBy,
                                    String modifiedByIp,
                                    String label,
                                    boolean updateExisting,
                                    boolean isReadOnly,
                                    boolean canUpdateReadOnly) throws CoalesceException
    {
    	linkEntitiesUniDirectional(entity1,
                     linkType,
                     entity2,
                     new Marking("(U)"),
                     modifiedBy,
                     modifiedByIp,
                     label,
                     Locale.US,
                     updateExisting,
                     isReadOnly,
                     canUpdateReadOnly);
    }
    
    /**
     * Links two entities together in one direction. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param classificationMarking the classification to mark the linkage with.
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIp the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param inputLang the language to associate with the linkage.
     * @param updateExisting whether to update an existing linkage.
     * @throws CoalesceException
     */
    public static void linkEntitiesUniDirectional(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    Marking classificationMarking,
                                    String modifiedBy,
                                    String modifiedByIp,
                                    String label,
                                    Locale inputLang,
                                    boolean updateExisting) throws CoalesceException
    {
    	linkEntitiesUniDirectional(entity1,
                     linkType,
                     entity2,
                     classificationMarking,
                     modifiedBy,
                     modifiedByIp,
                     label,
                     inputLang,
                     updateExisting,
                     false,
                     false);
    }
    
    
    /**
     * Links two entities together in one direction with a default Unclassified classification
     * and {@link Locale#US}. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param label the label to put on the link for display
     * @param updateExisting whether to update an existing linkage.
     * @return <code>true</code> if successful.
     * @throws CoalesceException 
     */
    public static boolean linkEntitiesUniDirectional(CoalesceEntity entity1,
                                       ELinkTypes linkType,
                                       CoalesceEntity entity2,
                                       String label,
                                       boolean updateExisting) throws CoalesceException
    {

        LOGGER.debug("Creating link: " + entity1.getKey() + " ---> " + entity2.getKey() + " of type " + linkType 
        		+ " with label " + label);
    	linkEntitiesUniDirectional(entity1, linkType, entity2, new Marking("(U)"), "", "", label, Locale.US, updateExisting, false, false);

            return true;


    }
    
    /**
     * Links two entities together in one direction with a default Unclassified classification
     * and {@link Locale#US}. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param label the label to put on the link for display
     * @param updateExisting whether to update an existing linkage.
     * @return <code>true</code> if successful.
     * @throws CoalesceException 
     */
    public static boolean linkEntitiesBiDirectional(CoalesceEntity entity1,
                                       ELinkTypes linkType,
                                       CoalesceEntity entity2,
                                       String label,
                                       boolean updateExisting) throws CoalesceException
    {

        LOGGER.debug("Creating link: " + entity1.getKey() + " ---> " + entity2.getKey() + " of type " + linkType 
        		+ " with label " + label);
    	linkEntitiesBiDirectional(entity1, linkType, entity2, new Marking("(U)"), "", "", label, Locale.US, updateExisting, false, false);

            return true;


    }
    
    /**
     * Links two entities together in one direction with a default Unclassified classification
     * and {@link Locale#US}. If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param label the label to put on the link for display
     * @param updateExisting whether to update an existing linkage.
     * @return <code>true</code> if successful.
     * @throws CoalesceException
     * @deprecated Implicit bi-directional link use one of the explicit APIs 
     */
    @Deprecated
    public static boolean linkEntities(CoalesceEntity entity1,
                                       ELinkTypes linkType,
                                       CoalesceEntity entity2,
                                       String label,
                                       boolean updateExisting) throws CoalesceException
    {

        LOGGER.debug("Creating link: " + entity1.getKey() + " ---> " + entity2.getKey() + " of type " + linkType 
        		+ " with label " + label);
    	linkEntitiesBiDirectional(entity1, linkType, entity2, new Marking("(U)"), "", "", label, Locale.US, updateExisting, false, false);

            return true;


    }
    /**
     * Links two entities together in one direction from entity1 to entity2. 
     * If <code>updateExisting</code> is
     * <code>true</code> then the first linkage found between the two entities
     * with the same
     * {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes} will be
     * updated instead of creating a new linkage.
     * 
     * @param entity1 the first entity to link
     * @param linkType the type of linkage to create between the two entities.
     * @param entity2 the second entity to link
     * @param classificationMarking the classification to mark the linkage with.
     * @param modifiedBy the identifier representing who modified the linkage.
     * @param modifiedByIP the IP address of the computer used to make the
     *            modification.
     * @param label
     * @param inputLang the language to associate with the linkage.
     * @param updateExisting whether to update an existing linkage.
     * @param isReadOnly specifies whether the linkage should be made as read
     *            only.
     * @param canUpdateReadOnly specifies whether the link can be updated if
     *            it's already marked as read only.
     * @throws CoalesceException
     */
    public static void linkEntitiesUniDirectional(CoalesceEntity entity1,
                                    ELinkTypes linkType,
                                    CoalesceEntity entity2,
                                    Marking classificationMarking,
                                    String modifiedBy,
                                    String modifiedByIP,
                                    String label,
                                    Locale inputLang,
                                    boolean updateExisting,
                                    boolean isReadOnly,
                                    boolean canUpdateReadOnly) throws CoalesceException
    {

        if (entity1 == null || entity2 == null)
            throw new IllegalArgumentException(MODULE_NAME + " : LinkEntities");

        // Don't allow linking to self.
        if (entity1.getKey().equalsIgnoreCase(entity2.getKey()))
            throw new IllegalArgumentException("Linking an object to itself is not allowed");

        // Get the LinkageSections for each Entity. Create if not found.

        // For Entity 1...
        CoalesceLinkageSection linkageSection1 = entity1.getLinkageSection();
        if (linkageSection1 == null)
            throw new IllegalArgumentException("Missing Linkage Section: " + entity1.getKey());

        // For Entity 2...
        CoalesceLinkageSection linkageSection2 = entity2.getLinkageSection();
        if (linkageSection2 == null)
            throw new IllegalArgumentException("Missing Linkage Section: " + entity1.getKey());

        establishLinkage(linkageSection1,
                         entity1,
                         linkType,
                         entity2,
                         classificationMarking,
                         modifiedBy,
                         modifiedByIP,
                         label,
                         inputLang,
                         updateExisting,
                         isReadOnly,
                         canUpdateReadOnly);

        
    }   

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private static void establishLinkage(CoalesceLinkageSection linkageSection,
                                         CoalesceEntity entity,
                                         ELinkTypes linkType,
                                         CoalesceEntity otherEntity,
                                         Marking classificationMarking,
                                         String modifiedBy,
                                         String modifiedByIP,
                                         String label,
                                         Locale inputLang,
                                         boolean updateExisting,
                                         boolean isReadOnly,
                                         boolean canUpdateReadOnly) throws CoalesceException
    {

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Linking: ({}) ({}) ({}) -[{}]-> ({}) ({}) ({})",
                         entity.getKey(),
                         entity.getClassName(),
                         entity.getObjectVersion(),
                         linkType.toString(),
                         otherEntity.getKey(),
                         otherEntity.getClassName(),
                         otherEntity.getObjectVersion());
        }

        CoalesceLinkage linkage = null;
        // Do we already have the Linkage made? (Same Entities and Same
        // LinkType)?
        for (ICoalesceObject cdo : linkageSection.getChildCoalesceObjects().values())
        {
            if (cdo instanceof CoalesceLinkage)
            {

                CoalesceLinkage childLinkage = (CoalesceLinkage) cdo;
                if (childLinkage.getEntity1Key().equals(entity.getKey()) && childLinkage.getLinkType() == linkType
                        && childLinkage.getEntity2Key().equalsIgnoreCase(otherEntity.getKey()))
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

            if (!canUpdateReadOnly && linkage.isReadOnly())
            {
                throw new CoalesceException(String.format("Linkage (%s) is read only", linkage.getKey()));
            }

            if (updateExisting)
            {
                // Update/Populate Existing
                linkage.establishLinkage(entity,
                                         linkType,
                                         otherEntity,
                                         classificationMarking,
                                         modifiedBy,
                                         modifiedByIP,
                                         label,
                                         inputLang,
                                         isReadOnly);
            }
        }
        else
        {
            // Create
            CoalesceLinkage newLinkage = linkageSection.createLinkage();
            newLinkage.setSuspendHistory(true);

            // Update/Populate
            newLinkage.establishLinkage(entity,
                                        linkType,
                                        otherEntity,
                                        classificationMarking,
                                        modifiedBy,
                                        modifiedByIP,
                                        label,
                                        inputLang,
                                        isReadOnly);

            newLinkage.setSuspendHistory(false);

        }
    }

    private static boolean markLinkageAsDeleted(CoalesceLinkageSection linkageSection,
                                                CoalesceEntity entity,
                                                CoalesceEntity otherEntity,
                                                ELinkTypes linkType,
                                                String modifiedBy,
                                                String modifiedByIP,
                                                boolean canUpdateReadOnly) throws CoalesceException
    {
        if (LOGGER.isDebugEnabled())
        {
            String rel = "*";

            if (linkType != null)
            {
                rel = linkType.toString();
            }

            LOGGER.debug("Un-Linking: ({}) ({}) ({}) -[{}]-> ({}) ({}) ({})",
                         entity.getKey(),
                         entity.getClassName(),
                         entity.getObjectVersion(),
                         rel,
                         otherEntity.getKey(),
                         otherEntity.getClassName(),
                         otherEntity.getObjectVersion());
        }

        for (ICoalesceObject cdo : linkageSection.getChildCoalesceObjects().values())
        {
            if (cdo instanceof CoalesceLinkage)
            {

                CoalesceLinkage linkage = (CoalesceLinkage) cdo;

                if (linkType == null || linkage.getLinkType() == linkType)
                {
                    if (linkage.getEntity1Key().equals(entity.getKey())
                            && linkage.getEntity2Key().equals(otherEntity.getKey()))
                    {

                        if (!canUpdateReadOnly && linkage.isReadOnly())
                        {
                            throw new CoalesceException(String.format("Linkage (%s) is read only", linkage.getKey()));
                        }

                        linkage.createHistory(modifiedBy, modifiedByIP, null);
                        linkage.setStatus(ECoalesceObjectStatus.DELETED);
                        linkage.setModifiedBy(modifiedBy);
                        linkage.setModifiedByIP(modifiedByIP);

                        return true;
                    }
                }
            }
        }

        return false;
    }
}
