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

package com.incadencecorp.coalesce.framework.datamodel;

/**
 * Contains history about linkage changes including who and when the change was
 * made.
 * 
 * @author Derek C.
 */
public class CoalesceHistory extends CoalesceObject implements ICoalesceHistory {

    // -----------------------------------------------------------------------//
    // Protected Member Variables
    // -----------------------------------------------------------------------//

    private History _history;

   
    // -----------------------------------------------------------------------//
    // Initialization
    // -----------------------------------------------------------------------//

    protected boolean initialize(CoalesceObjectHistory parent, History history)
    {

        // Set References
        setParent(parent);
        _history = history;

        return super.initialize(_history);

    }

    // -----------------------------------------------------------------------//
    // Overrides
    // -----------------------------------------------------------------------//

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        // This element has no children
        return false;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        return setOtherAttribute(name, value);
    }

}
