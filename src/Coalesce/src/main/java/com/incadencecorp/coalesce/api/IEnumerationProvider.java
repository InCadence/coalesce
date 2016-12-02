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

import java.security.Principal;
import java.util.List;

/**
 * Defines the interface used by the enumeration field type for converting
 * between integers and strings.
 * 
 * @author n78554
 *
 */
public interface IEnumerationProvider {

    /**
     * @param principal
     * @param enumeration
     * @return <code>true</code> if this provider handles the given enumeration;
     *         otherwise <code>false</code>
     */
    boolean handles(Principal principal, String enumeration);

    /**
     * @param principal
     * @param enumeration
     * @param value
     * @return the string representation of the given enumeration position if
     *         valid; otherwise throws an exception.
     * @throws IndexOutOfBoundsException
     */
    String toString(Principal principal, String enumeration, int value) throws IndexOutOfBoundsException;

    /**
     * 
     * @param principal
     * @param enumeration
     * @param value
     * @return the position of the given enumeration string if valid; otherwise
     *         throws an exception.
     * @throws IndexOutOfBoundsException
     */
    int toPosition(Principal principal, String enumeration, String value) throws IndexOutOfBoundsException;

    /**
     * 
     * @param principal
     * @param enumeration
     * @param value
     * @return <code>true</code> if the given value is within bounds for the
     *         specified enumeration; otherwise <code>false</code>
     */
    boolean isValid(Principal principal, String enumeration, int value);

    /**
     * 
     * @param principal
     * @param enumeration
     * @param value
     * @return <code>true</code> is the given value is valid for the specified
     *         enumeration; otherwise <code>false</code>
     */
    boolean isValid(Principal principal, String enumeration, String value);

    /**
     * @param principal
     * @param enumeration
     * @return a list of values for the given enumeration.
     */
    List<String> getValues(Principal principal, String enumeration);

    /**
     * @return a complete list of enumerations that are currently supported by
     *         this provider.
     */
    List<String> getEnumerations();

}
