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

package com.incadencecorp.coalesce.common.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;

/**
 * Object that represents an ACCM nickname.
 * 
 * @author Derek
 *
 */
public class ACCMNickname implements Comparable<ACCMNickname> {

    /*
     * ----------------------------------------------------------------------
     * Private Members
     * ----------------------------------------------------------------------
     */

    private String _nickname;
    private List<String> _caveats;

    /*
     * ----------------------------------------------------------------------
     * Constructors
     * ----------------------------------------------------------------------
     */

    /**
     *
     * @param nickname
     * @param caveats
     */
    public ACCMNickname(String nickname, String... caveats)
    {
        this(nickname);

        _caveats.addAll(Arrays.asList(caveats));
    }

    /**
     * Parses the provided string for the nick name and caveats.
     * 
     * @param value
     */
    public ACCMNickname(String value)
    {

        _caveats = new ArrayList<>();

        value = value.toUpperCase();

        // Trim off ACCM
        if (value.startsWith("ACCM-"))
        {
            value = value.replace("ACCM-", "");
        }

        String[] parts = value.trim().split(" ");

        if (parts.length > 0)
        {
            _nickname = parts[0];

            for (int ii = 1; ii < parts.length; ii++)
            {
                if (!StringHelper.isNullOrEmpty(parts[ii]))
                {
                    _caveats.add(parts[ii].trim());
                }
            }

        }

    }

    /*
     * ----------------------------------------------------------------------
     * Public Methods
     * ----------------------------------------------------------------------
     */

    /**
     * Adds an additional caveat.
     * 
     * @param caveat
     */
    public void addCaveat(String caveat)
    {
        _caveats.add(caveat);
    }

    /**
     * 
     * @return a list of caveats sorted alphanumerically.
     */
    public String[] getCaveat()
    {

        // Sort the list
        Collections.sort(_caveats);

        return _caveats.toArray(new String[_caveats.size()]);
    }

    /**
     * 
     * @return the nickname.
     */
    public String getNickname()
    {
        return _nickname;
    }

    /*
     * ----------------------------------------------------------------------
     * Overrides
     * ----------------------------------------------------------------------
     */

    @Override
    public String toString()
    {
        if (getCaveat().length != 0)
        {
            return _nickname + " " + StringUtils.join(getCaveat(), " ");
        }
        else
        {
            return _nickname;
        }
    }

    @Override
    public int compareTo(ACCMNickname value)
    {
        return getNickname().compareToIgnoreCase(value.getNickname());
    }

}
