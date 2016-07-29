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

package com.incadencecorp.coalesce.api;

/**
 * Defines error messages used throughout Coalesce.
 * 
 * @author Derek
 */
public final class CoalesceErrors {

    private CoalesceErrors()
    {
        // Do Nothing
    }

    /**
     * Error reported when the enumeration specified for the Enumeration Field
     * could not be located. Format: "Invalid Enumeration (%s)"
     */
    public static final String INVALID_ENUMERATION = "Invalid Enumeration (%s)";
    /**
     * Error reported when the enumeration value is not a member for the given
     * enumeration. Format: "(%s) is an invalid enumeration for (%s)"
     */
    public static final String INVALID_ENUMERATION_VALUE = "(%s) is an invalid enumeration for (%s)";
    /**
     * Error reported when the position is out of range for the given
     * enumeration. Format:
     * "Enumeration position (%s) is out of bounds for (%s)"
     */
    public static final String INVALID_ENUMERATION_POSITION = "Enumeration position (%s) is out of bounds for (%s)";
    /**
     * Error reported when access a utility class that has not been properly
     * initialized. Format: "(%s) not initialized"
     */
    public static final String NOT_INITIALIZED = "(%s) not initialized";

    /**
     * Error reported when a constraint is used on an invalid field type.
     * Format: "Constraint (%s) in Entity (%s) is invalid for field type (%s)
     */
    public static final String INVALID_CONSTRAINT = "Constraint (%s) in Entity (%s) is invalid for field type (%s)";

    /**
     * Error reported when a field's data types does not match its definition
     * Format: "Field (%s) of type (%s) in Entity (%s) does not match it's definition of (%s)"
     */
    public static final String INVALID_DATA_TYPE = "Field (%s) of type (%s) in Entity (%s) does not match it's definition of (%s)";

    /**
     * Error reported when a constraint is used on a deprecated field type.
     * Format:
     * "Constraint (%s) in Entity (%s) was deprecated for field type (%s)"
     */
    public static final String DEPRECATED_CONSTRAINT = "Constraint (%s) in Entity (%s) was deprecated for field type (%s)";

    /**
     * Error reported when a constraint is used on a deprecated field type.
     * Format: "Constraint (%s) not valid for field type (%s)"
     */
    public static final String INVALID_TYPE_CONSTRAINT = "Constraint (%s) not valid for field type (%s)";

    /**
     * Error reported when failing to load a template
     * Format: "Template (%s) (%s) (%s) failed to load"
     */
    public static final String TEMPLATE_LOAD = "Template (%s) (%s) (%s) failed to load";

    /**
     * Error reported when a task fails to execute.
     * Format: "(FAILED) Task (%s) failed on (%s) because (%s)."
     */
    public static final String FAILED_TASK = "(FAILED) Task (%s) failed in (%s) because (%s).";

}
