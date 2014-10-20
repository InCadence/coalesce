package com.incadencecorp.coalesce.common.helpers;

import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

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
 * Performs the translation between languages as strings stored in the format defined in
 * "RFC 3066, Tags for the Identification of Languages" and {@link Locale}.
 * 
 * @author InCadence
 *
 */
public class LocaleConverter {

    // Make static class
    private LocaleConverter()
    {

    }

    // -----------------------------------------------------------------------'
    // Public Shared Methods
    // -----------------------------------------------------------------------'

    /**
     * Converts a {@link Locale} to a string format to be stored in
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} xml.
     * 
     * @param value the Locale to be converted
     * @return A string version of <code>value</code>
     */
    public static String printLocale(Locale value)
    {
        if (value == null) return null;

        String language = value.getLanguage();
        String country = value.getCountry();

        if (StringHelper.isNullOrEmpty(country))
        {
            return language;
        }
        else
        {
            return language + "-" + country;
        }

    }

    /**
     * Converts a {@link java.lang.String} to a {@link Locale}.
     * 
     * @param value the {@link Locale} string to be converted
     * @return A {@link Locale} representation of <code>value</code>
     * 
     * @throws IllegalArgumentException if <code>value</code> cannot be parsed correctly
     */
    public static Locale parseLocale(String value) 
    {
        if (StringHelper.isNullOrEmpty(value)) return null;

        return LocaleUtils.toLocale(value.replace("-", "_"));

    }
}
