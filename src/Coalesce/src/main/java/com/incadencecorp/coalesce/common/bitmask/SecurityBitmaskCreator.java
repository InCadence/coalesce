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

package com.incadencecorp.coalesce.common.bitmask;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;

/**
 * This class is used for generating bitmasks based on the options provided
 * during construction.
 * 
 * @author n78554
 */
public class SecurityBitmaskCreator {

    protected List<String> validOptions = new ArrayList<>();
    private String name;
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityBitmaskCreator.class);

    /**
     * Constructs a creator.
     * 
     * @param options
     */
    public SecurityBitmaskCreator(Iterator<String> options)
    {
        while (options.hasNext())
        {
            validOptions.add(options.next());
        }
    }

    /**
     * Constructs a creator.
     * 
     * @param options
     */
    public SecurityBitmaskCreator(String... options)
    {
        Collections.addAll(validOptions, options);
    }

    /**
     * Constructs a creator.
     * 
     * @param clazz
     */
    public <E extends Enum<E>> SecurityBitmaskCreator(Class<E> clazz)
    {
        for (E e : EnumSet.allOf(clazz))
        {
            validOptions.add(e.toString());
        }
    }

    /**
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name
     * 
     * @param value
     */
    public void setName(String value)
    {
        name = value;
    }

    /**
     * 
     * @param value
     * @return a bit mask initialized to the provided value.
     */
    public boolean[] createFilledBitmask(boolean value)
    {
        boolean mask[] = new boolean[this.validOptions.size() + 1];
        Arrays.fill(mask, value);
        return mask;
    }

    /**
     * @param ignoreInvalid if <code>false</code> the no access bit (0) will be
     *            set to true if any of the options were not registered with
     *            this creator.
     * @param options
     * @return a mask for the provided options against the registered options.
     */
    public boolean[] createBitmask(boolean ignoreInvalid, String... options)
    {
        return createBitmask(ignoreInvalid, Arrays.asList(options));
    }

    /**
     * @param ignoreInvalid if <code>false</code> the no access bit (0) will be
     *            set to true if any of the options were not registered with
     *            this creator.
     * @param options
     * @return a mask for the provided options against the registered options.
     */
    public boolean[] createBitmask(boolean ignoreInvalid, Collection<String> options)
    {
        boolean mask[] = createFilledBitmask(false);

        if (!validOptions.isEmpty())
        {
            for (String option : options)
            {

                int idx = this.validOptions.indexOf(option);

                if (idx != -1)
                {
                    mask[idx + 1] = true;
                }
                else if (!ignoreInvalid)
                {
                    if (LOGGER.isWarnEnabled())
                    {
                        LOGGER.warn(String.format(CoalesceErrors.INVALID_HASH_OPTION, option, getName()));
                    }

                    // Set No Access
                    mask[0] = true;
                }
            }
        }

        return mask;
    }

    /**
     * @param mask
     * @return a truncated or padded mask that matches the options provided by
     *         this creator.
     */
    public boolean[] createBitmask(boolean[] mask)
    {
        boolean[] result = createFilledBitmask(false);

        for (int ii = 0; ii < result.length; ii++)
        {
            if (ii < mask.length)
            {
                result[ii] = mask[ii];
            }
        }

        return result;
    }

    /**
     * @param mask
     * @return a set of options specified by this mask.
     */
    public List<String> getOptions(boolean[] mask)
    {
        List<String> results = new ArrayList<>();

        for (int ii = 0; ii < mask.length; ii++)
        {
            if (mask[ii])
            {
                // Add Option
                if (ii == 0)
                {
                    results.add("NO ACCESS");
                }
                else
                {
                    results.add(validOptions.get(ii - 1));
                }
            }
        }

        return results;
    }

    /**
     * @param mask a string of 0(s) and 1(s).
     * @return a set of options specified by this mask.
     */
    public List<String> getOptions(String mask)
    {
        return getOptions(SecurityBitmaskHelper.fromString(mask));
    }

    /**
     * @return the options registered with this creator.
     */
    public List<String> getOptions()
    {
        List<String> results = new ArrayList<>();
        results.addAll(validOptions);

        return results;
    }

}
