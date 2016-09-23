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
     * Format: "Constraint (%s) in Entity (%s) is invalid for field type (%s)"
     */
    public static final String INVALID_CONSTRAINT = "Constraint (%s) in Entity (%s) is invalid for field type (%s)";

    /**
     * Error reported when a mandatory record set is missing records. Format:
     * "Missing Mandatory Recordset: (%s)"
     */
    public static final String INVALID_MANDOTORY_RECORDSET = "Missing Mandatory Recordset: (%s)";

    /**
     * Error reported when a mandatory field set is missing values. Format:
     * "Missing Mandatory Field: (%s)"
     */
    public static final String INVALID_MANDOTORY_FIELD = "Missing Mandatory Field: (%s)";

    /**
     * Error reported when field's data does not match a regular expression.
     * "Invalid Input (%s)"
     */
    public static final String INVALID_INPUT = "Invalid Input: (%s)";

    /**
     * Error reported when field's data does not match a regular expression.
     * "Invalid Input (%s): (%s)"
     */
    public static final String INVALID_INPUT_REASON = "Invalid Input (%s): (%s)";

    /**
     * Error reported when field's data does not match a regular expression.
     * "Invalid Input (Value exceeds the %s): (%s)"
     */
    public static final String INVALID_INPUT_EXCEEDS = "Invalid Input (Value exceeds the %s): (%s)";

    /**
     * Error reported when a list field's length does not match the constraint.
     * "Invalid List Length: (%s)"
     */
    public static final String INVALID_CONSTRAINT_LIST_LENGTH = "Invalid List Length: (%s)";

    /**
     * Error reported when a min or max constraint is applied to a non numeric field.
     * "Invalid Data Type (Min constraint can only be applied to numerics): (%s)"
     */
    public static final String INVALID_DATA_TYPE_NUMERIC = "Invalid Data Type (Constraint can only be applied to numerics): (%s)";

    
    /**
     * Error reported when a field's data types does not match its definition
     * Format:
     * "Field (%s) of type (%s) in Entity (%s) does not match it's definition of (%s)"
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
     * Error reported when failing to load a template Format:
     * "Template (%s) (%s) (%s) failed to load"
     */
    public static final String TEMPLATE_LOAD = "Template (%s) (%s) (%s) failed to load";

    /**
     * Error reported when a task fails to execute. Format:
     * "(FAILED) Task (%s) failed on (%s) because (%s)."
     */
    public static final String FAILED_TASK = "(FAILED) Task (%s) failed in (%s) because (%s).";

    /**
     * Error reported when attempting to verify a hash that has had items
     * removed. Format: "Invalid Hash (One or more values were removed): %s"
     */
    public static final String ERR_HASH_VALUES_RMV = "Invalid Hash (One or more values were removed): %s";

    /**
     * Error reported when attempting to verify a hash that has had items
     * modified. Format: "Invalid Hash (One or more values modified): %s"
     */
    public static final String ERR_HASH_VALUES_MODIFIED = "Invalid Hash (One or more values modified): %s";

    /**
     * Warning reported when attempting to verify a hash that has had items
     * added. Format: "Updating Hash (%s values added): %s"
     */
    public static final String ERR_HASH_VALUES_ADD = "Updating Hash (%s values added): %s";

}
