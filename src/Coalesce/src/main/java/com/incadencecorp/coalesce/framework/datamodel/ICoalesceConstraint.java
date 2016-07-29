package com.incadencecorp.coalesce.framework.datamodel;

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

/**
 * Interface for accessing constraints.
 * 
 * @author n78554
 */
public interface ICoalesceConstraint {

    /**
     * @return the type of this constraint.
     */
    ConstraintType getConstraintType();

    /**
     * Sets the type of this constraint.
     * 
     * @param type
     */
    void setConstraintType(ConstraintType type);

    /**
     * @return the arguments that are to be applied as the constraint.
     */
    String getValue();

    /**
     * Sets the arguments that are to be applied as the constraint.
     * 
     * @param value
     */
    void setValue(String value);

    /**
     * @return the field definition that this constraint belongs to.
     */
    CoalesceFieldDefinition getFieldDefinition();

}
