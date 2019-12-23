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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This implementation allows tying a Enumeration field to a java enumeration by
 * providing the fully qualified class name. The enumeration must be on the
 * classpath for it to work.
 *
 * @author n78554
 */
public class JavaEnumerationProviderImpl extends AbstractEnumerationProvider {

    @Override
    protected List<String> lookup(Principal principal, String enumeration)
    {
        try
        {
            // Check the Class Path
            return getValues(Class.forName(enumeration));
        }
        catch (ClassNotFoundException | ClassCastException e)
        {
            return null;
        }
    }

    @Override
    public int toPosition(Principal principal, String enumeration, String value) throws IndexOutOfBoundsException
    {
        return super.toPosition(principal, enumeration, value.toUpperCase());
    }

    @Override
    public boolean isValid(Principal principal, String enumeration, String value)
    {
        return super.isValid(principal, enumeration, value.toUpperCase());
    }

    @Override
    protected void addEnumeration(Principal principal, String enumeration, List<String> values)
    {
        super.addEnumeration(principal, enumeration, values.stream().map(String::toUpperCase).collect(Collectors.toList()));
    }

    private List<String> getValues(Class clazz)
    {

        List<String> values = new ArrayList<>();

        for (Object o : EnumSet.allOf(clazz))
        {
            values.add(o.toString());
        }

        return values;
    }
}
