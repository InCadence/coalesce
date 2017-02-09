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

import org.geotools.factory.CommonFactoryFinder;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;

import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;

/**
 * Defines the properties of a CoalesceEntity.
 * 
 * @author n78554
 */
public class CoalescePropertyFactory {

    private static FilterFactory ff;

    private static final String SEPERATOR = ".";
    private static final String COALESCE_ENTITY_TABLE = "coalesceentity" + SEPERATOR;
    private static final String COALESCE_LINKAGE_TABLE = "coalescelinkage" + SEPERATOR;

    /**
     * Overrides the default factory.
     *
     * @param value
     */
    public final static void initialize(FilterFactory value)
    {
        ff = value;
    }

    /**
     * @return the filter factory that this class has been initialized with.
     */
    public final static FilterFactory getFilterFactory()
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
    public static PropertyName getDateCreated()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "datecreated");
    }

    /**
     * @return the property used for filtering on last modified.
     */
    public static PropertyName getLastModified()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "lastmodified");
    }

    /**
     * @return the property used for filtering on version.
     */
    public static PropertyName getVersion()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "version");
    }

    /**
     * @return the property used for filtering on source.
     */
    public static PropertyName getSource()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "source");
    }

    /**
     * @return the property used for filtering on a entity name.
     */
    public static PropertyName getName()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "name");
    }

    /**
     * @return the property used for filtering on the entity key.
     */
    public static PropertyName getEntityKey()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "objectkey");
    }

    /**
     * @param key
     * @return a filter objectkey = key
     */
    public static Filter getEntityKey(String key)
    {
        return getFilterFactory().equals(getEntityKey(), getFilterFactory().literal(key));
    }

    /**
     * @return the property used for filtering on the entity title.
     */
    public static PropertyName getEntityTitle()
    {
        return getFilterFactory().property(COALESCE_ENTITY_TABLE + "title");
    }

    /*--------------------------------------------------------------------------
    Coalesce Linkage Table Expressions
    --------------------------------------------------------------------------*/

    /**
     * @return the property used for filtering on the entity key of linked
     *         entities.
     */
    public static PropertyName getLinkageEntityKey()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "entity2key");
    }

    /**
     * @param key
     * @return a filter objectkey = key
     */
    public static Filter getLinkageEntityKey(String key)
    {
        return getFilterFactory().equals(getLinkageEntityKey(), getFilterFactory().literal(key));
    }

    /**
     * @return the property used for filtering on versions of linked entities.
     */
    public static PropertyName getLinkageVersion()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "entity2version");
    }

    /**
     * @return the property used for filtering on sources of linked entities.
     */
    public static PropertyName getLinkageSource()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "entity2source");
    }

    /**
     * @return the property used for filtering on entity names of linked
     *         entities.
     */
    public static PropertyName getLinkageName()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "entity2name");
    }

    /**
     * @return the property used for filtering on linkage types.
     */
    public static PropertyName getLinkageType()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "linktype");
    }

    /**
     * @param type
     * @return the property used for filtering on linkage statuses.
     */
    public static Filter getLinkageType(ELinkTypes type)
    {
        return getFilterFactory().equals(getLinkageType(), getFilterFactory().literal(type.toString().toUpperCase()));
    }

    /**
     * @return the property used for filtering on linkage statuses.
     */
    public static PropertyName getLinkageStatus()
    {
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "linkstatus");
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
        return getFilterFactory().property(COALESCE_LINKAGE_TABLE + "linklabel");
    }
}
