/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.common.regex;

import com.incadencecorp.coalesce.common.helpers.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of RegEx patterns used for matching.
 *
 * @author Derek Clemenzi
 */
public class RegexCollection {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexCollection.class);

    private final Collection<Pattern> collection = new ArrayList<>();

    /**
     * @return regex expressions used by this collection.
     */
    public Collection<String> getRegex()
    {
        Collection<String> regexAsString = new HashSet<>();

        for (Pattern regex : collection)
        {
            regexAsString.add(regex.toString());
        }

        return regexAsString;
    }

    /**
     * Compiles the collection of regex into {@link Pattern}.
     *
     * @param values regex expressions
     */
    public void setRegex(Collection<String> values)
    {
        collection.clear();

        for (String regex : values)
        {
            collection.add(Pattern.compile(regex));
        }
    }

    /**
     * @param value to be matched
     * @return whether or not value matches all the regex expressions in this collection.
     */
    public boolean match(String value)
    {
        boolean result = true;

        if (value != null)
        {
            for (Pattern regex : collection)
            {
                LOGGER.trace("({}) check ({})", value, regex);

                result = regex.matcher(value).matches();

                if (!result)
                {
                    LOGGER.debug("(FAILED) ({}) check ({})", value, regex);
                    break;
                }
            }
        }

        return result;
    }

    public Collection<String> extract(String value, int max)
    {
        Collection<String> result = new ArrayList<>();

        if (value != null)
        {
            for (Pattern regex : collection)
            {
                LOGGER.trace("({}) extracting ({})", value, regex);

                Matcher matcher = regex.matcher(value);

                while (matcher.find())
                {
                    String match;

                    if (matcher.groupCount() >= 1)
                    {
                        match = matcher.group(matcher.groupCount());
                    }
                    else
                    {
                        match = matcher.group();
                    }

                    if (!StringHelper.isNullOrEmpty(match))
                    {
                        result.add(match);

                        if (result.size() >= max)
                        {
                            break;
                        }
                    }
                }

                if (result.size() >= max)
                {
                    break;
                }
            }
        }

        return result;
    }
}
