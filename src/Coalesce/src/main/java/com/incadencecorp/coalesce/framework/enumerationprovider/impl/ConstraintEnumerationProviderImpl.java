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

package com.incadencecorp.coalesce.framework.enumerationprovider.impl;

import java.security.Principal;
import java.util.List;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.ConstraintType;

/**
 * This implementations allows pulls enumerations from the constraints.
 * 
 * @author Derek
 */
public class ConstraintEnumerationProviderImpl extends AbstractEnumerationProvider {

    /**
     * Creates a provider with no default constraints.
     */
    public ConstraintEnumerationProviderImpl()
    {
    }

    /**
     * Construct the provider with the given supported enumerations.
     * 
     * @param constraints
     */
    public ConstraintEnumerationProviderImpl(CoalesceConstraint... constraints)
    {
        add(constraints);
    }

    /**
     * Add additional constraints to the supported enumerations.
     * 
     * @param constraints
     */
    public void add(CoalesceConstraint... constraints)
    {
        for (CoalesceConstraint constraint : constraints)
        {
            if (constraint.getConstraintType() == ConstraintType.ENUMERATION && !handles(null, constraint.getValue()))
            {

                String regex = constraint.getAttribute("regex");

                if (regex != null)
                {
                    addEnumeration(null, constraint.getValue(), CoalesceConstraint.regExToValues(regex));
                }

            }
        }
    }

    /**
     * This implementation relies on CoalesceValidator calling
     * {@link #add(CoalesceConstraint...)} to add supported enumerations.
     */
    @Override
    protected List<String> lookup(Principal principal, String enumeration)
    {
        return null;
    }
}
