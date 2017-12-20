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

package com.incadencecorp.coalesce.api.persistance;

/**
 * Capabilities of Coalesce persistors.
 *
 * @author Derek
 */
public enum EPersistorCapabilities
{
    /**
     * Whether or not this persistor can create entities.
     */
    CREATE,
    /**
     * Whether or not this persistor can read entities.
     */
    READ,
    /**
     * Whether or not this persistor can get the value of a field.
     */
    GET_FIELD_VALUE,
    /**
     * Whather or not this persistor can read templates.
     */
    READ_TEMPLATES,
    /**
     * Whether or not this persistor can update entities.
     */
    UPDATE,
    /**
     * Whether or not this persistor can delete entities.
     */
    DELETE,
    /**
     * Whether or not this persistor can search for entities.
     */
    SEARCH,
    /**
     * Whether or not this persistor can store binary large objects (BLOB).
     */
    SUPPORTS_BLOB,
    /**
     * Whether or not this persistor can geospatially search entities.
     */
    GEOSPATIAL_SEARCH,
    /**
     * Whether or not this persistor can temporally search entities.
     */
    TEMPORAL_SEARCH,
    /**
     * Whether or not this persistor can search list data types.
     */
    LIST_SEARCH,
    /**
     * Whether or not this persistor can index fields.
     */
    INDEX_FIELDS;
}
