/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.persistance.cosmos;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.opengis.filter.expression.PropertyName;

/**
 * @author Derek Clemenzi
 */
public class CosmosConstants {

    private static final ICoalesceNormalizer NORMALIZER = new CosmosNormalizer();

    protected static final String DATABASE_ID = CosmosSettings.getDatabaseName();
    protected static final String DATABASE_LINK = "/dbs/" + DATABASE_ID;

    protected static final String COLLECTION_PREFIX = CosmosSettings.getCollectionPrefix();

    protected static final String COLLECTION_LINK = "/dbs/" + DATABASE_ID + "/colls/";
    protected static final String COLLECTION_TEMPLATE = COLLECTION_PREFIX + "-templates";
    protected static final String COLLECTION_ENTITIES = COLLECTION_PREFIX + "-entities";
    protected static final String COLLECTION_LINKAGES = COLLECTION_PREFIX + "-linkages";

    public static final String FIELD_XML = normalize(CoalescePropertyFactory.getEntityXml());

    // Linkage Column Names
    public static final String LINKAGE_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageKey());
    public static final String LINKAGE_DATE_CREATED_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageDateCreated());
    public static final String LINKAGE_LAST_MODIFIED_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageLastModified());
    public static final String LINKAGE_LINK_TYPE_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageType());
    public static final String LINKAGE_LABEL_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageLabel());
    public static final String LINKAGE_STATUS_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageStatus());

    // Linkage Entity 1 Column Names
    public static final String ENTITY_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityKey());
    public static final String ENTITY_NAME_COLUMN_NAME = normalize(CoalescePropertyFactory.getName());
    public static final String ENTITY_SOURCE_COLUMN_NAME = normalize(CoalescePropertyFactory.getSource());
    public static final String ENTITY_VERSION_COLUMN_NAME = normalize(CoalescePropertyFactory.getVersion());
    public static final String ENTITY_DATE_CREATED_COLUMN_NAME = normalize(CoalescePropertyFactory.getDateCreated());
    public static final String ENTITY_CREATED_BY_COLUMN_NAME = normalize(CoalescePropertyFactory.getCreatedBy());
    public static final String ENTITY_LAST_MODIFIED_COLUMN_NAME = normalize(CoalescePropertyFactory.getLastModified());
    public static final String ENTITY_LAST_MODIFIED_BY_COLUMN_NAME = normalize(CoalescePropertyFactory.getLastModifiedBy());
    public static final String ENTITY_TITLE_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityTitle());
    public static final String ENTITY_STATUS_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityStatus());
    public static final String ENTITY_ID_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityId());
    public static final String ENTITY_ID_TYPE_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityIdType());

    // Linkage Entity 2 Column Names
    public static final String LINKAGE_ENTITY2_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageEntityKey());
    public static final String LINKAGE_ENTITY2_NAME_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageName());
    public static final String LINKAGE_ENTITY2_SOURCE_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageSource());
    public static final String LINKAGE_ENTITY2_VERSION_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageVersion());

    public static String getCollectionName(CoalesceEntityTemplate template)
    {
        return getCollectionName(template.getName());
    }

    public static String getCollectionName(String name)
    {
        return String.format("%s-%s", COLLECTION_PREFIX, normalize(name));
    }

    private static String normalize(PropertyName property)
    {
        return normalize(property.getPropertyName());
    }

    private static String normalize(String value)
    {
        String[] parts = value.split("[.]");

        return parts.length >= 2 ? NORMALIZER.normalize(parts[1]) : NORMALIZER.normalize(value);
    }
}
