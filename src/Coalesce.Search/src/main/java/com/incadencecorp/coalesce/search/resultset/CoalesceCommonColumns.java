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

package com.incadencecorp.coalesce.search.resultset;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.opengis.filter.expression.PropertyName;

/**
 * @author Derek Clemenzi
 */
public class CoalesceCommonColumns {

    private final String ENTITY_KEY_COLUMN_NAME;
    private final String TEMPLATE_KEY_COLUMN_NAME;
    private final String ENTITY_XML_COLUMN_NAME;
    private final String TEMPLATE_XML_COLUMN_NAME;
    private final String ENTITY_NAME_COLUMN_NAME;
    private final String ENTITY_SOURCE_COLUMN_NAME;
    private final String ENTITY_VERSION_COLUMN_NAME;
    private final String ENTITY_ID_COLUMN_NAME;
    private final String ENTITY_ID_TYPE_COLUMN_NAME;
    private final String ENTITY_DATE_CREATED_COLUMN_NAME;
    private final String ENTITY_LAST_MODIFIED_COLUMN_NAME;
    private final String ENTITY_LAST_MODIFIED_BY_COLUMN_NAME;
    private final String ENTITY_UPLOADED_TO_SERVER_NAME;
    private final String ENTITY_TITLE_COLUMN_NAME;
    private final String ENTITY_STATUS_COLUMN_NAME;
    private final String ENTITY_SCOPE_COLUMN_NAME;
    private final String ENTITY_CREATOR_COLUMN_NAME;
    private final String ENTITY_TYPE_COLUMN_NAME;

    private final String LINKAGE_ENTITY2_KEY_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_NAME_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_SOURCE_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_VERSION_COLUMN_NAME;
    private final String LINKAGE_LAST_MODIFIED_COLUMN_NAME;
    private final String LINKAGE_LABEL_COLUMN_NAME;
    private final String LINKAGE_LINK_TYPE_COLUMN_NAME;
    private final String LINKAGE_LINK_STATUS_COLUMN_NAME;

    private final ICoalesceNormalizer normalizer;

    public CoalesceCommonColumns(ICoalesceNormalizer normalizer)
    {
        this.normalizer = normalizer;

        ENTITY_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityKey());
        ENTITY_XML_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityXml());
        TEMPLATE_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getTemplateKey());
        TEMPLATE_XML_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getTemplateXml());
        ENTITY_NAME_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getName());
        ENTITY_SOURCE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getSource());
        ENTITY_VERSION_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getVersion());
        ENTITY_ID_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityId());
        ENTITY_ID_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityIdType());
        ENTITY_DATE_CREATED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getDateCreated());
        ENTITY_LAST_MODIFIED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLastModified());
        ENTITY_LAST_MODIFIED_BY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLastModifiedBy());
        ENTITY_UPLOADED_TO_SERVER_NAME = getColumnName(CoalescePropertyFactory.getUploadedToServer());
        ENTITY_TITLE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityTitle());
        ENTITY_STATUS_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityStatus());
        ENTITY_SCOPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityScope());
        ENTITY_CREATOR_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getCreatedBy());
        ENTITY_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityType());

        LINKAGE_ENTITY2_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageEntityKey());
        LINKAGE_ENTITY2_NAME_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageName());
        LINKAGE_ENTITY2_SOURCE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageSource());
        LINKAGE_ENTITY2_VERSION_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageVersion());
        LINKAGE_LAST_MODIFIED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLastModified());
        LINKAGE_LABEL_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageLabel());
        LINKAGE_LINK_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageType());
        LINKAGE_LINK_STATUS_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageStatus());

    }

    public String getTemplateKey()
    {
        return TEMPLATE_KEY_COLUMN_NAME;
    }

    public String getTemplateXml()
    {
        return TEMPLATE_XML_COLUMN_NAME;
    }

    public String getKey()
    {
        return ENTITY_KEY_COLUMN_NAME;
    }

    public String getXml()
    {
        return ENTITY_XML_COLUMN_NAME;
    }

    public String getName()
    {
        return ENTITY_NAME_COLUMN_NAME;
    }

    public String getSource()
    {
        return ENTITY_SOURCE_COLUMN_NAME;
    }

    public String getVersion()
    {
        return ENTITY_VERSION_COLUMN_NAME;
    }

    public String getEntityId()
    {
        return ENTITY_ID_COLUMN_NAME;
    }

    public String getEntityIdType()
    {
        return ENTITY_ID_TYPE_COLUMN_NAME;
    }

    public String getDateCreated()
    {
        return ENTITY_DATE_CREATED_COLUMN_NAME;
    }

    public String getLastModified()
    {
        return ENTITY_LAST_MODIFIED_COLUMN_NAME;
    }

    public String getLastModifiedBy()
    {
        return ENTITY_LAST_MODIFIED_BY_COLUMN_NAME;
    }

    public String getUploadedToServer()
    {
        return ENTITY_UPLOADED_TO_SERVER_NAME;
    }

    public String getTitle()
    {
        return ENTITY_TITLE_COLUMN_NAME;
    }

    public String getStatus()
    {
        return ENTITY_STATUS_COLUMN_NAME;
    }

    public String getScope()
    {
        return ENTITY_SCOPE_COLUMN_NAME;
    }

    public String getCreator()
    {
        return ENTITY_CREATOR_COLUMN_NAME;
    }

    public String getType()
    {
        return ENTITY_TYPE_COLUMN_NAME;
    }

    public String getEntity2Key()
    {
        return LINKAGE_ENTITY2_KEY_COLUMN_NAME;
    }

    public String getEntity2Name()
    {
        return LINKAGE_ENTITY2_NAME_COLUMN_NAME;
    }

    public String getEntity2Source()
    {
        return LINKAGE_ENTITY2_SOURCE_COLUMN_NAME;
    }

    public String getEntity2Version()
    {
        return LINKAGE_ENTITY2_VERSION_COLUMN_NAME;
    }

    public String getLinkageLastModified()
    {
        return LINKAGE_LAST_MODIFIED_COLUMN_NAME;
    }

    public String getLinkageLabel()
    {
        return LINKAGE_LABEL_COLUMN_NAME;
    }

    public String getLinkageType()
    {
        return LINKAGE_LINK_TYPE_COLUMN_NAME;
    }

    public String getLinkageStatus()
    {
        return LINKAGE_LINK_STATUS_COLUMN_NAME;
    }

    private String getColumnName(PropertyName name)
    {
        String[] parts = CoalescePropertyFactory.getColumnName(normalizer, name).split("[.]");
        return parts.length > 1 ? parts[1] : parts[0];
    }

}
