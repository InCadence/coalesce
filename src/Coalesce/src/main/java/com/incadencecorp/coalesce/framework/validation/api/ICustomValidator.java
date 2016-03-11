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

package com.incadencecorp.coalesce.framework.validation.api;

import java.security.Principal;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;

/**
 * Interface for specifying custom validators.
 * 
 * @author n78554
 */
public interface ICustomValidator {

    /**
     * 
     * @param principal of the user validating the field.
     * @param field being validated
     * @param constraint that the field is being validating against.
     * @return <code>null</code> if field is valid otherwise a detailed message
     *         explaining the violation.
     */
    String validate(Principal principal, CoalesceField<?> field, CoalesceConstraint constraint);

}
