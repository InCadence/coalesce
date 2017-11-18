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
package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;

import java.util.List;

/**
 * Interface for storing and retrieving templates from different
 * databases depending on the implementation.
 */
public interface ICoalesceTemplatePersister {

    /**
     * Saves the template in the database.
     *
     * @param templates an array of templates
     * @throws CoalescePersistorException on error
     */
    void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException;

    /**
     * Saves the template in the database as well as creates tables and indexes
     * needed for searching.
     *
     * @param templates an array of templates
     * @throws CoalescePersistorException on error
     */
    void registerTemplate(final CoalesceEntityTemplate... templates) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity template XML that matches the given
     * parameters.
     *
     * @param key the primary key of the entity.
     * @return the matching Coalesce entity's XML.
     * @throws CoalescePersistorException on error
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
     * @throws CoalescePersistorException on error
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
     * @throws CoalescePersistorException on error
     */
    String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity templates.
     *
     * @return ObjectMetaData
     * @throws CoalescePersistorException on error
     * @since 0.0.9 interface was changed from a string.
     */
    List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException;

}
