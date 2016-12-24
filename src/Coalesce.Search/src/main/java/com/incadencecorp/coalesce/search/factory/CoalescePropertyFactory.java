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
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;

/**
 * Defines the properties of a CoalesceEntity.
 * 
 * @author n78554
 */
public class CoalescePropertyFactory {

    private static FilterFactory ff;

    /**
     * Default Constructor
     */
    private CoalescePropertyFactory()
    {
    }

    /**
     * Overrides the default factory.
     *
     * @param value
     */
    public static void initialize(FilterFactory value)
    {
        ff = value;
    }

    /**
     * @return the filter factory that this class has been initialized with.
     */
    public static FilterFactory getFilterFactory()
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
        return getFilterFactory().property("coalesceentity.datecreated");
    }

    /**
     * @return the property used for filtering on last modified.
     */
    public static PropertyName getLastModified()
    {
        return getFilterFactory().property("coalesceentity.lastmodified");
    }

    /**
     * @return the property used for filtering on version.
     */
    public static PropertyName getVersion()
    {
        return getFilterFactory().property("coalesceentity.version");
    }

    /**
     * @return the property used for filtering on a entity name.
     */
    public static PropertyName getName()
    {
        return getFilterFactory().property("coalesceentity.name");
    }

    /**
     * @return the property used for filtering on the entity key.
     */
    public static PropertyName getEntityKey()
    {
        return getFilterFactory().property("coalesceentity.objectkey");
    }

    /**
     * @return the property used for filtering on the entity title.
     */
    public static PropertyName getEntityTitle()
    {
        return getFilterFactory().property("coalesceentity.title");
    }

}
