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

import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;

/**
 * Object that represents the ACCM dissemination control.
 * 
 * @author Derek
 */
public class ACCMControl {

    /*
     * ----------------------------------------------------------------------
     * Private Members
     * ----------------------------------------------------------------------
     */

    List<ACCMNickname> _nicknames;

    /*
     * ----------------------------------------------------------------------
     * Constructors
     * ----------------------------------------------------------------------
     */

    /**
     * Default Constructor
     */
    public ACCMControl()
    {
        _nicknames = new ArrayList<>();
    }

    /**
     * Parses the ACCM controls from a string.
     * 
     * @param value
     */
    public ACCMControl(String value)
    {
        this();

        if (!StringHelper.isNullOrEmpty(value))
        {
            // Trim off ACCM
            if (value.startsWith("ACCM-"))
            {
                value = value.replace("ACCM-", "");
            }

            for (String part : value.split("/"))
            {
                if (!StringHelper.isNullOrEmpty(part))
                {
                    _nicknames.add(new ACCMNickname(part));
                }
            }
        }

    }

    /**
     * @param nicknames
     */
    public ACCMControl(ACCMNickname... nicknames)
    {
        this();

        _nicknames.addAll(Arrays.asList(nicknames));
    }

    /*
     * ----------------------------------------------------------------------
     * Public Methods
     * ----------------------------------------------------------------------
     */

    /**
     * @return a list of nick names sorted alphanumerically.
     */
    public ACCMNickname[] getNicknames()
    {

        Collections.sort(_nicknames);

        return _nicknames.toArray(new ACCMNickname[_nicknames.size()]);

    }

    /**
     * Adds a nick name to the ACCM control.
     * 
     * @param nickname
     */
    public void addNickname(ACCMNickname nickname)
    {
        _nicknames.add(nickname);
    }

    /**
     * @return <code>true</code> if controls have been set.
     */
    public boolean hasControls()
    {
        return _nicknames.size() > 0;
    }

    /*
     * ----------------------------------------------------------------------
     * Override
     * ----------------------------------------------------------------------
     */

    @Override
    public String toString()
    {

        String marking = "";

        for (ACCMNickname nickname : getNicknames())
        {

            if (!StringHelper.isNullOrEmpty(marking))
            {
                marking += "/";
            }

            marking += nickname.toString();
        }

        return "ACCM-" + marking;

    }

}
