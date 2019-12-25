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

package com.incadencecorp.coalesce.search.factory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.datamodel.IFieldEnum;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;

/**
 * Defines the properties of a CoalesceEntity.
 *
 * @author n78554
 */
public class CoalescePropertyFactory {

    private static FilterFactory2 ff;

    /**
     * Separator used for property names when creating filters (recordsetname.fieldname)
     */
    public static final String SEPERATOR = ".";
    /**
     * The name to use for the recordset when specifying attributes of the root entity element.
     */
    public static final String COALESCE_ENTITY_TABLE = "coalesceentity" + SEPERATOR;
    /**
     * The name to use for the recordset when specifying fields of a linkage.
     */
    public static final String COALESCE_LINKAGE_TABLE = "coalescelinkage" + SEPERATOR;
    /**
     * The name to use for the CoalesceTemplate table.
     */
    public static final String COALESCE_TEMPLATE_TABLE = "CoalesceTemplates" + SEPERATOR;

    /**
     * Overrides the default factory.
     *
     * @param value
     */
    public static void initialize(FilterFactory2 value)
    {
        ff = value;
    }

    /**
     * @return the filter factory that this class has been initialized with.
     */
    public static FilterFactory2 getFilterFactory()
    {

        if (ff == null)
        {
            ff = CommonFactoryFinder.getFilterFactory2(null);
        }

        return ff;
    }

    /*--------------------------------------------------------------------------
    Coalesce Entity Table Expressions
    --------------------------------------------------------------------------*/

