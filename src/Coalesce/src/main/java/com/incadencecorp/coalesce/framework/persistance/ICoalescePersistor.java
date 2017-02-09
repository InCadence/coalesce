package com.incadencecorp.coalesce.framework.persistance;

import java.util.EnumSet;
import java.util.List;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;

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
 * Interface for storing and retrieving Coalesce entities from different
 * databases depending on the implementation.
 */
public interface ICoalescePersistor {

    /**
     * Sets the cacher.
     *
     * @param cacher Pass null if caching is not wanted
     */
    void setCacher(ICoalesceCacher cacher);

    /**
     * Saves the Coalesce entity to the database.
     *
     * @param entities the Coalesce entities to be saved.
     * @param allowRemoval specifies whether an entity marked as deleted should
     *            be removed from the database.
     * @return true if successfully saved.
     * @throws CoalescePersistorException
     */
    boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity that matches the given parameters.
     *
     * @param keys the primary key of the entity.
     * @return the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity that matches the given parameters.
     *
     * @param entityId the unique identifier, such as a TCN number for an EFT.
     * @param entityIdType the type of entityId, such as TCN.
     * @return the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    CoalesceEntity getEntity(String entityId, String entityIdType) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity that matches the given parameters.
     *
     * @param name the name of the entity.
     * @param entityId the unique identifier, such as a TCN number for an EFT.
     * @param entityIdType the type of entityId, such as TCN.
     * @return the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    CoalesceEntity getEntity(String name, String entityId, String entityIdType) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity's XML that matches the given parameters.
     *
     * @param keys the primary key of the entity.
     * @return the matching Coalesce entity's XML.
     * @throws CoalescePersistorException
     */
    String[] getEntityXml(String... keys) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity's XML that matches the given parameters.
     *
     * @param entityId the unique identifier, such as a TCN number for an EFT.
     * @param entityIdType the type of entityId, such as TCN.
     * @return the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity's XML that matches the given parameters.
     *
     * @param name the name of the entity.
     * @param entityId the unique identifier, such as a TCN number for an EFT.
     * @param entityIdType the type of entityId, such as TCN.
     * @return the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException;

    /**
     * Returns the value of the specified Coalesce field.
     *
     * @param fieldKey the primary key of the field.
     * @return returns the value of the matching field.
     * @throws CoalescePersistorException
     */
    Object getFieldValue(String fieldKey) throws CoalescePersistorException;

    /**
     * Returns the ElementMetaData for the Coalesce object that matches the
     * given parameters.
     *
     * @param key the Coalesce object primary key
     * @param objectType the Coalesce object type specification.
     * @return ElementMetaData
     * @throws CoalescePersistorException
     */
    ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException;

    /**
     * Returns the last modified date for the Coalesce object (entity, field,
     * record, linkage, etc.) that matches the given parameters.
     *
     * @param key the primary key of the Coalesce object.
     * @param objectType is the Coalesce object to retrieve the information for.
     * @return DateTime containing the last modified date for the Coalesce
     *         object matching the values.
     * @throws CoalescePersistorException
     */
    DateTime getCoalesceObjectLastModified(String key, String objectType) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity keys that matches the given parameters.
     *
     * @param entityId of the entity.
     * @param entityIdType of the entity.
     * @param entityName of the entity.
     * @param entitySource of the entity.
     * @return List<String> of primary keys for the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                  String entityIdType,
                                                  String entityName,
                                                  String entitySource) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity meta data that matches the given parameters.
     *
     * @param key the primary key of the entity.
     * @return EntityMetaData for the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException;

    /**
     * Returns the Coalesce field binary data that matches the given parameters.
     *
     * @param binaryFieldKey the primary key of the Coalesce field.
     * @return byte[] the binary data of the Coalesce field matching the value.
     * @throws CoalescePersistorException
     */
    byte[] getBinaryArray(String binaryFieldKey) throws CoalescePersistorException;

    /**
     * Saves the template in the database.
     *
     * @param templates
     * @throws CoalescePersistorException
     */
    void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException;

    /**
     * Saves the template in the database as well as creates tables and indexes
     * needed for searching.
     *
     * @param templates
     * @throws CoalescePersistorException
     */
    void registerTemplate(final CoalesceEntityTemplate... templates) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity template XML that matches the given
     * parameters.
     *
     * @param key the primary key of the entity.
     * @return the matching Coalesce entity's XML.
     * @throws CoalescePersistorException
     */
    String getEntityTemplateXml(String key) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity template XML that matches the given
     * parameters.
     *
     * @param name of the entity.
     * @param source of the entity.
     * @param version of the entity.
     * @return the matching Coalesce entity's XML.
     * @throws CoalescePersistorException
     */
    String getEntityTemplateXml(String name, String source, String version) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity template key that matches the given
     * parameters.
     *
     * @param name of the entity.
     * @param source of the entity.
     * @param version of the entity.
     * @return the matching Coalesce entity's primary key.
     * @throws CoalescePersistorException
     */
    String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity templates.
     *
     * @return ObjectMetaData
     * @throws CoalescePersistorException
     * @since 0.0.9 interface was changed from a string.
     */
    List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException;

    /**
     * @return EnumSet of EPersistorCapabilities
     */
    EnumSet<EPersistorCapabilities> getCapabilities();

}
