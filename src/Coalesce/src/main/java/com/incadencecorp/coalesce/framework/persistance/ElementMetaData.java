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


package com.incadencecorp.coalesce.framework.persistance;


/**
 * Stores meta data about an element within an Coalesce Entity.
 * 
 * @see ICoalescePersistor#getXPath(String, String)
 */
public class ElementMetaData {

    private String _entityKey;
    private String _elementXPath;

    /**
     * 
     * @param key
     * @param xPath
     */
    public ElementMetaData(final String key, final String xPath)
    {
        _entityKey = key;
        _elementXPath = xPath;
    }

    /**
     * @return the GUID that uniquely identifies a Coalesce entity that
     *         contains the element of interest.
     */
    public final String getEntityKey()
    {
        return _entityKey;
    }

    /**
     * @return the XML path within the Coalesce entity specified by
     *         _entityKey that contains the element of interest.
     */
    public final String getElementXPath()
    {
        return _elementXPath;
    }

}