    /**
     * @return the property used for filtering on date created.
     */
    public static PropertyName getEntityXml()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "entityxml");
    }

    /**
     * @return the property used for filtering on date created.
     */
    public static PropertyName getDateCreated()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_DATECREATED);
    }

    /**
     * @return the property used for filtering on last modified.
     */
    public static PropertyName getCreatedBy()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "creator");
    }

    public static Filter getCreatedBy(String user)
    {
        return getFilterFactory().equal(getCreatedBy(), getFilterFactory().literal(user), false);
    }

    /**
     * @return the property used for filtering on last modified.
     */
    public static PropertyName getLastModified()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_LASTMODIFIED);
    }

    /**
     * @return the property used for filtering on last modified.
     */
    public static PropertyName getLastModifiedBy()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_MODIFIEDBY);
    }

    public static Filter getLastModifiedBy(String user)
    {
        return getFilterFactory().equal(getLastModifiedBy(), getFilterFactory().literal(user), false);
    }

    /**
     * @return the property used for filtering on Uploaded to Server.
     */
    public static PropertyName getUploadedToServer()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_UPLOADEDTOSERVER);
    }

    /**
     * @return the property used for filtering on version.
     */
    public static PropertyName getVersion()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_VERSION);
    }

    /**
     * @return a filter for the specified source
     */
    public static Filter getVersion(String version)
    {
        return ff.equal(getVersion(), ff.literal(version), false);
    }

    /**
     * @return the property used for filtering on version.
     */
    public static PropertyName getDeleted()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "deleted");
    }

    /**
     * @return the property used for filtering on source.
     */
    public static PropertyName getSource()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_SOURCE);
    }

    /**
     * @return a filter for the specified source
     */
    public static Filter getSource(String source)
    {
        return ff.equal(getSource(), ff.literal(source), false);
    }

    /**
     * @return the property used for filtering on a entity name.
     */
    public static PropertyName getName()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_NAME);
    }

    /**
     * @return a filter for the specified name
     */
    public static Filter getName(String name)
    {
        return ff.equal(getName(), ff.literal(name), false);
    }

    /**
     * @return the property used for filtering on the entity key.
     */
    public static PropertyName getEntityKey()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "objectkey");
    }

    /**
     * @return the property used for filtering on the entity key.
     */
    public static PropertyName getTemplateKey()
    {
        return getFilterFactory().property(COALESCE_TEMPLATE_TABLE + "templatekey");
    }

    /**
     * @return the property used for filtering on date created.
     */
    public static PropertyName getTemplateXml()
    {
        return getFilterFactory().property(COALESCE_TEMPLATE_TABLE + "templatexml");
    }

    /**
     * @param recordset name of the record
     * @return the property used for filtering on the entity key.
     */
    public static PropertyName getRecordKey(String recordset)
    {
        return getFilterFactory().property(recordset + ".objectkey");
    }

    /**
     * @param recordset name of the record
     * @param key       of the record
     * @return a filter recordsetname.objectkey = key
     */
    public static Filter getRecordKey(String recordset, String key)
    {
        return getFilterFactory().equal(getRecordKey(recordset), getFilterFactory().literal(key), false);
    }

    /**
     * @param property to test
     * @return whether or not the property name refers to a record key verse a entity key.
     */
    public static boolean isRecordPropertyName(String property)
    {
        return property.toLowerCase().endsWith("objectkey") && !property.toLowerCase().startsWith("coalesceentity");
    }

    /**
     * @param key of the entity
     * @return a filter objectkey = key
     */
    public static Filter getEntityKey(String key)
    {
        return getFilterFactory().equal(getEntityKey(), getFilterFactory().literal(key), false);
    }

    /**
     * @return the property used for filtering on the entity's title.
     */
    public static PropertyName getEntityTitle()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_TITLE);
    }

    /**
     * @return the property used for filtering on the entity's type.
     */
    public static PropertyName getEntityType()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "type");
    }

    /**
     * @return the property used for filtering on the entity's id.
     */
    public static PropertyName getEntityId()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_ENTITYID);
    }

    /**
     * @return the property used for filtering on the entity's id type.
     */
    public static PropertyName getEntityIdType()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_ENTITYIDTYPE);
    }

    /**
     * @return the property used for filtering on the entity's status.
     */
    public static PropertyName getEntityStatus()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_STATUS);
    }

    /**
     * @return a filter that excludes entities that have been marked as deleted.
     */
    public static Filter getIsActive()
    {
        return ff.notEqual(getEntityStatus(), ff.literal(ECoalesceObjectStatus.DELETED.toString()));
    }

    /**
     * @return the property used for filtering on the entity's scope.
     */
    public static PropertyName getEntityScope()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "scope");
    }

    /*--------------------------------------------------------------------------
    Coalesce Linkage Table Expressions
    --------------------------------------------------------------------------*/

    /**
     * @return the property used for filtering on the linkage key.
     */
    public static PropertyName getLinkageKey()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + CoalesceLinkage.ATTRIBUTE_KEY);
    }

    /**
     * @return the property used for filtering on linkage's date created.
     */
    public static PropertyName getLinkageDateCreated()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_DATECREATED);
    }

    /**
     * @return the property used for filtering on linkages's last modified.
     */
    public static PropertyName getLinkageLastModified()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + CoalesceEntity.ATTRIBUTE_LASTMODIFIED);
    }

    /**
     * @return the property used for filtering on the entity key of linked
     * entities.
     */
    public static PropertyName getLinkageEntityKey()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + CoalesceLinkage.ATTRIBUTE_ENTITY2KEY);
    }

    /**
     * @param key
     * @return a filter objectkey = key
     */
    public static Filter getLinkageEntityKey(String key)
    {
        return getFilterFactory().equal(getLinkageEntityKey(), getFilterFactory().literal(key), false);
    }

    /**
     * @return the property used for filtering on versions of linked entities.
     */
    public static PropertyName getLinkageVersion()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + CoalesceLinkage.ATTRIBUTE_ENTITY2VERSION);
    }

    /**
     * @return the property used for filtering on sources of linked entities.
     */
    public static PropertyName getLinkageSource()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + CoalesceLinkage.ATTRIBUTE_ENTITY2SOURCE);
    }

    /**
     * @return the property used for filtering on entity names of linked
     * entities.
     */
    public static PropertyName getLinkageName()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + CoalesceLinkage.ATTRIBUTE_ENTITY2NAME);
    }

    /**
     * @return the property used for filtering on linkage types.
     */
    public static PropertyName getLinkageType()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + CoalesceLinkage.ATTRIBUTE_LINKTYPE);
    }

    /**
     * @param type
     * @return the property used for filtering on linkage statuses.
     */
    public static Filter getLinkageType(ELinkTypes type)
    {
        return getFilterFactory().equals(getLinkageType(), getFilterFactory().literal(type.getLabel()));
    }

    /**
     * @return the property used for filtering on linkage statuses.
     */
    public static PropertyName getLinkageStatus()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "link" + CoalesceLinkage.ATTRIBUTE_STATUS);
    }

    /**
     * @param status
     * @return the property used for filtering on linkage statuses.
     */
    public static Filter getLinkageStatus(ECoalesceObjectStatus status)
    {
        return getFilterFactory().equals(getLinkageStatus(), getFilterFactory().literal(status.toString().toUpperCase()));
    }

    /**
     * @return the property used for filtering on linkage statuses.
     */
    public static PropertyName getLinkageLabel()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "link" + CoalesceLinkage.ATTRIBUTE_LABEL);
    }

    /**
     * @return the property used for filtering on the provided field
     */
    public static PropertyName getFieldProperty(CoalesceField<?> field)
    {
        return getFieldProperty(field.getParent().getParent().getName(), field.getName());
    }

    /**
     * @return the property used for filtering on the provided recordset / field
     * name
     */
    public static PropertyName getFieldProperty(String recordset, String field)
    {
        return getFilterFactory().property(recordset + "." + field);
    }

    /**
     * @return the property used for filtering on the provided recordset / field
     * name
     */
    public static PropertyName getFieldProperty(String recordset, IFieldEnum field)
    {
        return getFieldProperty(recordset, field.getFieldName());
    }

    /**
     * @param field
     * @return the normalized name used as the column name in a result set.
     */
    public static String getColumnName(CoalesceField<?> field)
    {
        return getColumnName(getFieldProperty(field));
    }

    /**
     * @param property
     * @return the normalized name used as the column name in a result set.
     */
    public static String getColumnName(PropertyName property)
    {
        return getColumnName(property.getPropertyName());
    }

    /**
     * @param property
     * @return the normalized name used as the column name in a result set.
     */
    public static String getColumnName(String property)
    {
        return property.replaceAll("[.]", "");
    }

    /**
     * @param property
     * @return the normalized name used for storing the property
     */
    public static String getColumnName(ICoalesceNormalizer normalizer, PropertyName property)
    {
        String[] parts = property.getPropertyName().split("[.]");

        switch (parts.length)
        {
        case 1:
            return normalizer.normalize(parts[0]);

        case 2:
            return normalizer.normalize(parts[0], parts[1]);

        default:
            throw new IllegalArgumentException(String.format(CoalesceErrors.INVALID_INPUT, property.getPropertyName()));
        }
    }
}
